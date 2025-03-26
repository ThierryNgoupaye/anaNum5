package LaplaceEquationSolver;

import Laplace1DEquationSolver.ISolveLaplaceEquation1D;
import Laplace1DEquationSolver.LaplaceSolverCDS;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

public class SolverTest {

    /**
     * Test sans erreur : 
     * Cas : u(x) = x^2, avec f(x) = -2 et conditions aux bords u(0)=0 et u(1)=1.
     * Le schéma aux différences finies est exact pour cette fonction (à tolérance près).
     */
    @Test
    public void withoutError() {
        int compteur = 0;
        int nombreTests = 0;
        int[] nValues = {10, 20, 40, 80, 160, 320};
        double a = 0.0, b = 1.0;
        // Tolérance très stricte car la méthode est exacte pour une fonction polynomiale de degré 2.
        double tol = 1e-6;

        ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

        System.out.println("=== Tests sans erreur pour u(x) = x^2 ===");
        double alpha = 0.0;
        double beta  = 1.0;
        // Pour u(x)=x^2, u'' = 2 donc -u'' = -2, solution exacte : u(x)=x^2.
        for (int n : nValues) {
            nombreTests++;
            double h = (b - a) / (n + 1);
            List<Double> fList = new ArrayList<>();
            // Construction de f_i = h^2 * (-2) pour chaque point interne.
            for (int i = 0; i < n; i++) {
                fList.add(h * h * (-2.0));
            }
            // Conditions aux bords appliquées sur le vecteur f.
            fList.set(0, fList.get(0) + alpha);
            fList.set(n - 1, fList.get(n - 1) + beta);

            List<Double> sol = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, fList);
            boolean testPassed = true;
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h;
                double expected = xi * xi;
                if (Math.abs(sol.get(i) - expected) > tol) {
                    testPassed = false;
                    System.out.println("Echec pour u(x)=x^2, n=" + n + " à x=" + xi +
                                       " : calculé=" + sol.get(i) + " attendu=" + expected);
                }
            }
            if (testPassed) {
                compteur++;
                System.out.println("Test reussi pour u(x)=x^2, n = " + n);
            }
        }
        System.out.println("Nombre de tests reussis (x^2) : " + compteur + " / " + nombreTests);
        assertTrue(compteur == nombreTests);
    }

    /**
     * Test avec erreur :
     * Cas : u(x) = sin(pi*x), avec f(x) = pi^2*sin(pi*x) et conditions aux bords u(0)=0 et u(1)=sin(pi)=0.
     * La méthode aux différences finies ne reproduit pas exactement la solution exacte (erreur de troncature).
     * On vérifie que, pour chaque n, l'erreur (max |solution calculée - solution exacte|) est strictement > 0.
     */
    @Test
    public void withError() {
        int compteur = 0;
        int nombreTests = 0;
        int[] nValues = {10, 20, 40, 80, 160, 320};
        double a = 0.0, b = 1.0;
        // Pour ce cas, même si l'erreur décroît avec n, elle ne doit jamais être nulle.
        double tolError = 1e-12;

        ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

        System.out.println("=== Tests avec erreur pour u(x) = sin(pi*x) ===");
        double alpha = 0.0;
        double beta  = Math.sin(Math.PI);  // sin(pi) = 0
        // Pour u(x)= sin(pi*x), u'' = -pi^2*sin(pi*x) donc -u'' = pi^2*sin(pi*x).
        for (int n : nValues) {
            nombreTests++;
            double h = (b - a) / (n + 1);
            List<Double> fList = new ArrayList<>();
            // Construction de f_i = h^2 * (pi^2*sin(pi*x_i))
            for (int i = 0; i < n; i++) {
                double xi = a + (i + 1) * h;
                fList.add(h * h * (Math.PI * Math.PI * Math.sin(Math.PI * xi)));
            }
            // Conditions aux bords appliquées
            fList.set(0, fList.get(0) + alpha);
            fList.set(n - 1, fList.get(n - 1) + beta);

            List<Double> sol = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, fList);
            double maxError = 0.0;
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h;
                double expected = Math.sin(Math.PI * xi);
                double err = Math.abs(sol.get(i) - expected);
                if (err > maxError) {
                    maxError = err;
                }
            }
            System.out.println("Pour n = " + n + ", erreur maximale = " + maxError);
            // On s'attend à ce que l'erreur soit non nulle (inférieure à tolError n'est pas acceptable)
            boolean testPassed = maxError > tolError;
            if (testPassed) {
                compteur++;
                System.out.println("Test reussi pour u(x)=sin(pi*x), n = " + n);
            } else {
                System.out.println("Echec pour u(x)=sin(pi*x), n = " + n + " : erreur trop faible.");
            }
        }
        System.out.println("Nombre de tests reussis (sin(pi*x)) : " + compteur + " / " + nombreTests);
        assertTrue(compteur == nombreTests);
    }
}
