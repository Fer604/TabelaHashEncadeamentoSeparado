package tabela;

import java.util.Random;

public class geradorDados {
    private final Random rnd;

    public geradorDados(long seed) {
        this.rnd = new Random(seed);
    }

    public registro[] gerar(int tamanho) {
        registro[] dados = new registro[tamanho];
        for (int i = 0; i < tamanho; i++) {
            int num = rnd.nextInt(999999999);
            dados[i] = new registro(Integer.toString(num));
        }
        return dados;
    }

    public long inserirTodos(tabela tabela, registro[] dados) {
        long inicio = System.currentTimeMillis();
        for (registro r : dados) tabela.inserir(r);
        long fim = System.currentTimeMillis();
        return fim - inicio;
    }
}
