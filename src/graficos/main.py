import sys
import os
from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt


def ensure_outdir(path: str) -> Path:
    p = Path(path)
    p.mkdir(parents=True, exist_ok=True)
    return p


def load_data(csv_path: str) -> pd.DataFrame:
    df = pd.read_csv(csv_path)

    # normalização
    if "func" in df.columns:
        df["func"] = df["func"].astype(str).str.strip().str.upper()

    return df


def bar_group_by_func_per_N(df: pd.DataFrame, metric: str, outdir: Path, title_prefix: str):
    """
    Faz 1 gráfico por N.
    Each figure: eixo X = func, barras = média do metric.
    """
    for N, sub in df.groupby("n"):
        pivot = sub.groupby("func", as_index=False)[metric].mean()
        pivot = pivot.sort_values("func")

        plt.figure()
        plt.bar(pivot["func"], pivot[metric])
        plt.title(f"{title_prefix} — N={N}")
        plt.xlabel("função hash")
        plt.ylabel(metric)
        plt.tight_layout()
        plt.savefig(outdir / f"{metric}_por_func_N{N}.png", dpi=130)
        plt.close()


def plot_metric_vs_N_by_func(df: pd.DataFrame, metric: str, outdir: Path, title_prefix: str):
    """
    Para cada func, 1 gráfico: eixo X = N, eixo Y = média do metric.
    """
    for func, sub in df.groupby("func"):
        gp = sub.groupby("n", as_index=False)[metric].mean().sort_values("n")

        plt.figure()
        plt.plot(gp["n"], gp[metric], marker="o")
        plt.title(f"{title_prefix} — func: {func}")
        plt.xlabel("n")
        plt.ylabel(metric)
        plt.tight_layout()
        plt.savefig(outdir / f"{metric}_por_N_func_{func}.png", dpi=130)
        plt.close()


def main():
    # Uso: python graficos2.py resultados2.csv plots2
    if len(sys.argv) < 3:
        print("Uso: python main2.py <csv_entrada> <pasta_saida>")
        sys.exit(1)

    csv_path = sys.argv[1]
    outdir = ensure_outdir(sys.argv[2])

    df = load_data(csv_path)

    # -------------------------------
    # Gráficos principais
    # -------------------------------
    bar_group_by_func_per_N(df, "ins_ms", outdir, "Tempo de inserção (ms)")
    bar_group_by_func_per_N(df, "coll_tbl", outdir, "Colisões na tabela")
    bar_group_by_func_per_N(df, "coll_lst", outdir, "Colisões na lista")

    plot_metric_vs_N_by_func(df, "ins_ms", outdir, "Tempo de inserção (ms)")
    plot_metric_vs_N_by_func(df, "coll_tbl", outdir, "Colisões na tabela")
    plot_metric_vs_N_by_func(df, "coll_lst", outdir, "Colisões na lista")

    plot_metric_vs_N_by_func(df, "find_ms_hits", outdir, "Tempo de busca (hits)")
    plot_metric_vs_N_by_func(df, "find_ms_misses", outdir, "Tempo de busca (misses)")

    plot_metric_vs_N_by_func(df, "cmp_hits", outdir, "Comparações (hits)")
    plot_metric_vs_N_by_func(df, "cmp_misses", outdir, "Comparações (misses)")

    # -------------------------------
    # Resumo CSV
    # -------------------------------
    resumo = df.groupby(["n", "func"], as_index=False).agg({
        "ins_ms": "mean",
        "coll_tbl": "mean",
        "coll_lst": "mean",
        "find_ms_hits": "mean",
        "find_ms_misses": "mean",
        "cmp_hits": "mean",
        "cmp_misses": "mean",
    })

    resumo.to_csv(outdir / "resumo_n_func.csv", index=False)

    print("\nGráficos e resumo salvos em:", outdir.resolve())


if __name__ == "__main__":
    main()
