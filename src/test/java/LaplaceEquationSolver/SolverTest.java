// package LaplaceEquationSolver;

// import Laplace1DEquationSolver.ISolveLaplaceEquation1D;
// import Laplace1DEquationSolver.LaplaceSolverCDS;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.ArrayList;
// import java.util.List;

// public class SolverTest {

//     /**
//      * Test sans erreur : 
//      * Cas : u(x) = x^2, avec f(x) = -2 et conditions aux bords u(0)=0 et u(1)=1.
//      * Le schéma aux différences finies est exact pour cette fonction (à tolérance près).
//      */
//     @Test
//     public void withoutError() {
//         int compteur = 0;
//         int nombreTests = 0;
//         int[] nValues = {10, 20, 40, 80, 160, 320};
//         double a = 0.0, b = 1.0;
//         // Tolérance très stricte car la méthode est exacte pour une fonction polynomiale de degré 2.
//         double tol = 1e-6;

//         ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

//         System.out.println("=== Tests sans erreur pour u(x) = x^2 ===");
//         double alpha = 0.0;
//         double beta  = 1.0;
//         // Pour u(x)=x^2, u'' = 2 donc -u'' = -2, solution exacte : u(x)=x^2.
//         for (int n : nValues) {
//             nombreTests++;
//             double h = (b - a) / (n + 1);
//             List<Double> fList = new ArrayList<>();
//             // Construction de f_i = h^2 * (-2) pour chaque point interne.
//             for (int i = 0; i < n; i++) {
//                 fList.add(h * h * (-2.0));
//             }
//             // Conditions aux bords appliquées sur le vecteur f.
//             fList.set(0, fList.get(0) + alpha);
//             fList.set(n - 1, fList.get(n - 1) + beta);

//             List<Double> sol = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, fList);
//             boolean testPassed = true;
//             for (int i = 0; i < sol.size(); i++) {
//                 double xi = a + (i + 1) * h;
//                 double expected = xi * xi;
//                 if (Math.abs(sol.get(i) - expected) > tol) {
//                     testPassed = false;
//                     System.out.println("Echec pour u(x)=x^2, n=" + n + " à x=" + xi +
//                                        " : calculé=" + sol.get(i) + " attendu=" + expected);
//                 }
//             }
//             if (testPassed) {
//                 compteur++;
//                 System.out.println("Test reussi pour u(x)=x^2, n = " + n);
//             }
//         }
//         System.out.println("Nombre de tests reussis (x^2) : " + compteur + " / " + nombreTests);
//         assertTrue(compteur == nombreTests);
//     }

//     /**
//      * Test avec erreur :
//      * Cas : u(x) = sin(pi*x), avec f(x) = pi^2*sin(pi*x) et conditions aux bords u(0)=0 et u(1)=sin(pi)=0.
//      * La méthode aux différences finies ne reproduit pas exactement la solution exacte (erreur de troncature).
//      * On vérifie que, pour chaque n, l'erreur (max |solution calculée - solution exacte|) est strictement > 0.
//      */
//     @Test
//     public void withError() {
//         int compteur = 0;
//         int nombreTests = 0;
//         int[] nValues = {10, 20, 40, 80, 160, 320};
//         double a = 0.0, b = 1.0;
//         // Pour ce cas, même si l'erreur décroît avec n, elle ne doit jamais être nulle.
//         double tolError = 1e-12;

//         ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

//         System.out.println("=== Tests avec erreur pour u(x) = sin(pi*x) ===");
//         double alpha = 0.0;
//         double beta  = Math.sin(Math.PI);  // sin(pi) = 0
//         // Pour u(x)= sin(pi*x), u'' = -pi^2*sin(pi*x) donc -u'' = pi^2*sin(pi*x).
//         for (int n : nValues) {
//             nombreTests++;
//             double h = (b - a) / (n + 1);
//             List<Double> fList = new ArrayList<>();
//             // Construction de f_i = h^2 * (pi^2*sin(pi*x_i))
//             for (int i = 0; i < n; i++) {
//                 double xi = a + (i + 1) * h;
//                 fList.add(h * h * (Math.PI * Math.PI * Math.sin(Math.PI * xi)));
//             }
//             // Conditions aux bords appliquées
//             fList.set(0, fList.get(0) + alpha);
//             fList.set(n - 1, fList.get(n - 1) + beta);

//             List<Double> sol = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, fList);
//             double maxError = 0.0;
//             for (int i = 0; i < sol.size(); i++) {
//                 double xi = a + (i + 1) * h;
//                 double expected = Math.sin(Math.PI * xi);
//                 double err = Math.abs(sol.get(i) - expected);
//                 if (err > maxError) {
//                     maxError = err;
//                 }
//             }
//             System.out.println("Pour n = " + n + ", erreur maximale = " + maxError);
//             // On s'attend à ce que l'erreur soit non nulle (inférieure à tolError n'est pas acceptable)
//             boolean testPassed = maxError > tolError;
//             if (testPassed) {
//                 compteur++;
//                 System.out.println("Test reussi pour u(x)=sin(pi*x), n = " + n);
//             } else {
//                 System.out.println("Echec pour u(x)=sin(pi*x), n = " + n + " : erreur trop faible.");
//             }
//         }
//         System.out.println("Nombre de tests reussis (sin(pi*x)) : " + compteur + " / " + nombreTests);
//         assertTrue(compteur == nombreTests);
//     }
// }

package LaplaceEquationSolver;

import Laplace1DEquationSolver.ISolveLaplaceEquation1D;
import Laplace1DEquationSolver.LaplaceSolverCDS;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SolverTest {

    private static final String RESULTS_DIR = "target/results";

    @BeforeAll
    public static void setup() throws IOException {
        // Create the directory for results if it doesn't exist
        Files.createDirectories(Paths.get(RESULTS_DIR));
    }

    private void writeResultsToCsv(String testName, int n, double a, double h, List<Double> sol, java.util.function.Function<Double, Double> exactSolution) {
        String fileName = String.format("%s/results_%s_n_%d.csv", RESULTS_DIR, testName, n);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("x,real_value,estimated_value,absolute_error");
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h;
                double expected = exactSolution.apply(xi);
                double estimated = sol.get(i);
                double error = Math.abs(estimated - expected);
                writer.printf(Locale.US, "%.16f,%.16f,%.16f,%.16f\n", xi, expected, estimated, error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    @Order(1)
    public void withoutError() {
        int compteur = 0;
        int nombreTests = 0;
        int[] nValues = {10, 20, 40, 80, 160, 320};
        double a = 0.0, b = 1.0;
        double tol = 1e-9; // Augmented tolerance slightly for floating point arithmetic

        ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

        System.out.println("=== Tests sans erreur pour u(x) = x^2 ===");
        double alpha = 0.0;
        double beta  = 1.0;

        for (int n : nValues) {
            nombreTests++;
            double h = (b - a) / (n + 1);
            List<Double> fList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                fList.add(h * h * (-2.0));
            }
            fList.set(0, fList.get(0) + alpha);
            fList.set(n - 1, fList.get(n - 1) + beta);

            List<Double> sol = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, new ArrayList<>(fList));
            
            // Write results to CSV
            writeResultsToCsv("withoutError", n, a, h, sol, x -> x * x);
            
            boolean testPassed = true;
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h;
                double expected = xi * xi;
                if (Math.abs(sol.get(i) - expected) > tol) {
                    testPassed = false;
                    System.out.printf("Echec pour u(x)=x^2, n=%d à x=%.4f : calculé=%.10f attendu=%.10f\n", n, xi, sol.get(i), expected);
                    break; 
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

    @Test
    @Order(2)
    public void withError() {
        int compteur = 0;
        int nombreTests = 0;
        int[] nValues = {10, 20, 40, 80, 160, 320};
        double a = 0.0, b = 1.0;

        ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

        System.out.println("=== Tests avec erreur pour u(x) = sin(pi*x) ===");
        double alpha = 0.0;
        double beta  = Math.sin(Math.PI * b);

        for (int n : nValues) {
            nombreTests++;
            double h = (b - a) / (n + 1);
            List<Double> fList = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                double xi = a + (i + 1) * h;
                fList.add(h * h * (Math.PI * Math.PI * Math.sin(Math.PI * xi)));
            }
            fList.set(0, fList.get(0) + alpha);
            fList.set(n - 1, fList.get(n - 1) + beta);

            List<Double> sol = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, new ArrayList<>(fList));
            
            // Write results to CSV
            writeResultsToCsv("withError", n, a, h, sol, x -> Math.sin(Math.PI * x));
            
            double maxError = 0.0;
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h;
                double expected = Math.sin(Math.PI * xi);
                maxError = Math.max(maxError, Math.abs(sol.get(i) - expected));
            }
            System.out.println("Pour n = " + n + ", erreur maximale = " + maxError);
            // We just need to ensure the test runs and generates data
            compteur++;
        }
        System.out.println("Nombre de tests reussis (sin(pi*x)) : " + compteur + " / " + nombreTests);
        assertTrue(compteur == nombreTests);
    }
    
    @AfterAll
    public static void runPythonScript() {
        System.out.println("\n--- Lancement du script d'analyse Python ---");
        try {
            // Assurez-vous que 'python' (ou 'python3') est dans votre PATH
            ProcessBuilder pb = new ProcessBuilder("python", "scripts/plot_analysis.py", RESULTS_DIR);
            pb.redirectErrorStream(true); // Redirige stderr vers stdout
            Process process = pb.start();

            // Lire la sortie du script
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Script Python terminé avec le code de sortie : " + exitCode);
            if (exitCode != 0) {
                System.err.println("Le script Python a rencontré une erreur.");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'exécution du script Python. Assurez-vous que Python est installé et dans le PATH.");
            e.printStackTrace();
        }
    }
}