package LaplaceEquationSolver;

import Laplace2DEquationSolver.LaplaceSolverRedBlack; // NOUVEAU SOLVEUR
import Laplace2DEquationSolver.LaplaceSolverAnisotropicFVM; // NOUVEAU SOLVEUR ANISOTROPE
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

    private void writeResultsToCsvRectangularMesh(String testName, int n, int m, List<Double> sol, BiFunction<Double, Double, Double> exactSolution) {
        String fileName = String.format("%s/%s_n%d_m%d.csv", RESULTS_DIR, testName, n, m);
        double hx = 1.0 / (n + 1);
        double hy = 1.0 / (m + 1);
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("x,y,exact,numerical,error");
            for (int j = 0; j < m; j++) { // Boucle sur m (coordonnée y)
                double y = (j + 1) * hy;
                for (int i = 0; i < n; i++) { // Boucle sur n (coordonnée x)
                    double x = (i + 1) * hx;
                    int k = j * n + i; // Index dans la liste aplatie
                    if (k >= sol.size()) continue; // Sécurité
                    
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
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-10s -> %-10s | %-12s | %-12s | %-12s | %-12s | %-10s\n",
                "n_1", "n_2", "h_1", "h_2", "Erreur_1", "Erreur_2", "Ordre (p)");
        System.out.println("-------------------------------------------------------------------------------------");

        // Utiliser une liste de n pour avoir un accès par index
        List<Integer> nValues = new ArrayList<>(errors.keySet());
        
        for (int i = 0; i < nValues.size() - 1; i++) {
            int n1 = nValues.get(i);
            int n2 = nValues.get(i + 1);

            double h1 = 1.0 / (n1 + 1);
            double h2 = 1.0 / (n2 + 1);

            double error1 = errors.get(n1);
            double error2 = errors.get(n2);

            if (error1 > 0 && error2 > 0) {
                double p = Math.log(error1 / error2) / Math.log(h1 / h2);
                System.out.printf("%-10d -> %-10d | %.6e | %.6e | %.6e | %.6e | %-10.4f\n",
                        n1, n2, h1, h2, error1, error2, p);
            }
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
    }

    @Test
    @Order(1)
    public void testUEqualsXSquareYSquare() {
        System.out.println("\n=== Test 2D avec u(x,y) = x^2 * y^2 ===");
        int[] nValues = {10, 20, 40, 80, 160, 320};
        Map<Integer, Double> errors = new LinkedHashMap<>();

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
            errors.put(n, maxError); // Stocker l'erreur

            
            System.out.printf(Locale.US, "n=%d: Itérations=%d, Erreur Max=%.4e, Résidu Final=%.4e\n", 
                n, solver.getIterations(), maxError, solver.getFinalResidualNorm());
            
            // L'erreur de troncature est de l'ordre de h², donc l'erreur doit être proche
            assertTrue(maxError < 10 * h * h, "L'erreur est trop grande pour n=" + n);
        }
        calculateAndPrintConvergenceRates(errors);
    }

    @Test
    @Order(2)
    public void testUEqualsSinPiXSinPiY() {
        System.out.println("\n=== Test 2D avec u(x,y) = sin(πx)sin(πy) ===");
        int[] nValues = {10, 20, 40, 80, 160, 320};
        Map<Integer, Double> errors = new LinkedHashMap<>();

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
        calculateAndPrintConvergenceRates(errors);
    }

    // Dans la classe Laplace2DSolverTest

    @Test
    @Order(5) // S'exécute en dernier
    public void testAnisotropicConvergenceRate() {
        System.out.println("\n=== Analyse de Convergence Anisotrope avec u(x,y) = x^2 * sin(πy) ===");
        
        BiFunction<Double, Double, Double> f_func = (x, y) -> (Math.PI * Math.PI * x * x - 2.0) * Math.sin(Math.PI * y);
        BiFunction<Double, Double, Double> boundary_func = (x, y) -> x * x * Math.sin(Math.PI * y);
        BiFunction<Double, Double, Double> exact_sol = (x, y) -> x * x * Math.sin(Math.PI * y);

        // On définit un rapport d'aspect, ici m = 2n
        // Et on double la résolution à chaque étape
        int[][] testSizes = {
            {10, 20},
            {20, 40},
            {40, 80},
            {80, 160} // Optionnel, peut être très long
        };
        
        // On utilise un Map pour stocker les erreurs par rapport à n (la dimension de base)
        Map<Integer, Double> errors = new LinkedHashMap<>();
        String testName = "AnisotropicConvergence_m_eq_2n"; // Nom de base pour les fichiers

        for (int[] size : testSizes) {
            int n = size[0];
            int m = size[1];
            
            System.out.printf("\n--- Test pour la taille n=%d, m=%d ---\n", n, m);

            LaplaceSolverAnisotropicFVM solver = new LaplaceSolverAnisotropicFVM(n, m, f_func, boundary_func);

            double hx = 1.0 / (n + 1);
            double hy = 1.0 / (m + 1);
            double h_min = Math.min(hx, hy);
            double tolerance = h_min * h_min * 1e-3;
            
            List<Double> result = solver.solve(tolerance, 400000); // Augmenter max iter pour les grilles fines

            // Sauvegarde avec un nom de fichier qui inclut n et m
            writeResultsToCsvRectangularMesh(testName, n, m, result, exact_sol);

            double maxError = 0.0;
            for (int j = 0; j < m; j++) {
                double y = (j + 1) * hy;
                for (int i = 0; i < n; i++) {
                    double x = (i + 1) * hx;
                    int k = j * n + i;
                    if (k >= result.size()) continue;
                    maxError = Math.max(maxError, Math.abs(result.get(k) - exact_sol.apply(x, y)));
                }
            }
            
            // On stocke l'erreur par rapport à n
            errors.put(n, maxError);

            System.out.printf(Locale.US, "Taille (n,m): (%d, %d) | Itérations: %d | Erreur Max: %.4e\n", 
                n, m, solver.getIterations(), maxError);
        }
        
        // --- Calcul de la vitesse de convergence ---
        // On la calcule par rapport à hx (puisque hy est proportionnel)
        System.out.println("\n--- Calcul de l'ordre de convergence (base n) ---");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.printf("%-10s -> %-10s | %-12s | %-12s | %-12s | %-12s | %-10s\n",
                "n_1", "n_2", "hx_1", "hx_2", "Erreur_1", "Erreur_2", "Ordre (p)");
        System.out.println("-------------------------------------------------------------------------------------");

        List<Integer> nValues = new ArrayList<>(errors.keySet());
        for (int i = 0; i < nValues.size() - 1; i++) {
            int n1 = nValues.get(i);
            int n2 = nValues.get(i + 1);

            double hx1 = 1.0 / (n1 + 1);
            double hx2 = 1.0 / (n2 + 1);

            double error1 = errors.get(n1);
            double error2 = errors.get(n2);

            if (error1 > 0 && error2 > 0) {
                double p = Math.log(error1 / error2) / Math.log(hx1 / hx2);
                System.out.printf("%-10d -> %-10d | %.6e | %.6e | %.6e | %.6e | %-10.4f\n",
                        n1, n2, hx1, hx2, error1, error2, p);
            }
        }
        System.out.println("-------------------------------------------------------------------------------------\n");
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