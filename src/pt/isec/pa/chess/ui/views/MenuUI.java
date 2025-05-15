package pt.isec.pa.chess.ui.views;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MenuUI {
    MenuBar menuBar;
    private MenuItem newGame, openGame, saveGame, importGame, exportGame, quitGame;
    private MenuItem normalMode, learningMode, listOfMoves, undoMove, redoMove;

    public MenuUI() {
        createViews();
    }

    private void createViews(){
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
    }

    public MenuBar getMenuBar(){return menuBar;}
    public MenuItem getNewGame() {return newGame;}
    public MenuItem getOpenGame() {return openGame;}
    public MenuItem getSaveGame() {return saveGame;}
    public MenuItem getImportGame() {return importGame;}
    public MenuItem getExportGame() {return exportGame;}
    public MenuItem getQuitGame() {return quitGame;}
    public MenuItem getNormalMode() {return normalMode;}
    public MenuItem getLearningMode() {return learningMode;}
    public MenuItem getListOfMoves() {return listOfMoves;}
    public MenuItem getUndoMove() {return undoMove;}
    public MenuItem getRedoMove() {return redoMove;}
}
