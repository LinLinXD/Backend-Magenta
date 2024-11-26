package com.magenta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Data Transfer Object (DTO) para modificar la información del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyUserDTO {
    private String name; // Nombre del usuario
    private String email; // Correo electrónico del usuario
    private String phone; // Teléfono del usuario
    private String username; // Útil si el usuario necesita actualizar su nombre de usuario
    private MultipartFile profileImage; // Imagen del usuario
}