// package LaplaceEquationSolver;

// import Laplace2DEquationSolver.LaplaceSolverCDS;
// import Laplace2DEquationSolver.ISolveLaplaceEquation2D;
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

//     private void writeResultsToCsv(String testName, int n, double a, double h, List<Double> sol, BiFunction<Double, Double, Double> exactSolution) {
//         String fileName = String.format("%s/results_%s_n_%d.csv", RESULTS_DIR, testName, n);
//         try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
//             writer.println("x,y,real_value,estimated_value,absolute_error");
//             for (int j = 0; j < n; j++) {
//                 double y = a + (j + 1) * h;
//                 for (int i = 0; i < n; i++) {
//                     double x = a + (i + 1) * h;
//                     int k = j * n + i;
//                     double expected = exactSolution.apply(x, y);
//                     double estimated = sol.get(k);
//                     double error = Math.abs(estimated - expected);
//                     writer.printf(Locale.US, "%.6f,%.6f,%.16f,%.16f,%.16f\n", x, y, expected, estimated, error);
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     @Test
//     @Order(1)
//     public void testUEqualsXSquarePlusYSquare() {
//         int compteur = 0;
//         int total = 0;
//         int[] nValues = {10, 20, 40, 80, 160, 320};
//         double a = 0.0, b = 1.0;

//         ISolveLaplaceEquation2D solver = new LaplaceSolverCDS();
//         System.out.println("=== Test 2D avec u(x,y) = x^2 + y^2 ===");

//         for (int n : nValues) {
//             total++;
//             double h = (b - a) / (n + 1);
//             double tol = 1.0 / ((n + 1) * (n + 1));
//             List<Double> fList = new ArrayList<>();
//             for (int j = 1; j <= n; j++) {
//                 double y = a + j * h;
//                 for (int i = 1; i <= n; i++) {
//                     double x = a + i * h;
//                     fList.add(-4.0); // Laplacien de x² + y² = 2 + 2 = 4
//                 }
//             }

//             List<Double> result = solver.solveForValuesWithoutErrors(a, b, 0.0, 0.0, n, fList);
//             writeResultsToCsv("x2_y2", n, a, h, result, (x, y) -> x * x + y * y);

//             double maxError = 0;
//             for (int j = 0; j < n; j++) {
//                 double y = a + (j + 1) * h;
//                 for (int i = 0; i < n; i++) {
//                     double x = a + (i + 1) * h;
//                     int k = j * n + i;
//                     double expected = x * x + y * y;
//                     double err = Math.abs(result.get(k) - expected);
//                     maxError = Math.max(maxError, err);
//                 }
//             }

//             if (maxError < tol) {
//                 System.out.println("Test réussi pour n = " + n + ", erreur max = " + maxError);
//                 compteur++;
//             } else {
//                 System.out.println("Échec pour n = " + n + ", erreur max = " + maxError);
//             }
//         }

//         System.out.println("Tests réussis : " + compteur + " / " + total);
//         assertTrue(compteur == total);
//     }

//     @Test
//     @Order(2)
//     public void testUEqualsSinPiXSinPiY() {
//         int compteur = 0;
//         int total = 0;
//         int[] nValues = {10, 20, 40, 80, 160, 320};
//         double a = 0.0, b = 1.0;
        

//         ISolveLaplaceEquation2D solver = new LaplaceSolverCDS();
//         System.out.println("=== Test 2D avec u(x,y) = sin(pi x) sin(pi y) ===");

//         for (int n : nValues) {
//             total++;
//             double h = (b - a) / (n + 1);
//             List<Double> fList = new ArrayList<>();
//             double tol = 1.0 / ((n + 1) * (n + 1));


//             // Construire f selon -Δu = 2*pi^2*sin(pi x)*sin(pi y)
//             for (int j = 1; j <= n; j++) {
//                 double y = a + j * h;
//                 for (int i = 1; i <= n; i++) {
//                     double x = a + i * h;
//                     double val = 2 * Math.PI * Math.PI * Math.sin(Math.PI * x) * Math.sin(Math.PI * y);
//                     fList.add(val);
//                 }
//             }

//             // Conditions aux bords homogènes u=0 sur le bord
//             List<Double> result = solver.solveForValuesWithoutErrors(a, b, 0.0, 0.0, n, fList);

//             // Écrire les résultats dans un CSV (si tu as la méthode writeResultsToCsv)
//             writeResultsToCsv("sin_pi_x_sin_pi_y", n, a, h, result,
//                 (x, y) -> Math.sin(Math.PI * x) * Math.sin(Math.PI * y));

//             double maxError = 0;
//             for (int j = 0; j < n; j++) {
//                 double y = a + (j + 1) * h;
//                 for (int i = 0; i < n; i++) {
//                     double x = a + (i + 1) * h;
//                     int k = j * n + i;
//                     double expected = Math.sin(Math.PI * x) * Math.sin(Math.PI * y);
//                     double err = Math.abs(result.get(k) - expected);
//                     maxError = Math.max(maxError, err);
//                 }
//             }

//             if (maxError < tol) {
//                 System.out.println("Test réussi pour n = " + n + ", erreur max = " + maxError);
//                 compteur++;
//             } else {
//                 System.out.println("Échec pour n = " + n + ", erreur max = " + maxError);
//             }
//         }

//         System.out.println("Tests réussis : " + compteur + " / " + total);
//         assertTrue(compteur == total, "Certains tests ont échoué.");
//     }


//     @AfterAll
//     public static void runPlotScript() {
//         System.out.println("\n--- Lancement du script Python d'analyse 2D ---");
//         try {
//             ProcessBuilder pb = new ProcessBuilder("python", "scripts/plot_analysis.py", RESULTS_DIR);
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
//             System.err.println("Erreur lors de l'exécution du script.");
//             e.printStackTrace();
//         }
//     }
// }




package LaplaceEquationSolver;

import Laplace2DEquationSolver.LaplaceSolverRedBlack; // NOUVEAU SOLVEUR
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

    // La méthode pour écrire en CSV reste la même, mais on va l'adapter un peu
    private void writeResultsToCsv(String testName, int n, List<Double> sol, BiFunction<Double, Double, Double> exactSolution) {
        String fileName = String.format("%s/%s_n%d.csv", RESULTS_DIR, testName, n);
        double h = 1.0 / (n + 1);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("x,y,exact,numerical,error"); // Noms de colonnes plus simples
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

    @Test
    @Order(1)
    public void testUEqualsXSquareYSquare() {
        System.out.println("\n=== Test 2D avec u(x,y) = x^2 * y^2 ===");
        int[] nValues = {10, 20, 40, 80, 160, 320};
        
        for (int n : nValues) {
            // Définition du problème: -Δu = f
            // u = x²y² -> Δu = 2y² + 2x² -> f = -2(x²+y²)
            BiFunction<Double, Double, Double> f_func = (x, y) -> -2.0 * (x * x + y * y);
            BiFunction<Double, Double, Double> boundary_func = (x, y) -> x * x * y * y;
            BiFunction<Double, Double, Double> exact_sol = (x, y) -> x * x * y * y;

            LaplaceSolverRedBlack solver = new LaplaceSolverRedBlack(n, f_func, boundary_func);
            
            double h = 1.0 / (n + 1);
            double tolerance = h * h; // Tolérance proportionnelle au pas au carré
            List<Double> result = solver.solve(tolerance, 100000);

            writeResultsToCsv("quadratic2D_solution", n, result, exact_sol);

            double maxError = 0;
            for (int j = 0; j < n; j++) {
                double y = (j + 1) * h;
                for (int i = 0; i < n; i++) {
                    double x = (i + 1) * h;
                    int k = j * n + i;
                    double expected = exact_sol.apply(x, y);
                    maxError = Math.max(maxError, Math.abs(result.get(k) - expected));
                }
            }
            
            System.out.printf(Locale.US, "n=%d: Itérations=%d, Erreur Max=%.4e, Résidu Final=%.4e\n", 
                n, solver.getIterations(), maxError, solver.getFinalResidualNorm());
            
            // L'erreur de troncature est de l'ordre de h², donc l'erreur doit être proche
            assertTrue(maxError < 10 * h * h, "L'erreur est trop grande pour n=" + n);
        }
    }

    @Test
    @Order(2)
    public void testUEqualsSinPiXSinPiY() {
        System.out.println("\n=== Test 2D avec u(x,y) = sin(πx)sin(πy) ===");
        int[] nValues = {10, 20, 40, 80, 160, 320};

        for (int n : nValues) {
            // Définition du problème: -Δu = f
            // u = sin(πx)sin(πy) -> Δu = -2π²sin(πx)sin(πy) -> f = 2π²sin(πx)sin(πy)
            BiFunction<Double, Double, Double> f_func = (x, y) -> 2 * Math.PI * Math.PI * Math.sin(Math.PI * x) * Math.sin(Math.PI * y);
            BiFunction<Double, Double, Double> boundary_func = (x, y) -> 0.0; // "zéro aux frontières"
            BiFunction<Double, Double, Double> exact_sol = (x, y) -> Math.sin(Math.PI * x) * Math.sin(Math.PI * y);

            LaplaceSolverRedBlack solver = new LaplaceSolverRedBlack(n, f_func, boundary_func);

            double h = 1.0 / (n + 1);
            double tolerance = h * h;
            List<Double> result = solver.solve(tolerance, 100000);

            writeResultsToCsv("sin2D_solution", n, result, exact_sol);

            double maxError = 0;
            for (int j = 0; j < n; j++) {
                double y = (j + 1) * h;
                for (int i = 0; i < n; i++) {
                    double x = (i + 1) * h;
                    int k = j * n + i;
                    double expected = exact_sol.apply(x, y);
                    maxError = Math.max(maxError, Math.abs(result.get(k) - expected));
                }
            }
            
            System.out.printf(Locale.US, "n=%d: Itérations=%d, Erreur Max=%.4e, Résidu Final=%.4e\n", 
                n, solver.getIterations(), maxError, solver.getFinalResidualNorm());
                
            assertTrue(maxError < 10 * h * h, "L'erreur est trop grande pour n=" + n);
        }
    }
    
    @AfterAll
    public static void runPlotScript() {
        System.out.println("\n--- Lancement du script Python d'analyse 2D ---");
        try {
            // Utilisez le script plus complet que je fournis ci-dessous
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
            System.out.println("Script terminé avec code : " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'exécution du script.");
            e.printStackTrace();
        }
    }
}