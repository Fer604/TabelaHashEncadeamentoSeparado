package lista;
import tabela.registro;
public class node {
    private tabela.registro registro;
    private node proximo;
    public node()
    {
        this.registro = null;
        this.proximo = null;
    }
    public registro getInformacao() {
        return registro;
    }
    public void setInformacao(registro registro) {
        this.registro = registro;
    }
    public node getProximo() {
        return proximo;
    }
    public void setProximo(node proximo) {
        this.proximo = proximo;
    }
    public void getUltimo(){

    }
}
