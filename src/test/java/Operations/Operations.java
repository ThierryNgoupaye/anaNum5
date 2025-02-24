package Operations;

import java.util.List;

public class Operations {



    public Operations(){}


    public double[] geminiSolveQuadraticEquation(double a, double b, double c) throws Exception {
        if (a == 0)
        { // Équation linéaire
            if (b==0)
            {
                throw new Exception("b cannot be null for this equation");
            }
            else
            {
                double x = -c / b;
                System.out.println("valeur de" +x);
                return new double[] {x};
            }

        } else {
            double delta = b * b - 4 * a * c;
            if (delta > 0) {
                double x1 = (-b + Math.sqrt(delta)) / (2 * a);
                double x2 = (-b - Math.sqrt(delta)) / (2 * a);
                return new double[] { x1, x2 };
            } else if (delta == 0) {
                double x = -b / (2 * a);
                return new double[] { x, x };
            }
            else {
                return null; // Pas de solution réelle
            }
        }
    }

    public List<Double> deepSeekSolveQuadraticEquation(double a, double b, double c) {
        if (a == 0) {
            if (b == 0) {
                return List.of(); // Indeterminate or no solution
            } else {
                return List.of(-c / b);
            }
        }

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return List.of(); // No real solution
        } else if (discriminant == 0) {
            return List.of(-b / (2 * a));
        } else {
            double sqrtDisc = Math.sqrt(discriminant);
            return List.of((-b + sqrtDisc) / (2 * a), (-b - sqrtDisc) / (2 * a));
        }
    }
}
