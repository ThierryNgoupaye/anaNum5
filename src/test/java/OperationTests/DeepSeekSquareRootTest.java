package OperationTests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeepSeekSquareRootTest {

    @Test
    public void testSquareRootPositive() {
        assertEquals(2.0, Math.sqrt(4.0), 0.0001);
    }

    @Test
    public void testSquareRootNonInteger() {
        assertEquals(1.4142135623730951, Math.sqrt(2.0), 0.0001);
    }

    @Test
    public void testSquareRootVerySmall() {
        assertEquals(0.01, Math.sqrt(0.0001), 0.0001);
    }

    @Test
    public void testSquareRootVeryLarge() {
        assertEquals(100000.0, Math.sqrt(1.0E10), 0.0001);
    }

    @Test
    public void testSquareRootZero() {
        assertEquals(0.0, Math.sqrt(0.0), 0.0001);
    }
}