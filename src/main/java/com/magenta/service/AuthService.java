package com.magenta.service;

import com.magenta.dto.*;
import com.magenta.persistence.entity.*;
import com.magenta.persistence.repository.RoleRepository;
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

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de autenticación y gestión de usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ImageService imageService;
    private final RoleRepository roleRepository;

    /**
     * Autentica a un usuario y genera un token JWT.
     *
     * @param request los datos de inicio de sesión
     * @return los datos de autenticación
     */
    public AuthDTO login(LoginDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return createAuthResponse(user);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request los datos de registro
     * @return los datos de autenticación
     */
    public AuthDTO register(RegisterDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        try {
            RoleEntity userRole = roleRepository.findByName("USER")
                    .orElseGet( () -> {
                        RoleEntity newRole = RoleEntity.builder()
                                .name(Role.USER.name())
                                .build();
                        return roleRepository.save(newRole);
                    });

            UserEntity user = UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .roles(Set.of(userRole))
                    .build();


            user = userRepository.save(user);



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
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }

    /**
     * Modifica la información de un usuario.
     *
     * @param request los datos de modificación
     * @return los datos de autenticación
     */
    @Transactional
    public AuthDTO modifyUser(ModifyUserDTO request) {

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            if (request.getName() != null) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }

            MultipartFile profileImage = request.getProfileImage();
            if (profileImage != null && !profileImage.isEmpty()) {

                imageService.validateImage(profileImage);

                byte[] processedImageBytes = imageService.processImage(profileImage);


                user.setProfileImage(processedImageBytes);
                user.setImageContentType("image/jpeg");

            }


            UserEntity savedUser = userRepository.saveAndFlush(user);



            UserEntity verifiedUser = userRepository.findByUsername(savedUser.getUsername())
                    .orElseThrow(() -> new RuntimeException("Error al verificar el guardado"));


            AuthDTO response = createAuthResponse(verifiedUser);


            return response;

        } catch (Exception e) {
             throw new RuntimeException("Error al modificar usuario: " + e.getMessage());
        }
    }

    /**
     * Crea la respuesta de autenticación.
     *
     * @param user la entidad de usuario
     * @return los datos de autenticación
     */
    private AuthDTO createAuthResponse(UserEntity user) {
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
     * Obtiene la información de un usuario.
     *
     * @param username el nombre de usuario
     * @return los datos de autenticación
     */
    public AuthDTO getUserInfo(String username) {
        try {
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

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

            if (user.getProfileImage() != null && user.getImageContentType() != null) {
                String imageBase64 = imageService.convertImageToBase64(
                        user.getProfileImage(),
                        user.getImageContentType()
                );
                response.setProfileImageUrl(imageBase64);
            }

            return response;

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener información del usuario", e);
        }
    }
}