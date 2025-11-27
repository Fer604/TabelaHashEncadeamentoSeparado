package tabela;

public class registro {
    //objeto principal a ser armazenado nas tabelas
    private final String codigo;//só possui um codigo em string

    public registro(String codigo) {
        this.codigo = codigo;
    }
    public String getCodigo() {
        return codigo;
    }
    public int getCodigoNumerico() {
        return Integer.parseInt(codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
