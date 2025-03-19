public class SolveLaplaceEquation1DTest implements ISolveLaplaceEquation1D{


    private final ISolveLaplaceEquation1D solver = new SolveLaplaceEquation1D();
    //Tests avec les fonctions dont on connait les valeurs exactes en tout point
    // u(x) = 1, u(x) = x, u(x) = x².
    @Test
    public List<Double> solveForValuesWithoutErrors(double a, double b, double alpha, double beta, int N, List<Double> f){
        // TODO
        List<Double> solution = solver.solve(a, b, alpha, beta, N, f);
        List<Double> expectedSolution = new ArrayList<>();

        double h = (b - a) / (N + 1);
        for (int i = 1; i <= N; i++) {
            double xi = a + i * h;

            if (Math.abs(f.get(0)) < 1e-9) {
                if (Math.abs(alpha - beta) < 1e-9) {
                    expectedSolution.add(1.0);
                } else {
                    expectedSolution.add(xi);
                }
            } else if (Math.abs(f.get(0) - 2.0) < 1e-9) {
                expectedSolution.add(xi * xi);
            }

        }

        for (int i = 0; i < N; i++) {
            assertTrue(Math.abs(solution.get(i) - expectedSolution.get(i)) < 1e-6,
                    "Erreur trop grande à l'index " + i);
        }

        return solution;
    }

}

// Tests avec les fonctions dont on connait les valeurs approchees en tout point
public List<Double> solveForValuesWithErrors(double a, double b, double alpha, double beta, int N, List<Double> f){
    // TODO
}

}