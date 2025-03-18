import java.util.List;

public interface ISolveLaplaceEquation1D {

    public List<Double> solve(double a, double b, double alpha, double beta, int N, List<Double> f);
}
