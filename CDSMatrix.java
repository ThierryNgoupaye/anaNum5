import java.util.HashMap;
import java.util.Map;

public class CDSMatrix {
    private final int size;
    // Map: 
    // clé = indice de la  diagonale (exemple : -1, 0, 1), 
    // valeur = tableau des éléments
    private final Map<Integer, double[]> diagonals;

    public CDSMatrix(int size) {
        this.size = size;
        this.diagonals = new HashMap<>();
    }

    // Ajouter une diagonale
    public void addDiagonal(int k) {
        if (!diagonals.containsKey(k)) {
            int length = size - Math.abs(k);
            if (length > 0) {
                diagonals.put(k, new double[length]);
            }
        }
    }

    public double get(int i, int j) {
        int k = j - i;
        if (!diagonals.containsKey(k)) return 0.0;

        int index = k >= 0 ? i : j;
        return diagonals.get(k)[index];
    }

    public void set(int i, int j, double val) {
        int k = j - i;
        if (!diagonals.containsKey(k)) {
            throw new IllegalArgumentException("La diagonale " + k + " n'est pas stockée.");
        }
        int index = k >= 0 ? i : j;
        diagonals.get(k)[index] = val;
    }

    public double[] getDiagonal(int k) {
        return diagonals.getOrDefault(k, null);
    }

    public double[] multiply(double[] vector) {
        if (vector.length != size)
            throw new IllegalArgumentException("Taille incompatible pour le produit matrice-vecteur.");

        double[] result = new double[size];
        for (Map.Entry<Integer, double[]> entry : diagonals.entrySet()) {
            int k = entry.getKey();
            double[] diag = entry.getValue();
            for (int idx = 0; idx < diag.length; idx++) {
                int i = k >= 0 ? idx : idx - k;
                int j = k >= 0 ? idx + k : idx;
                result[i] += diag[idx] * vector[j];
            }
        }
        return result;
    }
}
