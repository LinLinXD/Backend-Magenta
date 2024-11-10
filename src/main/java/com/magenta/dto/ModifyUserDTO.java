package com.magenta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyUserDTO {
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @Email(message = "Debe proporcionar un email válido")
    private String email;

    @Size(min = 9, max = 15, message = "El teléfono debe tener entre 9 y 15 dígitos")
    private String phone;

    // Este campo solo se usará para hacer la búsqueda del usuario
    private String username;
}