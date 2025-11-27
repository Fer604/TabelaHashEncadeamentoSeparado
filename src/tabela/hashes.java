package tabela;

public class hashes {
    public static int hDiv(int x, int tamanhoTabela) {
        x = x & 0x7fffffff;//zera o bit de sinal
        return x%tamanhoTabela;
    }
    public static int hMul(int x,int tamanhoTabela) {
        x = x & 0x7fffffff;//zera o bit de sinal
        final double A = 0.6180339887;
        double frac = (x * A) % 1.0;
        return (int) (tamanhoTabela * frac);
    }
    public static int hDobramento(int x,int tamanhoTabela) {
        x = x & 0x7fffffff;//zera o bit de sinal
        int soma = 0;

        while (x > 0) {
            soma += x % 1000;
            x /= 1000;
        }

        return soma % tamanhoTabela;
    }
}
