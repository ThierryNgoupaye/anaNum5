package quadraticEquationsTests;


import org.junit.jupiter.api.Test;

import principalProgramm.JennyProcessor;
import principalProgramm.QuadraticEquationSolver;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraticEquationSolverCombinationTest {

    @Test
    public void testWithGeneratedCombinations() throws Exception {
        // Créer une instance de JennyProcessor
        JennyProcessor jennyProcessor = new JennyProcessor();

        // Générer les combinaisons
        jennyProcessor.run(); // Cela génère les combinaisons et les stocke dans resultMap

        // Récupérer les combinaisons générées
        Map<Integer, List<double[]>> resultMap = jennyProcessor.getResultMap();

        // Itérer sur chaque combinaison
        for (Map.Entry<Integer, List<double[]>> entry : resultMap.entrySet()) {
            List<double[]> combinations = entry.getValue();
            for (double[] coefficients : combinations) {
                double a = coefficients[0];
                double b = coefficients[1];
                double c = coefficients[2];

                // Tester solveQuadraticEquation avec ces coefficients
                try {
                    double[] solutions = QuadraticEquationSolver.solveQuadraticEquation(a, b, c);

                    // Vérifier que les solutions sont cohérentes
                    if (solutions.length == 1) {
                        // Cas d'une solution unique (discriminant proche de 0 ou équation linéaire)
                        double x = solutions[0];
                        assertTrue(isRootValid(a, b, c, x), "La solution ne satisfait pas l'équation.");
                    } else if (solutions.length == 2) {
                        // Cas de deux solutions distinctes
                        double x1 = solutions[0];
                        double x2 = solutions[1];
                        assertTrue(isRootValid(a, b, c, x1), "La première solution ne satisfait pas l'équation.");
                        assertTrue(isRootValid(a, b, c, x2), "La deuxième solution ne satisfait pas l'équation.");
                    }
                } catch (IllegalArgumentException e) {
                    // Vérifier que l'exception est attendue
                    if (a == 0 && b == 0) {
                        assertEquals("Pas de solution : a et b ne peuvent pas être simultanément 0.", e.getMessage());
                    } else if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
                        assertEquals("Les coefficients ne peuvent pas être NaN.", e.getMessage());
                    } else if (Double.isInfinite(a) || Double.isInfinite(b) || Double.isInfinite(c)) {
                        assertEquals("Les coefficients ne peuvent pas être infinis.", e.getMessage());
                    } else {
                        assertEquals("Pas de solution réelle : solutions complexes.", e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Vérifie si une valeur x est une racine valide de l'équation quadratique ax² + bx + c = 0.
     */
    private boolean isRootValid(double a, double b, double c, double x) {
        double result = a * x * x + b * x + c;
        return Math.abs(result) < 1e-10; // Tolérance pour les erreurs d'arrondi
    }
}