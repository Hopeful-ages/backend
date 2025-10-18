package ages.hopeful.modules.pdf.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/document.pdf")
    public ResponseEntity<byte[]> gerarPdf() throws Exception {
        String watermarkImage = imageService.getWatermarkImageBase64();
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("titulo", "Terremoto");
        vars.put("origem", "Natural");
        vars.put("subgrupo", "Geológico");
        vars.put("codigo", "Múltiplos");
        vars.put("descricao", "1. Tremor de terra (1.1.1.1.0): Vibrações do terreno que provocam oscilações verticais e horizontais na superfície da Terra (ondas sísmicas). Pode ser natural (tectônica) ou induzido (explosões, injeção profunda de líquidos e gás, extração de fluidos, alívio de carga de minas, enchimento de lagos artificiais). 2. Tsunami (1.1.1.2.0): Série de ondas geradas por deslocamento de um grande volume de água causado geralmente por terremotos, erupções vulcânicas ou movimentos de massa.");
        vars.put("alertaNivel", "Alerta Verde");
        vars.put("parametros", "Tremor (< 4,1 na escala Richter)");
        vars.put("acao", "Acompanhamento contínuo com monitoramento");
        vars.put("watermarkImage", watermarkImage);
        vars.put("tarefas", List.of(
            "Os Departamentos de Defesa Civil devem publicar notas de orientação, instruindo, não obstante, orientações, sugestões ao equipamento e capacitação dos agentes de defesa civil.",
            "Estabelecer protocolos de comunicação entre os órgãos competentes e a população.",
            "Verificar a integridade de estruturas críticas como hospitais, escolas e pontes.",
            "Orientar a população sobre procedimentos básicos de segurança durante tremores."
        ));
        vars.put("secoes", List.of(
            Map.of("titulo", "DURANTE AS ONDAS SÍSMICAS", 
                   "itens", List.of(
                       "Manter a calma e procurar abrigo sob mesas ou estruturas resistentes.",
                       "Afastar-se de janelas, espelhos e objetos que possam cair.",
                       "Se estiver ao ar livre, manter distância de edifícios, postes e árvores."
                   )),
            Map.of("titulo", "APÓS AS ONDAS SÍSMICAS",
                   "itens", List.of(
                       "Verificar se há feridos e prestar primeiros socorros se necessário.",
                       "Inspecionar a residência em busca de danos estruturais.",
                       "Desligar gás, água e eletricidade se houver suspeita de vazamentos.",
                       "Sintonizar rádios para informações oficiais das autoridades."
                   ))
        ));

    byte[] pdf = pdfService.renderPdf("document", vars);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=terremoto.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
