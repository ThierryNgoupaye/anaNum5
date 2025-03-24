package Laplace1D;



import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import Interfaces.Equation.ISolveLaplaceEquation1D;

public class LaplaceSolverCDS implements ISolveLaplaceEquation1D {
    
    public List<Double> solveForValuesWithoutErrors(double a, double b, double alpha, double beta, int n, List<Double> f) {
        
        double h = (b - a) / (n + 1);

        // Stockage compact CDS : trois diagonales
        double[] diag_principale = new double[n];
        double[] diag_inf = new double[n - 1];
        
        
        // Remplissage des diagonales de la matrice tridiagonale
        for (int i = 0; i < n; i++) {
            diag_principale[i] = 2.0; // Diagonale principale
            if (i < n - 1) {
                diag_inf[i] = -1.0; // Diagonale inférieure
            }
        }

        List<Double> diagPrin = Arrays.stream(diag_principale).boxed().collect(Collectors.toList());
        List<Double> diagInf = Arrays.stream(diag_inf).boxed().collect(Collectors.toList());
        
        // Résolution du système via la méthode de Thomas
        return thomasAlgorithm(diagInf, diagPrin, diagInf, f, a, h);
    }

    public static List<Double> thomasAlgorithm(List<Double> L, List<Double> D, List<Double> U, List<Double> B, double a, double h) {
        int n = B.size();
        List<Double> X = new java.util.ArrayList<>(java.util.Collections.nCopies(n, 0.0));
        
        // Étape de forward elimination
        for (int i = 1; i < n; i++) {
            double w = L.get(i - 1) / D.get(i - 1);
            D.set(i, D.get(i) - w * U.get(i - 1));
            B.set(i, B.get(i) - w * B.get(i - 1));
        }
        
        // Back substitution
        X.set(n - 1, B.get(n - 1) / D.get(n - 1));
        for (int i = n - 2; i >= 0; i--) {
            X.set(i, (B.get(i) - U.get(i) * X.get(i + 1)) / D.get(i));
        }

        return X;
    }

}

