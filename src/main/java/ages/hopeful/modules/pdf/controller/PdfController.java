package ages.hopeful.modules.pdf.controller;

import ages.hopeful.modules.pdf.service.PdfService;
import java.lang.Object;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pdf/generate")
public class PdfController {

    private PdfService pdfService;

    @Autowired
    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping
    public ResponseEntity<byte[]> returnPdf(@RequestParam String template) throws Exception {
        
        String watermarkImageBase64 = pdfService.getWatermarkImageBase64();
        String templatePath = "pdf/" + template + ".html";

        Map<String, Object> variables = new HashMap<>();

        byte[] data = pdfService.renderPdf(template, variables);

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=terremoto.pdf"
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(data);
    }
}
