package tabela;

import lista.listaEncadeada;

public class tabela{
    private listaEncadeada[] tabela;
    private int colisoesLista;
    private int colisoesTabela;
    private int elementos;
    private int tamanhoTabela;
    private int[] len; //comprimento por "bucket"/slot/lista na tabela.tabela hash
    private static int funcaoHashBase;


    public tabela(int tamanho){
        tamanhoTabela = tamanho;
        tabela = new listaEncadeada[tamanhoTabela];
        len = new int[tamanhoTabela];
        for(int i = 0; i < tamanhoTabela; i++) {
            tabela[i] = new listaEncadeada();
            len[i] = 0;
        }
        colisoesLista = 0;
        elementos = 0;
    }
    public int hash(int chave){
        switch (funcaoHashBase) {
            case 0:return (hashes.hMul(chave,tamanhoTabela));
            case 1: return (hashes.hDobramento(chave,tamanhoTabela));
            default:return (hashes.hDiv(chave,tamanhoTabela));
        }
    }


    public static void setHashBase(int b) {
        funcaoHashBase = b;
    }



    public void inserir(registro registro){
        int indice = hash(registro.getCodigoNumerico());

        colisoesLista += len[indice];
        if (len[indice] > 0){
            colisoesTabela++;
        }

        tabela[indice].inserir(registro);//não sei porque no final só deixa mais devagar, O(n) em vez de O(1)
        len[indice] ++;

        elementos++;
    }

    public boolean buscar(registro r) {
        int indice = hash(r.getCodigoNumerico());
        return tabela[indice].contem(r);
    }
    // busca que retorna comparações: usado para medir cmp_hits / cmp_misses e tempo
    // retorna um inteiro: se >0 => hit com 'x' comparações; se <0 => miss com '-x' comparações
    public int buscarComparacoes(int codigo) {
        int indice = hash(codigo);
        return tabela[indice].procurarComparacoes(codigo);
    }


    public int getColisoesTabela() {
        return colisoesTabela;
    }
    public int getColisoesLista() {
        return colisoesLista;
    }

    public  int getTamanho() {
        return tamanhoTabela;
    }








    public void limpar(){
        for (int i = 0; i < tamanhoTabela; i++){
            tabela[i] = new listaEncadeada();
            len[i] = 0;
        }
        colisoesLista = 0;
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
            if (len[i] > 0) {  // bucket/slot/espaço ocupado
                if (anterior != -1){
                    int gap = i - anterior- 1;
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
