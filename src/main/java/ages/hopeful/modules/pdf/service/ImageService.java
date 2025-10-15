package ages.hopeful.modules.pdf.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class ImageService {

    public String getWatermarkImageBase64() {
        ClassPathResource resource = new ClassPathResource("src/main/resources/images/hopeful_pdf_logo.png");
        try (InputStream is = resource.getInputStream()) {
            if (is == null) {
                return null;
            }
            byte[] imageBytes = is.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            // Se a imagem não for encontrada ou ocorrer erro de leitura, retorna null
            return null;
        }
    }
}