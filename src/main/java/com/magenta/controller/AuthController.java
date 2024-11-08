package com.magenta.controller;

import com.magenta.dto.AuthDTO;
import com.magenta.dto.LoginDTO;
import com.magenta.dto.RegisterDTO;
import com.magenta.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Obtenemos el username de forma segura
        String username = "";
        if (auth.getPrincipal() instanceof UserDetails) {
            username = ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            username = auth.getName();
        }

        return ResponseEntity.ok().build();
    }


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
}
