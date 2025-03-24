package Interfaces.Error;

import java.util.List;
import java.util.function.Function;

public interface IError {
    void compute(List<Double> solution, Function<Double, Double> testFunction, int n);
}
