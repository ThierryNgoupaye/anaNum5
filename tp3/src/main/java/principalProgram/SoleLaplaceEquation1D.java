package principalProgram;

import ISolveLaplaceEquation1D;

import java.util.List;

public class SoleLaplaceEquation1D implements ISolveLaplaceEquation1D{

    public double[] solve(double a, double b, double alpha, double beta, int N, double[] f){
        // TODO
        f[0] += alpha; // Ajoute la condition de Dirichlet sur la première valeur
        f[N] += beta; // Ajoute la condition de Dirichlet sur la dernière valeur
// Création de la matrice tridiagonale
        double[][] A = new double[N][N];
        for (int i = 0; i < N; i++) {
            A[i][i] = 2.0; // Diagonale principale

            if (i > 0) {
                A[i][i - 1] = -1.0; // Sous-diagonale
            }
            if (i < N - 1) {
                A[i][i + 1] = -1.0; // Sur-diagonale
            }
        }
        // Résolution du système AU = F
        double[] U = solveTridiagonal(A, f, N);

        // Affichage des résultats
        System.out.println("Solution U:");
        for (double u : U) {
            System.out.print(u + " ");
        }
        System.out.println();
        return U;

    }

    public static double[] solveTridiagonal(double[][] A, double[] F, int N) {
        // Initialize arrays for the three diagonals
        double[] a = new double[N - 1]; // lower diagonal (below main diagonal)
        double[] b = new double[N];     // main diagonal
        double[] c = new double[N - 1]; // upper diagonal (above main diagonal)
        double[] U = new double[N];     // solution vector

        // Extract the diagonals from matrix A
        for (int i = 0; i < N; i++) {
            b[i] = A[i][i]; // Main diagonal

            if (i < N - 1) {
                c[i] = A[i][i + 1];    // Upper diagonal
            }

            if (i > 0) {
                a[i - 1] = A[i][i - 1]; // Lower diagonal
            }
        }

        // Forward elimination phase
        // These arrays store the transformed coefficients
        double[] cPrime = new double[N - 1];
        double[] dPrime = new double[N];

        // Initialize the first elements
        cPrime[0] = c[0] / b[0];
        dPrime[0] = F[0] / b[0];

        // Forward sweep
        for (int i = 1; i < N; i++) {
            double m = 1.0 / (b[i] - a[i - 1] * cPrime[i - 1]);

            if (i < N - 1) {
                cPrime[i] = c[i] * m;
            }

            dPrime[i] = (F[i] - a[i - 1] * dPrime[i - 1]) * m;
        }

        // Back substitution phase
        U[N - 1] = dPrime[N - 1];

        for (int i = N - 2; i >= 0; i--) {
            U[i] = dPrime[i] - cPrime[i] * U[i + 1];
        }

        return U;
    }

    @Override
    public List<Double> solve(double a, double b, double alpha, double beta, int N, List<Double> f) {
        return List.of();
    }
}
