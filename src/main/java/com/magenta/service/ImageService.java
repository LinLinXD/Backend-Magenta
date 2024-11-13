package com.magenta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

@Service
@Slf4j
public class ImageService {
    @Value("${app.image.max-size:5242880}") // 5MB por defecto
    private long maxFileSize;

    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;
    private static final float COMPRESSION_QUALITY = 0.7f;
    private static final int MAX_BYTES = 65536; // 64KB máximo

    public byte[] processImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("Archivo de imagen vacío o nulo");
            return null;
        }

        try {
            // Obtener los bytes originales
            byte[] originalBytes = file.getBytes();
            log.debug("📥 Bytes originales leídos: {}", originalBytes.length);

            // Leer la imagen original
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (originalImage == null) {
                throw new IOException("No se pudo leer la imagen");
            }

            // Calcular nuevas dimensiones
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            double scale = Math.min(
                    (double) MAX_WIDTH / originalWidth,
                    (double) MAX_HEIGHT / originalHeight
            );

            int newWidth = (int) (originalWidth * scale);
            int newHeight = (int) (originalHeight * scale);

            // Crear imagen redimensionada
            BufferedImage resizedImage = new BufferedImage(
                    newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = resizedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // Compresión iterativa
            float quality = COMPRESSION_QUALITY;
            byte[] imageData;

            do {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");

                if (!writers.hasNext()) {
                    throw new IOException("No hay escritores JPEG disponibles");
                }

                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);

                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                writer.setOutput(ios);
                writer.write(null, new IIOImage(resizedImage, null, null), param);

                imageData = baos.toByteArray();

                writer.dispose();
                ios.close();
                baos.close();

                quality *= 0.8f; // Reducir la calidad en un 20% en cada iteración

                log.debug("Intento de compresión: calidad={}, tamaño={} bytes",
                        quality, imageData.length);

            } while (imageData.length > MAX_BYTES && quality > 0.1f);

            if (imageData.length > MAX_BYTES) {
                throw new IOException("No se pudo comprimir la imagen lo suficiente");
            }

            log.debug("Imagen procesada exitosamente: {} bytes con calidad {}",
                    imageData.length, quality);

            return imageData;

        } catch (IOException e) {
            log.error("Error al procesar imagen: ", e);
            throw e;
        }
    }

    public String getImageContentType(MultipartFile file) {
        if (file == null) {
            return null;
        }
        return "image/jpeg"; // Siempre JPEG después del procesamiento
    }

    public String convertImageToBase64(byte[] imageData, String contentType) {
        if (imageData == null || contentType == null) {
            log.debug("No hay datos de imagen para convertir a base64");
            return null;
        }

        try {
            String base64Image = Base64.getEncoder().encodeToString(imageData);
            log.debug("Imagen convertida a base64: {} caracteres", base64Image.length());
            return "data:" + contentType + ";base64," + base64Image;
        } catch (Exception e) {
            log.error("Error al convertir imagen a base64: ", e);
            return null;
        }
    }

    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo de imagen inválido");
        }

        // Validar tamaño original
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("La imagen no debe superar los %d MB", maxFileSize / (1024 * 1024))
            );
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // Validar que sea un formato de imagen soportado
        String[] supportedFormats = {"image/jpeg", "image/png", "image/gif"};
        boolean isSupported = false;
        for (String format : supportedFormats) {
            if (contentType.equals(format)) {
                isSupported = true;
                break;
            }
        }
        if (!isSupported) {
            throw new IllegalArgumentException("Formato de imagen no soportado. Use JPEG, PNG o GIF");
        }

        try {
            // Intentar leer la imagen para validar que es una imagen válida
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new IllegalArgumentException("El archivo no es una imagen válida");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer la imagen: " + e.getMessage());
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        int targetWidth = MAX_WIDTH;
        int targetHeight = MAX_HEIGHT;

        // Mantener la relación de aspecto
        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        if (aspectRatio > 1) {
            targetHeight = (int) (targetWidth / aspectRatio);
        } else {
            targetWidth = (int) (targetHeight * aspectRatio);
        }

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}