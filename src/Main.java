import tabela.*;
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
                    testeDesempenho.executar(tabela, tabela.getTamanho(), new int[]{tamanhoDados}, nomeHash, seed);
                }
            }
        }
    }
}