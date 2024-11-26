package com.magenta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object (DTO) para la autenticación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    private String token;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    private String username;
    private String name;
    private String email;
    private String phone;
    private String error;

    private String profileImageUrl;

    /**
     * Constructor personalizado para la construcción más fácil.
     */
    public static class AuthDTOBuilder {
        /**
         * Establece la URL de la imagen de perfil a partir de datos en base64.
         *
         * @param imageData los datos de la imagen en formato byte[]
         * @param contentType el tipo de contenido de la imagen
         * @return el constructor AuthDTOBuilder
         */
        public AuthDTOBuilder profileImageFromBase64(byte[] imageData, String contentType) {
            if (imageData != null && contentType != null) {
                String base64Image = java.util.Base64.getEncoder().encodeToString(imageData);
                this.profileImageUrl = "data:" + contentType + ";base64," + base64Image;
            }
            return this;
        }
    }
}