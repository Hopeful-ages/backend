package ages.hopeful.modules.pdf.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);
    private final SpringTemplateEngine templateEngine;

    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] renderPdf(String template, Map<String, Object> data) throws Exception {
        Context ctx = new Context();
        ctx.setVariables(data);

        String html = templateEngine.process(template, ctx);
        
        // Remove leading/trailing whitespace and ensure proper XML formatting
        html = html.trim();
        
        // Log first 500 characters for debugging
        logger.debug("Generated HTML (first 500 chars): {}", html.substring(0, Math.min(500, html.length())));

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
