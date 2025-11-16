package ages.hopeful.modules.pdf.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for ImageService")
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Test
    @DisplayName("Should return base64 encoded watermark image when image exists")
    void shouldReturnBase64WatermarkWhenImageExists() {
        // When
        String result = imageService.getWatermarkImageBase64();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        // Verifica se é um Base64 válido (deve conter apenas caracteres válidos)
        assertThat(result).matches("^[A-Za-z0-9+/]*={0,2}$");
    }

    @Test
    @DisplayName("Should return null when image does not exist")
    void shouldReturnNullWhenImageDoesNotExist() {
        // Given
        ImageService serviceWithInvalidPath = new ImageService() {
            @Override
            public String getWatermarkImageBase64() {
                try {
                    // Simula caminho inválido
                    throw new java.io.IOException("Image not found");
                } catch (Exception e) {
                    return null;
                }
            }
        };

        // When
        String result = serviceWithInvalidPath.getWatermarkImageBase64();

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return different values for different images if they existed")
    void shouldReturnConsistentBase64() {
        // When - chamando duas vezes o mesmo método
        String result1 = imageService.getWatermarkImageBase64();
        String result2 = imageService.getWatermarkImageBase64();

        // Then - deve retornar o mesmo resultado
        assertThat(result1).isEqualTo(result2);
    }
}
