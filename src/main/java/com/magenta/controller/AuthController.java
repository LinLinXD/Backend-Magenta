package com.magenta.controller;

import com.magenta.dto.AuthDTO;
import com.magenta.dto.LoginDTO;
import com.magenta.dto.ModifyUserDTO;
import com.magenta.dto.RegisterDTO;
import com.magenta.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthDTO> login(@RequestBody LoginDTO request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthDTO> register(@RequestBody RegisterDTO request)
    {
        return ResponseEntity.ok(authService.register(request));
    }

    @PutMapping(value = "/modifyUser")
    public ResponseEntity<AuthDTO> modifyUser(@Valid @RequestBody ModifyUserDTO request) {
        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Asignar el username al DTO
            request.setUsername(username);

            // Intentar modificar el usuario
            AuthDTO modifiedUser = authService.modifyUser(request);

            return ResponseEntity.ok(modifiedUser);
        } catch (Exception e) {
            // Log del error
            return ResponseEntity.badRequest().build();
        }
    }
}
