package ages.hopeful.modules.pdf.dto;

import java.util.List;
import java.util.Map;

public class PdfRequest {

    private String titulo;
    private String origem;
    private String subgrupo;
    private String codigo;
    private String descricao;
    private List<String> antes;
    private List<String> durante;
    private List<String> apos;

    public PdfRequest() {
    }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTitulo() { return titulo; }

    public void setOrigem(String origem) { this.origem = origem; }
    public String getOrigem() { return origem; }

    public void setSubgrupo(String subgrupo) { this.subgrupo = subgrupo; }
    public String getSubgrupo() { return subgrupo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCodigo() { return codigo; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }

    public void setAntes(List<String> antes) { this.antes = antes; }
    public List<String> getAntes() { return antes; }

    public void setDurante(List<String> durante) { this.durante = durante; }
    public List<String> getDurante() { return durante; }

    public void setApos(List<String> apos) { this.apos = apos; }
    public List<String> getApos() { return apos; }

}
