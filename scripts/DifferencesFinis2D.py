# import numpy as np
# import pandas as pd
# import matplotlib.pyplot as plt
# from matplotlib.patches import Rectangle
# from matplotlib.colors import Normalize
# from matplotlib.cm import ScalarMappable
# import matplotlib.patches as patches
# from mpl_toolkits.mplot3d import Axes3D
# import os

# class EDP2DVisualizer:
#     def __init__(self, csv_file, title="Solution EDP 2D"):
#         """
#         Initialise le visualiseur avec un fichier CSV.
        
#         Args:
#             csv_file (str): Chemin vers le fichier CSV
#             title (str): Titre pour les graphiques
#         """
#         self.csv_file = csv_file
#         self.title = title
#         self.data = None
#         self.x_unique = None
#         self.y_unique = None
#         self.nx = None
#         self.ny = None
        
#         self.load_data()
        
#     def load_data(self):
#         """Charge les données depuis le fichier CSV."""
#         try:
#             # Lecture du fichier CSV avec séparateur point-virgule
#             self.data = pd.read_csv(self.csv_file, sep=';')
#             print(f"Données chargées: {len(self.data)} points")
#             print(f"Colonnes: {list(self.data.columns)}")
            
#             # Récupération des coordonnées uniques
#             self.x_unique = np.sort(self.data['x'].unique())
#             self.y_unique = np.sort(self.data['y'].unique())
#             self.nx = len(self.x_unique)
#             self.ny = len(self.y_unique)
            
#             print(f"Maillage: {self.nx} × {self.ny} points")
#             print(f"Domaine: x ∈ [{self.x_unique.min():.3f}, {self.x_unique.max():.3f}]")
#             print(f"Domaine: y ∈ [{self.y_unique.min():.3f}, {self.y_unique.max():.3f}]")
            
#         except Exception as e:
#             print(f"Erreur lors du chargement: {e}")
#             raise
    
#     def data_to_grid(self, column_name):
#         """
#         Convertit une colonne de données en grille 2D.
        
#         Args:
#             column_name (str): Nom de la colonne à convertir
            
#         Returns:
#             np.ndarray: Grille 2D des valeurs
#         """
#         # Création d'une grille vide
#         grid = np.full((self.ny, self.nx), np.nan)
        
#         # Remplissage de la grille
#         for _, row in self.data.iterrows():
#             x_idx = np.where(np.isclose(self.x_unique, row['x'], rtol=1e-10))[0]
#             y_idx = np.where(np.isclose(self.y_unique, row['y'], rtol=1e-10))[0]
            
#             if len(x_idx) > 0 and len(y_idx) > 0:
#                 grid[y_idx[0], x_idx[0]] = row[column_name]
        
#         return grid
    
#     def plot_surface_3d(self, column_name, title_suffix=""):
#         """
#         Trace une surface 3D pour une colonne donnée.
        
#         Args:
#             column_name (str): Nom de la colonne à tracer
#             title_suffix (str): Suffixe pour le titre
#         """
#         fig = plt.figure(figsize=(12, 8))
#         ax = fig.add_subplot(111, projection='3d')
        
#         # Conversion en grille
#         Z = self.data_to_grid(column_name)
#         X, Y = np.meshgrid(self.x_unique, self.y_unique)
        
#         # Masquer les NaN pour un meilleur rendu
#         mask = ~np.isnan(Z)
#         if np.any(mask):
#             # Surface 3D
#             surf = ax.plot_surface(X, Y, Z, cmap='viridis', alpha=0.8, 
#                                  linewidth=0, antialiased=True)
            
#             # Ajout des contours projetés
#             contours = ax.contour(X, Y, Z, levels=10, cmap='viridis', 
#                                 alpha=0.6, offset=np.nanmin(Z))
            
#             # Configuration des axes
#             ax.set_xlabel('x', fontsize=12)
#             ax.set_ylabel('y', fontsize=12)
#             ax.set_zlabel(column_name, fontsize=12)
#             ax.set_title(f"{self.title} - {title_suffix}", fontsize=14)
            
#             # Barre de couleur
#             fig.colorbar(surf, ax=ax, shrink=0.5, aspect=20)
            
#             # Statistiques
#             stats_text = f"Min: {np.nanmin(Z):.2e}\nMax: {np.nanmax(Z):.2e}\nMoyenne: {np.nanmean(Z):.2e}"
#             ax.text2D(0.02, 0.98, stats_text, transform=ax.transAxes, 
#                      verticalalignment='top', fontsize=10, 
#                      bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        
#         plt.tight_layout()
#         return fig
    
#     def plot_mesh_colored(self, column_name, title_suffix=""):
#         """
#         Trace le maillage avec couleurs basées sur les valeurs aux sommets.
        
#         Args:
#             column_name (str): Nom de la colonne pour les couleurs
#             title_suffix (str): Suffixe pour le titre
#         """
#         fig, ax = plt.subplots(figsize=(12, 10))
        
#         # Conversion en grille
#         Z = self.data_to_grid(column_name)
        
#         # Calcul des dimensions des mailles
#         if self.nx > 1:
#             dx = (self.x_unique.max() - self.x_unique.min()) / (self.nx - 1)
#         else:
#             dx = 0.1
            
#         if self.ny > 1:
#             dy = (self.y_unique.max() - self.y_unique.min()) / (self.ny - 1)
#         else:
#             dy = 0.1
        
#         # Normalisation pour les couleurs
#         valid_values = Z[~np.isnan(Z)]
#         if len(valid_values) > 0:
#             vmin, vmax = np.min(valid_values), np.max(valid_values)
#             norm = Normalize(vmin=vmin, vmax=vmax)
            
#             # Tracé des mailles colorées
#             for i in range(self.ny):
#                 for j in range(self.nx):
#                     if not np.isnan(Z[i, j]):
#                         # Position du coin inférieur gauche de la maille
#                         x_corner = self.x_unique[j] - dx/2
#                         y_corner = self.y_unique[i] - dy/2
                        
#                         # Couleur basée sur la valeur
#                         color = plt.cm.viridis(norm(Z[i, j]))
                        
#                         # Rectangle représentant la maille
#                         rect = Rectangle((x_corner, y_corner), dx, dy, 
#                                        facecolor=color, edgecolor='black', 
#                                        linewidth=0.5, alpha=0.8)
#                         ax.add_patch(rect)
                        
#                         # Ajout du point central
#                         ax.plot(self.x_unique[j], self.y_unique[i], 'ko', 
#                                markersize=3, alpha=0.7)
                        
#                         # Valeur au centre de la maille (optionnel)
#                         if self.nx <= 20 and self.ny <= 20:  # Éviter l'encombrement
#                             ax.text(self.x_unique[j], self.y_unique[i], 
#                                    f'{Z[i, j]:.2e}', ha='center', va='center', 
#                                    fontsize=8, color='white', weight='bold')
            
#             # Configuration des axes
#             ax.set_xlim(self.x_unique.min() - dx, self.x_unique.max() + dx)
#             ax.set_ylim(self.y_unique.min() - dy, self.y_unique.max() + dy)
#             ax.set_xlabel('x', fontsize=12)
#             ax.set_ylabel('y', fontsize=12)
#             ax.set_title(f"{self.title} - Maillage coloré - {title_suffix}", fontsize=14)
#             ax.grid(True, alpha=0.3)
#             ax.set_aspect('equal')
            
#             # Barre de couleur avec légende détaillée
#             sm = ScalarMappable(norm=norm, cmap=plt.cm.viridis)
#             sm.set_array([])
#             cbar = fig.colorbar(sm, ax=ax, shrink=0.8)
#             cbar.set_label(column_name, fontsize=12)
            
#             # Ajout de niveaux de contour pour la légende
#             levels = np.linspace(vmin, vmax, 6)
#             cbar.set_ticks(levels)
#             cbar.set_ticklabels([f'{level:.2e}' for level in levels])
            
#             # Statistiques dans une boîte
#             stats_text = (f"Statistiques {column_name}:\n"
#                          f"Min: {vmin:.2e}\n"
#                          f"Max: {vmax:.2e}\n"
#                          f"Moyenne: {np.mean(valid_values):.2e}\n"
#                          f"Écart-type: {np.std(valid_values):.2e}")
            
#             ax.text(0.02, 0.98, stats_text, transform=ax.transAxes, 
#                    verticalalignment='top', fontsize=10,
#                    bbox=dict(boxstyle='round', facecolor='white', alpha=0.9))
        
#         plt.tight_layout()
#         return fig
    
#     def plot_comparison_2d(self):
#         """
#         Trace une comparaison 2D des trois quantités : exacte, numérique, erreur.
#         """
#         fig, axes = plt.subplots(2, 3, figsize=(18, 12))
        
#         columns = ['exact', 'numerical', 'error']
#         titles = ['Solution Exacte', 'Solution Numérique', 'Erreur Absolue']
        
#         for i, (col, title) in enumerate(zip(columns, titles)):
#             # Surface 2D (contour plot)
#             Z = self.data_to_grid(col)
#             X, Y = np.meshgrid(self.x_unique, self.y_unique)
            
#             # Graphique de contour rempli
#             if not np.all(np.isnan(Z)):
#                 im1 = axes[0, i].contourf(X, Y, Z, levels=20, cmap='viridis')
#                 axes[0, i].contour(X, Y, Z, levels=20, colors='black', alpha=0.3, linewidths=0.5)
#                 fig.colorbar(im1, ax=axes[0, i])
                
#                 # Graphique avec maillage
#                 im2 = axes[1, i].imshow(Z, extent=[X.min(), X.max(), Y.min(), Y.max()], 
#                                        origin='lower', cmap='viridis', aspect='auto')
                
#                 # Ajout du maillage
#                 for x in self.x_unique:
#                     axes[1, i].axvline(x, color='white', alpha=0.3, linewidth=0.5)
#                 for y in self.y_unique:
#                     axes[1, i].axhline(y, color='white', alpha=0.3, linewidth=0.5)
                
#                 fig.colorbar(im2, ax=axes[1, i])
            
#             # Configuration
#             for row in range(2):
#                 axes[row, i].set_xlabel('x')
#                 axes[row, i].set_ylabel('y')
#                 axes[row, i].set_title(f"{title}")
#                 axes[row, i].grid(True, alpha=0.3)
        
#         # Titres des lignes
#         axes[0, 0].text(-0.3, 0.5, 'Contours', transform=axes[0, 0].transAxes, 
#                        rotation=90, va='center', fontsize=14, weight='bold')
#         axes[1, 0].text(-0.3, 0.5, 'Maillage', transform=axes[1, 0].transAxes, 
#                        rotation=90, va='center', fontsize=14, weight='bold')
        
#         plt.suptitle(f"{self.title} - Comparaison complète", fontsize=16)
#         plt.tight_layout()
#         return fig
    
#     def generate_report(self):
#         """Génère un rapport complet avec tous les graphiques."""
#         print(f"\n=== GÉNÉRATION DU RAPPORT POUR {self.title} ===")
        
#         figures = []
        
#         # 1. Surfaces 3D
#         print("1. Génération des surfaces 3D...")
#         fig1 = self.plot_surface_3d('exact', 'Solution Exacte')
#         figures.append(('surface_3d_exact', fig1))
        
#         fig2 = self.plot_surface_3d('numerical', 'Solution Numérique')
#         figures.append(('surface_3d_numerical', fig2))
        
#         fig3 = self.plot_surface_3d('error', 'Erreur Absolue')
#         figures.append(('surface_3d_error', fig3))
        
#         # 2. Maillages colorés
#         print("2. Génération des maillages colorés...")
#         fig4 = self.plot_mesh_colored('exact', 'Solution Exacte')
#         figures.append(('mesh_exact', fig4))
        
#         fig5 = self.plot_mesh_colored('numerical', 'Solution Numérique')
#         figures.append(('mesh_numerical', fig5))
        
#         fig6 = self.plot_mesh_colored('error', 'Erreur Absolue')
#         figures.append(('mesh_error', fig6))
        
#         # 3. Comparaison 2D
#         print("3. Génération de la comparaison 2D...")
#         fig7 = self.plot_comparison_2d()
#         figures.append(('comparison_2d', fig7))
        
#         return figures

# def main():
#     """Fonction principale pour traiter les fichiers CSV."""

#     # Récupère le chemin absolu du dossier parent
#     base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    
#     # Liste des fichiers à traiter
#     csv_files = [
#         (os.path.join(base_dir, 'sin2D_solution.csv'), 'u(x,y) = sin(πx)sin(πy)'),
#         (os.path.join(base_dir, 'quadratic2D_solution.csv'), 'u(x,y) = x²y²')
#     ]
    
#     print("=== VISUALISATION DES RÉSULTATS EDP 2D ===\n")
    
#     for csv_file, title in csv_files:
#         if os.path.exists(csv_file):
#             print(f"\nTraitement de {csv_file}...")
            
#             try:
#                 # Création du visualiseur
#                 visualizer = EDP2DVisualizer(csv_file, title)
                
#                 # Génération de tous les graphiques
#                 figures = visualizer.generate_report()
                
#                 # Sauvegarde des figures
#                 base_name = csv_file.replace('.csv', '')
#                 for fig_name, fig in figures:
#                     filename = f"{base_name}_{fig_name}.png"
#                     fig.savefig(filename, dpi=300, bbox_inches='tight')
#                     print(f"  Sauvegardé: {filename}")
                
#                 # Fermeture des figures pour libérer la mémoire
#                 for _, fig in figures:
#                     plt.close(fig)
                
#                 print(f"  ✓ Traitement terminé pour {csv_file}")
                
#             except Exception as e:
#                 print(f"  ✗ Erreur lors du traitement de {csv_file}: {e}")
#         else:
#             print(f"  ⚠ Fichier non trouvé: {csv_file}")
    
#     print("\n=== VISUALISATION TERMINÉE ===")
#     print("Fichiers générés:")
#     print("  - *_surface_3d_*.png : Surfaces 3D")
#     print("  - *_mesh_*.png : Maillages colorés avec légende")
#     print("  - *_comparison_2d.png : Comparaisons complètes")
#     print("\nUtilisez ces images pour analyser:")
#     print("  • La précision de la solution numérique")
#     print("  • La distribution spatiale de l'erreur")
#     print("  • La qualité du maillage")

# if __name__ == "__main__":
#     # Configuration de matplotlib pour de meilleurs rendus
#     plt.rcParams['figure.dpi'] = 100
#     plt.rcParams['savefig.dpi'] = 300
#     plt.rcParams['font.size'] = 10
#     plt.rcParams['axes.titlesize'] = 12
#     plt.rcParams['axes.labelsize'] = 11
#     plt.rcParams['xtick.labelsize'] = 9
#     plt.rcParams['ytick.labelsize'] = 9
#     plt.rcParams['legend.fontsize'] = 10
    
#     main()






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


def main():
    """Fonction principale pour trouver et traiter tous les fichiers CSV de résultats."""
    # Le script est dans 'scripts/', les résultats dans 'target/results_2d/'
    # Donc on remonte d'un niveau pour trouver 'target'
    script_dir = os.path.dirname(__file__)
    project_root = os.path.abspath(os.path.join(script_dir, '..'))
    results_dir = os.path.join(project_root, 'target', 'results_2d')

    if not os.path.isdir(results_dir):
        print(f"Le répertoire des résultats '{results_dir}' n'a pas été trouvé.")
        return

    print(f"Recherche des fichiers CSV dans : {results_dir}")
    for filename in os.listdir(results_dir):
        if filename.endswith(".csv"):
            process_file(os.path.join(results_dir, filename))

    print("\nAnalyse terminée.")


if __name__ == "__main__":
    main()