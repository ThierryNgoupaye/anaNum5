package Laplace1DEquationSolver;
import java.util.List;
import java.util.function.Function; // Importer Function

public interface ISolveLaplaceEquation1D {
    List<Double> solve(double a, double b, double alpha, double beta, int n, Function<Double, Double> f_func);
}