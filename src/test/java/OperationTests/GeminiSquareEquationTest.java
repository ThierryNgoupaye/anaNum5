package OperationTests;

import Operations.Operations;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GeminiSquareEquationTest {

    public Operations op = new Operations();


    @Test
    public void testDeuxSolutionsReelles() throws Exception {
        double[] solutions = op.geminiSolveQuadraticEquation(1, 0, -4);
        assertEquals(2.0, solutions[0], 0.0001);
        assertEquals(-2.0, solutions[1], 0.0001);
    }

    @Test
    public void testSolutionReelleDouble() throws Exception {
        double[] solutions = op.geminiSolveQuadraticEquation(1, 2, 1);
        assertEquals(-1.0, solutions[0], 0.0001);
        assertEquals(-1.0, solutions[1], 0.0001);
    }

    @Test
    public void testPasDeSolutionReelle() throws Exception {
        double[] solutions = op.geminiSolveQuadraticEquation(1, 1, 1);
        assertNull(solutions); // Vérifie si le résultat est null
    }

    @Test
    public void testEquationLineaire() throws Exception {
        double[] solutions = op.geminiSolveQuadraticEquation(0, 2, -4);
        assertEquals(2.0, solutions[0], 0.0001);
    }
}

