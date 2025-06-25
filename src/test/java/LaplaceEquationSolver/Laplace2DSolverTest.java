// package LaplaceEquationSolver;

// import Laplace2DEquationSolver.LaplaceSolverRedBlack; // NOUVEAU SOLVEUR
// import org.junit.jupiter.api.*;

// import java.io.*;
// import java.nio.file.*;
// import java.util.*;
// import java.util.function.BiFunction;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class Laplace2DSolverTest {

//     private static final String RESULTS_DIR = "target/results_2d";

//     @BeforeAll
//     public static void setup() throws IOException {
//         Files.createDirectories(Paths.get(RESULTS_DIR));
//     }

//     // La méthode pour écrire en CSV reste la même, mais on va l'adapter un peu
//     private void writeResultsToCsv(String testName, int n, List<Double> sol, BiFunction<Double, Double, Double> exactSolution) {
//         String fileName = String.format("%s/%s_n%d.csv", RESULTS_DIR, testName, n);
//         double h = 1.0 / (n + 1);
//         try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
//             writer.println("x,y,exact,numerical,error"); // Noms de colonnes plus simples
//             for (int j = 0; j < n; j++) {
//                 double y = (j + 1) * h;
//                 for (int i = 0; i < n; i++) {
//                     double x = (i + 1) * h;
//                     int k = j * n + i;
//                     double expected = exactSolution.apply(x, y);
//                     double estimated = sol.get(k);
//                     double error = Math.abs(estimated - expected);
//                     writer.printf(Locale.US, "%.8f,%.8f,%.16f,%.16f,%.16f\n", x, y, expected, estimated, error);
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private void calculateAndPrintConvergenceRates(Map<Integer, Double> errors) {
//         System.out.println("\n--- Calcul de l'ordre de convergence numérique ---");
//         System.out.println("-------------------------------------------------------------------------------------");
//         System.out.printf("%-10s -> %-10s | %-12s | %-12s | %-12s | %-12s | %-10s\n",
//                 "n_1", "n_2", "h_1", "h_2", "Erreur_1", "Erreur_2", "Ordre (p)");
//         System.out.println("-------------------------------------------------------------------------------------");

//         // Utiliser une liste de n pour avoir un accès par index
//         List<Integer> nValues = new ArrayList<>(errors.keySet());
        
//         for (int i = 0; i < nValues.size() - 1; i++) {
//             int n1 = nValues.get(i);
//             int n2 = nValues.get(i + 1);

//             double h1 = 1.0 / (n1 + 1);
//             double h2 = 1.0 / (n2 + 1);

//             double error1 = errors.get(n1);
//             double error2 = errors.get(n2);

//             if (error1 > 1e-15 && error2 > 1e-15) { // Éviter la division par zéro ou des valeurs non significatives
//                 double p = Math.log(error1 / error2) / Math.log(h1 / h2);
//                 System.out.printf("%-10d -> %-10d | %.6e | %.6e | %.6e | %.6e | %-10.4f\n",
//                         n1, n2, h1, h2, error1, error2, p);
//             } else {
//                  System.out.printf("%-10d -> %-10d | %.6e | %.6e | %.6e | %.6e | %-10s\n",
//                         n1, n2, h1, h2, error1, error2, "N/A");
//             }
//         }
//         System.out.println("-------------------------------------------------------------------------------------\n");
//     }

//     @Test
//     @Order(1)
//     public void testUEqualsXSquareYSquare() {
//         System.out.println("\n=== Test 2D avec u(x,y) = x^2 * y^2 ===");
//         int[] nValues = {10, 20, 40, 80};
//         Map<Integer, Double> errors = new LinkedHashMap<>();

//         for (int n : nValues) {
//             // Définition du problème: -Δu = f
//             // u = x²y² -> Δu = 2y² + 2x² -> f = -2(x²+y²)
//             BiFunction<Double, Double, Double> f_func = (x, y) -> -2.0 * (x * x + y * y);
//             BiFunction<Double, Double, Double> boundary_func = (x, y) -> x * x * y * y;
//             BiFunction<Double, Double, Double> exact_sol = (x, y) -> x * x * y * y;

//             LaplaceSolverRedBlack solver = new LaplaceSolverRedBlack(n, f_func, boundary_func);
            
//             double h = 1.0 / (n + 1);
//             double tolerance = h * h; // Tolérance proportionnelle au pas au carré
//             List<Double> result = solver.solve(tolerance, 100000);

//             writeResultsToCsv("quadratic2D_solution", n, result, exact_sol);

//             double maxError = 0;
//             for (int j = 0; j < n; j++) {
//                 double y = (j + 1) * h;
//                 for (int i = 0; i < n; i++) {
//                     double x = (i + 1) * h;
//                     int k = j * n + i;
//                     double expected = exact_sol.apply(x, y);
//                     maxError = Math.max(maxError, Math.abs(result.get(k) - expected));
//                 }
//             }
//             errors.put(n, maxError); // Stocker l'erreur

            
//             System.out.printf(Locale.US, "n=%d: Itérations=%d, Erreur Max=%.4e, Résidu Final=%.4e\n", 
//                 n, solver.getIterations(), maxError, solver.getFinalResidualNorm());
            
//             // L'erreur de troncature est de l'ordre de h², donc l'erreur doit être proche
//             assertTrue(maxError < 10 * h * h, "L'erreur est trop grande pour n=" + n);
//         }
//         calculateAndPrintConvergenceRates(errors);
//     }

//     @Test
//     @Order(2)
//     public void testUEqualsSinPiXSinPiY() {
//         System.out.println("\n=== Test 2D avec u(x,y) = sin(πx)sin(πy) ===");
//         int[] nValues = {10, 20, 40, 80};
//         Map<Integer, Double> errors = new LinkedHashMap<>();

//         for (int n : nValues) {
//             // Définition du problème: -Δu = f
//             // u = sin(πx)sin(πy) -> Δu = -2π²sin(πx)sin(πy) -> f = 2π²sin(πx)sin(πy)
//             BiFunction<Double, Double, Double> f_func = (x, y) -> 2 * Math.PI * Math.PI * Math.sin(Math.PI * x) * Math.sin(Math.PI * y);
//             BiFunction<Double, Double, Double> boundary_func = (x, y) -> 0.0; // "zéro aux frontières"
//             BiFunction<Double, Double, Double> exact_sol = (x, y) -> Math.sin(Math.PI * x) * Math.sin(Math.PI * y);

//             LaplaceSolverRedBlack solver = new LaplaceSolverRedBlack(n, f_func, boundary_func);

//             double h = 1.0 / (n + 1);
//             double tolerance = h * h;
//             List<Double> result = solver.solve(tolerance, 100000);

//             writeResultsToCsv("sin2D_solution", n, result, exact_sol);

//             double maxError = 0;
//             for (int j = 0; j < n; j++) {
//                 double y = (j + 1) * h;
//                 for (int i = 0; i < n; i++) {
//                     double x = (i + 1) * h;
//                     int k = j * n + i;
//                     double expected = exact_sol.apply(x, y);
//                     maxError = Math.max(maxError, Math.abs(result.get(k) - expected));
//                 }
//             }
//             errors.put(n, maxError);
            
//             System.out.printf(Locale.US, "n=%d: Itérations=%d, Erreur Max=%.4e, Résidu Final=%.4e\n", 
//                 n, solver.getIterations(), maxError, solver.getFinalResidualNorm());
                
//             assertTrue(maxError < 10 * h * h, "L'erreur est trop grande pour n=" + n);
//         }
//         calculateAndPrintConvergenceRates(errors);
//     }
    
//     @Test
//     @Order(3)
//     public void testLinearSolution() {
//         System.out.println("\n=== Test 2D avec u(x,y) = 2x + y (f=0) ===");
//         // Test avec le maillage 100x100 demandé, et d'autres pour voir la "convergence"
//         int[] nValues = {10, 20, 50, 100}; 
//         Map<Integer, Double> errors = new LinkedHashMap<>();

//         for (int n : nValues) {
//             // Définition du problème: -Δu = f
//             // u = 2x + y -> Δu = 0 -> f = 0
//             BiFunction<Double, Double, Double> f_func = (x, y) -> 0.0;
//             BiFunction<Double, Double, Double> boundary_func = (x, y) -> 2 * x + y;
//             BiFunction<Double, Double, Double> exact_sol = (x, y) -> 2 * x + y;

//             LaplaceSolverRedBlack solver = new LaplaceSolverRedBlack(n, f_func, boundary_func);
            
//             // Pour une solution exacte, la tolérance peut être beaucoup plus stricte
//             double tolerance = 1e-12; 
//             List<Double> result = solver.solve(tolerance, 100000);

//             writeResultsToCsv("linear2D_solution", n, result, exact_sol);

//             double maxError = 0;
//             for (int j = 0; j < n; j++) {
//                 double y = (j + 1) * (1.0 / (n+1));
//                 for (int i = 0; i < n; i++) {
//                     double x = (i + 1) * (1.0 / (n+1));
//                     int k = j * n + i;
//                     double expected = exact_sol.apply(x, y);
//                     maxError = Math.max(maxError, Math.abs(result.get(k) - expected));
//                 }
//             }
//             errors.put(n, maxError);

//             System.out.printf(Locale.US, "n=%d: Itérations=%d, Erreur Max=%.4e, Résidu Final=%.4e\n", 
//                 n, solver.getIterations(), maxError, solver.getFinalResidualNorm());
            
        
//             assertTrue(maxError < 1e-9, "L'erreur pour la solution linéaire devrait être proche de la précision machine pour n=" + n);
//         }
 
//         System.out.println("Note: L'ordre de convergence pour une solution exacte peut ne pas être significatif.");
//         calculateAndPrintConvergenceRates(errors);
//     }

//     @AfterAll
//     public static void runPlotScript() {
//         System.out.println("\n--- Lancement du script Python d'analyse 2D ---");
//         try {
//             // Utilise le script Python pour générer les visualisations
//             ProcessBuilder pb = new ProcessBuilder("python", "scripts/DifferencesFinis2D.py");
//             pb.redirectErrorStream(true);
//             Process process = pb.start();

//             try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                 String line;
//                 while ((line = reader.readLine()) != null) {
//                     System.out.println(line);
//                 }
//             }

//             int exitCode = process.waitFor();
//             System.out.println("Script terminé avec code : " + exitCode);
//         } catch (IOException | InterruptedException e) {
//             System.err.println("Erreur lors de l'exécution du script Python.");
//             e.printStackTrace();
//         }
//     }
// }
//---> PATH: /c/Users/PC/School/AnaNum/Tp4/src/test/java/LaplaceEquationSolver/Laplace2DSolverTest.java
package LaplaceEquationSolver;

import Laplace2DEquationSolver.LaplaceSolverCDS;
import Laplace2DEquationSolver.LaplaceSolverRedBlack;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Laplace2DSolverTest {

    private static final String RESULTS_DIR = "target/results_2d";

    @BeforeAll
    public static void setup() throws IOException {
        Files.createDirectories(Paths.get(RESULTS_DIR));
    }

    @Test
    @Order(3)
    public void testLinearSolution() {
        System.out.println("\n=== Test 2D avec u(x,y) = 2x + y (f=0) ===");
        int[] nValues = {10, 20, 30, 40, 50, 60, 100};
        Map<Integer, Double> errors = new LinkedHashMap<>();

        BiFunction<Double, Double, Double> f_func = (x, y) -> 0.0;
        BiFunction<Double, Double, Double> boundary_func = (x, y) -> 2 * x + y;
        BiFunction<Double, Double, Double> exact_sol = (x, y) -> 2 * x + y;

        for (int n : nValues) {
            if (n == 3) {
                System.out.printf("\n--- Comparaison des solveurs pour n=%d ---\n", n);
                
                // 1. Résolution avec le nouveau LaplaceSolverCDS.solve3x3
                LaplaceSolverCDS solverCDS = new LaplaceSolverCDS();
                List<Double> resultCDS = solverCDS.solve3x3(n, boundary_func);
                double maxErrorCDS = calculateMaxError(n, resultCDS, exact_sol);
                errors.put(n, maxErrorCDS);
                System.out.printf(Locale.US, "n=%d (LaplaceSolverCDS): Erreur Max = %.4e\n", n, maxErrorCDS);
                writeResultsToCsv("linear2D_solution_CDS", n, resultCDS, exact_sol);
                
                // 2. Résolution avec Gauss-Seidel (Red-Black) pour comparaison
                LaplaceSolverRedBlack solverRB = new LaplaceSolverRedBlack(n, f_func, boundary_func);
                List<Double> resultRB = solverRB.solve(1e-12, 100000);
                double maxErrorRB = calculateMaxError(n, resultRB, exact_sol);
                System.out.printf(Locale.US, "n=%d (Gauss-Seidel RB): Erreur Max = %.4e\n", n, maxErrorRB);
                writeResultsToCsv("linear2D_solution_RedBlack", n, resultRB, exact_sol);

                // 3. Comparaison des deux tableaux de résultats
                double maxDiff = 0;
                for (int k = 0; k < resultCDS.size(); k++) {
                    maxDiff = Math.max(maxDiff, Math.abs(resultCDS.get(k) - resultRB.get(k)));
                }
                System.out.printf(Locale.US, "=> Différence maximale entre les deux solveurs: %.4e\n", maxDiff);
                assertTrue(maxDiff < 1e-9, "Les deux solveurs doivent produire des résultats quasi-identiques.");

            } else {
                System.out.printf("\n--- Lancement de Gauss-Seidel (Red-Black) pour n=%d ---\n", n);
                LaplaceSolverRedBlack solver = new LaplaceSolverRedBlack(n, f_func, boundary_func);
                List<Double> result = solver.solve(1e-12, 100000);
                
                double maxError = calculateMaxError(n, result, exact_sol);
                errors.put(n, maxError);
                System.out.printf(Locale.US, "n=%d (Gauss-Seidel RB): Erreur Max=%.4e, Itérations=%d\n", 
                    n, maxError, solver.getIterations());
                
                writeResultsToCsv("linear2D_solution_RedBlack", n, result, exact_sol);
                assertTrue(maxError < 1e-9, "L'erreur pour la solution linéaire doit être proche de la précision machine.");
            }
        }
        
        System.out.println("\nNote: L'ordre de convergence pour une solution exacte peut ne pas être significatif.");
        calculateAndPrintConvergenceRates(errors);
    }
    
    // La méthode runWithSolverCDS n'est plus nécessaire et peut être supprimée
    
    private double calculateMaxError(int n, List<Double> solution, BiFunction<Double, Double, Double> exact_sol) {
        double maxError = 0;
        double h = 1.0 / (n + 1);
        for (int j = 0; j < n; j++) {
            double y = (j + 1) * h;
            for (int i = 0; i < n; i++) {
                double x = (i + 1) * h;
                int k = j * n + i;
                double expected = exact_sol.apply(x, y);
                maxError = Math.max(maxError, Math.abs(solution.get(k) - expected));
            }
        }
        return maxError;
    }

    private void writeResultsToCsv(String testName, int n, List<Double> sol, BiFunction<Double, Double, Double> exactSolution) {
        String fileName = String.format("%s/%s_n%d.csv", RESULTS_DIR, testName, n);
        double h = 1.0 / (n + 1);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("x,y,exact,numerical,error");
            for (int j = 0; j < n; j++) {
                double y = (j + 1) * h;
                for (int i = 0; i < n; i++) {
                    double x = (i + 1) * h;
                    int k = j * n + i;
                    double expected = exactSolution.apply(x, y);
                    double estimated = sol.get(k);
                    double error = Math.abs(estimated - expected);
                    writer.printf(Locale.US, "%.8f,%.8f,%.16f,%.16f,%.16f\n", x, y, expected, estimated, error);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void calculateAndPrintConvergenceRates(Map<Integer, Double> errors) {
        System.out.println("\n--- Calcul de l'ordre de convergence numérique ---");
        System.out.printf("%-10s -> %-10s | %-12s | %-12s | %-12s | %-12s | %-10s\n", "n_1", "n_2", "h_1", "h_2", "Erreur_1", "Erreur_2", "Ordre (p)");
        System.out.println("-------------------------------------------------------------------------------------");
        List<Integer> nValues = new ArrayList<>(errors.keySet());
        Collections.sort(nValues);
        for (int i = 0; i < nValues.size() - 1; i++) {
            int n1 = nValues.get(i); int n2 = nValues.get(i + 1);
            double h1 = 1.0 / (n1 + 1); double h2 = 1.0 / (n2 + 1);
            double error1 = errors.get(n1); double error2 = errors.get(n2);
            if (error1 > 1e-15 && error2 > 1e-15) {
                double p = Math.log(error1 / error2) / Math.log(h1 / h2);
                System.out.printf(Locale.US, "%-10d -> %-10d | %.6e | %.6e | %.6e | %.6e | %-10.4f\n", n1, n2, h1, h2, error1, error2, -p);
            } else {
                 System.out.printf(Locale.US, "%-10d -> %-10d | %.6e | %.6e | %.6e | %.6e | %-10s\n", n1, n2, h1, h2, error1, error2, "N/A");
            }
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }

    @AfterAll
    public static void runPlotScript() {
        System.out.println("\n--- Lancement du script Python d'analyse 2D ---");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "scripts/DifferencesFinis2D.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                 System.err.println("Le script Python a terminé avec une erreur (code: " + exitCode + ").");
            } else {
                System.out.println("Script terminé avec succès.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'exécution du script Python.");
            e.printStackTrace();
        }
    }
}