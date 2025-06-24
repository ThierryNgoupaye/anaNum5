package Laplace2DEquationSolver;

import java.util.List;

public interface ISolveLaplaceEquation2D {
    List<Double> solveForValuesWithoutErrors(double a, double b, double alpha, double beta, int n, List<Double> f);
}
