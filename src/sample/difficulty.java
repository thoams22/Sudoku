package sample;

/**
 * Difficulté qui peuvent être utiliser, chaque valeur a un argument (value) qui équivaut au nombre de cellules vides qui vont être enlevée de la grille du sudoku.
 */
enum difficulty {
    facile(15), moyen(25), dure(35);
    private final int value;

    /**
     * Constructeur qui specifie la valeur du nombre de chiffre qui vont être enlevé de la grille du sudoku.
     */
    difficulty(int val) {
        value = val;
    }

    /**
     * @return retourne le nombre de chiffre qui vont être enlevé de la grille du sudoku.
     */
    int getValue() {
        return value;
    }
}
