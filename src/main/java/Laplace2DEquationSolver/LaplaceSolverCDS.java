package Laplace2DEquationSolver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import Laplace2DEquationSolver.ISolveLaplaceEquation2D;

public class LaplaceSolverCDS implements ISolveLaplaceEquation2D {
    
    public List<Double> solveForValuesWithoutErrors(double a, double b, double alpha, double beta, int n, List<Double> f) {
        int N = n * n; // Nombre total d'inconnues
        double h = (b - a) / (n + 1);
        double h2 = h * h;
        double coeffCenter = 4.0 / h2;
        double coeffNeighbor = -1.0 / h2;

        // Initialiser solution
        double[] u = new double[N];

        // CDS : 5 diagonales => on stocke dans 2D array [5][N]
        double[][] cds = new double[5][N]; // [main, -1, +1, -n, +n]

        for (int k = 0; k < N; k++) {
            // Position (i,j) à partir de k
            int j = k / n;
            int i = k % n;

            cds[0][k] = coeffCenter; // diagonale principale

            if (i > 0)     cds[1][k] = coeffNeighbor; // -1 (gauche)
            if (i < n - 1) cds[2][k] = coeffNeighbor; // +1 (droite)
            if (j > 0)     cds[3][k] = coeffNeighbor; // -n (bas)
            if (j < n - 1) cds[4][k] = coeffNeighbor; // +n (haut)
        }

        // Appliquer Dirichlet sur le bord (alpha et beta)
        for (int k = 0; k < N; k++) {
            int j = k / n;
            int i = k % n;

            if (i == 0) f.set(k, f.get(k) - alpha / h2);
            if (i == n - 1) f.set(k, f.get(k) - beta / h2);
            if (j == 0) f.set(k, f.get(k) - alpha / h2);
            if (j == n - 1) f.set(k, f.get(k) - beta / h2);
        }

        // Résolution simple par Gauss-Seidel (itératif)
        for (int iter = 0; iter < 10000; iter++) {
            for (int k = 0; k < N; k++) {
                int j = k / n;
                int i = k % n;

                double sum = f.get(k);

                if (i > 0)     sum -= cds[1][k] * u[k - 1];
                if (i < n - 1) sum -= cds[2][k] * u[k + 1];
                if (j > 0)     sum -= cds[3][k] * u[k - n];
                if (j < n - 1) sum -= cds[4][k] * u[k + n];

                u[k] = sum / cds[0][k];
            }
        }

        List<Double> result = new ArrayList<>();
        for (double val : u) result.add(val);
        return result;
    }

    public static void main(String[] args) {
        int n = 10; // nombre de points internes en x et y
        double a = 0.0, b = 1.0;
        double alpha = 0.0, beta = 0.0;

        // Test 1 : u(x, y) = x^2 + y^2 → f = 4 partout
        List<Double> f1 = new ArrayList<>();
        for (int j = 1; j <= n; j++) {
            double y = a + j * (b - a) / (n + 1);
            for (int i = 1; i <= n; i++) {
                double x = a + i * (b - a) / (n + 1);
                f1.add(4.0);
            }
        }

        LaplaceSolverCDS R0 = new LaplaceSolverCDS();

        List<Double> result1 = R0.solveForValuesWithoutErrors(a, b, alpha, beta, n, f1);
        System.out.println("Résultat pour u(x,y) = x^2 + y^2 (approximation) :");
        printGrid(result1, n);

        // Test 2 : u(x, y) = sin(pi x) sin(pi y) → f = 2π² sin(pi x) sin(pi y)
        List<Double> f2 = new ArrayList<>();
        for (int j = 1; j <= n; j++) {
            double y = a + j * (b - a) / (n + 1);
            for (int i = 1; i <= n; i++) {
                double x = a + i * (b - a) / (n + 1);
                double val = 2 * Math.PI * Math.PI * Math.sin(Math.PI * x) * Math.sin(Math.PI * y);
                f2.add(val);
            }
        }

        List<Double> result2 = R0.solveForValuesWithoutErrors(a, b, alpha, beta, n, f2);
        System.out.println("\nRésultat pour u(x,y) = sin(pi x) sin(pi y) (approximation) :");
        printGrid(result2, n);
    }

    public static void printGrid(List<Double> values, int n) {
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                System.out.printf("%.5f\t", values.get(j * n + i));
            }
            System.out.println();
        }
    }
}

