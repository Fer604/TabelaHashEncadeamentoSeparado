import sys
import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

def ensure_outdir(path: str) -> Path:
    p = Path(path)
    p.mkdir(parents=True, exist_ok=True)
    return p

def load_data(csv_path: str) -> pd.DataFrame:
    df = pd.read_csv(csv_path)
    # Normalização mínima
    df["func"] = df["func"].astype(str).str.strip().str.upper()
    return df


# --------------------------------------------------------------------
#  GRÁFICOS BÁSICOS: ins_ms, coll_tbl, coll_lst, cmp_hits, cmp_misses
# --------------------------------------------------------------------

def bar_group_by_func_per_N(df: pd.DataFrame, metric: str, outdir: Path, title_prefix: str):
    """
    Gera para cada N um gráfico de barras com média da métrica por função hash.
    """
    for N, sub in df.groupby("n"):
        pivot = sub.groupby("func", as_index=False)[metric].mean().sort_values("func")
        plt.figure()
        plt.bar(pivot["func"], pivot[metric])
        plt.title(f"{title_prefix} — n={N}")
        plt.xlabel("Func")
        plt.ylabel(metric)
        plt.tight_layout()
        plt.savefig(outdir / f"{metric}_por_func_n{N}.png", dpi=130)
        plt.close()

def time_insert(df: pd.DataFrame, outdir: Path):
    bar_group_by_func_per_N(df, "ins_ms", outdir, "Tempo de Inserção (ms)")

def collisions_table(df: pd.DataFrame, outdir: Path):
    bar_group_by_func_per_N(df, "coll_tbl", outdir, "Colisões na Tabela")

def collisions_list(df: pd.DataFrame, outdir: Path):
    bar_group_by_func_per_N(df, "coll_lst", outdir, "Colisões nas Listas")

def compares_hits(df: pd.DataFrame, outdir: Path):
    bar_group_by_func_per_N(df, "cmp_hits", outdir, "Comparações (hits)")

def compares_misses(df: pd.DataFrame, outdir: Path):
    bar_group_by_func_per_N(df, "cmp_misses", outdir, "Comparações (misses)")


# --------------------------------------------------------------------
#       GRÁFICOS POR FUNÇÃO: curvas variando N
# --------------------------------------------------------------------

def line_metric_over_N(df: pd.DataFrame, column: str, ylabel: str, prefix: str, outdir: Path):
    """
    Para cada função, plota uma curva da métrica variando n.
    """
    for func, sub in df.groupby("func"):
        gp = sub.groupby("n", as_index=False)[column].mean().sort_values("n")
        plt.figure()
        plt.plot(gp["n"], gp[column], marker="o")
        plt.title(f"{prefix} — func={func}")
        plt.xlabel("n")
        plt.ylabel(ylabel)
        plt.tight_layout()
        plt.savefig(outdir / f"{column}_por_n_func_{func}.png", dpi=130)
        plt.close()


# --------------------------------------------------------------------
#                        RESUMO CSV
# --------------------------------------------------------------------

def salvar_resumo(df: pd.DataFrame, outdir: Path):
    resumo = df.groupby(["n", "func"], as_index=False).agg({
        "ins_ms": "mean",
        "coll_tbl": "mean",
        "coll_lst": "mean",
        "find_ms_hits": "mean",
        "find_ms_misses": "mean",
        "cmp_hits": "mean",
        "cmp_misses": "mean"
    })
    resumo.to_csv(outdir / "resumo_por_n_func.csv", index=False)


# --------------------------------------------------------------------
#                                MAIN
# --------------------------------------------------------------------

def main():
    if len(sys.argv) < 3:
        print("Uso: python graficos_hash_novo.py <csv_entrada> <pasta_saida>")
        sys.exit(1)

    csv_path = sys.argv[1]
    out_dir = ensure_outdir(sys.argv[2])

    df = load_data(csv_path)

    # Gráficos principais
    time_insert(df, out_dir)
    collisions_table(df, out_dir)
    collisions_list(df, out_dir)
    compares_hits(df, out_dir)
    compares_misses(df, out_dir)

    # Gráficos com N variando
    line_metric_over_N(df, "ins_ms", "ins_ms", "Tempo inserção vs N", out_dir)
    line_metric_over_N(df, "coll_tbl", "coll_tbl", "Colisões tabela vs N", out_dir)
    line_metric_over_N(df, "coll_lst", "coll_lst", "Colisões lista vs N", out_dir)
    line_metric_over_N(df, "cmp_hits", "cmp_hits", "Hits comparação vs N", out_dir)
    line_metric_over_N(df, "cmp_misses", "cmp_misses", "Misses comparação vs N", out_dir)

    salvar_resumo(df, out_dir)

    print(f"Gráficos e resumo salvos em: {out_dir.resolve()}")


if __name__ == "__main__":
    main()
