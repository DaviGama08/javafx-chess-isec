package pt.isec.pa.chess.ui;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;

import java.io.File;
import java.io.IOException;

public class MenuUI {

    private MenuBar menuBar;
    private MenuItem newGame, openGame, saveGame, importGame, exportGame, quitGame;
    private MenuItem normalMode, LearningMode, listOfMoves, undoMove, redoMove;
    private ChessGameManager facade;
    private RootPane rootPane;

    public MenuUI(ChessGameManager facade, RootPane rootPane) {
        this.facade = facade;
        this.rootPane = rootPane;
    }

    public MenuItem getNewGame() {return newGame;}
    public MenuItem getOpenGame() {return openGame;}
    public MenuItem getSaveGame() {return saveGame;}
    public MenuItem getImportGame() {return importGame;}
    public MenuItem getExportGame() {return exportGame;}
    public MenuItem getQuitGame() {return quitGame;}
    public MenuItem getNormalMode() {return normalMode;}
    public MenuItem getLearningMode() {return LearningMode;}
    public MenuItem getListOfMoves() {return listOfMoves;}
    public MenuItem getUndoMove() {return undoMove;}
    public MenuItem getRedoMove() {return redoMove;}

    public MenuBar createMenu() {

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

        return menuBar;
    }

    public void newGame(){
        TextInputDialog dialogWhite = new TextInputDialog();
        dialogWhite.setTitle("New Game");
        dialogWhite.setHeaderText("Enter name for WHITE player:");
        dialogWhite.setContentText("White:");

        String whiteName = dialogWhite.showAndWait().orElse(null);
        if (whiteName == null || whiteName.isBlank())
            return;

        TextInputDialog dialogBlack = new TextInputDialog();
        dialogBlack.setTitle("New Game");
        dialogBlack.setHeaderText("Enter name for BLACK player:");
        dialogBlack.setContentText("Black:");

        String blackName = dialogBlack.showAndWait().orElse(null);
        if (blackName == null || blackName.isBlank())
            return;

        facade.newGame(whiteName, blackName);
        rootPane.boardEnabled(true);

        rootPane.update();
    }

    public void openGame(){
        System.out.println("Open Game.");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Game");
        new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat");

        File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if(selectedFile != null){
            try {
                boolean ok = facade.loadGame(selectedFile);
                if(ok){
                    rootPane.update();
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "Jogo carregado de:\n" + selectedFile.getAbsolutePath());
                    info.setHeaderText("Sucesso");
                    info.showAndWait();
                }
            } catch (IOException | ClassNotFoundException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Not possible to save game.");
                alert.setContentText(ex.getMessage()); // ou uma mensagem mais amigável
                alert.showAndWait();
            }
        }
    }

    public void saveGame(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Game");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat")
        );

        File destiny = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if(destiny != null){
            try {
                facade.saveGame(destiny);
                Alert info = new Alert(Alert.AlertType.INFORMATION,
                        "Jogo salvo em:\n" + destiny.getAbsolutePath());
                info.setHeaderText("Sucesso");
                info.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Not possible to save game.");
                alert.setContentText(ex.getMessage()); // ou uma mensagem mais amigável
                alert.showAndWait();
            }
        }
    }
}
