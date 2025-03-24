import Interfaces.Equation.ISolveLaplaceEquation1D;
import Laplace1D.LaplaceSolverCDS;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        final ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();

        double a = 0.0, b = 1.0;
        double alpha = 0.0, beta = 1.0;
        int n = 20;
        int j = 0;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  // Nombre de points internes
        double[] Fh = new double[n];  // Vecteur du second membre
        double h = (b - a) / (n + 1); // Pas de discrétisation
        double[] x = new double[n];   // Points internes du maillage


        Function<Double, Double> f = u -> Math.sin(Math.PI * u); // Fonction f(x)
        // Remplissage du maillage et du vecteur Fh
        for (int i = 0; i < n; i++) {
            x[i] = a + (i + 1) * h; // Points internes
            Fh[i] = h * h * f.apply(x[i]); // Valeur de f(x_i) fois h^2
        }

        // Appliquer les conditions aux bords
        Fh[0] += alpha; // Condition u(a) = alpha
        Fh[n - 1] += beta; // Condition u(b) = beta

        Function<Double, Double> testFunction = u -> (1 / (Math.PI*Math.PI))*Math.sin(Math.PI*u) + alpha - (1/(Math.PI*Math.PI)) + (beta - alpha + (1/(Math.PI*Math.PI)))*u;
        double[] test = new double[n];
        for(int k = 0; k < n; ++k){
            test[k] = testFunction.apply(x[k]);
        }

        List<Double> fh = Arrays.stream(Fh).boxed().collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("function.txt"))) {
            for (Double value : test) {
                writer.write(x[j] + " " + value.toString());
                writer.newLine();
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Double> solution = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, fh);

        //Journalisation de la solution dans le fichier result.txt, et des erreurs dans le fichier error.txt
        double[] error = new double[n];
        j = 0;
        try (BufferedWriter solutionWriter = new BufferedWriter(new FileWriter("./log/result.txt"));
             BufferedWriter errorWriter = new BufferedWriter(new FileWriter("./log/error.txt"))) {
            for (Double value : solution) {
                error[j] = Math.abs(test[solution.indexOf(value)] - value);
                solutionWriter.write(x[solution.indexOf(value)] + " " + value);
                solutionWriter.newLine();
                errorWriter.write(x[solution.indexOf(value)] + " " + error[solution.indexOf(value)]);
                errorWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}