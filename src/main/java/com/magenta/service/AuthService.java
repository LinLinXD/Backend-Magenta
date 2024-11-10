package com.magenta.service;


import com.magenta.dto.AuthDTO;
import com.magenta.dto.LoginDTO;
import com.magenta.dto.ModifyUserDTO;
import com.magenta.dto.RegisterDTO;
import com.magenta.persistence.entitiy.Role;
import com.magenta.persistence.entitiy.RoleEntity;
import com.magenta.persistence.entitiy.UserEntity;
import com.magenta.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;


import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthDTO login(LoginDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthDTO.builder()
                .token(token)
                .roles(roles)
                .username(user.getUsername())
                .build();
    }



    public AuthDTO register(RegisterDTO request) {
        RoleEntity userRole = RoleEntity.builder()
                .name(Role.USER.name())
                .build();

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode( request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        return AuthDTO.builder()
            .token(jwtService.getToken(user))
            .build();
    }


        public AuthDTO modifyUser(ModifyUserDTO request) {
            // Buscar el usuario por username
            UserEntity user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Actualizar solo los campos que no son null
            if (request.getName() != null && !request.getName().isEmpty()) {
                user.setName(request.getName());
            }
            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                // Opcional: Verificar si el email ya está en uso
                if (userRepository.existsByEmailAndUsernameNot(request.getEmail(), request.getUsername())) {
                    throw new RuntimeException("Email ya está en uso");
                }
                user.setEmail(request.getEmail());
            }
            if (request.getPhone() != null && !request.getPhone().isEmpty()) {
                user.setPhone(request.getPhone());
            }

            // Guardar los cambios
            user = userRepository.save(user);

            // Construir la respuesta
            Set<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            return AuthDTO.builder()
                    .token(jwtService.getToken(user))
                    .username(user.getUsername())
                    .roles(roles)
                    .build();
        }

}
