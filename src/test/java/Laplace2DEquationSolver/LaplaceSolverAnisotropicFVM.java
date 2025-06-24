package Laplace2DEquationSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LaplaceSolverAnisotropicFVM {

    private final int n; // points en x
    private final int m; // points en y
    private final double hx, hy;
    private final double hx2, hy2;
    private final double[][] f;
    private double[][] u;

    // Coefficients pour la formule de Gauss-Seidel
    private final double c_p, c_x, c_y;

    private int iterations = 0;
    
    public LaplaceSolverAnisotropicFVM(int n, int m, BiFunction<Double, Double, Double> f_func, BiFunction<Double, Double, Double> boundary_func) {
        this.n = n;
        this.m = m;
        
        // Domaine [0,1] x [0,1]
        this.hx = 1.0 / (n + 1);
        this.hy = 1.0 / (m + 1);
        this.hx2 = hx * hx;
        this.hy2 = hy * hy;

        // u est de taille (n+2) x (m+2)
        this.u = new double[n + 2][m + 2];
        this.f = new double[n + 2][m + 2];

        // Pré-calcul des coefficients pour la formule de résolution
        // De: (2/hx² + 2/hy²)u_p = f + (u_E+u_W)/hx² + (u_N+u_S)/hy²
        // On isole u_p
        this.c_p = 1.0 / (2.0 / hx2 + 2.0 / hy2);
        this.c_x = 1.0 / hx2;
        this.c_y = 1.0 / hy2;

        // Remplissage de u et f
        for (int j = 0; j < m + 2; j++) {
            double y_j = j * hy;
            for (int i = 0; i < n + 2; i++) {
                double x_i = i * hx;
                
                if (i == 0 || i == n + 1 || j == 0 || j == m + 1) {
                    u[i][j] = boundary_func.apply(x_i, y_j);
                } else {
                    f[i][j] = f_func.apply(x_i, y_j);
                }
            }
        }
    }

    public List<Double> solve(double tolerance, int maxIterations) {
        double residualNorm;

        for (int iter = 0; iter < maxIterations; iter++) {
            
            // Passe Rouge-Noir
            // ROUGE: (i+j) pair
            for (int j = 1; j <= m; j++) {
                for (int i = 1; i <= n; i++) {
                    if ((i + j) % 2 == 0) {
                        double sum_neighbors = c_x * (u[i + 1][j] + u[i - 1][j]) + c_y * (u[i][j + 1] + u[i][j - 1]);
                        u[i][j] = c_p * (f[i][j] + sum_neighbors);
                    }
                }
            }

            // NOIRE: (i+j) impair
            for (int j = 1; j <= m; j++) {
                for (int i = 1; i <= n; i++) {
                     if ((i + j) % 2 != 0) {
                        double sum_neighbors = c_x * (u[i + 1][j] + u[i - 1][j]) + c_y * (u[i][j + 1] + u[i][j - 1]);
                        u[i][j] = c_p * (f[i][j] + sum_neighbors);
                    }
                }
            }
            
            // Le calcul du résidu doit aussi être adapté
            if (iter % 10 == 0) {
                residualNorm = calculateResidualNorm();
                // La tolérance doit dépendre des deux pas
                if (residualNorm < tolerance) {
                    this.iterations = iter + 1;
                    return getSolutionAsList();
                }
            }
        }
        
        this.iterations = maxIterations;
        System.out.println("Attention: Nombre maximal d'itérations atteint.");
        return getSolutionAsList();
    }
    
    private double calculateResidualNorm() {
        double totalResidual = 0;
        for (int j = 1; j <= m; j++) {
            for (int i = 1; i <= n; i++) {
                double laplacian = (u[i + 1][j] - 2*u[i][j] + u[i - 1][j]) / hx2 + (u[i][j + 1] - 2*u[i][j] + u[i][j - 1]) / hy2;
                double residual = f[i][j] + laplacian;
                totalResidual += residual * residual;
            }
        }
        return Math.sqrt(totalResidual);
    }

    private List<Double> getSolutionAsList() {
        // Attention: la liste est maintenant de taille n*m
        List<Double> solList = new ArrayList<>(n * m);
        for (int j = 1; j <= m; j++) {
            for (int i = 1; i <= n; i++) {
                solList.add(u[i][j]);
            }
        }
        return solList;
    }
    
    public int getIterations() {
        return iterations;
    }
}