import tabela.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        int[] tamanhosTabela = {1_009, 10_007, 100_003};
        int[] tamanhosDados = {1_000, 10_000, 100_000};
        long[] seeds = {137,271828,314159};

        System.out.printf("========COMEÇANDO========");

        int[][] combinacoes = {
                {0, 0},
                {0, 1},//vai fica ruim
                {0, 2},//vai fica ruim
                {1, 0},
                {1, 1},
                {1, 2},//vai fica ruim
                {2, 0},
                {2, 1},
                {2, 2}
        };
        final String outCsv = "output.csv";
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outCsv), StandardCharsets.UTF_8), 1<<20)) {//filOutputStream dá "throw" de uma exception não consigo utilizar sem try
            // Cabeçalho do CSV com todas as métricas pedidas no enunciado.
            bw.write("m,n,func,seed,ins_ms,coll_tbl,coll_lst,find_ms_hits,find_ms_misses,cmp_hits,cmp_misses,checksum,\n");

            for (int i = 0; i < 3; i++) {
                    long seed = seeds[i];
                    for (int hashBase = 0; hashBase < 3; hashBase++) {
                        String nomeHash = "";
                        switch (hashBase) {
                            case 0 -> nomeHash = "H_MUL";
                            case 1 -> nomeHash = "H_FOLD";
                            case 2 -> nomeHash = "H_DIV";
                        }
                        for (int[] combo : combinacoes) {
                            int tamanhoTabela = tamanhosTabela[combo[0]];
                            int tamanhoDados = tamanhosDados[combo[1]];


                            tabela tabela = new tabela(tamanhoTabela);
                            tabela.setHashBase(hashBase);

                            System.out.println("\n=== Testando ===");
                            testeDesempenho.executar(tabela, tabela.getTamanho(), new int[]{tamanhoDados}, nomeHash, seed,bw);
                        }
                    }
                }
        }
        catch (IOException e) {
            // Se deu algum erro de IO ao escrever o CSV final.Praticamente nunca vai ocorrer
            e.printStackTrace();
            System.exit(2);
        }

    }
}