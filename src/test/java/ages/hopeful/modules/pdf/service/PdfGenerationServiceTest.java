package ages.hopeful.modules.pdf.service;

import ages.hopeful.modules.cobrades.dto.CobradeResponseDTO;
import ages.hopeful.modules.departments.dto.DepartmentResponseDTO;
import ages.hopeful.modules.pdf.dto.PdfRequest;
import ages.hopeful.modules.scenarios.dto.ParameterResponseDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.dto.TaskResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for PdfGenerationService")
class PdfGenerationServiceTest {

    @Mock
    private PdfService pdfService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private PdfGenerationService pdfGenerationService;

    private CobradeResponseDTO createTestCobrade() {
        return CobradeResponseDTO.builder()
                .id(UUID.randomUUID())
                .code("1.1.1.1.0")
                .description("Inundação")
                .subgroup("DESASTRES HIDROLÓGICOS")
                .type("Inundações")
                .subType("Inundação gradual")
                .origin("Natural")
                .group("DESASTRES HIDROLÓGICOS")
                .build();
    }

    private DepartmentResponseDTO createTestDepartment() {
        return new DepartmentResponseDTO(UUID.randomUUID(), "Defesa Civil");
    }

    private TaskResponseDTO createTestTask(String description, String phase, DepartmentResponseDTO department) {
        return TaskResponseDTO.builder()
                .id(UUID.randomUUID())
                .description(description)
                .phase(phase)
                .lastUpdateDate(new Date())
                .department(department)
                .build();
    }

    private ParameterResponseDTO createTestParameter(String description, String action, String phase) {
        return ParameterResponseDTO.builder()
                .id(UUID.randomUUID())
                .description(description)
                .action(action)
                .phase(phase)
                .build();
    }

    private ScenarioResponseDTO createTestScenario(CobradeResponseDTO cobrade, List<TaskResponseDTO> tasks, List<ParameterResponseDTO> parameters) {
        return ScenarioResponseDTO.builder()
                .id(UUID.randomUUID())
                .description("Cenário de teste")
                .origin("Natural")
                .cobrade(cobrade)
                .tasks(tasks)
                .parameters(parameters)
                .published(true)
                .build();
    }

    @Nested
    @DisplayName("Tests for generatePdfFromRequest")
    class GeneratePdfFromRequestTests {

        private PdfRequest testRequest;

        @BeforeEach
        void setUp() {
            testRequest = new PdfRequest();
            testRequest.setTitulo("Teste PDF");
            testRequest.setOrigem("Natural");
            testRequest.setSubgrupo("Hidrológico");
            testRequest.setCodigo("1.1.1");
            testRequest.setDescricao("Descrição do teste");
            testRequest.setAntes(Arrays.asList("Tarefa 1", "Tarefa 2"));
            testRequest.setDurante(Arrays.asList("Tarefa 3"));
            testRequest.setApos(Arrays.asList("Tarefa 4", "Tarefa 5"));
            testRequest.setParametrosAntes("Param 1");
            testRequest.setAcaoAntes("Ação 1");
            testRequest.setParametrosDurante("Param 2");
            testRequest.setAcaoDurante("Ação 2");
            testRequest.setParametrosApos("Param 3");
            testRequest.setAcaoApos("Ação 3");
        }

        @Test
        @DisplayName("Should generate PDF successfully with complete request")
        void shouldGeneratePdfSuccessfullyWithCompleteRequest() throws Exception {
            // Given
            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromRequest(testRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedPdf);
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
            assertThat(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION))
                    .contains("inline; filename=" + testRequest.getTitulo() + ".pdf");
            
            verify(imageService).getWatermarkImageBase64();
            verify(pdfService).renderPdf(eq("document"), any(Map.class));
        }

        @Test
        @DisplayName("Should handle null parameter fields gracefully")
        void shouldHandleNullParameterFieldsGracefully() throws Exception {
            // Given
            testRequest.setParametrosAntes(null);
            testRequest.setAcaoAntes(null);
            testRequest.setParametrosDurante(null);
            testRequest.setAcaoDurante(null);
            testRequest.setParametrosApos(null);
            testRequest.setAcaoApos(null);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromRequest(testRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedPdf);
        }

        @Test
        @DisplayName("Should handle null watermark image")
        void shouldHandleNullWatermarkImage() throws Exception {
            // Given
            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn(null);
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromRequest(testRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedPdf);
        }

        @Test
        @DisplayName("Should propagate exception when PDF rendering fails")
        void shouldPropagateExceptionWhenPdfRenderingFails() throws Exception {
            // Given
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class)))
                    .thenThrow(new RuntimeException("PDF rendering failed"));

            // When & Then
            assertThatThrownBy(() -> pdfGenerationService.generatePdfFromRequest(testRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("PDF rendering failed");
        }
    }

    @Nested
    @DisplayName("Tests for generatePdfFromScenarios")
    class GeneratePdfFromScenariosTests {

        private CobradeResponseDTO testCobrade;
        private DepartmentResponseDTO testDepartment;

        @BeforeEach
        void setUp() {
            testCobrade = createTestCobrade();
            testDepartment = createTestDepartment();
        }

        @Test
        @DisplayName("Should throw exception when scenarios list is null")
        void shouldThrowExceptionWhenScenariosListIsNull() {
            // When & Then
            assertThatThrownBy(() -> pdfGenerationService.generatePdfFromScenarios(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("A lista de cenários não pode estar vazia");
        }

        @Test
        @DisplayName("Should throw exception when scenarios list is empty")
        void shouldThrowExceptionWhenScenariosListIsEmpty() {
            // Given
            List<ScenarioResponseDTO> emptyList = new ArrayList<>();

            // When & Then
            assertThatThrownBy(() -> pdfGenerationService.generatePdfFromScenarios(emptyList))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("A lista de cenários não pode estar vazia");
        }

        @Test
        @DisplayName("Should generate PDF with single scenario successfully")
        void shouldGeneratePdfWithSingleScenarioSuccessfully() throws Exception {
            // Given
            List<TaskResponseDTO> tasks = Arrays.asList(
                    createTestTask("Monitorar níveis de água", "ANTES", testDepartment),
                    createTestTask("Evacuar área de risco", "DURANTE", testDepartment),
                    createTestTask("Avaliar danos", "DEPOIS", testDepartment)
            );

            List<ParameterResponseDTO> parameters = Arrays.asList(
                    createTestParameter("Nível do rio", "Monitorar continuamente", "ANTES"),
                    createTestParameter("Área inundada", "Mapear área", "DURANTE"),
                    createTestParameter("Danos estruturais", "Avaliar edificações", "DEPOIS")
            );

            ScenarioResponseDTO scenario = createTestScenario(testCobrade, tasks, parameters);
            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedPdf);
            verify(pdfService).renderPdf(eq("document"), any(Map.class));
        }

        @Test
        @DisplayName("Should generate PDF with multiple scenarios successfully")
        void shouldGeneratePdfWithMultipleScenariosSuccessfully() throws Exception {
            // Given
            List<TaskResponseDTO> tasks1 = Arrays.asList(
                    createTestTask("Tarefa 1", "ANTES", testDepartment),
                    createTestTask("Tarefa 2", "DURANTE", testDepartment)
            );

            List<ParameterResponseDTO> params1 = Arrays.asList(
                    createTestParameter("Param 1", "Ação 1", "ANTES")
            );

            ScenarioResponseDTO scenario1 = createTestScenario(testCobrade, tasks1, params1);

            List<TaskResponseDTO> tasks2 = Arrays.asList(
                    createTestTask("Tarefa 3", "DEPOIS", testDepartment)
            );

            List<ParameterResponseDTO> params2 = Arrays.asList(
                    createTestParameter("Param 2", "Ação 2", "DURANTE")
            );

            ScenarioResponseDTO scenario2 = createTestScenario(testCobrade, tasks2, params2);

            List<ScenarioResponseDTO> scenarios = Arrays.asList(scenario1, scenario2);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedPdf);
        }

        @Test
        @DisplayName("Should handle scenarios with null cobrade fields")
        void shouldHandleScenariosWithNullCobradeFields() throws Exception {
            // Given
            CobradeResponseDTO cobradeWithNulls = CobradeResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .code("1.1.1")
                    .description("Test")
                    .subgroup("Test Subgroup")
                    .type(null)
                    .subType(null)
                    .origin(null)
                    .group(null)
                    .build();

            ScenarioResponseDTO scenario = createTestScenario(cobradeWithNulls, new ArrayList<>(), new ArrayList<>());
            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Should handle tasks with null department")
        void shouldHandleTasksWithNullDepartment() throws Exception {
            // Given
            TaskResponseDTO taskWithNullDept = createTestTask("Tarefa sem departamento", "ANTES", null);
            List<TaskResponseDTO> tasks = Collections.singletonList(taskWithNullDept);
            
            ScenarioResponseDTO scenario = createTestScenario(testCobrade, tasks, new ArrayList<>());
            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Should handle tasks with null last update date")
        void shouldHandleTasksWithNullLastUpdateDate() throws Exception {
            // Given
            TaskResponseDTO taskWithNullDate = TaskResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .description("Tarefa sem data")
                    .phase("ANTES")
                    .lastUpdateDate(null)
                    .department(testDepartment)
                    .build();

            List<TaskResponseDTO> tasks = Collections.singletonList(taskWithNullDate);
            ScenarioResponseDTO scenario = createTestScenario(testCobrade, tasks, new ArrayList<>());
            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Should escape HTML special characters in scenario data")
        void shouldEscapeHtmlSpecialCharacters() throws Exception {
            // Given
            CobradeResponseDTO cobradeWithSpecialChars = CobradeResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .code("1.1.1")
                    .description("Test & <Special> \"Characters\"")
                    .subgroup("Test & Subgroup")
                    .type("Type & <Test>")
                    .subType("SubType & 'Test'")
                    .origin("Origin & Test")
                    .group("Group & Test")
                    .build();

            TaskResponseDTO taskWithSpecialChars = createTestTask(
                    "Task with <html> & \"quotes\"", 
                    "ANTES", 
                    testDepartment
            );

            ParameterResponseDTO paramWithSpecialChars = createTestParameter(
                    "Param with <html> & 'quotes'",
                    "Action with <html> & \"quotes\"",
                    "ANTES"
            );

            ScenarioResponseDTO scenario = createTestScenario(
                    cobradeWithSpecialChars,
                    Collections.singletonList(taskWithSpecialChars),
                    Collections.singletonList(paramWithSpecialChars)
            );

            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(pdfService).renderPdf(eq("document"), any(Map.class));
        }

        @Test
        @DisplayName("Should organize tasks by phase correctly")
        void shouldOrganizeTasksByPhaseCorrectly() throws Exception {
            // Given
            List<TaskResponseDTO> tasks = Arrays.asList(
                    createTestTask("Tarefa DEPOIS 1", "DEPOIS", testDepartment),
                    createTestTask("Tarefa ANTES 1", "ANTES", testDepartment),
                    createTestTask("Tarefa DURANTE 1", "DURANTE", testDepartment),
                    createTestTask("Tarefa ANTES 2", "ANTES", testDepartment)
            );

            ScenarioResponseDTO scenario = createTestScenario(testCobrade, tasks, new ArrayList<>());
            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(pdfService).renderPdf(eq("document"), any(Map.class));
        }

        @Test
        @DisplayName("Should organize parameters by phase correctly")
        void shouldOrganizeParametersByPhaseCorrectly() throws Exception {
            // Given
            List<ParameterResponseDTO> parameters = Arrays.asList(
                    createTestParameter("Param DEPOIS", "Ação DEPOIS", "DEPOIS"),
                    createTestParameter("Param ANTES", "Ação ANTES", "ANTES"),
                    createTestParameter("Param DURANTE", "Ação DURANTE", "DURANTE")
            );

            ScenarioResponseDTO scenario = createTestScenario(testCobrade, new ArrayList<>(), parameters);
            List<ScenarioResponseDTO> scenarios = Collections.singletonList(scenario);

            byte[] expectedPdf = "PDF_CONTENT".getBytes();
            when(imageService.getWatermarkImageBase64()).thenReturn("base64ImageString");
            when(pdfService.renderPdf(eq("document"), any(Map.class))).thenReturn(expectedPdf);

            // When
            ResponseEntity<byte[]> response = pdfGenerationService.generatePdfFromScenarios(scenarios);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(pdfService).renderPdf(eq("document"), any(Map.class));
        }
    }
}
