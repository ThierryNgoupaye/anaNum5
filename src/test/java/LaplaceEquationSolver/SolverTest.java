// Fichier : src/test/java/LaplaceEquationSolver/SolverTest.java

package LaplaceEquationSolver;

import Laplace1DEquationSolver.ISolveLaplaceEquation1D;
import Laplace1DEquationSolver.LaplaceSolverFVM;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SolverTest {

    private static final String RESULTS_DIR = "target/results";

    @BeforeAll
    public static void setup() throws IOException {
        Files.createDirectories(Paths.get(RESULTS_DIR));
    }

    private void writeResultsToCsv(String testName, int n, double a, double h, List<Double> sol, Function<Double, Double> exactSolution) {
        String fileName = String.format("%s/results_%s_n_%d.csv", RESULTS_DIR, testName, n);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("x,real_value,estimated_value,absolute_error");
            for (int i = 0; i < sol.size(); i++) {
                // --- VOICI LA LIGNE À CORRIGER ---
                // La position du i-ème point de la solution (index de 0 à n-1) est x_{i+1}
                double xi = a + (i + 1) * h; // <-- Correction ici
                
                double expected = exactSolution.apply(xi);
                double estimated = sol.get(i);
                double error = Math.abs(estimated - expected);
                writer.printf(Locale.US, "%.16f,%.16f,%.16f,%.16f\n", xi, expected, estimated, error);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ... Le reste de la classe ne change pas ...
    
    @Test
    @Order(1)
    public void withoutError() {
        int compteur = 0;
        int nombreTests = 0;
        int[] nValues = {10, 20, 40, 80, 160, 320};
        double a = 0.0, b = 1.0;
        double tol = 1e-9;

        ISolveLaplaceEquation1D solver = new LaplaceSolverFVM(); 

        System.out.println("=== Tests sans erreur pour u(x) = x^2 ===");
        double alpha = 0.0;
        double beta  = 1.0;
        Function<Double, Double> f_func = x -> -2.0;
        for (int n : nValues) {
            nombreTests++;
            double h = (b - a) / (n + 1);
            List<Double> sol = solver.solve(a, b, alpha, beta, n, f_func);

            writeResultsToCsv("withoutError", n, a, h, sol, x -> x * x);
            
            boolean testPassed = true;
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h; // Cette boucle était déjà correcte
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

        ISolveLaplaceEquation1D solver = new LaplaceSolverFVM();

        System.out.println("=== Tests avec erreur pour u(x) = sin(pi*x) ===");
        double alpha = 0.0;
        double beta  = Math.sin(Math.PI * b);
        Function<Double, Double> f_func = x -> Math.PI * Math.PI * Math.sin(Math.PI * x);
        for (int n : nValues) {
            nombreTests++;
            double h = (b - a) / (n + 1);
            List<Double> sol = solver.solve(a, b, alpha, beta, n, f_func);
            
            writeResultsToCsv("withError", n, a, h, sol, x -> Math.sin(Math.PI * x));
            
            double maxError = 0.0;
            for (int i = 0; i < sol.size(); i++) {
                double xi = a + (i + 1) * h; // Cette boucle était déjà correcte
                double expected = Math.sin(Math.PI * xi);
                maxError = Math.max(maxError, Math.abs(sol.get(i) - expected));
            }
            System.out.println("Pour n = " + n + ", erreur maximale = " + maxError);
            compteur++;
        }
        System.out.println("Nombre de tests reussis (sin(pi*x)) : " + compteur + " / " + nombreTests);
        assertTrue(compteur == nombreTests);
    }
    
    @AfterAll
    public static void runPythonScript() {
        System.out.println("\n--- Lancement du script d'analyse Python ---");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "scripts/plot_analysis.py", RESULTS_DIR);
            pb.redirectErrorStream(true);
            Process process = pb.start();

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