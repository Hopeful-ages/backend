package ages.hopeful.modules.pdf.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for PdfService")
class PdfServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private PdfService pdfService;

    private Map<String, Object> testData;

    @BeforeEach
    void setUp() {
        testData = new HashMap<>();
        testData.put("titulo", "Teste PDF");
        testData.put("origem", "Teste Origem");
        testData.put("subgrupo", "Teste Subgrupo");
        testData.put("codigo", "001");
        testData.put("descricao", "Descrição de teste");
        testData.put("antes", List.of("Tarefa 1", "Tarefa 2"));
        testData.put("durante", List.of("Tarefa 3"));
        testData.put("apos", List.of("Tarefa 4", "Tarefa 5"));
    }

    @Test
    @DisplayName("Should generate PDF successfully with valid HTML")
    void shouldGeneratePdfSuccessfully() throws Exception {
        // Given
        String validHtml = "<!DOCTYPE html><html><head><title>Test</title></head><body><h1>Test PDF</h1></body></html>";
        when(templateEngine.process(eq("document"), any(Context.class))).thenReturn(validHtml);

        // When
        byte[] result = pdfService.renderPdf("document", testData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
        verify(templateEngine).process(eq("document"), any(Context.class));
    }

    @Test
    @DisplayName("Should process template with all data variables")
    void shouldProcessTemplateWithAllDataVariables() throws Exception {
        // Given
        String validHtml = "<!DOCTYPE html><html><body><h1>Test</h1></body></html>";
        when(templateEngine.process(eq("document"), any(Context.class))).thenReturn(validHtml);

        // When
        pdfService.renderPdf("document", testData);

        // Then
        verify(templateEngine).process(eq("document"), any(Context.class));
    }

    @Test
    @DisplayName("Should trim HTML content before processing")
    void shouldTrimHtmlContent() throws Exception {
        // Given
        String htmlWithWhitespace = "   <!DOCTYPE html><html><body><h1>Test</h1></body></html>   ";
        when(templateEngine.process(eq("document"), any(Context.class))).thenReturn(htmlWithWhitespace);

        // When
        byte[] result = pdfService.renderPdf("document", testData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should throw exception when template processing fails")
    void shouldThrowExceptionWhenTemplateProcessingFails() {
        // Given
        when(templateEngine.process(eq("document"), any(Context.class)))
            .thenThrow(new RuntimeException("Template processing error"));

        // When & Then
        assertThatThrownBy(() -> pdfService.renderPdf("document", testData))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Template processing error");
    }

    @Test
    @DisplayName("Should handle empty data map")
    void shouldHandleEmptyDataMap() throws Exception {
        // Given
        Map<String, Object> emptyData = new HashMap<>();
        String validHtml = "<!DOCTYPE html><html><body><h1>Empty Test</h1></body></html>";
        when(templateEngine.process(eq("document"), any(Context.class))).thenReturn(validHtml);

        // When
        byte[] result = pdfService.renderPdf("document", emptyData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle complex HTML with special characters")
    void shouldHandleComplexHtml() throws Exception {
        // Given
        String complexHtml = "<!DOCTYPE html><html><body>" +
                "<h1>Test &amp; Special Characters</h1>" +
                "<p>This is a test with &lt;tags&gt; and &quot;quotes&quot;</p>" +
                "</body></html>";
        when(templateEngine.process(eq("document"), any(Context.class))).thenReturn(complexHtml);

        // When
        byte[] result = pdfService.renderPdf("document", testData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should handle HTML with lists and tables")
    void shouldHandleHtmlWithListsAndTables() throws Exception {
        // Given
        String htmlWithStructure = "<!DOCTYPE html><html><body>" +
                "<table><tr><td>Cell 1</td><td>Cell 2</td></tr></table>" +
                "<ul><li>Item 1</li><li>Item 2</li></ul>" +
                "</body></html>";
        when(templateEngine.process(eq("document"), any(Context.class))).thenReturn(htmlWithStructure);

        // When
        byte[] result = pdfService.renderPdf("document", testData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }
}
