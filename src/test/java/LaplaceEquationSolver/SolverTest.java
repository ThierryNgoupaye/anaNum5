package LaplaceEquationSolver;

import Laplace1DEquationSolver.CalculationFunction;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;


public class SolverTest {
    
    private int[] N = {10, 20, 40, 80, 160, 320};
    private CalculationFunction solver;
    private Utils utils = new Utils();

    public SolverTest(CalculationFunction solver) {
        this.solver = solver;
    }

    public void withoutError() {
        
    }
    public void withError() {
        for (int n : N) {
            double h = 1.0 / (n + 1);
            List<Double> f = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                double x = (i + 1) * h; // Valeurs dans [0,1]
                f.add(utils.sin(x));
            }
            
            List<Double> Uh = solver.solve(n, f);


            //Calcul de l'erreur maximale Eh = Sup |AhUh - f|
            double maxError = 0.0;
            for (int i = 1; i < n - 1; i++) {
                double AhUh = (Uh.get(i - 1) - 2 * Uh.get(i) + Uh.get(i + 1)) / (h * h);
                double error = Math.abs(AhUh - f.get(i));
                if (error > maxError) {
                    maxError = error;
                }
            }
            
            System.out.println("Erreur maximale (Eh) pour N=" + n + " : " + maxError);
            assertTrue("L'erreur doit être contrôlée pour N=" + n, maxError < 1e-2);
        }
    }

}

