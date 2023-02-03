package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe qui permet de génerer une grille de sudoku avec différente difficulté, de la résoudre et de vérifier les erreurs.
 */
public class Sudoku {

    private final int[][] grid;
    private final int[][] gridResolved;
    private final List<Integer> numbers;

    public static void main(String[] args) {
        // test unitaire
        Sudoku sudoku = new Sudoku();
        sudoku.generate(difficulty.facile);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(sudoku.grid[i][j]);
            }
            System.out.println(" ");
        }
        System.out.println(" ");
        System.out.println(sudoku.getCell(4, 4));
        sudoku.setCell(4, 4, 3);
        System.out.println(sudoku.getCell(4, 4));
        System.out.println(" ");
        System.out.println(sudoku.isComplete());
        System.out.println(" ");
        for (int[] notValid : sudoku.verify()) {
            System.out.println(notValid[0] + ":" + notValid[1]);
        }
        // les autres méthodes sont appelées indirectement donc pas besoin de les tester.
    }

    /**
     * Constructeur de la classe.
     */
    public Sudoku() {
        grid = new int[9][9];
        gridResolved = new int[9][9];
        numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        reset();
    }

    /**
     * La méthode reset remet à 0 toutes les cellules de la grille.
     */
    private void reset() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col] = 0;
            }
        }
    }

    /**
     * La méthode generate génère un nouveau Sudoku en réinitialisant d'abord la grille,
     * puis en résolvant la grille avec la méthode solve, et enfin en retirant un certain nombre d'éléments de la grille
     * en fonction de la difficulté spécifiée en argument.
     *
     * @param diff La difficulty choisie.
     */
    public void generate(difficulty diff) {
        reset();
        solve(0, 0);
        removeElements(diff);
    }

    /**
     * La méthode verify vérifie si les entrées de la grille sont correctes en les comparant à la solution stockée dans gridResolved.
     * Elle renvoie un ArrayList des positions des entrées incorrectes sous la forme {row, col}.
     *
     * @return Un ArrayList int[] des cellules de la grille qui ne sont pas bonnes sous la forme {ligne, colonnes}.
     */
    public ArrayList<int[]> verify() {
        ArrayList<int[]> notValid = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] != 0 && grid[row][col] != gridResolved[row][col]) {
                    notValid.add(new int[]{row, col});
                }
            }
        }
        return notValid;
    }

    /**
     * La méthode solve essaie de remplir la grille avec une séquence de chiffre,
     * en commençant par la cellule spécifiée par les arguments row et col. Pour ce faire, elle essaie différents chiffres à chaque position
     * et vérifie s'ils sont valides selon la méthode isValid.
     * Si un nombre valide est trouvé, la méthode passe à la position suivante en s'appelant récursivement avec la ligne et la colonne mises à jour.
     * Si aucun nombre valide n'est trouvé, la méthode renvoie false et revient à la position précédente pour essayer un autre chiffre.
     * Si la méthode réussit à remplir toute la grille, elle renvoie true.
     *
     * @param row Ligne appartir de laquelle la resolution commence.
     * @param col Colonne appartir de laquelle la resolution commence.
     * @return true si la grille est résolue, false si elle n'est pas résolue.
     */
    private boolean solve(int row, int col) {
        if (col == 9) {
            col = 0;
            row++;
            if (row == 9) {
                return true;
            }
        }

        Collections.shuffle(numbers);

        for (int num : numbers) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;
                gridResolved[row][col] = num;
                if (solve(row, col + 1)) {
                    return true;
                }
                grid[row][col] = 0;
            }
        }
        return false;
    }

    /**
     * La méthode removeElements supprime un certain nombre d'éléments de la grille en fonction de la difficulté spécifiée en argument.
     * Pour ce faire, elle sélectionne aléatoirement des positions dans la grille et leur attribue la valeur 0.
     * Le nombre d'éléments supprimés dépend de la difficulté donnée en argument.
     *
     * @param diff difficulté choisie.
     */
    private void removeElements(difficulty diff) {

        List<int[]> positions = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int[] position = {i, j};
                positions.add(position);
            }
        }
        Collections.shuffle(positions);

        for (int i = 0; i < diff.getValue(); i++) {
            int[] position = positions.get(i);
            grid[position[0]][position[1]] = 0;
        }
    }

    /**
     * La méthode isComplete vérifie que toutes les cellules soit remplie d'un chiffre différent de 0.
     * Et vérifie que ce chiffre est le bon par rapport à la grille résolue gridResolved.
     *
     * @return Si un chiffre n'est pas présent ou pas bon la méthode renvoie false, sinon renvoie true.
     */
    public boolean isComplete() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0 || grid[i][j] != gridResolved[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * La méthode isValid vérifie s'il est valide de placer un chiffre donné à une position donnée dans la grille.
     * Pour ce faire, elle vérifie si le chiffre est déjà présent dans la même ligne, colonne ou bloc 3x3 que la position donne.
     * Si le chiffre n'est pas présent, la méthode renvoie true, sinon elle renvoie false.
     *
     * @param row Ligne qui va être verifiée.
     * @param col Colonne qui va être verifiée.
     * @param num Chiffre qui va être utilisée pour la verification.
     * @return Si le chiffre n'est pas présent, la méthode renvoie true, sinon elle renvoie false.
     */
    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num) {
                return false;
            }
        }

        for (int i = 0; i < 9; i++) {
            if (grid[i][col] == num) {
                return false;
            }
        }

        int blockRow = row / 3;
        int blockCol = col / 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[blockRow * 3 + i][blockCol * 3 + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * La méthode setCell insert le chiffre à la position dans la grille donnée par row, col.
     *
     * @param row Ligne dans laquelle le chiffre est inseré.
     * @param col Colonne dans laquelle le chiffre est inseré.
     * @param num Chiffre à insérer.
     */
    public void setCell(int row, int col, int num) {
        grid[row][col] = num;
    }

    /**
     * La méthode getCell retourne le chiffre stocké à la position dans la grille donnée par row, col.
     *
     * @param row Ligne de laquelle le chiffre est retourné.
     * @param col Colonne de laquelle le chiffre est retourné.
     * @return Le chiffre présent à la position donné en arguments.
     */
    public int getCell(int row, int col) {
        return grid[row][col];
    }
}
