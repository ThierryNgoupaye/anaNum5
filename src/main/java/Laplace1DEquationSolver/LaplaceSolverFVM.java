// src/main/java/Laplace1DEquationSolver/LaplaceSolverFVM.java

package Laplace1DEquationSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LaplaceSolverFVM implements ISolveLaplaceEquation1D {
    
    @Override
    public List<Double> solve(double a, double b, double alpha, double beta, int n, Function<Double, Double> f_func) {
        
        double h = (b - a) / (n + 1);

        // La matrice A reste la même.
        double[] diag_principale = new double[n];
        double[] diag_inférieure = new double[n - 1];
        double[] diag_supérieure = new double[n - 1];
        
        for (int i = 0; i < n; i++) {
            diag_principale[i] = 2.0;
            if (i < n - 1) {
                diag_inférieure[i] = -1.0;
                diag_supérieure[i] = -1.0;
            }
        }
        
        // --- CORRECTION MAJEURE : Calcul du second membre (le vecteur B) ---
        List<Double> B = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double xi = a + (i + 1) * h;
            
            // L'équation discrète est -u_{i-1} + 2u_i - u_{i+1} = h^2 * f(x_i)
            // Donc chaque terme du vecteur B est h^2 * f(x_i)
            double bi = h * h * f_func.apply(xi);
            B.add(bi);
        }

        // Application des conditions aux bords de Dirichlet
        B.set(0, B.get(0) + alpha);
        B.set(n - 1, B.get(n - 1) + beta);

        // Résolution avec l'algorithme de Thomas
        List<Double> diagPrin = Arrays.stream(diag_principale).boxed().collect(Collectors.toList());
        List<Double> diagSup = Arrays.stream(diag_supérieure).boxed().collect(Collectors.toList());
        List<Double> diagInf = Arrays.stream(diag_inférieure).boxed().collect(Collectors.toList());
        
        return thomasAlgorithm(diagInf, diagPrin, diagSup, B);
    }

    // L'algorithme de Thomas ne change pas.
    public static List<Double> thomasAlgorithm(List<Double> L, List<Double> D, List<Double> U, List<Double> B) {
        int n = B.size();
        List<Double> X = new java.util.ArrayList<>(java.util.Collections.nCopies(n, 0.0));
        
        List<Double> d_prime = new ArrayList<>(D);
        List<Double> b_prime = new ArrayList<>(B);

        for (int i = 1; i < n; i++) {
            double w = L.get(i - 1) / d_prime.get(i - 1);
            d_prime.set(i, d_prime.get(i) - w * U.get(i - 1));
            b_prime.set(i, b_prime.get(i) - w * b_prime.get(i - 1));
        }
        
        X.set(n - 1, b_prime.get(n - 1) / d_prime.get(n - 1));
        for (int i = n - 2; i >= 0; i--) {
            X.set(i, (b_prime.get(i) - U.get(i) * X.get(i + 1)) / d_prime.get(i));
        }

        return X;
    }
}