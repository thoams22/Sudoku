package sample;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;

import javafx.scene.shape.Rectangle;

/**
 * Classe qui permet de crée l'interface graphique et de la lier avec la classe sudoku.
 */
public class Controller {

    @FXML
    private GridPane SudokuGrid;

    @FXML
    private VBox Vbox;

    private difficulty diff = difficulty.facile;
    private final int[] focused = {0, 0};

    private final ArrayList<ArrayList<StackPane>> Grid = new ArrayList<>(9);

    private final Sudoku sudoku = new Sudoku();

    /**
     * La méthode initialize génère l'inteface de la grille de sudoku.
     * Elle invoke sudoku.generate(diff) pour générer la grille de sudoku à afficher.
     */
    public void initialize() {
        SudokuGrid.setAlignment(Pos.CENTER);
        sudoku.generate(diff);
        for (int i = 0; i < 9; i++) {
            Grid.add(i, new ArrayList<>(9));
            for (int j = 0; j < 9; j++) {
                StackPane cell = createCell(i, j);
                Grid.get(i).add(j, cell);
                SudokuGrid.add(cell, j, i);
            }
        }
        initializeRadioButton();
        initializeButton();
    }

    /**
     * La méthode updateGrid met à jour le texte de toutes les cellules de l'interface avec les valeurs contenue dans la grille de sudoku.
     * Elle réinitialise aussi comment les évènement de clique vont être géré.
     */
    private void updateGrid() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String txt = "";
                Text tx = (Text) Grid.get(i).get(j).getChildren().get(0);
                if (sudoku.getCell(i,j) != 0) {
                    txt = Integer.toString(sudoku.getCell(i,j));
                }
                Grid.get(i).get(j).setBorder(getBorderColored(i, j, Color.BLACK));

                tx.setText(txt);
                Rectangle rect = (Rectangle) Grid.get(i).get(j).getChildren().get(1);
                rectangleOnMouseClicked(rect, i, j);
            }
        }
    }

    /**
     * La méthode initializeButton génère les boutons de l'interface et initialise comment vont être géré les actions de ceux-ci.
     */
    private void initializeButton() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        Vbox.getChildren().add(hBox);

        Button btn1 = new Button("Nouvelle grille");
        btn1.setOnMouseClicked(event -> {
            sudoku.generate(diff);
            setCellUnFocus();
            updateGrid();
        });
        hBox.getChildren().add(btn1);

        Button btn2 = new Button("Verifier");
        btn2.setOnMouseClicked(event -> {
            setCellUnFocus();
            ArrayList<int[]> notValid = sudoku.verify();
            for (int[] coord : notValid) {
                setCellNotValid(coord[0], coord[1]);
            }
        });
        hBox.getChildren().add(btn2);
    }

    /**
     * La méthode initializeRadioButton génère les boutons radio en fonction des valeurs de l'enum difficulty et
     * initialise comment vont être géré les évenements de ceux-ci.
     *
     * @see difficulty
     */
    private void initializeRadioButton() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        Vbox.getChildren().add(hBox);

        ToggleGroup tg = new ToggleGroup();

        for (int i = 0; i < difficulty.values().length; i++) {
            RadioButton rb = new RadioButton(difficulty.values()[i].toString());
            rb.setOnMouseClicked(event -> {
                diff = difficulty.valueOf(rb.getText());
                setCellUnFocus();
            });
            rb.setToggleGroup(tg);
            if (i == 0) {
                rb.setSelected(true);
            }
            hBox.getChildren().add(i, rb);
        }
    }

    /**
     * La méthode createCell crée un stackPane pour chaque cellule qui est composée d'un rectangle, qui permet de recevoir les évènements de souris
     * et de clavier, et d'un texte pour afficher la valeur de la grille de sudoku.
     *
     * @param i Ligne de la cellule.
     * @param j Colonne de la celulle.
     * @return Un StackPane qui représente la cellule.
     */
    private StackPane createCell(int i, int j) {
        Rectangle rect = new Rectangle(30, 30, Color.TRANSPARENT);
        String txt = "";
        if (sudoku.getCell(i,j) != 0) {
            txt = Integer.toString(sudoku.getCell(i,j));
        }
        Text text = new Text(txt);
        StackPane stack = new StackPane(text, rect);

        stack.setBorder(getBorderColored(i, j, Color.BLACK));

        rectangleOnMouseClicked(rect, i, j);

        rect.setOnKeyReleased(e -> {
            int col = SudokuGrid.getColumnIndex(rect.getParent());
            int row = SudokuGrid.getRowIndex(rect.getParent());
            Text tx = (Text) Grid.get(row).get(col).getChildren().get(0);

            if (e.getCode().isDigitKey()) { // fait en sorte que les keyPressed géré ne soit que des touches qui peuvent être des chiffre et les convertit.
                int num = e.getCode().toString().charAt(e.getCode().toString().length() - 1) - '0';
                String t = "";
                if (num != 0) {
                    t = Integer.toString(num);
                }
                tx.setText(t);
                sudoku.setCell(row, col, num);
                if (sudoku.isComplete()) { // verifie si la grile est complete et correct.
                    for (int k = 0; k < 9; k++) {
                        for (int l = 0; l < 9; l++) {
                            Grid.get(k).get(l).setBorder(getBorderColored(k, l, Color.GREEN));
                        }
                    }
                }
            }
        });
        return stack;
    }

    /**
     * La méthode rectangleOnMouseClicked initialise setOnMouseClicked pour le Rectangle choisi,
     * fait en sort que seulement les celulles de sudoku.grid qui sont initialement vide(=0) puissent être modifié.
     *
     * @param rect le rectangle auquel la méthode est associée.
     * @param i    Ligne du rectangle dans le gridPane.
     * @param j    Colonne du rectangle dans le gridPane.
     */
    private void rectangleOnMouseClicked(Rectangle rect, int i, int j) {
        if (sudoku.getCell(i, j) == 0) {
            rect.setOnMouseClicked(event -> {
                int column = SudokuGrid.getColumnIndex(rect.getParent());
                int row = SudokuGrid.getRowIndex(rect.getParent());

                StackPane sp = Grid.get(row).get(column);

                if (rect.isFocused()) {
                    Vbox.requestFocus();
                    sp.setBorder(getBorderColored(row, column, Color.BLACK));
                } else {
                    setCellUnFocus();

                    focused[0] = row;
                    focused[1] = column;

                    rect.requestFocus();
                    // Genere une bordure Bleu pour signifier que la cellule à le focus.
                    sp.setBorder(new Border(new BorderStroke(Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE,
                            BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                            null, new BorderWidths(2, 2, 2, 2), null)));
                }
            });
        } else {
            rect.setOnMouseClicked(event -> {
                Vbox.requestFocus();
                setCellUnFocus();
            });
        }
    }

    /**
     * La méthode getThickness permet de générer l'épaisseur des bordures des cellules afin de mettre en evidence la grille
     * et les block 3x3 de la grille.
     *
     * @param row    Ligne de la cellule pour laquelle l'épaisseur est calculée.
     * @param column Colonne de la cellule pour laquelle l'épaisseur est calculée.
     * @return Un tableau avec l'épaisseur des 4 trait de la bordure sous la forme {top , right, bottom, left}.
     */
    private double[] getThickness(int row, int column) {
        double[] thickness = new double[]{0.5, 0.5, 0.5, 0.5};
        if (row == 0 || row == 3 || row == 6) {
            thickness[0] = 3.0; //top
        } else if (row == 8) {
            thickness[2] = 3.0; // bottom
        }

        if (column == 0 || column == 3 || column == 6) {
            thickness[3] = 3.0; // left
        } else if (column == 8) {
            thickness[1] = 3.0; // right
        }
        return thickness;
    }

    /**
     * La méthode getBorderColored génère une bordure de la couleur en argument pour la cellule donnée.
     *
     * @param row    Ligne de la cellule pour laquelle la bordure est généré.
     * @param column Colonnes de la cellule pour laquelle la bordure est généré.
     * @param color Couleur de laquelle sera la bordure.
     * @return La bordure de la couleur choisie pour la cellule.
     */
    private Border getBorderColored(int row, int column, Color color) {
        double[] thickness = getThickness(row, column);

        BorderStroke Bs = new BorderStroke(color, color, color, color, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null,
                new BorderWidths(thickness[0], thickness[1], thickness[2], thickness[3]), null);

        return new Border(Bs);
    }

    /**
     * La méthode setCellUnFocus applique la bordure de base(Noir) à la dernière cellule qui a reçu le focus.
     */
    private void setCellUnFocus() {
        Grid.get(focused[0]).get(focused[1]).setBorder(getBorderColored(focused[0], focused[1], Color.BLACK));
    }

    /**
     * La méthode setCellNotValid applique une bordure rouge à la cellule donnée avec les paramètres.
     *
     * @param row Ligne de la cellule pour laquelle la bordure est généré.
     * @param col Colonne de la cellule pour laquelle la bordure est généré.
     */
    private void setCellNotValid(int row, int col) {
        Grid.get(row).get(col).setBorder(new Border(new BorderStroke(Color.RED, Color.RED, Color.RED, Color.RED, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(2, 2, 2, 2), null)));
    }
}