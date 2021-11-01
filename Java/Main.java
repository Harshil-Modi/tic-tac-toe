// Programming Assignment 3 (Group Assignment) Problem 1

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private static int BOARD_ROWS = 3;
    private static int BOARD_COLS = BOARD_ROWS;
    private static int CHECK_ADJACENT_BUTTONS = 3;

    public BorderPane container;
    public GridPane boardContainer, controllerContainer;
    public HBox gameControllersContainer;
    public VBox statisticsContainer;
    public Button[][] buttons;
    public Button resetBtn, exitBtn;
    public Label lblWinsX, lblWinsO, lblDraws;

    private Scanner sc;

    private int[] data;
    private boolean player_1_turn = true;

    private static int GAME_STATUS_DRAW = 0;
    private static int GAME_STATUS_WINS_PLAYER_X = 1;
    private static int GAME_STATUS_WINS_PLAYER_O = 2;

    private static String FILE_NAME = "data.txt";
    private static String X = "X";
    private static String O = "O";
    private static boolean DISABLE = true;
    private static boolean ENABLE = false;

    private static double WINDOW_WIDTH = 700;
    private static double WINDOW_HEIGHT = 500;
    // 70%
    private static double BOARD_WIDTH = WINDOW_WIDTH * 0.7;
    private static double BOARD_HEIGHT = WINDOW_HEIGHT;
    private static double LABEL_INSETS = 5;
    private static double STATISTICS_MARGIN = 10;

    private Button getBoardButton() {
        Button btn = new Button();
        btn.setStyle("-fx-pref-width: infinity; -fx-pref-height: infinity;");
        btn.setOnAction(e -> boardButtonClick(btn, e));
        return btn;
    }

    private Label getStatisticsDisplayLabel() {
        Label lbl = new Label();
        lbl.setStyle("-fx-border-color: #000000; -fx-background-color: #ffffff;");
        lbl.setPadding(new Insets(LABEL_INSETS));
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    private static ColumnConstraints getColumnConstraints() {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setMinWidth(10);
        cc.setPrefWidth(10);
        cc.setHgrow(Priority.SOMETIMES);
        return cc;
    }

    private static RowConstraints getRowConstraints() {
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(10);
        rc.setPrefHeight(10);
        rc.setVgrow(Priority.SOMETIMES);
        return rc;
    }

    private static String intArrayToString(int[] array) {
        String str = "";
        for (int i = 0; i < array.length; ++i) {
            str += String.valueOf(array[i]) + (i == (array.length - 1) ? "" : ",");
        }
        return str;
    }

    private static int[] strArrayToIntArray(String[] strArray) {
        int[] arr = new int[strArray.length];
        for (int i = 0; i < strArray.length; ++i) {
            arr[i] = Integer.parseInt(strArray[i]);
        }
        return arr;
    }

    private void boardButtonEvents(Button boardBtn, boolean player_1_turn) {
        boardBtn.setText((player_1_turn ? X : O));
        boardBtn.setDisable(true);
        boolean isGameFinished = false;

        int result = check();
        if (result == GAME_STATUS_WINS_PLAYER_X) {
            data[0] += 1;
            isGameFinished = true;

        } else if (result == GAME_STATUS_WINS_PLAYER_O) {
            data[1] += 1;
            isGameFinished = true;

        } else if (areAllBoardButtonsDisabled() && result == GAME_STATUS_DRAW) {
            data[2] += 1;
            isGameFinished = true;
        }

        if (isGameFinished) {
            setAllBoardButtons(DISABLE);
            saveData();
        }
    }

    private boolean checkRows(String lbl, boolean isRow) {
        for (int i = 0; i < BOARD_ROWS; ++i) {
            for (int j = 0; j <= (BOARD_COLS - CHECK_ADJACENT_BUTTONS); ++j) {
                int count = 0;
                for (int k = j; k < (CHECK_ADJACENT_BUTTONS + j); ++k, ++count) {
                    Button btn = (isRow ? buttons[i][k] : buttons[k][i]);
                    if (!(btn.getText().toString() == lbl)) {
                        break;
                    }
                }
                if (count == CHECK_ADJACENT_BUTTONS) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkRows(String lbl) {
        return checkRows(lbl, false);
    }

    private boolean checkColumns(String lbl) {
        return checkRows(lbl, true);
    }

    private boolean checkDiagonals(String lbl) {
        // This block checks for lbl in fwd diagonal
        for (int i = (CHECK_ADJACENT_BUTTONS - 1); i < BOARD_ROWS; ++i) {
            for (int j = 0; j <= (BOARD_COLS - CHECK_ADJACENT_BUTTONS); ++j) {
                int count = 0;
                for (int k = i, l = j; l < (CHECK_ADJACENT_BUTTONS + j); --k, ++l, ++count) {
                    if (!(buttons[l][k].getText().toString() == lbl)) {
                        break;
                    }
                }
                if (count == CHECK_ADJACENT_BUTTONS) {
                    return true;
                }
            }
        }

        // This block checks for lbl in bckwd diagonal
        for (int i = (BOARD_ROWS - CHECK_ADJACENT_BUTTONS); i >= 0; --i) {
            for (int j = 0; j <= (BOARD_COLS - CHECK_ADJACENT_BUTTONS); ++j) {
                int count = 0;
                for (int k = i, l = j; k < (CHECK_ADJACENT_BUTTONS + i); ++k, ++l, ++count) {
                    if (!(buttons[k][l].getText().toString() == lbl)) {
                        break;
                    }
                }
                if (count == CHECK_ADJACENT_BUTTONS) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns {@code GAME_STATUS_WINS_PLAYER_X} if player X wins, if player O wins then returns {@code GAME_STATUS_WINS_PLAYER_O}, else returns {@code GAME_STATUS_DRAW}
     */
    private int check() {

        // Checking all rows if there exists X, `CHECK_ADJACENT_BUTTONS` times
        if (checkRows(X)) {
            return GAME_STATUS_WINS_PLAYER_X;
        }
        // Checking all rows if there exists o, `CHECK_ADJACENT_BUTTONS` times
        else if (checkRows(O)) {
            return GAME_STATUS_WINS_PLAYER_O;
        }
        // Checking all columns if there exists X, `CHECK_ADJACENT_BUTTONS` times
        else if (checkColumns(X)) {
            return GAME_STATUS_WINS_PLAYER_X;
        }
        // Checking all columns if there exists o, `CHECK_ADJACENT_BUTTONS` times
        else if (checkColumns(O)) {
            return GAME_STATUS_WINS_PLAYER_O;
        }
        // Checks all the diagonals for X of size `CHECK_ADJACENT_BUTTONS`
        else if (checkDiagonals(X)) {
            return GAME_STATUS_WINS_PLAYER_X;
        }
        // Checks all the diagonals for O of size `CHECK_ADJACENT_BUTTONS`
        else if (checkDiagonals(O)) {
            return GAME_STATUS_WINS_PLAYER_O;
        }

        return GAME_STATUS_DRAW;
    }

    private boolean areAllBoardButtonsDisabled() {
        for (int i = 0; i < buttons.length; ++i) {
            for (int j = 0; j < buttons[i].length; ++j) {
                if (!buttons[i][j].isDisable()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setAllBoardButtons(boolean value) {
        for (int i = 0; i < buttons.length; ++i) {
            for (int j = 0; j < buttons[i].length; ++j) {
                buttons[i][j].setDisable(value);
            }
        }
    }

    private void readData() {
        try {
            File file = new File(FILE_NAME);
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
        }
        try {
            String rawData = sc.nextLine();
            data = strArrayToIntArray(rawData.split(","));
        } catch (Exception e) {
            // When file (data.txt) is not found, then this block will exectute
            data = new int[3];
            data[0] = 0;
            data[1] = 0;
            data[2] = 0;
        }
        lblWinsX.setText("X Wins : " + data[0]);
        lblWinsO.setText("O Wins : " + data[1]);
        lblDraws.setText("Draws : " + data[2]);
    }

    private void saveData() {
        try {
            FileWriter fw = new FileWriter(FILE_NAME);
            fw.write(intArrayToString(data) + "\nX,O,Draws\n\nOnly first line will be read by the program");
            fw.close();
        } catch (Exception e) {
        }
        readData();
    }

    public void boardButtonClick(Button btn, ActionEvent e) {
        boardButtonEvents(btn, player_1_turn);
        player_1_turn = !player_1_turn;
    }

    public void resetGame() {
        player_1_turn = true;

        setAllBoardButtons(ENABLE);

        // sets text of all buttons to an empty string, as reset game was called
        for (int i = 0; i < buttons.length; ++i) {
            for (int j = 0; j < buttons[i].length; ++j) {
                buttons[i][j].setText("");
            }
        }

        // resets 
        data[0] = data[1] = data[2] = 0;
        saveData();
    }

    public void exit() {
        saveData();
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        buttons = new Button[BOARD_ROWS][BOARD_COLS];

        container = new BorderPane();

        boardContainer = new GridPane();

        controllerContainer = new GridPane();

        statisticsContainer = new VBox();

        gameControllersContainer = new HBox();

        boardContainer.setStyle("-fx-hgap: 5; -fx-vgap: 5; -fx-padding: 5;");
        boardContainer.setPrefWidth(BOARD_WIDTH);
        boardContainer.setPrefHeight(BOARD_HEIGHT);

        // Initializes buttons[][] and adds it in the container by setting constraints
        for (int i = 0; i < buttons.length; ++i) {
            boardContainer.getColumnConstraints().add(getColumnConstraints());
            boardContainer.getRowConstraints().add(getRowConstraints());

            for (int j = 0; j < buttons[i].length; ++j) {
                buttons[i][j] = getBoardButton();
                GridPane.setConstraints(buttons[i][j], i, j);
                boardContainer.getChildren().add(buttons[i][j]);
            }
        }
        boardContainer.setAlignment(Pos.CENTER);

        // Initilizes button that says "Reset"
        resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> resetGame());
        resetBtn.setPrefWidth(150);
        resetBtn.setPrefHeight(25);

        // Initilizes button that says "Quit"
        exitBtn = new Button("Quit");
        exitBtn.setOnAction(e -> exit());
        exitBtn.setPrefWidth(150);
        exitBtn.setPrefHeight(25);

        // Initializes lables that displays the wins/draws of past games
        lblWinsX = getStatisticsDisplayLabel();
        lblWinsO = getStatisticsDisplayLabel();
        lblDraws = getStatisticsDisplayLabel();

        // Inserts lables on the container that will hold it on the display
        statisticsContainer.getChildren().add(lblWinsX);
        VBox.setMargin(lblWinsX, new Insets(STATISTICS_MARGIN));
        statisticsContainer.getChildren().add(lblWinsO);
        VBox.setMargin(lblWinsO, new Insets(STATISTICS_MARGIN));
        statisticsContainer.getChildren().add(lblDraws);
        VBox.setMargin(lblDraws, new Insets(STATISTICS_MARGIN));

        gameControllersContainer.setStyle("-fx-padding: 5; -fx-spacing: 5;");
        gameControllersContainer.setPrefHeight(100);
        gameControllersContainer.setPrefWidth(200);
        gameControllersContainer.getChildren().addAll(resetBtn, exitBtn);
        HBox.setMargin(lblDraws, new Insets(STATISTICS_MARGIN));

        statisticsContainer.setPrefWidth(WINDOW_WIDTH - BOARD_WIDTH);
        statisticsContainer.setPrefHeight(WINDOW_HEIGHT - BOARD_HEIGHT);

        GridPane.setConstraints(statisticsContainer, 0, 0);
        GridPane.setConstraints(gameControllersContainer, 0, 1);

        controllerContainer.getChildren().addAll(statisticsContainer, gameControllersContainer);

        container.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        container.setCenter(boardContainer);
        container.setRight(controllerContainer);

        readData();
        stage.setTitle("Tic Tac Toe");
        stage.setScene(new Scene(container));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
