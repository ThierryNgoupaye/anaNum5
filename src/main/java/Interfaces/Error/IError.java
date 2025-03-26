package Interfaces.Error;

import java.util.List;

public interface IError {
    double globalError(List<Double> solution);
    void localError(List<Double> solution);
}
