package ages.hopeful.modules.pdf.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@AllArgsConstructor
@NoArgsConstructor
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
}
