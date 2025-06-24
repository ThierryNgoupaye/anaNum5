import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle
from mpl_toolkits.mplot3d import Axes3D
import os

def plot_2d_heatmap(X, Y, Z, title, ax):
    """Affiche une carte de chaleur 2D sur un axe donné."""
    if np.all(np.isnan(Z)):
        ax.text(0.5, 0.5, 'Données non disponibles', ha='center', va='center')
        ax.set_title(title)
        return None
    
    vmin, vmax = np.nanmin(Z), np.nanmax(Z)
    im = ax.imshow(Z, extent=[X.min(), X.max(), Y.min(), Y.max()],
                   origin='lower', cmap='viridis', aspect='equal',
                   vmin=vmin, vmax=vmax)
    ax.set_title(title)
    ax.set_xlabel('x')
    ax.set_ylabel('y')
    return im

def process_file(csv_path):
    """Traite un fichier CSV et génère un graphique de comparaison."""
    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError:
        print(f"Fichier non trouvé: {csv_path}")
        return
        
    base_name = os.path.basename(csv_path).replace('.csv', '')
    print(f"Traitement de {base_name}...")

    x_unique = np.sort(df['x'].unique())
    y_unique = np.sort(df['y'].unique())
    nx, ny = len(x_unique), len(y_unique)
    
    X, Y = np.meshgrid(x_unique, y_unique)

    # Convertir les données en grilles
    grid_exact = df.pivot(index='y', columns='x', values='exact').values
    grid_numerical = df.pivot(index='y', columns='x', values='numerical').values
    grid_error = df.pivot(index='y', columns='x', values='error').values

    fig, axes = plt.subplots(1, 3, figsize=(18, 5))
    fig.suptitle(f"Analyse pour {base_name}", fontsize=16)

    # Plot 1: Solution Numérique
    im1 = plot_2d_heatmap(X, Y, grid_numerical, 'Solution Numérique', axes[0])
    if im1: fig.colorbar(im1, ax=axes[0], label='Valeur u(x,y)')

    # Plot 2: Solution Exacte
    im2 = plot_2d_heatmap(X, Y, grid_exact, 'Solution Exacte', axes[1])
    if im2: fig.colorbar(im2, ax=axes[1], label='Valeur u(x,y)')

    # Plot 3: Erreur Absolue
    im3 = plot_2d_heatmap(X, Y, grid_error, 'Erreur Absolue', axes[2])
    if im3: fig.colorbar(im3, ax=axes[2], label='|Num - Exact|')

    plt.tight_layout(rect=[0, 0.03, 1, 0.95])
    
    # Sauvegarde de la figure
    output_filename = os.path.join(os.path.dirname(csv_path), f"{base_name}_comparison.png")
    fig.savefig(output_filename, dpi=150)
    print(f"  -> Graphique sauvegardé : {output_filename}")
    plt.close(fig)

def plot_convergence_rate(base_name, results_dir):
    """
    Analyse tous les fichiers CSV pour un cas de test donné
    et trace le graphique de convergence.
    """
    csv_files = glob.glob(os.path.join(results_dir, f"{base_name}_n*.csv"))
    if not csv_files:
        return

    print(f"\nAnalyse de la convergence pour '{base_name}'...")
    
    errors = []
    h_values = []

    for f in csv_files:
        try:
            # Extraire n de nom de fichier
            n_str = os.path.basename(f).split('_n')[1].replace('.csv', '')
            n = int(n_str)
            h = 1.0 / (n + 1)
            
            df = pd.read_csv(f)
            max_error = df['error'].max()
            
            errors.append(max_error)
            h_values.append(h)
        except (IndexError, ValueError, FileNotFoundError):
            continue
    
    if not errors:
        print("Aucune donnée d'erreur trouvée.")
        return

    # Trier par h pour un tracé correct
    sorted_pairs = sorted(zip(h_values, errors))
    h_sorted, errors_sorted = zip(*sorted_pairs)

    # Création du graphique log-log
    plt.figure(figsize=(8, 6))
    plt.loglog(h_sorted, errors_sorted, 'o-', label='Erreur Numérique (E_max)')
    
    # Ajout d'une ligne de référence de pente 2
    # E = C*h^2 -> on trouve C avec le premier point
    C = errors_sorted[0] / (h_sorted[0]**2)
    h_ref = np.array(h_sorted)
    plt.loglog(h_ref, C * h_ref**2, 'r--', label='Référence (pente 2)')
    
    plt.title(f"Taux de Convergence pour {base_name}")
    plt.xlabel("Pas du maillage (h)")
    plt.ylabel("Erreur Maximale Absolue (E_max)")
    plt.grid(True, which="both", ls="--")
    plt.gca().invert_xaxis() # h diminue de gauche à droite
    plt.legend()
    
    output_filename = os.path.join(results_dir, f"{base_name}_convergence_rate.png")
    plt.savefig(output_filename, dpi=150)
    print(f"  -> Graphique de convergence sauvegardé : {output_filename}")
    plt.close()


def main():
    """Fonction principale pour trouver et traiter tous les fichiers CSV de résultats."""
    script_dir = os.path.dirname(__file__)
    project_root = os.path.abspath(os.path.join(script_dir, '..'))
    results_dir = os.path.join(project_root, 'target', 'results_2d')

    if not os.path.isdir(results_dir):
        print(f"Le répertoire des résultats '{results_dir}' n'a pas été trouvé.")
        return

    print(f"Recherche des fichiers CSV dans : {results_dir}")
    processed_bases = set()
    all_files = os.listdir(results_dir)
    
    for filename in all_files:
        if filename.endswith(".csv"):
            process_file(os.path.join(results_dir, filename))
            # Identifier le cas de test de base (ex: 'sin2D_solution')
            base_name = filename.split('_n')[0]
            processed_bases.add(base_name)

    # Après avoir traité les fichiers individuels, générer les graphiques de convergence
    for base_name in processed_bases:
        plot_convergence_rate(base_name, results_dir)
        
    print("\nAnalyse terminée.")


# 