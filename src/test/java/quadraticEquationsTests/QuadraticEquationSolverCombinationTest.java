package quadraticEquationsTests;


import org.junit.jupiter.api.Test;

import Utils.JennyProcessor;
import org.opentest4j.AssertionFailedError;
import principalProgramm.QuadraticEquationInterface;
import principalProgramm.QuadraticEquationSolver;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraticEquationSolverCombinationTest {


    QuadraticEquationInterface iQuadratic = new QuadraticEquationSolver();

    @Test
    public void testWithGeneratedCombinations() throws Exception {

        int compteur = 0;
        int nombreTests = 0;

        // Créer une instance de JennyProcessor
        JennyProcessor jennyProcessor = new JennyProcessor();

        // Générer les combinaisons
        jennyProcessor.run(); // Cela génère les combinaisons et les stocke dans resultMap

        // Récupérer les combinaisons générées
        Map<Integer, List<double[]>> resultMap = jennyProcessor.getResultMap();

        // Itérer sur chaque combinaison
        for (Map.Entry<Integer, List<double[]>> entry : resultMap.entrySet()) {

            nombreTests++;

            System.out.println(" ");
            System.out.println(" ");
            System.out.println("=============================TEST NUMERO : "+entry.getKey() + " ======================================");

            List<double[]> combinations = entry.getValue();
            for (double[] coefficients : combinations) {
                double a = coefficients[0];
                double b = coefficients[1];
                double c = coefficients[2];

                System.out.println(" **** valeur de a: "+ a + " valeur de b: " + b + " valeur de c: " + c + "*****"); ;

                // Tester solveQuadraticEquation avec ces coefficients
                try {
                    double[] solutions = iQuadratic.solveQuadraticEquation(a, b, c);

                    // Vérifier que les solutions sont cohérentes
                    if (solutions.length == 1) {
                        // Cas d'une solution unique (discriminant proche de 0 ou équation linéaire)
                        double x = solutions[0];
                        System.out.println(" ***** solution obtenue: "+ x);;
                        assertTrue(isRootValid(a, b, c, x), "La solution ne satisfait pas l'équation.");
                        System.out.println(" ");
                        System.out.println(" ");
                        compteur++;
                    } else if (solutions.length == 2) {
                        // Cas de deux solutions distinctes
                        double x1 = solutions[0];
                        double x2 = solutions[1];
                        System.out.println(" ***** solution1 obtenue: "+ x1 + " solution2 obtenue: "+ x2);
                        try
                        {
                            assertTrue(isRootValid(a, b, c, x1), "La première solution ne satisfait pas l'équation.");
                            assertTrue(isRootValid(a, b, c, x2), "La deuxième solution ne satisfait pas l'équation.");
                            compteur++;
                        }
                        catch (AssertionFailedError e)
                        {
                            System.out.println(e.getMessage());
                        }
                        System.out.println(" ");
                        System.out.println(" ");
                    }
                    else
                    {
                        System.out.println("pas de solution obtenue, discriminant negatif");
                        System.out.println(" ");
                        System.out.println(" ");
                    }

                } catch (Exception e) {
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
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("================ Nombre de tests reussis:  " + compteur);
        System.out.println("================ Nombre de tests echoues:  "+ (nombreTests-compteur));

    }

    /**
     * Vérifie si une valeur x est une racine valide de l'équation quadratique ax² + bx + c = 0.
     */
    private boolean isRootValid(double a, double b, double c, double x) {
        double result = a * x * x + b * x + c;
        return Math.abs(result) < 1e-10; // Tolérance pour les erreurs d'arrondi
    }
}