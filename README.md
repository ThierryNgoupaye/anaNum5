

# Projet de Test : Résolution d'Équations Quadratiques

Ce projet a pour objectif de fournir et de tester une implémentation robuste pour la résolution d'équations quadratiques de la forme **ax² + bx + c = 0**. L'originalité de ce projet réside dans l'utilisation de la technique de **test combinatoire** via l'outil **Jenny** pour générer automatiquement des scénarios de test pertinents et couvrir un large éventail de cas limites.

Le projet est structuré en utilisant Java et Maven, avec des tests unitaires gérés par JUnit 5.

## 👥 Membres du groupe

| Nom | Rôle |
| :--- | :--- |
| **DONCHI LEROY** | Chef de groupe |
| KOUASSI DE YOBO | Sous-chef |
| NGOUPAYE THIERRY | Membre |
| NGOM Christine | Membre |
| YOMBA Merveille | Membre |
| NOUNDJEU Franck | Membre |
| MEKIAGE Oliver | Membre |
| MENGOSSO Adrien | Membre |

## 📖 Définitions

#### Équation quadratique
Une équation quadratique est une équation polynomiale de degré 2, qui peut s'écrire sous la forme canonique :
`ax² + bx + c = 0`
où `a`, `b`, et `c` sont les coefficients, et `a` est non nul. La résolution de cette équation dépend de la valeur du discriminant `Δ = b² - 4ac`.

#### Test unitaire
Un test unitaire est une procédure de test logiciel qui vise à vérifier le bon fonctionnement d'une partie spécifique d'un programme (une "unité"). Dans ce projet, nous utilisons le framework **JUnit** pour écrire des tests qui valident le comportement de la classe `QuadraticEquationSolver`.

#### Test combinatoire
Le test combinatoire (ou test n-way) est une technique de test qui vise à identifier les défauts causés par les **interactions** entre différents paramètres d'entrée. Au lieu de tester toutes les combinaisons possibles (ce qui est souvent infaisable), cette approche génère un sous-ensemble optimisé de tests qui garantit que chaque combinaison de `n` paramètres est testée au moins une fois.

#### Jenny
**Jenny** est un outil en ligne de commande qui implémente le test combinatoire. Il génère des scénarios de test à partir d'un ensemble de paramètres et de leurs valeurs possibles. Dans notre projet, nous l'utilisons pour générer des combinaisons de coefficients `a`, `b` et `c` afin de tester notre solveur d'équations de manière exhaustive et efficace.

## ⚙️ Génération de Tests avec Jenny

Le test traditionnel exigerait d'écrire manuellement des dizaines de cas de test pour couvrir les situations classiques et les cas limites (a=0, Δ=0, grands nombres, petits nombres, etc.). L'approche avec Jenny automatise et systématise ce processus.

Voici comment cela fonctionne dans notre projet :

1.  **Définition des paramètres** : Dans la classe `Utils/JennyProcessor.java`, nous définissons un ensemble de valeurs représentatives pour nos coefficients. Ces valeurs incluent des nombres très grands, très petits, nuls, et des fractions pour tester la précision.
    ```java
    private static final Map<String, Double> valueMap = Map.of(
        "a", 10e10,
        "b", 10.0 / 3,
        "c", 10e-10,
        "d", 0.0,
        "e", -10e-10,
        "f", -10.0 / 3,
        "g", -10e10
    );
    ```

2.  **Exécution de Jenny** : La classe `JennyProcessor` exécute l'outil Jenny via une commande système. La commande utilisée est :
    ```bash
    # Pour Linux/macOS
    ./jenny -n2 7 7 7 > scenario.txt
    # Pour Windows
    cmd /c .\\jenny.exe -n2 7 7 7 > scenario.txt
    ```
    -   `-n2` signifie que nous voulons faire du **test par paires (pairwise testing)**. Jenny garantira que chaque paire de valeurs possible entre les différents paramètres sera testée au moins une fois.
    -   `7 7 7` indique que nous avons 3 paramètres (`a`, `b`, `c`), et que chacun peut prendre 7 valeurs distinctes (celles définies dans notre `valueMap`).

3.  **Génération du fichier `scenario.txt`** : Jenny génère un fichier contenant des combinaisons symboliques, comme par exemple `P1a P2d P3g`.

4.  **Interprétation des résultats** : La classe `JennyProcessor` lit le fichier `scenario.txt` et traduit chaque combinaison symbolique en un triplet de valeurs numériques `(a, b, c)` en utilisant la `valueMap`.

5.  **Exécution des tests** : La classe de test `QuadraticEquationSolverCombinationTest` récupère ces triplets de coefficients et exécute la méthode `solveQuadraticEquation` pour chaque combinaison. Elle vérifie ensuite que les solutions trouvées sont mathématiquement correctes ou que les exceptions attendues (par exemple, pour un discriminant négatif) sont bien levées.

Cette approche permet de tester des interactions complexes entre les coefficients qui auraient pu être manquées lors d'une conception manuelle des tests.

## 🚀 Comment Lancer le Projet

Pour exécuter ce projet et lancer la suite de tests, suivez ces étapes.

### Prérequis

-   **JDK** (Java Development Kit) 11 ou supérieur.
-   **Apache Maven**.
-   L'exécutable **Jenny**. Vous devez télécharger la version correspondant à votre système d'exploitation (Windows, Linux, ou macOS) sur le site officiel ou une source fiable.

### Étapes d'exécution

1.  **Cloner le dépôt**
    ```bash
    git clone <url_du_depot>
    cd <nom_du_repertoire_du_projet>
    ```

2.  **Placer l'exécutable Jenny**
    **Ceci est une étape cruciale.** Vous devez placer le fichier exécutable `jenny` (ou `jenny.exe` sur Windows) à la **racine du projet**, au même niveau que le fichier `pom.xml`. Le code est conçu pour l'exécuter depuis cet emplacement.

3.  **Exécuter les tests avec Maven**
    Ouvrez un terminal à la racine du projet et lancez la commande suivante :
    ```bash
    mvn test
    ```
    Maven va compiler le projet, puis exécuter la suite de tests définie dans `QuadraticEquationSolverCombinationTest`.

    Pendant l'exécution, vous verrez dans la console :
    -   La confirmation que le processus Jenny s'est bien exécuté.
    -   La liste des combinaisons de test générées.
    -   Le résultat de chaque test pour chaque combinaison (valeurs des coefficients, solution(s) obtenue(s)).
    -   Un résumé final indiquant le nombre de tests réussis et échoués.

Le projet est maintenant configuré pour démontrer la puissance du test combinatoire pour la validation d'algorithmes mathématiques.