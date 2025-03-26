package Error;

import Interfaces.Error.IError;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import polynomial.Legendre;

public class Error implements IError{

    private final int degree;
    private final double lowerBound;
    private final double upperBound;
    private final double[] legendreRoots;
    private final double[] legendreWeights;
    private final Function<Double, Double> testFunction;

    /**
     * Constructs an Error object.
     *
     * @param lowerBound  the lower bound of the interval
     * @param upperBound  the upper bound of the interval
     * @param degree      the degree of the polynomial
     * @param testFunction           the function to interpolate
     */
    public Error(double lowerBound, double upperBound, int degree, Function<Double, Double> testFunction) {
        this.testFunction = testFunction;
        this.degree = degree;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        Legendre legendre = new Legendre(degree);
        this.legendreRoots = legendre.getRoots();
        this.legendreWeights = legendre.getWeight(this.legendreRoots);
    }
    @Override
    public double globalError(List<Double> solution) {
        double sum = .0;
        double m = (lowerBound + upperBound) / 2.;
        double sr = (upperBound - lowerBound) / 2.;
        for (int  i = 0; i < legendreRoots.length; ++i) {
            double toEval = sr*legendreRoots[i] + m;
            sum += legendreWeights[i]*Math.pow(testFunction.apply(toEval) - solution.get(i), 2.);
        }

        return  Math.sqrt(sr*sum);
    }

    public void localError(List<Double> solution){
        double error;
        double x;
        int i;
        try (BufferedWriter errorWriter = new BufferedWriter(new FileWriter("../log/error.txt"))) {
            for (Double value : solution) {
                i = solution.indexOf(value) + 1;
                x = lowerBound + i*(upperBound - lowerBound)/(degree + 1);
                error = Math.abs(testFunction.apply(x) - value);
                errorWriter.write(x + " " + error);
                errorWriter.newLine();
            }
        } catch (IOException e) {
            e.getMessage();
            e.getLocalizedMessage();
        }
    }



}
