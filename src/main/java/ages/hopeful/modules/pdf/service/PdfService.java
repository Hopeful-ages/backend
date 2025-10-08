package ages.hopeful.modules.pdf.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class PdfService {

    private SpringTemplateEngine templateEngine;

    public PdfService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] renderPdf(String template, Map<String, Object> variables)
        throws Exception {
        Context ctx = new Context();
        ctx.setVariables(variables);

        String html = templateEngine.process(template, ctx);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }

    public String getWatermarkImageBase64() {
        try {
            ClassPathResource resource = new ClassPathResource(
                "images/hopefull_logo.png"
            );
            byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            // Fallback para marca d'água de texto se a imagem não for encontrada
            return null;
        }
    }
}
