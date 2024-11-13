package com.magenta.service;


import com.magenta.dto.AuthDTO;
import com.magenta.dto.LoginDTO;
import com.magenta.dto.ModifyUserDTO;
import com.magenta.dto.RegisterDTO;
import com.magenta.persistence.entitiy.Role;
import com.magenta.persistence.entitiy.RoleEntity;
import com.magenta.persistence.entitiy.UserEntity;
import com.magenta.persistence.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ImageService imageService;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);


    /**
     * Procesa el inicio de sesión del usuario
     */
    public AuthDTO login(LoginDTO request) {
        // Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );


        // Buscar el usuario en la base de datos
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        return createAuthResponse(user);
    }


    /**
     * Registra un nuevo usuario en el sistema
     */
    public AuthDTO register(RegisterDTO request) {
        log.debug("Iniciando proceso de registro para usuario: {}", request.getUsername());


        // Validar si el usuario ya existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Usuario {} ya existe", request.getUsername());
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }


        // Validar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email {} ya está en uso", request.getEmail());
            throw new RuntimeException("El email ya está en uso");
        }


        try {
            // Crear el rol de usuario
            RoleEntity userRole = RoleEntity.builder()
                    .name(Role.USER.name())
                    .build();


            // Crear la entidad de usuario
            UserEntity user = UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .roles(Set.of(userRole))
                    .build();


            log.debug("Guardando nuevo usuario en la base de datos");


            // Guardar el usuario
            user = userRepository.save(user);


            log.debug("Usuario guardado exitosamente con ID: {}", user.getId());


            // Crear respuesta
            return AuthDTO.builder()
                    .token(jwtService.getToken(user))
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .roles(user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()))
                    .build();


        } catch (Exception e) {
            log.error("Error al registrar usuario: ", e);
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }


    /**
     * Obtiene la información de un usuario
     */
    @Transactional
    public AuthDTO modifyUser(ModifyUserDTO request) {
        log.debug("⚡ Iniciando modificación de usuario: {}", request.getUsername());

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Actualizar campos básicos
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }

            // Procesar imagen si existe
            MultipartFile profileImage = request.getProfileImage();
            if (profileImage != null && !profileImage.isEmpty()) {
                imageService.validateImage(profileImage);
                log.debug("📸 Procesando imagen - Tamaño: {}", profileImage.getSize());

                // Procesar y comprimir la imagen
                byte[] processedImageBytes = imageService.processImage(profileImage);
                log.debug("🔄 Imagen procesada y comprimida: {} bytes", processedImageBytes.length);

                // Asignar la imagen procesada y su tipo de contenido
                user.setProfileImage(processedImageBytes);
                user.setImageContentType("image/jpeg");

                log.debug("💾 Imagen y tipo de contenido establecidos - Tamaño: {}",
                        user.getProfileImage().length);
            }

            // Guardar el usuario
            log.debug("💾 Guardando usuario en la base de datos");

            // Forzar un flush para asegurar que se guarde en la base de datos
            UserEntity savedUser = userRepository.saveAndFlush(user);

            log.debug("✅ Usuario guardado exitosamente. ¿Tiene imagen? {} Tamaño: {}",
                    savedUser.getProfileImage() != null,
                    savedUser.getProfileImage() != null ? savedUser.getProfileImage().length : 0);

            // Verificar explícitamente que los datos se guardaron
            UserEntity verifiedUser = userRepository.findByUsername(savedUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("Error al verificar el guardado"));

            log.debug("🔍 Verificación después de guardar - ¿Tiene imagen? {} Tamaño: {}",
                    verifiedUser.getProfileImage() != null,
                    verifiedUser.getProfileImage() != null ? verifiedUser.getProfileImage().length : 0);

            // Crear respuesta con el usuario verificado
            AuthDTO response = createAuthResponse(verifiedUser);
            log.debug("📤 Respuesta creada, tiene imagen: {}",
                    response.getProfileImageUrl() != null);

            return response;

        } catch (Exception e) {
            log.error("❌ Error en modifyUser: ", e);
            throw new RuntimeException("Error al modificar usuario: " + e.getMessage());
        }
    }


    private AuthDTO createAuthResponse(UserEntity user) {
        // Procesar la imagen si existe
        String imageBase64 = null;
        if (user.getProfileImage() != null && user.getImageContentType() != null) {
            try {
                imageBase64 = imageService.convertImageToBase64(
                        user.getProfileImage(),
                        user.getImageContentType()
                );
                log.debug("🖼️ Imagen convertida a base64: {} caracteres",
                        imageBase64 != null ? imageBase64.length() : 0);
            } catch (Exception e) {
                log.error("Error al convertir imagen a base64", e);
            }
        }

        return AuthDTO.builder()
                .token(jwtService.getToken(user))
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImageFromBase64(user.getProfileImage(), user.getImageContentType())
                .build();
    }
    /**
     * Valida la imagen subida
     */

    public AuthDTO getUserInfo(String username) {
        try {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));


            // Crear respuesta con la información del usuario
            AuthDTO response = AuthDTO.builder()
                    .token(jwtService.getToken(user))
                    .username(user.getUsername())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .roles(user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet()))
                    .build();


            // Procesar imagen de perfil si existe
            if (user.getProfileImage() != null && user.getImageContentType() != null) {
                try {
                    String imageBase64 = imageService.convertImageToBase64(
                            user.getProfileImage(),
                            user.getImageContentType()
                    );
                    response.setProfileImageUrl(imageBase64);
                } catch (Exception e) {
                    // No establecemos la imagen pero permitimos que el resto de la información se envíe
                }
            } else {


            }


            return response;


        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener información del usuario", e);
        }
    }




}


