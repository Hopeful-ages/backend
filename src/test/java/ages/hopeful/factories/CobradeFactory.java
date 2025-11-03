package ages.hopeful.factories;

import ages.hopeful.modules.cobrades.model.Cobrade;

import java.util.UUID;

/**
 * Factory para criar instâncias de Cobrade para testes.
 */
public class CobradeFactory {

    public static Cobrade createCobrade() {
        return createInundacao();
    }

    public static Cobrade createCobrade(String code, String description, String subgroup, String type, String subType) {
        Cobrade cobrade = new Cobrade();
        cobrade.setCode(code);
        cobrade.setDescription(description);
        cobrade.setSubgroup(subgroup);
        cobrade.setType(type);
        cobrade.setSubType(subType);
        cobrade.setGroup("Hidrológico");
        cobrade.setOrigin("Natural");
        return cobrade;
    }

    public static Cobrade createCobradeWithId(UUID id, String code, String description) {
        Cobrade cobrade = createCobrade(code, description, "Inundações", null, null);
        cobrade.setId(id);
        return cobrade;
    }

    public static Cobrade createInundacao() {
        return createCobrade(
            "1.2.1.0.0",
            "Submersão de áreas fora dos limites normais de um curso de água",
            "Inundações",
            null,
            null
        );
    }

    public static Cobrade createEnxurrada() {
        return createCobrade(
            "1.2.2.0.0",
            "Escoamento superficial de alta velocidade e energia",
            "Enxurradas",
            null,
            null
        );
    }

    public static Cobrade createAlagamento() {
        return createCobrade(
            "1.2.3.0.0",
            "Extrapolação da capacidade de escoamento de sistemas de drenagem urbana",
            "Alagamentos",
            null,
            null
        );
    }

    public static Cobrade createVendaval() {
        Cobrade cobrade = new Cobrade();
        cobrade.setCode("1.3.2.1.5");
        cobrade.setDescription("Forte deslocamento de uma massa de ar em uma região");
        cobrade.setSubgroup("Tempestades");
        cobrade.setType("Tempestade local/Convectiva");
        cobrade.setSubType("Vendaval");
        cobrade.setGroup("Meteorológico");
        cobrade.setOrigin("Natural");
        return cobrade;
    }

    public static Cobrade createIncendioFlorestal() {
        Cobrade cobrade = new Cobrade();
        cobrade.setCode("1.4.1.3.2");
        cobrade.setDescription("Propagação de fogo sem controle em vegetação");
        cobrade.setSubgroup("Incêndio florestal");
        cobrade.setType("Incêndios em áreas não protegidas");
        cobrade.setSubType(null);
        cobrade.setGroup("Climatológico");
        cobrade.setOrigin("Natural");
        return cobrade;
    }
}
