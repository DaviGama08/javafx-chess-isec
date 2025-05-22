package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.ui.alerts.AlertManager;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;

public class MenuUI {
    private final ChessGameManager facade;
    private final RootPane rootPane;
    private final PlayerInfoPane whiteBottomPane;
    private final PlayerInfoPane blackTopPane;
    private final StatusBarPane statusBar;
    private final BoardCanvas boardCanvas;
    private final IntroPane introPane;
    private final VBox centerPane;
    private final AlertManager alertManager;
    private Menu learningSubMenu;
    private boolean learningModeActive;
    private String learningModeText;
    private MenuItem newGame, openGame, saveGame, importGame, exportGame, quitGame;
    private MenuItem learningModeActivation;
    private MenuItem undoMove;
    private MenuItem redoMove;

    public MenuUI(ChessGameManager facade, AlertManager alertManager, RootPane rootPane, PlayerInfoPane whiteBottomPane, PlayerInfoPane blackTopPane, StatusBarPane statusBar, BoardCanvas boardCanvas, IntroPane introPane, VBox centerPane) {
        this.rootPane = rootPane;
        this.facade = facade;
        this.whiteBottomPane = whiteBottomPane;
        this.blackTopPane = blackTopPane;
        this.statusBar = statusBar;
        this.boardCanvas = boardCanvas;
        this.introPane = introPane;
        this.centerPane = centerPane;
        this.alertManager = alertManager;
        this.learningModeActive = false;
        this.learningModeText = "Ativar";
        createViews();
        registerHandlers();
    }

    private void createViews() {
        MenuBar menuBar = new MenuBar();
        rootPane.setTop(menuBar);

        Menu gameMenu = new Menu("Game");
        newGame = new MenuItem("New");
        openGame = new MenuItem("Open");
        saveGame = new MenuItem("Save");
        importGame = new MenuItem("Import");
        exportGame = new MenuItem("Export");
        quitGame = new MenuItem("Quit");

        Menu modeMenu = new Menu("Mode");
        MenuItem normalMode = new MenuItem("Normal");

        learningSubMenu = new Menu("Learning");
        learningModeActivation = new MenuItem(learningModeText);
        undoMove = new MenuItem("Undo Move");
        redoMove = new MenuItem("Redo Move");

        gameMenu.getItems().addAll(newGame, openGame, saveGame, importGame, exportGame,
                new SeparatorMenuItem(), quitGame);
        learningSubMenu.getItems().addAll(learningModeActivation);
        modeMenu.getItems().addAll(normalMode, learningSubMenu);

        menuBar.getMenus().addAll(gameMenu, modeMenu);
    }

    private void registerHandlers(){
        facade.addPropertyChangeListener(ChessGameManager.PROP_UNDO_PERFORMED, this::onUndoPerformed);
        facade.addPropertyChangeListener(ChessGameManager.PROP_REDO_PERFORMED, this::onRedoPerformed);
        newGame.setOnAction(e -> handleNewGame());
        openGame.setOnAction(e -> handleOpenGame());
        saveGame.setOnAction(e -> handleSaveGame());
        importGame.setOnAction(e -> handleImportGame());
        exportGame.setOnAction(e -> handleExportGame());
        quitGame.setOnAction(e -> handleQuitGame());
        learningModeActivation.setOnAction(e -> handleLearningMode());
        undoMove.setOnAction(e -> handleUndoMove());
        redoMove.setOnAction(e -> handleRedoMove());
    }

    // Menu Options Handles
    public void handleNewGame(){
        if(!initGame(true))
            return;
        boardCanvas.setDisable(false);
        rootPane.setCenter(centerPane);
        rootPane.setBottom(statusBar);
        Platform.runLater(boardCanvas::draw);

        String whiteName = facade.getPlayerName(true);
        String blackName = facade.getPlayerName(false);

        whiteBottomPane.setPlayerInfo(whiteName, true);
        blackTopPane.setPlayerInfo(blackName, false);

        boardCanvas.clearHighlights();
        boardCanvas.draw();
        // AINDA NAO HA JOGADORES
        statusBar.setTurn(whiteName, "brancas");
    }

    public void handleOpenGame(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Jogo");
        new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat");

        File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if(selectedFile != null){
            if (!selectedFile.getName().toLowerCase().endsWith(".dat")) {
                alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Ficheiro inválido", "O ficheiro selecionado não é um .dat válido.");
                return;
            }
            try {
                if(facade.loadGame(selectedFile)){
                    boardCanvas.setDisable(false);
                    rootPane.setCenter(centerPane);
                    Platform.runLater(boardCanvas::draw);
                    rootPane.setBottom(statusBar);
                    boardCanvas.clearHighlights();
                    boardCanvas.draw();
                    alertManager.launchAlertBox(Alert.AlertType.INFORMATION, "Jogo carregado de:\n" + selectedFile.getCanonicalPath(), "Sucesso", "");
                }
            } catch (IOException | ClassNotFoundException ex) {
                alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao abrir jogo.", ex.getMessage());
            }
        }
    }

    public void handleSaveGame(){
        if(isGameStarted(false)) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Jogo");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat")
            );

            File destiny = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
            if(destiny != null){
                try {
                    facade.saveGame(destiny);
                    alertManager.launchAlertBox(Alert.AlertType.INFORMATION, "Jogo salvo de:\n" + destiny.getCanonicalPath(), "Sucesso", "");
                } catch (IOException ex) {
                    alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao guardar jogo.", ex.getMessage());
                }
            }
        }
    }

    public void handleImportGame(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Jogo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Jogo (*.txt)", "*.txt")
        );

        File selectedFile = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if(selectedFile != null){
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Ficheiro inválido", "O ficheiro selecionado não é um .txt válido.");
                return;
            }
            try {
                if(facade.importGame(selectedFile)){
                    if(!initGame(false))
                        return;
                    boardCanvas.setDisable(false);
                    rootPane.setCenter(centerPane);
                    Platform.runLater(boardCanvas::draw);
                    rootPane.setBottom(statusBar);
                    String whiteName = facade.getPlayerName(true);
                    String blackName = facade.getPlayerName(false);

                    whiteBottomPane.setPlayerInfo(whiteName, true);
                    blackTopPane.setPlayerInfo(blackName, false);

                    // AINDA NAO HA JOGADORES
                    if(facade.isEmptyTurn())
                        return;
                    boolean isWhiteTurn = facade.isWhiteTurn();
                    String playerName = facade.getPlayerName(isWhiteTurn);
                    String playerTeam = isWhiteTurn ? "brancas" : "pretas";
                    statusBar.setTurn(playerName, playerTeam);
                    if (playerName.isEmpty())
                        return;
                    boardCanvas.clearHighlights();
                    boardCanvas.draw();
                    alertManager.launchAlertBox(Alert.AlertType.INFORMATION, "Jogo importado de:\n" + selectedFile.getCanonicalPath(), "Sucesso", "");
                }
            } catch (IOException ex) {
                alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao importar jogo.", ex.getMessage());
            }
        }
    }

    public void handleExportGame(){
        if(isGameStarted(true))
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exportar Jogo");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivos de Jogo (*.txt)", "*.txt")
            );
            File destiny = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
            if(destiny != null){
                try {
                    if(facade.exportGame(destiny))
                        alertManager.launchAlertBox(Alert.AlertType.INFORMATION, "Jogo exportado para:\n" + destiny.getCanonicalPath(), "Sucesso", "");
                } catch (IOException ex) {
                    alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao exportar jogo.", ex.getMessage());
                }
            }
        }
    }

    private void handleQuitGame() {
        rootPane.setCenter(introPane);
        boardCanvas.setDisable(true);
        boardCanvas.clearHighlights();
        boardCanvas.setSelected(-1, -1);
    }

    private void handleLearningMode(){
        learningModeActive = !learningModeActive;
        boardCanvas.setLearningMode(learningModeActive);
        if(learningModeActive){
            learningModeText = "Desativar";
            learningSubMenu.getItems().addAll(undoMove, redoMove);
        }else{
            learningModeText = "Ativar";
            learningSubMenu.getItems().removeAll(undoMove, redoMove);
        }
        learningModeActivation.setText(learningModeText);
    }

    private void handleUndoMove() {
        facade.undo();
    }

    private void handleRedoMove() {
        facade.redo();
    }

    private void onUndoPerformed(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            boardCanvas.clearSelection();
            boardCanvas.clearHighlights();
            boardCanvas.draw();
        });
    }

    private void onRedoPerformed(PropertyChangeEvent evt) {
        Platform.runLater(() -> {
            boardCanvas.clearSelection();
            boardCanvas.clearHighlights();
            boardCanvas.draw();
        });
    }

    //Aux functions
    public boolean isGameStarted(boolean export) {
        boolean result = true;
        if(!facade.isStarted()){
            String  headerText = "Erro ao " + (export ? "exportar" : "guardar") + " o jogo";
            alertManager.launchAlertBox(Alert.AlertType.ERROR, "Erro", headerText, "Jogo não iniciado!");
            result = false;
        }
        return result;
    }

    private boolean initGame (boolean newGame)
    {
        TextInputDialog dialogWhite = alertManager.launchDialogBox(true);

        String whiteName = dialogWhite.showAndWait().orElse(null);
        if (whiteName == null || whiteName.isBlank())
            return false;

        TextInputDialog dialogBlack = alertManager.launchDialogBox(false);

        String blackName = dialogBlack.showAndWait().orElse(null);
        if (blackName == null || blackName.isBlank())
            return false;

        if(newGame)
            facade.newGame(whiteName, blackName);
        else
            facade.setImportGameData(whiteName, blackName);
        return true;
    }

}
