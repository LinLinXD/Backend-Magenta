package com.magenta.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyUserDTO {
    private String name;
    private String email;
    private String phone;
    private String username;  // Útil si el usuario necesita actualizar su nombre de usuario
    private MultipartFile profileImage;  // Este campo es necesario para recibir la imagen del usuario
}