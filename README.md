# 🧮 Projet d'Analyse Numérique : Résolution d'Équations Différentielles

## 📘 Présentation

Ce projet s'inscrit dans le cadre du cours **d'Analyse Numérique** encadré par **Pr. MOUKOUOP Ibrahim**.  
Il traite de la **résolution des équations différentielles du second ordre** à l'aide de deux méthodes numériques fondamentales :

- ✅ La **méthode des différences finies**
- ✅ La **méthode des volumes finis**

Une attention particulière est portée à la **vérification de la qualité logicielle** via des **tests unitaires** utilisant **Maven**.

---

## 🧑‍🏫 Enseignant encadrant

**Pr. MOUKOUOP Ibrahim**

---

## 👥 Membres du groupe

| Nom              | Rôle           |
| ---------------- | -------------- |
| **DONCHI LEROY** | Chef de groupe |
| KOUASSI DE YOBO  | Sous-chef      |
| NGOUPAYE THIERRY | Membre         |
| NGOM Christine   | Membre         |
| YOMBA Merveille  | Membre         |
| NOUNDJEU Franck  | Membre         |
| MEKIAGE Oliver   | Membre         |
| MENGOSSO Adrien  | Membre         |

---


## 📂 Structure du projet

Le projet suit l’architecture standard **Maven** :

```

projet-analyse-numerique/
├── pom.xml
├── src
│   ├── main
│   │   └── java
│   │       └── principalProgramm
│   │           └── \[Fichiers principaux et interface.java]
│   └── test
│       └── java
│           ├── Utils
│           │   └── \[Tests utilitaires]
│           └── \[Autres fichiers de test]

````

- `principalProgramm` : contient la logique des méthodes numériques
- `Utils` : regroupe les tests et fonctions d'aide

---

## 📊 Méthodes implémentées

### 🔹 Différences Finies
- Discrétisation de l'EDP sur des grilles
- Construction de matrices tridiagonales
- Résolution via Gauss-Seidel ou d'autres solveurs

### 🔹 Volumes Finis
- Maillage régulier

---

## 🧪 Tests logiciels

L’approche par **tests** permet de :
- Vérifier la **convergence** et la **précision** des méthodes
- Comparer les résultats numériques à des **solutions exactes**
- Génération des cas de test avec **Jenny**

L’ensemble des tests est écrit avec **JUnit** et exécuté via **Maven**.

### ▶️ Exécuter les tests
```bash
mvn clean test
````


## ⚙️ Compilation et exécution

### Compilation

```bash
mvn compile
```

### Exécution (si une classe principale est définie)

```bash
mvn exec:java -Dexec.mainClass="principalProgramm.Main"
```

