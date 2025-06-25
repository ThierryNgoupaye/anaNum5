import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import glob
import os
import re
import sys

def analyze_results(results_dir):
    """
    Analyzes CSV files. For 'withError' cases, it calculates and displays 
    the L-infinity convergence rate 'p'. For 'withoutError' cases, it only 
    shows the error.
    """
    if not os.path.exists(results_dir):
        print(f"Error: Directory '{results_dir}' not found.", file=sys.stderr)
        sys.exit(1)

    csv_files = glob.glob(os.path.join(results_dir, "results_*.csv"))
    if not csv_files:
        print(f"No 'results_*.csv' files found in '{results_dir}'.", file=sys.stderr)
        return

    # Group files by test case
    test_cases = {}
    for f in csv_files:
        match = re.search(r'results_(.*)_n_(\d+)\.csv', os.path.basename(f))
        if match:
            case_name = match.group(1)
            n = int(match.group(2))
            if case_name not in test_cases:
                test_cases[case_name] = []
            test_cases[case_name].append({'file': f, 'n': n})

    # --- Process each test case ---
    for case_name, files_info in test_cases.items():
        print(f"\n--- Analyzing Test Case: {case_name} ---")
        
        sorted_files = sorted(files_info, key=lambda x: x['n'])
        
        # --- 1. Pre-calculate errors ---
        errors_Linf = {}
        h_values = {}
        
        for info in sorted_files:
            n = info['n']
            df = pd.read_csv(info['file'])
            errors_Linf[n] = df['absolute_error'].max()
            h_values[n] = 1.0 / (n + 1)

        # --- 2. Print summary table header ---
        print("\nConvergence Analysis:")
        if case_name == 'withError':
            print(f"{'n':>5} | {'h':>10} | {'Erreur L-inf':>15} | {'Vitesse p (L-inf)':>20}")
            print("-" * 65)
        else:
            print(f"{'n':>5} | {'h':>10} | {'Erreur L-inf':>15}")
            print("-" * 45)

        # --- 3. Generate plots and table rows ---
        sorted_n = sorted(h_values.keys())
        for i, n in enumerate(sorted_n):
            file_path = next(item['file'] for item in sorted_files if item['n'] == n)
            df = pd.read_csv(file_path)
            L_inf = errors_Linf[n]
            h = h_values[n]
            
            # --- Conditional calculation and display of convergence rate ---
            rate_Linf = None
            rate_Linf_str = "-"

            # Only calculate 'p' for the "withError" case and if it's not the first data point
            if case_name == 'withError' and i > 0:
                prev_n = sorted_n[i-1]
                prev_Linf = errors_Linf[prev_n]
                prev_h = h_values[prev_n]
                
                # p = log(E_prev / E_current) / log(h_prev / h_current)
                if L_inf > 1e-16 and prev_Linf > 1e-16: # Avoid log(0) or division by zero
                    rate_Linf = np.log(prev_Linf / L_inf) / np.log(prev_h / h)
                    rate_Linf_str = f"{rate_Linf:.4f}"

            # Print table row
            if case_name == 'withError':
                print(f"{n:>5} | {h:>10.4e} | {L_inf:>15.4e} | {rate_Linf_str:>20}")
            else:
                print(f"{n:>5} | {h:>10.4e} | {L_inf:>15.4e}")

            # --- Plotting ---
            plt.figure(figsize=(12, 8))
            plt.plot(df['x'], df['real_value'], 'b-', label='Valeur Exacte', linewidth=2)
            plt.plot(df['x'], df['estimated_value'], 'r--', label='Valeur Approchée', markersize=4)
            plt.plot(df['x'], df['absolute_error'], 'g:', label='Erreur Absolue', linewidth=2)
            
            plt.title(f'Analyse pour {case_name} (n={n})')
            plt.xlabel('x')
            plt.ylabel('Valeur')
            plt.legend()
            plt.grid(True)
            
            # --- Add a specific text box for each case ---
            info_text = f'Erreur L-infini: {L_inf:.2e}'
            if rate_Linf is not None:
                info_text += f'\nVitesse de Convergence p: {rate_Linf:.4f}'
            
            plt.figtext(0.5, 0.01, info_text, ha='center', fontsize=10, 
                        bbox={"facecolor":"orange", "alpha":0.5, "pad":5})

            plot_filename = os.path.join(results_dir, f'plot_{case_name}_n_{n}.png')
            plt.savefig(plot_filename)
            plt.close()

if __name__ == "__main__":
    results_path = sys.argv[1] if len(sys.argv) > 1 else "target/results"
    analyze_results(results_path)