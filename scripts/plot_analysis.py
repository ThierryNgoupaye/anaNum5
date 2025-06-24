import os
import sys
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D

def load_csv(filepath):
    return pd.read_csv(filepath)

def plot_error_heatmap(df, title, output_path):
    pivot = df.pivot(index='y', columns='x', values='absolute_error')
    x_vals = sorted(df['x'].unique())
    y_vals = sorted(df['y'].unique())

    plt.figure(figsize=(8, 6))
    plt.imshow(pivot.values, origin='lower', extent=[min(x_vals), max(x_vals), min(y_vals), max(y_vals)],
               cmap='hot', interpolation='nearest', aspect='auto')
    plt.colorbar(label='Erreur absolue')
    plt.title(f"Carte de chaleur de l'erreur\n{title}")
    plt.xlabel("x")
    plt.ylabel("y")
    plt.savefig(output_path)
    plt.close()

def plot_3d_surface(df, value_column, title, output_path):
    x = sorted(df['x'].unique())
    y = sorted(df['y'].unique())
    X, Y = np.meshgrid(x, y)

    Z = df.pivot(index='y', columns='x', values=value_column).values

    fig = plt.figure(figsize=(10, 7))
    ax = fig.add_subplot(111, projection='3d')
    surf = ax.plot_surface(X, Y, Z, cmap=cm.viridis, linewidth=0, antialiased=True)
    fig.colorbar(surf, shrink=0.5, aspect=10, label=value_column)
    ax.set_title(title)
    ax.set_xlabel('x')
    ax.set_ylabel('y')
    ax.set_zlabel(value_column)
    plt.savefig(output_path)
    plt.close()

def main(results_dir):
    for filename in os.listdir(results_dir):
        if filename.endswith(".csv"):
            filepath = os.path.join(results_dir, filename)
            print(f"Traitement de {filename}...")
            df = load_csv(filepath)

            test_name = filename.replace(".csv", "")
            heatmap_path = os.path.join(results_dir, f"{test_name}_heatmap.png")
            plot_error_heatmap(df, test_name, heatmap_path)

            plot_3d_surface(df, 'real_value', f"{test_name} - Solution exacte",
                            os.path.join(results_dir, f"{test_name}_real_3d.png"))
            plot_3d_surface(df, 'estimated_value', f"{test_name} - Solution estimée",
                            os.path.join(results_dir, f"{test_name}_estimated_3d.png"))
    print("Visualisations terminées.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage : python plot_analysis_2d.py <répertoire_des_résultats>")
        sys.exit(1)
    results_directory = sys.argv[1]
    if not os.path.isdir(results_directory):
        print(f"Erreur : le répertoire '{results_directory}' est introuvable.")
        sys.exit(1)
    main(results_directory)
