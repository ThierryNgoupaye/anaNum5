package OperationTests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SquareRootTest {

    @Test
    public void testRacineCarreePositive() {
        assertEquals(2.0, Math.sqrt(4.0), 0.0001); // Marge d'erreur pour les nombres à virgule flottante
        assertEquals(1.41421356, Math.sqrt(2.0), 0.0001);
    }

    @Test
    public void testRacineCarreeZero() {
        assertEquals(0.0, Math.sqrt(0.0), 0.0001);
    }

    @Test
    public void testRacineCarreeUn() {
        assertEquals(1.0, Math.sqrt(1.0), 0.0001);
    }

    @Test
    public void testRacineCarreeNegative() {
        assertTrue(Double.isNaN(Math.sqrt(-1.0))); // Vérifie si le résultat est NaN
    }


}
