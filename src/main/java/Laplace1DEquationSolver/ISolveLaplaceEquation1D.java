package Laplace1DEquationSolver;
import java.util.List;

public interface ISolveLaplaceEquation1D {
    List<Double> solveForValuesWithoutErrors(double a, double b, double alpha, double beta, int n, List<Double> f);
} 
