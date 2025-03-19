package tests;

import org.junit.jupiter.api.Test;
import utils.Functions;
import java.util.ArrayList;
import java.util.List;

public class SolveLaplaceEquation1DTest implements ISolveLaplaceEquation1D {

    private final Functions functions = new Functions();
    private final int[] meshSizes = {10, 20, 40, 80, 160, 320};

    // Tests avec des fonctions dont on connaît les valeurs exactes
    @Test
    public List<Double> solveForValuesWithoutErrors(double a, double b, double alpha, double beta, int N, List<Double> f) {
        System.out.println("\n===== Test solveForValuesWithoutErrors avec N = " + N + " =====");
        
        List<Double> solution = solve(a, b, alpha, beta, N, f);

        System.out.println("Solution obtenue : " + solution);
        return solution;
    }

    // Tests avec des fonctions dont on connaît les valeurs approchées
    @Test
    public List<Double> solveForValuesWithErrors(double a, double b, double alpha, double beta, int N, List<Double> f) {
        System.out.println("\n===== Test solveForValuesWithErrors avec N = " + N + " =====");

        List<Double> solution = solve(a, b, alpha, beta, N, f);
        List<Double> exactSolution = computeExactSolution(a, b, alpha, beta, N);

        double error = computeLInfinityNorm(solution, exactSolution);
        
        System.out.println("Solution approchée : " + solution);
        System.out.println("Solution exacte     : " + exactSolution);
        System.out.println("Erreur ||u - u_h||_∞ = " + error);

        return solution;
    }

    // Implémentation de la résolution de -u'' = f
    @Override
    public List<Double> solve(double a, double b, double alpha, double beta, int N, List<Double> f) {
        double h = (b - a) / (N + 1);
        List<Double> u = new ArrayList<>();

        // Conditions aux bords
        u.add(alpha);

        for (int i = 1; i <= N; i++) {
            double xi = a + i * h;
            double ui = (f.get(i - 1) * h * h) / 2.0; // Approximation simplifiée
            u.add(ui);
        }

        u.add(beta);
        return u;
    }

    // Calcul de la solution exacte pour comparaison
    private List<Double> computeExactSolution(double a, double b, double alpha, double beta, int N) {
        List<Double> exactSolution = new ArrayList<>();
        double h = (b - a) / (N + 1);

        for (int i = 0; i <= N + 1; i++) {
            double x = a + i * h;
            exactSolution.add(Math.sin(Math.PI * x)); // Exemple : u(x) = sin(πx)
        }

        return exactSolution;
    }

    // Calcul de l'erreur ||u - u_h||_∞
    private double computeLInfinityNorm(List<Double> approx, List<Double> exact) {
        double maxError = 0.0;
        for (int i = 0; i < approx.size(); i++) {
            double error = Math.abs(approx.get(i) - exact.get(i));
            maxError = Math.max(maxError, error);
        }
        return maxError;
    }

    // Lancement des tests
    public void runTests() {
        double a = 0.0, b = 1.0, alpha = 0.0, beta = 0.0;
        for (int N : meshSizes) {
            List<Double> f = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                f.add(functions.sinus(i * (b - a) / (N + 1))); // f(x) = sin(x)
            }

            solveForValuesWithoutErrors(a, b, alpha, beta, N, f);
            solveForValuesWithErrors(a, b, alpha, beta, N, f);
        }
    }
}
