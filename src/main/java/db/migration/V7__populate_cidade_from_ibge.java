package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class V7__populate_cidade_from_ibge extends BaseJavaMigration {

    private static final String URL_ESTADOS = "https://servicodados.ibge.gov.br/api/v1/localidades/estados?orderBy=nome";
    private static final String URL_MUN_UF  = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/%s/municipios?orderBy=nome";

    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper om  = new ObjectMapper();

    @Override
    public void migrate(Context context) throws Exception {
        Connection conn = context.getConnection();

        // 1) busca UFs
        JsonNode estados = getJson(URL_ESTADOS);
        System.out.println("[V7] UFs: " + estados.size());

        // 2) INSERT preparado (gera UUID no Java, ignora duplicata pelo índice)
        String sql = """
            INSERT INTO city (id, name, state)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (JsonNode e : estados) {
                String uf = e.get("sigla").asText().toUpperCase();
                System.out.println("[V7] Buscando municípios de " + uf + "...");

                JsonNode municipios = getJson(String.format(URL_MUN_UF, uf));
                int pend = 0;

                for (JsonNode m : municipios) {
                    String cityName = m.get("nome").asText();

                    ps.setObject(1, UUID.randomUUID());
                    ps.setString(2, cityName);
                    ps.setString(3, uf);  // salva a SIGLA da UF (ex.: RS, SP)
                    ps.addBatch();

                    if (++pend % 1000 == 0) {
                        ps.executeBatch();
                        pend = 0;
                    }
                }
                if (pend > 0) ps.executeBatch();

                System.out.println("[V7] " + uf + " ok (" + municipios.size() + " cidades).");
            }
        }

        // log final
        try (var st = conn.createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM city")) {
            if (rs.next()) System.out.println("[V7] Total na tabela city: " + rs.getLong(1));
        }
    }

    private JsonNode getJson(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IllegalStateException("Falha ao consultar " + url + " (HTTP " + resp.statusCode() + ")");
        }
        return om.readTree(resp.body());
    }
}
