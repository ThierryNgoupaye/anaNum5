package OperationTests;

import Operations.Operations;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class DeepSeekQuadraticEquationTest {

    private final Operations op = new Operations();

    @Test
    public void testTwoRealSolutions() {
        List<Double> solutions = op.deepSeekSolveQuadraticEquation(1.0, -3.0, 2.0);
        assertArrayEquals(new Double[]{2.0, 1.0}, solutions.toArray());
    }

    @Test
    public void testOneRealSolution() {
        List<Double> solutions = op.deepSeekSolveQuadraticEquation(1.0, 2.0, 1.0);
        assertArrayEquals(new Double[]{-1.0}, solutions.toArray());
    }

    @Test
    public void testNoRealSolution() {
        List<Double> solutions = op.deepSeekSolveQuadraticEquation(1.0, 0.0, 1.0);
        assertTrue(solutions.isEmpty());
    }

    @Test
    public void testLinearEquation() {
        List<Double> solutions = op.deepSeekSolveQuadraticEquation(0.0, 2.0, -4.0);
        assertArrayEquals(new Double[]{2.0}, solutions.toArray());
    }

    @Test
    public void testIndeterminateEquation() {
        List<Double> solutions = op.deepSeekSolveQuadraticEquation(0.0, 0.0, 0.0);
        assertTrue(solutions.isEmpty());
    }


}