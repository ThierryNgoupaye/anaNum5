package principalProgramm;

import ISolveLaplaceEquation1D;

public class SoleLaplaceEquation1D implements ISolveLaplaceEquation1D{

    public List<Double> solve(douvle a, double b, double alpha, double beta, int N, List<Double> f){
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


}
