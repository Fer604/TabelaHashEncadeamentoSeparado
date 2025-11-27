package tabela;

import java.io.BufferedWriter;
import java.util.Random;
import java.io.*;

public class testeDesempenho {

    // Executa experimentos para uma tabela já configurada (hashBase setado), imprime CSV no console.
    public static void executar(tabela tabela, int tamanhoTabela, int[] tamanhosDados, String nomeHash, long seed,BufferedWriter bw) {
        geradorDados gerador = new geradorDados(seed);

        // label exata exigida no enunciado: H_DIV, H_MUL, H_FOLD
        String etiqueta = nomeHash;

        try{//praticamente toda função de buffered writer dá throw de alguma exceção não tem como utiliza sem try

            // Para cada tamanho de dataset
            for (int tamanhoDados : tamanhosDados) {
                // gerar dados (mesmo conjunto para todas repetições)
                registro[] dados = gerador.gerar(tamanhoDados);

                // COMPUTAR checksum: soma dos primeiros 10 valores h(k) na ordem de geração (mod 1000003)
                long somaChecksum = 0;
                int limite = 10;
                if (tamanhoDados < 10) limite = tamanhoDados;
                for (int i = 0; i < limite; i++) {
                    int h = tabela.hash(dados[i].getCodigoNumerico());
                    somaChecksum += h;
                }
                int checksum = (int)(somaChecksum % 1000003L);

                // imprimir etiqueta exata + m + seed (exigência de auditoria)
                System.out.printf("%s m=%d seed=%d%n", etiqueta, tabela.getTamanho(), seed);

                // WARMUP
                tabela.limpar();
                for (registro r : dados) tabela.inserir(r);

                // repetições (5)
                final int REP = 5;
                long somaInsercoesMs = 0;
                long somaColisoesTabela = 0;
                long somaColisoesLista = 0;

                for (int rep = 0; rep < REP; rep++) {
                    tabela.limpar();
                    long inicio = System.currentTimeMillis();
                    for (registro r : dados) tabela.inserir(r);
                    long dur = System.currentTimeMillis() - inicio;
                    somaInsercoesMs += dur;
                    somaColisoesTabela += tabela.getColisoesTabela();
                    somaColisoesLista += tabela.getColisoesLista();
                }

                long ins_ms = somaInsercoesMs / REP;
                long coll_tbl = somaColisoesTabela / REP;
                long coll_lst = somaColisoesLista / REP;

                // --- BUSCAS: preparar lote 50% hits / 50% misses ---
                // vamos fazer REP(5) repetições das buscas e tirar médias
                long somaFindHitsMs = 0;
                long somaFindMissMs = 0;
                long somaCmpHits = 0;
                long somaCmpMiss = 0;

                final int NUM_QUERIES = tamanhoDados; // mesmo tamanho do dataset (pode ajustar)
                Random rnd = new Random(seed + 12345); // determinístico

                for (int rep = 0; rep < REP; rep++) {
                    // tabela precisa estar com os dados (reconstruir)
                    tabela.limpar();
                    for (registro r : dados) tabela.inserir(r);

                    // montar vetores
                    int half = NUM_QUERIES / 2;
                    int[] queries = new int[NUM_QUERIES];

                    // pegar half chaves presentes (escolhidas aleatoriamente entre os dados)
                    for (int i = 0; i < half; i++) {
                        int idx = rnd.nextInt(999999999);
                        idx = idx & 0x7fffffff;//zera o bit de sinal
                        idx = idx % tamanhoDados;
                        queries[i] = dados[idx].getCodigoNumerico();
                    }

                    // gerar half chaves ausentes (garantir que não estejam na tabela)
                    int outIdx = half;
                    while (outIdx < NUM_QUERIES) {
                        int cand = rnd.nextInt(999999999);
                        cand = cand & 0x7fffffff;//zera o bit de sinal
                        cand = cand% 999_999_999;
                        // testar se está presente:
                        int res = tabela.buscarComparacoes(cand);
                        if (res < 0) { // miss
                            queries[outIdx] = cand;
                            outIdx++;
                        } // se hit, gerar outro
                    }

                    // embaralhar queries (Fisher-Yates)
                    for (int i = NUM_QUERIES - 1; i > 0; i--) {
                        int j = rnd.nextInt();
                        j = j & 0x7fffffff;
                        j = j % (i + 1);
                        int tmp = queries[i];
                        queries[i] = queries[j];
                        queries[j] = tmp;
                    }

                    // executar buscas e medir separadamente hits e misses
                    long durHits = 0;
                    long durMiss = 0;
                    long cmpHits = 0;
                    long cmpMiss = 0;

                    // Para separar tempos de hits e misses precisamos medir cada operação e acumular no bucket respectivo
                    for (int q : queries) {
                        long t0 = System.currentTimeMillis();
                        int rcmp = tabela.buscarComparacoes(q);
                        long t1 = System.currentTimeMillis();
                        if (rcmp > 0) {
                            durHits += (t1 - t0);
                            cmpHits += rcmp;
                        } else {
                            durMiss += (t1 - t0);
                            cmpMiss += -rcmp;
                        }
                    }

                    somaFindHitsMs += durHits;
                    somaFindMissMs += durMiss;
                    somaCmpHits += cmpHits;
                    somaCmpMiss += cmpMiss;
                }

                long find_ms_hits = somaFindHitsMs / REP;
                long find_ms_misses = somaFindMissMs / REP;
                long cmp_hits = somaCmpHits / REP;
                long cmp_misses = somaCmpMiss / REP;

                // Imprimir resultado (CSV header implicitamente)
                // formato pedido: m,n,func,seed,ins_ms,coll_tbl,coll_lst,find_ms_hits,find_ms_misses,cmp_hits,cmp_misses,checksum
                String funcCode = etiqueta;//sla man




                // CSV line (impressão no console)
                System.out.printf("%d,%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
                        tamanhoTabela,
                        tamanhoDados,
                        funcCode,
                        seed,
                        ins_ms,
                        coll_tbl,
                        coll_lst,
                        find_ms_hits,
                        find_ms_misses,
                        cmp_hits,
                        cmp_misses,
                        checksum
                );
                //           --escreve no CSV de fato --
                bw.write(Integer.toString(tamanhoTabela));bw.write(',');
                bw.write(Integer.toString(tamanhoDados));bw.write(',');
                bw.write(funcCode);bw.write(',');
                bw.write(Long.toString(seed));bw.write(',');
                bw.write(Long.toString(ins_ms));bw.write(',');
                bw.write(Long.toString(coll_tbl));bw.write(',');
                bw.write(Long.toString(coll_lst));bw.write(',');
                bw.write(Long.toString(find_ms_hits));bw.write(',');
                bw.write(Long.toString(find_ms_misses));bw.write(',');
                bw.write(Long.toString(cmp_hits));bw.write(',');
                bw.write(Long.toString(cmp_misses));bw.write(',');
                bw.write(Integer.toString(checksum));bw.write(',');

                bw.write('\n');



                // imprimir algumas métricas adicionais legíveis
                int[] maiores = tabela.maioresListas();
                double[] gaps = tabela.calcularGaps();
                System.out.printf("Resumo -> fator: %.3f | Maiores listas: %d, %d, %d | Gaps (menor,maior,media): %.0f, %.0f, %.2f%n",
                        (double) tamanhoDados / tabela.getTamanho(), maiores[0], maiores[1], maiores[2],
                        gaps[0], gaps[1], gaps[2]
                );

                // limpar antes do próximo ajuste
                tabela.limpar();
            }
        } catch (IOException e) {
            // Se deu algum erro de IO ao escrever o CSV final.
            e.printStackTrace();
            System.exit(2);
        }
    }
}
