package Laplace2DEquationSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LaplaceSolverRedBlack {

    private final int n;
    private final double h;
    private final double h2;
    private final double[][] f; // Second membre
    private double[][] u; // Solution numérique

    private int iterations = 0;
    private double finalResidualNorm = 0.0;

    public LaplaceSolverRedBlack(int n, BiFunction<Double, Double, Double> f_func, BiFunction<Double, Double, Double> boundary_func) {
        this.n = n;
        this.h = 1.0 / (n + 1);
        this.h2 = h * h;

        // Initialisation de u et f
        this.u = new double[n + 2][n + 2];
        this.f = new double[n + 2][n + 2];

        // Remplissage de f et des conditions aux limites pour u
        for (int j = 0; j < n + 2; j++) {
            double y = j * h;
            for (int i = 0; i < n + 2; i++) {
                double x = i * h;
                
                // Conditions aux limites sur le carré [0,1]x[0,1]
                if (i == 0 || i == n + 1 || j == 0 || j == n + 1) {
                    u[i][j] = boundary_func.apply(x, y);
                } else {
                    // Points intérieurs
                    f[i][j] = f_func.apply(x, y);
                }
            }
        }
    }

    public List<Double> solve(double tolerance, int maxIterations) {
        double residualNorm;

        for (int iter = 0; iter < maxIterations; iter++) {
            
            // --- PASSE ROUGE (i+j est pair) ---
            for (int j = 1; j <= n; j++) {
                for (int i = 1; i <= n; i++) {
                    if ((i + j) % 2 == 0) {
                        u[i][j] = 0.25 * (h2 * f[i][j] + u[i - 1][j] + u[i + 1][j] + u[i][j - 1] + u[i][j + 1]);
                    }
                }
            }

            // --- PASSE NOIRE (i+j est impair) ---
            for (int j = 1; j <= n; j++) {
                for (int i = 1; i <= n; i++) {
                    if ((i + j) % 2 != 0) {
                        u[i][j] = 0.25 * (h2 * f[i][j] + u[i - 1][j] + u[i + 1][j] + u[i][j - 1] + u[i][j + 1]);
                    }
                }
            }
            
            // Calcul du résidu toutes les 10 itérations (pour optimiser)
            if (iter % 10 == 0) {
                residualNorm = calculateResidualNorm();
                if (residualNorm < tolerance) {
                    this.iterations = iter + 1;
                    this.finalResidualNorm = residualNorm;
                    return getSolutionAsList();
                }
            }
        }
        
        // Si maxIterations est atteint
        this.iterations = maxIterations;
        this.finalResidualNorm = calculateResidualNorm();
        System.out.println("Attention: Nombre maximal d'itérations atteint.");
        return getSolutionAsList();
    }

    private double calculateResidualNorm() {
        double totalResidual = 0;
        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= n; i++) {
                double laplacian_u = (u[i - 1][j] + u[i + 1][j] + u[i][j - 1] + u[i][j + 1] - 4 * u[i][j]) / h2;
                double residual = f[i][j] + laplacian_u;
                totalResidual += residual * residual;
            }
        }
        return Math.sqrt(totalResidual);
    }

    private List<Double> getSolutionAsList() {
        List<Double> solList = new ArrayList<>(n * n);
        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= n; i++) {
                solList.add(u[i][j]);
            }
        }
        return solList;
    }
    
    public int getIterations() {
        return iterations;
    }

    public double getFinalResidualNorm() {
        return finalResidualNorm;
    }
}