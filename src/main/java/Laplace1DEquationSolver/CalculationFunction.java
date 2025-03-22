package Laplace1DEquationSolver;
import java.util.List;

public interface CalculationFunction {
    List<Double> solve(int N, List<Double> f);
} 
