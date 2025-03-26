import Interfaces.Equation.ISolveLaplaceEquation1D;
import Interfaces.Error.IError;
import Laplace1D.LaplaceSolverCDS;
import Error.Error;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    private static void write_function(Function<Double, Double> test, String path, double lowerbound, double upperbound, int n){
        double x;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < n; ++i) {
                x = lowerbound + (i + 1)*(upperbound - lowerbound)/(n + 1);
                writer.write(x + " " + test.apply(x));
                writer.newLine();
            }
        } catch (IOException e) {
            e.getMessage();
            e.getLocalizedMessage();
        }
    }

    public static void main(String[] args) {

        System.out.println("If you want to use specific paramaters, usage is:\n" +
                "Main <value of lowerbound> <value of upperbound> <number of points> <first initial condition> <second initial condition>" +
                "Default values are:\n" +
                "lowerbound: 0.0\n" +
                "upperbound: 1.0\n" +
                "number of pints: 10000\n" +
                "first condition value: 0.0\n" +
                "second condition value: 1.0\n"
        );

        double a = 0;
        double b = 1.0;
        double alpha = 0.0, beta = 1.0;
        int n = 20;
        if(args.length == 5){
            try{
                a = Double.parseDouble(args[0]);
                b = Double.parseDouble(args[1]);
                n = Integer.parseInt(args[2]);
                alpha = Double.parseDouble(args[3]);
                beta = Double.parseDouble(args[4]);
            } catch (NumberFormatException e){
                System.out.println("Entrez des réels, la virgule étant représenté par un point '.'");
                e.getMessage();
            } catch (Exception e){
                e.getMessage();
            }
        }
        double[] Fh = new double[n];
        double h = (b - a) / (n + 1);
        double[] x = new double[n];
        List<Double> fh;
        List<Double> solution;

        final double finalAlpha = alpha;
        final double finalBeta = beta;
        Function<Double, Double> f = u -> Math.sin(Math.PI * u);
        Function<Double, Double> testFunction = u -> (1 / (Math.PI*Math.PI))*Math.sin(Math.PI*u) + finalAlpha - (1/(Math.PI*Math.PI)) + (finalBeta - finalAlpha + (1/(Math.PI*Math.PI)))*u;
        final ISolveLaplaceEquation1D solver = new LaplaceSolverCDS();
        final IError error =  new Error(a, b, n, testFunction);

        write_function(testFunction, "./log/function.txt", a, b, n);

        for (int i = 0; i < n; i++) {
            x[i] = a + (i + 1) * h;
            Fh[i] = h * h * f.apply(x[i]);
        }
        Fh[0] += alpha; // Condition u(a) = alpha
        Fh[n - 1] += beta; // Condition u(b) = beta
        fh = Arrays.stream(Fh).boxed().collect(Collectors.toList());

        solution = solver.solveForValuesWithoutErrors(a, b, alpha, beta, n, fh);
        error.localError(solution);
        System.out.println("Computation done:)\n" +
                "The global error made by our approximation is " + error.globalError(solution)
        );


    }
}