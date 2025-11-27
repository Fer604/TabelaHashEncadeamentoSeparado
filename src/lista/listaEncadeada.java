package lista;
import tabela.registro;

public class listaEncadeada {
    //eu tive que refatora altas coisas nisso mlhr nem usa essas funções se quiser a funcionalidade deles mlhr reescrever o codigo

    private node Lista;
    public listaEncadeada(){
        this.Lista = null;
    }

    //Inserindo elementos
    public void inserir (registro informacao){
        //Declarando nosso novo nó
        node no = new node();
        no.setInformacao(informacao);
        if(Lista == null){
            Lista=no;
        }
        else{
            //Aqui se cria um apontador para a lista.
            node atual =Lista;
            while(atual.getProximo()!=null){
                atual = atual.getProximo();
            }
            atual.setProximo(no);
//            System.out.println("Nó atual no endereço" + atual);
        }
    }
    //Imprimindo elementos
    public void imprime(){
        node atual = Lista;
        while (atual != null) {
            System.out.print(atual.getInformacao()+ " -> ");
            atual= atual.getProximo();
        }
        System.out.println("Null");
    }

    //cara manja mto dos programationsss(ingles fluent btw)
    public boolean vazia(){
        return Lista == null;
    }


    // move toda a lista pra frente SEM perder a info do primeiro anterior oia só o cara sabe
    public void inserePrimeiro(registro registro){
        node novoPrimeiro = new node();

        novoPrimeiro.setInformacao(registro);

        novoPrimeiro.setProximo(Lista);

        Lista=novoPrimeiro;
    }
    public void insereDepois(node node, registro registro){ //lista.node é o ponto 0
        node novoDepois = new node();
        novoDepois.setInformacao(registro);
        //PREGYUÇA
        novoDepois.setProximo(node.getProximo());
        node.setProximo(novoDepois);

        //acho q é isso

    }

    public void insereUltimo(registro registro){
        node novoUltimo = new node();
        node atual=Lista;
        novoUltimo.setInformacao(registro);
        //prvlvmente era mlhr coloca essa parte em uma função
        while (atual.getProximo() != null) {
            atual= atual.getProximo();
        }

        atual.setProximo(novoUltimo);

    }

    public void insereOrdenado(registro informacao){
        //essa ficou muito feia
        node noOrdenado = new node();
        noOrdenado.setInformacao(informacao);
        // eu não sei o q acontece qnd faz uma comparaçao entre um int com valor e outro sem (e eu NÃO QRO DESCOBRI) então eu lancei essa
        int maior = -99999;
        //percorre a lista
        node atual = Lista;
        int indiceDoMaior= 0;
        int ind = 0;
        while (atual.getProximo() != null) {
            if (atual.getInformacao().getCodigoNumerico() > maior) {
                maior = atual.getInformacao().getCodigoNumerico();
                indiceDoMaior = ind;
            }
            atual= atual.getProximo();
            ind++;
        }
        node velhoMaior = getNoFromPosi(indiceDoMaior);
        node proximoDoVelhoMaior = velhoMaior.getProximo();
        velhoMaior.setProximo(noOrdenado);
        noOrdenado.setProximo(proximoDoVelhoMaior);
    }

    public node removePrimeiro(){
        node removido =  Lista;
        Lista = Lista.getProximo();
        return removido;
    }

    public node removeUltimo(){
        node penultimo = Lista;
        while ((penultimo.getProximo()).getProximo() != null) {
            penultimo= penultimo.getProximo();
        }
        node removido = penultimo.getProximo();
        penultimo.setProximo(null);
        return removido;
    }



    public node remove(node no) {
        // se o nó a remover for o primeiro
        if (Lista == no) {
            return removePrimeiro();
        }

        node atual = Lista;
        node anterior = null;

        // percorre a lista até achar o nó
        while (atual != null && atual != no) {
            anterior = atual;
            atual = atual.getProximo();
        }

        if (atual == null) {
            return null; // nó não encontrado
        }

        // pula o nó que será removido
        anterior.setProximo(atual.getProximo());

        return atual;
    }


    public node getNoFromPosi(int posicaoDesejada){// eu odeio q essa função precisa existir
        node atual =Lista;
        int counter = 0;
        while(counter != posicaoDesejada){
            atual = atual.getProximo();
            counter++;
        }
        return atual;

    }
    public boolean contem(registro registro){
        int valor = registro.getCodigoNumerico();
        node atual = Lista;
        if (atual == null){
            return false;//gambiarra na cara dura
        }
        while (atual.getProximo() != null) {
            if (atual.getInformacao().getCodigoNumerico() == valor) {
                return true;
            }
            atual= atual.getProximo();
        }
        return false;
    }
    public int tamanho(){
        node atual=Lista;
        int tamanho=0;
        //prvlvmente era mlhr coloca essa parte em uma função
        if (atual ==null){
            return tamanho;
        }
        while (atual.getProximo() != null) {
            tamanho++;
            atual= atual.getProximo();
        }
        return tamanho;
    }
}
