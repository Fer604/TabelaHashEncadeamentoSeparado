import lista.listaEncadeada;

public class tabela{
    private listaEncadeada[] tabela;
    private int colisoes;
    private int elementos;
    private int tamanhoTabela;
    private int[] len; //comprimento por "bucket"/slot/lista na tabela hash
    private int funcaoHashBase;


    public tabela(int tamanho){
        tamanhoTabela = tamanho;
        tabela = new listaEncadeada[tamanhoTabela];
        len = new int[tamanhoTabela];
        for(int i = 0; i < tamanhoTabela; i++) {
            tabela[i] = new listaEncadeada();
            len[i] = 0;
        }
        colisoes = 0;
        elementos = 0;
    }
    public int hash(int chave){
        switch (funcaoHashBase) {
            case 0:return (hashes.hMul(chave) & 0x7fffffff)%tamanhoTabela;
            case 1: return (hashes.hMisto(chave) & 0x7fffffff)%tamanhoTabela;
            default:return (hashes.hDiv(chave,tamanhoTabela));
        }
    }


    public void setHashBase(int b) {
        funcaoHashBase = b;
    }



    public void inserir(registro registro){
        int indice = hash(registro.getCodigoNumerico());

        colisoes += len[indice];

        tabela[indice].inserePrimeiro(registro);
        len[indice] ++;

        elementos++;
    }

    public boolean buscar(registro r) {
        int indice = hash(r.getCodigoNumerico());
        return tabela[indice].contem(r);
    }



    public int getColisoes() {
        return colisoes;
    }

    public  int getTamanho() {
        return tamanhoTabela;
    }


    public  int getElementos() {
        return elementos;
    }


    public double getFatorCarga(){
        return (double) elementos /tamanhoTabela;
    }


    public void limpar(){
        for (int i = 0; i < tamanhoTabela; i++){
            tabela[i] = new listaEncadeada();
            len[i] = 0;
        }
        colisoes = 0;
        elementos = 0;
    }



    public int[] maioresListas() {
        int[] maiores = {0,0,0};
        for (var lista : tabela) {
            if (lista != null) {
                int tam = lista.tamanho();
                if (tam > maiores[0]) {
                    maiores[2] = maiores[1];
                    maiores[1] = maiores[0];
                    maiores[0] = tam;
                } else if (tam > maiores[1]) {
                    maiores[2] = maiores[1];
                    maiores[1] = tam;
                } else if (tam > maiores[2]) {
                    maiores[2] = tam;
                }
            }
        }
        return maiores;
    }


    public double[] calcularGaps(){
        int menor = Integer.MAX_VALUE;
        int maior = 0;
        int soma = 0;
        int qtd = 0;

        int anterior = -1;

        for (int i=0 ; i<tamanhoTabela ; i++){
            if (len[i] > 0) {  // bucket ocupado
                if (anterior != -1){
                    int gap = i - anterior;
                    soma += gap;
                    if (gap < menor) menor = gap;
                    if (gap > maior) maior = gap;
                    qtd++;
                }
                anterior = i;
            }
        }

        double media;
        if(qtd > 0){
            media  = (double)soma/qtd;
        }
        else{
            media  = 0;
        }


        if (menor == Integer.MAX_VALUE) menor = 0;//vai que

        return new double[]{menor, maior, media};
    }
}
