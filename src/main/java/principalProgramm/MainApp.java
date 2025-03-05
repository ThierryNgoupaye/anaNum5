package principalProgramm;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainApp {
    public static void main(String[] args) {
        JennyProcessor processor = new JennyProcessor();
        try {
            // Lire et traiter les combinaisons
            Map<Integer, List<double[]>> resultMap = processor.getResultMap();

            // Résoudre les équations quadratiques et afficher les solutions
            for (Map.Entry<Integer, List<double[]>> entry : resultMap.entrySet()) {
                int index = entry.getKey();
                List<double[]> triplets = entry.getValue();

                for (double[] triplet : triplets) {
                    double a = triplet[0];
                    double b = triplet[1];
                    double c = triplet[2];

                    try {
                        double[] solutions = QuadraticEquationSolver.solveQuadraticEquation(a, b, c);
                        System.out.print("Index " + index + ": Solutions = ");
                        for (double solution : solutions) {
                            System.out.print(solution + " ");
                        }
                        System.out.println();
                    } catch (IllegalArgumentException e) {
                        System.err.println("Index " + index + ": " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
