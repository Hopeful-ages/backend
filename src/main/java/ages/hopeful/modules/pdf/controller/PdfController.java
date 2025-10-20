package ages.hopeful.modules.pdf.controller;

import java.util.HashMap;
import java.util.Map;

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
@RestController
public class PdfController {

    private final PdfService pdfService;
    private final ImageService imageService;

    public PdfController(PdfService pdfService, ImageService imageService) {
        this.pdfService = pdfService;
        this.imageService = imageService;
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

        byte[] pdf = pdfService.renderPdf("document-new", vars);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=document.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
