package principalProgram;

import ISolveLaplaceEquation1D;

import java.util.ArrayList;
import java.util.List;

public class SolveLaplaceEquation1D implements ISolveLaplaceEquation1D{

    @Override
    public List<Double> solve(double a, double b, double alpha, double beta, int N, List<Double> f) {
        // Conversion de List<Double> en double[]
        double[] fArray = new double[N];
        for (int i = 0; i < N; i++) {
            fArray[i] = f.get(i);
        }
        fArray[0] += alpha; // Ajoute la condition de Dirichlet sur la première valeur
        fArray[N-1] += beta; // Ajoute la condition de Dirichlet sur la dernière valeur

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

        // recuperation du resultat
        double[] resultArray = solveTridiagonal(A,fArray,N);

        // Conversion du résultat en List<Double> et affichage des resultats
        List<Double> resultList = new ArrayList<>();
        for (double value : resultArray) {
            resultList.add(value);
            System.out.println(value + " ");
        }

        return resultList;
    }

    //Fonction de resolution du systeme
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

}
