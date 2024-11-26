package com.magenta.controller;

import com.magenta.dto.AuthDTO;
import com.magenta.dto.LoginDTO;
import com.magenta.dto.ModifyUserDTO;
import com.magenta.dto.RegisterDTO;
import com.magenta.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador para la autenticación y gestión de usuarios.
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Inicia sesión con las credenciales proporcionadas.
     *
     * @param request los datos de inicio de sesión
     * @return la respuesta de autenticación
     */
    @PostMapping(value = "/login")
    public ResponseEntity<AuthDTO> login(@RequestBody LoginDTO request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthDTO.builder()
                    .error(e.getMessage())
                    .build());
        }
    }

    /**
     * Registra un nuevo usuario con los datos proporcionados.
     *
     * @param request los datos de registro
     * @return la respuesta de autenticación
     */
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthDTO> register(@RequestBody RegisterDTO request) {
        try {
            AuthDTO response = authService.register(request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(AuthDTO.builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    /**
     * Modifica los datos de un usuario existente.
     *
     * @param name el nuevo nombre del usuario
     * @param email el nuevo correo electrónico del usuario
     * @param phone el nuevo número de teléfono del usuario
     * @param username el nombre de usuario
     * @param profileImage la nueva imagen de perfil del usuario (opcional)
     * @return la respuesta de autenticación
     */
    @PostMapping(value = "/modifyUser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthDTO> modifyUser(
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("phone") String phone,
            @RequestPart("username") String username,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            ModifyUserDTO dto = ModifyUserDTO.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .username(username)
                    .profileImage(profileImage)
                    .build();

            AuthDTO response = authService.modifyUser(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(AuthDTO.builder()
                            .error("Error al actualizar el perfil: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Obtiene la información de un usuario específico.
     *
     * @param username el nombre de usuario
     * @return la información del usuario
     */
    @GetMapping("/user/info")
    public ResponseEntity<AuthDTO> getUserInfo(@RequestParam String username) {
        try {
            AuthDTO userInfo = authService.getUserInfo(username);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthDTO.builder()
                    .error(e.getMessage())
                    .build());
        }
    }
}