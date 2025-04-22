package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import pt.isec.pa.chess.model.data.Game.ChessGame;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;

import java.io.IOException;

public class RootPane extends BorderPane { //View-Controller
    ChessGameManager facade;
    private MenuBar menuBar;
    private MenuItem newGame, openGame, saveGame, importGame, exportGame, quitGame;
    private MenuItem normalMode, LearningMode, listOfMoves, undoMove, redoMove;
    // variables, including reference to views
    Label lbExample,lbCounter;
    TextField tfExample;
    Button btnExample;

    public RootPane(ChessGameManager facade) {
        this.facade = facade;

        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {

        menuBar = new MenuBar();

        Menu gameMenu = new Menu("Game");
        newGame = new MenuItem("New");
        openGame = new MenuItem("Open");
        saveGame = new MenuItem("Save");
        importGame = new MenuItem("Import");
        exportGame = new MenuItem("Export");
        quitGame = new MenuItem("Quit");

        Menu modeMenu = new Menu("Mode");
        normalMode = new MenuItem("Normal");

        Menu learningSubMenu = new Menu("Learning");
        listOfMoves = new MenuItem("List of Moves");
        undoMove = new MenuItem("Undo Move");
        redoMove = new MenuItem("Redo Move");

        gameMenu.getItems().addAll(newGame, openGame, saveGame, importGame, exportGame, new SeparatorMenuItem(), quitGame);
        learningSubMenu.getItems().addAll(listOfMoves, undoMove, redoMove);
        modeMenu.getItems().addAll(normalMode, learningSubMenu);

        menuBar.getMenus().add(gameMenu);
        menuBar.getMenus().add(modeMenu);
        this.setTop(menuBar);

        HBox content = new HBox(10);
        content.setPadding(new Insets(16));
        content.setAlignment(Pos.BASELINE_LEFT);

        lbExample = new Label("Name:");
        tfExample = new TextField();
        btnExample = new Button("Confirm");
        lbCounter = new Label();
        lbCounter.setStyle("-fx-font-family: 'Courier New'; -fx-background-color: #c0c0ff;");

        content.getChildren().addAll(lbExample, tfExample, btnExample, lbCounter);
        this.setCenter(content);
    }

    private void registerHandlers() {
        /* handlers/listeners */
        btnExample.setOnAction(actionEvent -> {
            System.out.println(tfExample.getText());
            update();
        });

        newGame.setOnAction(e -> {
            System.out.println("New Game.");
        });

        openGame.setOnAction(e -> {
            System.out.println("Open Game.");
        });

        saveGame.setOnAction(e -> {
            try {
                facade.saveGame();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Not possible to save game.");
                alert.setContentText(ex.getMessage()); // ou uma mensagem mais amigável
                alert.showAndWait();
            }
        });

        importGame.setOnAction(e -> {
            facade.importGame();
        });

        exportGame.setOnAction(e -> {
            facade.exportGame();
        });

        quitGame.setOnAction(e -> {
            System.exit(0);
        });
    }

    private void update() {
        // Update
    }
}
