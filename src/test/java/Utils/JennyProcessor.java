package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JennyProcessor {

    private static final Map<String, Double> valueMap = Map.of(
            "a", 10e10,
            "b", 10.0 / 3,
            "c", 10e-10,
            "d", 0.0,
            "e", -10e-10,
            "f", -10.0 / 3,
            "g", -10e10
    );

    public void run() {
        try {

            String os = System.getProperty("os.name").toLowerCase(); // Convertir en minuscule pour éviter la casse

            ProcessBuilder processBuilder = null;
            File workingDirectory = null;

            if (os.contains("win"))
            {
                System.out.println("Système d'exploitation : Windows");

            // Définir le répertoire de travail comme étant le répertoire courant (demo)
             workingDirectory = new File(".");

            // Construire la commande
            String command = "cmd /c .\\jenny.exe -n2 7 7 7 > scenario.txt";

            // Exécuter la commande dans le répertoire spécifié
             processBuilder = new ProcessBuilder("cmd", "/c", command);
             processBuilder.directory(workingDirectory);
            }
            else if (os.contains("nix") || os.contains("nux") || os.contains("mac"))
            {
                System.out.println("Système d'exploitation : Linux / Unix / macOS");
                 workingDirectory = new File("."); // Répertoire courant

                // Construire la commande sous forme de liste
                 processBuilder = new ProcessBuilder("./jenny", "-n2", "7", "7", "7");

                // Définir le répertoire de travail
                processBuilder.directory(workingDirectory);

                // Rediriger la sortie vers un fichier
                processBuilder.redirectOutput(new File("scenario.txt"));

            } else {
                System.out.println("Système d'exploitation inconnu : " + os);
                 workingDirectory = new File("."); // Répertoire courant

                // Construire la commande sous forme de liste
                 processBuilder = new ProcessBuilder("./jenny", "-n2", "7", "7", "7");

                // Définir le répertoire de travail
                processBuilder.directory(workingDirectory);

                // Rediriger la sortie vers un fichier
                processBuilder.redirectOutput(new File("scenario.txt"));
            }


            Process process = processBuilder.start();

            // Attendre la fin de l'exécution
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

            // Lire le fichier généré
            List<String> combinations = readFile(new File(workingDirectory, "scenario.txt").getPath());
            Map<Integer, List<double[]>> resultMap = processCombinations(combinations);

            // Afficher les résultats
            resultMap.forEach((key, value) -> {
                System.out.println("Index " + key + ":");
                for (double[] triplet : value) {
                    System.out.println("  " + triplet[0] + ", " + triplet[1] + ", " + triplet[2]);
                }
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<String> readFile(String fileName) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim()); // Trim pour enlever les espaces en début et fin
            }
        }
        return lines;
    }

    private Map<Integer, List<double[]>> processCombinations(List<String> combinations) {
        Map<Integer, List<double[]>> resultMap = new HashMap<>();
        for (int i = 0; i < combinations.size(); i++) {
            String combination = combinations.get(i);
            String[] parts = combination.split(" ");

            // Vérifiez que chaque partie a la longueur attendue
            if (parts.length == 3) {
                double[] values = new double[3];
                for (int j = 0; j < 3; j++) {
                    if (parts[j].length() == 2) { // Vérifiez que la partie a exactement 2 caractères
                        String key = parts[j].substring(1);
                        values[j] = valueMap.get(key);
                    } else {
                        // Gérer le cas où la partie est incorrecte
                        System.err.println("Invalid part in combination: " + parts[j]);
                        values[j] = 0.0; // Valeur par défaut ou gestion d'erreur
                    }
                }
                resultMap.computeIfAbsent(i, k -> new ArrayList<>()).add(values);
            } else {
                // Gérer le cas où la combinaison n'a pas le bon nombre de parties
                System.err.println("Invalid combination format: " + combination);
            }
        }
        return resultMap;
    }

    public Map<Integer, List<double[]>> getResultMap() throws Exception{
        Map<Integer, List<double[]>> resultMap = processCombinations(readFile("scenario.txt"));
        return resultMap;
    }
}