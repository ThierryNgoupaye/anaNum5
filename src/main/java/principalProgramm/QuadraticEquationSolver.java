package principalProgramm;

public class QuadraticEquationSolver implements QuadraticEquationInterface {

    @Override
    public double[] solveQuadraticEquation(double a, double b, double c) {
        // Vérification des cas invalides
        if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
            throw new IllegalArgumentException("Les coefficients ne peuvent pas être NaN.");
        }
        if (Double.isInfinite(a) || Double.isInfinite(b) || Double.isInfinite(c)) {
            throw new IllegalArgumentException("Les coefficients ne peuvent pas être infinis.");
        }
        // Cas d'une équation linéaire (a = 0)
        if (a == 0) {
            if (b == 0) {
                throw new IllegalArgumentException("Pas de solution : a et b ne peuvent pas être simultanément 0.");
            }
            return new double[]{-c / b}; // Une solution unique
        }
        // Calcul du discriminant Δ = b² - 4ac
        double discriminant = b * b - 4 * a * c;
        // Gestion des problèmes numériques dus aux erreurs d'arrondi
        if (Math.abs(discriminant) < 1e-20) { // Discriminant proche de 0
            double root = -b / (2 * a);
            return new double[]{root}; // Racine double
        }
        if (discriminant > 0) {
            double sqrtDiscriminant = Math.sqrt(discriminant);
            double root1 = (-b + sqrtDiscriminant) / (2 * a);
            double root2 = (-b - sqrtDiscriminant) / (2 * a);
            return new double[]{root1, root2}; // Deux solutions réelles distinctes
        } else {
            throw new IllegalArgumentException("Pas de solution réelle : solutions complexes.");
        }
    }
}