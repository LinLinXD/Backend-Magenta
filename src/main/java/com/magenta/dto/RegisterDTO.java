package com.magenta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para el registro de usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username; // Nombre de usuario

    @NotBlank(message = "La contraseña es obligatoria")
    private String password; // Contraseña

    @NotBlank(message = "El nombre es obligatorio")
    private String name; // Nombre

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String phone; // Teléfono

    @Email(message = "Debe proporcionar un email válido")
    @NotBlank(message = "El email es obligatorio")
    private String email; // Correo electrónico
}