package ages.hopeful.modules.pdf.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ages.hopeful.modules.pdf.dto.PdfRequest;
import ages.hopeful.modules.pdf.service.ImageService;
import ages.hopeful.modules.pdf.service.PdfService;
import ages.hopeful.modules.scenarios.dto.ParameterResponseDTO;
import ages.hopeful.modules.scenarios.dto.ScenarioResponseDTO;
import ages.hopeful.modules.scenarios.dto.TaskResponseDTO;

@RestController
public class PdfController {

    private final PdfService pdfService;
    private final ImageService imageService;

    public PdfController(PdfService pdfService, ImageService imageService) {
        this.pdfService = pdfService;
        this.imageService = imageService;
    }

    /**
     * Escapa caracteres especiais HTML para evitar problemas de parsing XML
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> gerarPdf(@RequestBody PdfRequest request) throws Exception {
        String watermarkImage = imageService.getWatermarkImageBase64();

        Map<String, Object> vars = new HashMap<>();
        vars.put("titulo", request.getTitulo());
        vars.put("origem", request.getOrigem());
        vars.put("subgrupo", request.getSubgrupo());
        vars.put("codigo", request.getCodigo());
        vars.put("descricao", request.getDescricao());
        vars.put("watermarkImage", watermarkImage);
        vars.put("antes", request.getAntes());
        vars.put("durante", request.getDurante());
        vars.put("apos", request.getApos());
        
        // Parameter fields - handle null values
        vars.put("parametrosAntes", request.getParametrosAntes() != null ? request.getParametrosAntes() : "");
        vars.put("acaoAntes", request.getAcaoAntes() != null ? request.getAcaoAntes() : "");
        vars.put("parametrosDurante", request.getParametrosDurante() != null ? request.getParametrosDurante() : "");
        vars.put("acaoDurante", request.getAcaoDurante() != null ? request.getAcaoDurante() : "");
        vars.put("parametrosApos", request.getParametrosApos() != null ? request.getParametrosApos() : "");
        vars.put("acaoApos", request.getAcaoApos() != null ? request.getAcaoApos() : "");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(vars);
        System.out.println(json);

        byte[] pdf = pdfService.renderPdf("document", vars);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=document.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @PostMapping("/pdf/scenarios")
    public ResponseEntity<byte[]> gerarPdfFromScenarios(@RequestBody List<ScenarioResponseDTO> scenarios) throws Exception {
        if (scenarios == null || scenarios.isEmpty()) {
            throw new IllegalArgumentException("A lista de cenários não pode estar vazia");
        }

        // Formatar data para ano apenas
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");

        // Titulo: pegar o subgroup do primeiro cenário (todos serão iguais)
        String titulo = scenarios.get(0).getCobrade().getSubgroup();

        // Origem: pegar origin de dentro da cobrade
        String origem = scenarios.get(0).getCobrade().getOrigin() != null ? 
                        scenarios.get(0).getCobrade().getOrigin() : "";

        // Subgrupo: pegar group de dentro da cobrade
        String subgrupo = scenarios.get(0).getCobrade().getGroup() != null ? 
                          scenarios.get(0).getCobrade().getGroup() : "";

        // Codigo: "Múltiplos" se mais de um cenário, "Singular" se apenas um
        String codigo = scenarios.size() > 1 ? "Múltiplos" : "Singular";

        // Descrição: concatenar subType + code : description de cada cenário
        StringBuilder descricaoBuilder = new StringBuilder();
        for (int i = 0; i < scenarios.size(); i++) {
            ScenarioResponseDTO scenario = scenarios.get(i);
            String subType = escapeHtml(scenario.getCobrade().getSubType());
            String code = escapeHtml(scenario.getCobrade().getCode());
            String description = escapeHtml(scenario.getCobrade().getDescription());
            
            if (i > 0) {
                descricaoBuilder.append("<br/><br/>");
            }
            
            if (scenarios.size() > 1) {
                // Se houver múltiplos cenários, enumerar
                descricaoBuilder.append("<b>").append(i + 1).append(". ")
                               .append(subType).append(" (").append(code).append("):</b> ")
                               .append(description);
            } else {
                // Se houver apenas um cenário, não enumerar
                descricaoBuilder.append("<b>").append(subType).append(" (").append(code)
                               .append("):</b> ").append(description);
            }
        }
        String descricao = descricaoBuilder.toString();

        // Listas de tarefas por fase
        List<String> tarefasAntes = new ArrayList<>();
        List<String> tarefasDurante = new ArrayList<>();
        List<String> tarefasApos = new ArrayList<>();

        // Processar todas as tarefas de todos os cenários
        for (ScenarioResponseDTO scenario : scenarios) {
            for (TaskResponseDTO task : scenario.getTasks()) {
                String serviceName = task.getService() != null ? escapeHtml(task.getService().getName()) : "Sem serviço";
                String updateDate = task.getLastUpdateDate() != null ? dateFormat.format(task.getLastUpdateDate()) : "";
                
                // Formato: description (serviceName, updateDate)
                String taskInfo = escapeHtml(task.getDescription()) + " (" + serviceName + ", " + updateDate + ")";
                
                switch (task.getPhase()) {
                    case "ANTES":
                        tarefasAntes.add(taskInfo);
                        break;
                    case "DURANTE":
                        tarefasDurante.add(taskInfo);
                        break;
                    case "DEPOIS":
                        tarefasApos.add(taskInfo);
                        break;
                }
            }
        }

        // Processar parâmetros
        StringBuilder parametrosAntes = new StringBuilder();
        StringBuilder acaoAntes = new StringBuilder();
        StringBuilder parametrosDurante = new StringBuilder();
        StringBuilder acaoDurante = new StringBuilder();
        StringBuilder parametrosApos = new StringBuilder();
        StringBuilder acaoApos = new StringBuilder();

        for (ScenarioResponseDTO scenario : scenarios) {
            for (ParameterResponseDTO param : scenario.getParameters()) {
                switch (param.getPhase()) {
                    case "ANTES":
                        if (parametrosAntes.length() > 0) parametrosAntes.append("\n");
                        parametrosAntes.append(escapeHtml(param.getDescription()));
                        if (acaoAntes.length() > 0) acaoAntes.append("\n");
                        acaoAntes.append(escapeHtml(param.getAction()));
                        break;
                    case "DURANTE":
                        if (parametrosDurante.length() > 0) parametrosDurante.append("\n");
                        parametrosDurante.append(escapeHtml(param.getDescription()));
                        if (acaoDurante.length() > 0) acaoDurante.append("\n");
                        acaoDurante.append(escapeHtml(param.getAction()));
                        break;
                    case "DEPOIS":
                        if (parametrosApos.length() > 0) parametrosApos.append("\n");
                        parametrosApos.append(escapeHtml(param.getDescription()));
                        if (acaoApos.length() > 0) acaoApos.append("\n");
                        acaoApos.append(escapeHtml(param.getAction()));
                        break;
                }
            }
        }

        // Criar o PdfRequest
        PdfRequest pdfRequest = new PdfRequest();
        pdfRequest.setTitulo(escapeHtml(titulo));
        pdfRequest.setOrigem(escapeHtml(origem));
        pdfRequest.setSubgrupo(escapeHtml(subgrupo));
        pdfRequest.setCodigo(escapeHtml(codigo));
        pdfRequest.setDescricao(descricao); // Já foi escapado no stream acima
        pdfRequest.setAntes(tarefasAntes);
        pdfRequest.setDurante(tarefasDurante);
        pdfRequest.setApos(tarefasApos);
        pdfRequest.setParametrosAntes(parametrosAntes.toString());
        pdfRequest.setAcaoAntes(acaoAntes.toString());
        pdfRequest.setParametrosDurante(parametrosDurante.toString());
        pdfRequest.setAcaoDurante(acaoDurante.toString());
        pdfRequest.setParametrosApos(parametrosApos.toString());
        pdfRequest.setAcaoApos(acaoApos.toString());

        // Usar o método existente para gerar o PDF
        return gerarPdf(pdfRequest);
    }
}
