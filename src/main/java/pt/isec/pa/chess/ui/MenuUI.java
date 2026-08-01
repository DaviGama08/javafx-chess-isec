package pt.isec.pa.chess.ui;

import javafx.scene.control.*;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.ui.alerts.AlertManager;
import pt.isec.pa.chess.ui.res.SoundManager;
import pt.isec.pa.chess.ui.services.FileChooserService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MenuUI {
    private final ChessGameManager facade;
    private MenuBar menuBar;

    private boolean  soundMuted;
    private String   toggleSoundText;

    private Menu learningSubMenu;
    private boolean learningModeActive;
    private String learningModeText;

    private String editModeText;
    private MenuItem newGame, openGame, saveGame, importGame, exportGame, quitGame;
    private MenuItem learningModeActivation, undoMove, redoMove, editMode, toggleSound;

    public MenuUI(ChessGameManager facade) {
        this.facade = facade;
        createViews();
        registerHandlers();
    }

    private void createViews() {
        menuBar = new MenuBar();
        this.learningModeActive = false;
        this.learningModeText = "Activate";
        this.editModeText = "Activate Edit Mode";

        this.soundMuted         = false;
        this.toggleSoundText    = "Disable Sound";

        Menu gameMenu = new Menu("Game");
        newGame = new MenuItem("New");
        openGame = new MenuItem("Open");
        saveGame = new MenuItem("Save");
        importGame = new MenuItem("Import");
        exportGame = new MenuItem("Export");
        quitGame = new MenuItem("Quit");
        editMode = new MenuItem(editModeText);

        Menu modeMenu = new Menu("Mode");
        MenuItem normalMode = new MenuItem("Normal");

        learningSubMenu = new Menu("Learning");
        learningModeActivation = new MenuItem(learningModeText);
        undoMove = new MenuItem("Undo Move");
        redoMove = new MenuItem("Redo Move");

        Menu soundMenu = new Menu("Sound");
        toggleSound = new MenuItem(toggleSoundText);

        soundMenu.getItems().add(toggleSound);

        gameMenu.getItems().addAll(newGame, openGame, saveGame, importGame, exportGame,
                new SeparatorMenuItem(), quitGame);

        learningSubMenu.getItems().addAll(learningModeActivation);
        modeMenu.getItems().addAll(normalMode, learningSubMenu, editMode);

        menuBar.getMenus().addAll(gameMenu, modeMenu, soundMenu);
    }

    private void registerHandlers(){
        newGame.setOnAction(_ -> initGame(true));
        openGame.setOnAction(_ -> handleOpenGame());
        saveGame.setOnAction(_ -> handleSaveGame());
        importGame.setOnAction(_ -> handleImportGame());
        exportGame.setOnAction(_ -> handleExportGame());
        quitGame.setOnAction(_ -> handleQuitGame());
        learningModeActivation.setOnAction(_ -> handleLearningMode());
        undoMove.setOnAction(_ -> handleUndoMove());
        redoMove.setOnAction(_ -> handleRedoMove());
        editMode.setOnAction(_ -> handleEditMode());
        toggleSound.setOnAction(_ -> handleToggleSound());
    }

    private void handleOpenGame(){
        File selectedFile = FileChooserService.showOpenGameDialog();

        if(selectedFile != null){
            if (!selectedFile.getName().toLowerCase().endsWith(".dat")) {
                AlertManager.getInstance().launchAlertBox(Alert.AlertType.ERROR, "Erro", "Ficheiro inválido", "O ficheiro selecionado não é um .dat válido.");
                return;
            }
            try {
                if(facade.loadGame(selectedFile)){
                    AlertManager.getInstance().launchAlertBox(Alert.AlertType.INFORMATION, "Jogo carregado de:\n" + selectedFile.getCanonicalPath(), "Sucesso", "");
                }
            } catch (IOException | ClassNotFoundException ex) {
                AlertManager.getInstance().launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao abrir jogo.", ex.getMessage());
            }
        }
    }

    private void handleSaveGame() {
        if (facade.isEditModeActive()) {
            String whiteName = facade.getPlayerName(true);
            String blackName = facade.getPlayerName(false);
            if(whiteName.isEmpty() || blackName.isEmpty()) {
                TextInputDialog dialogWhite = AlertManager.getInstance().launchDialogBox(true);
                dialogWhite.setTitle("White Player Name");
                dialogWhite.setHeaderText("Enter White player's name:");
                whiteName = dialogWhite.showAndWait().orElse(null);
                if (whiteName == null || whiteName.isEmpty()) {
                    return;
                }

                TextInputDialog dialogBlack = AlertManager.getInstance().launchDialogBox(false);
                dialogBlack.setTitle("Black Player Name");
                dialogBlack.setHeaderText("Enter Black player's name:");
                blackName = dialogBlack.showAndWait().orElse(null);
                if (blackName == null || blackName.isEmpty()) {
                    return;
                }
            }

            ButtonType whiteTurnBtn = new ButtonType("White's Turn");
            ButtonType blackTurnBtn = new ButtonType("Black's Turn");
            Alert turnDialog = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Who will play next?",
                    whiteTurnBtn, blackTurnBtn, ButtonType.CANCEL
            );
            turnDialog.setTitle("Choose Next Turn");
            turnDialog.setHeaderText("Select which side moves first in the saved configuration:");
            Optional<ButtonType> turnResult = turnDialog.showAndWait();
            if (turnResult.isEmpty() || turnResult.get() == ButtonType.CANCEL) {
                return;
            }
            boolean isWhiteTurn = (turnResult.get() == whiteTurnBtn);

            facade.configureEditedGameForSave(whiteName, blackName, isWhiteTurn);

            File destiny = FileChooserService.showSaveGameDialog();
            if (destiny != null) {
                try {
                    facade.saveGame(destiny);
                    AlertManager.getInstance().launchAlertBox(
                            Alert.AlertType.INFORMATION,
                            "Game saved to:\n" + destiny.getCanonicalPath(),
                            "Success",
                            ""
                    );
                } catch (IOException ex) {
                    AlertManager.getInstance().launchAlertBox(
                            Alert.AlertType.ERROR,
                            "Error",
                            "Failed to save game.",
                            ex.getMessage()
                    );
                }
            }
            return;
        }

        if (isGameStarted(false)) {
            File destiny = FileChooserService.showSaveGameDialog();
            if (destiny != null) {
                try {
                    facade.saveGame(destiny);
                    AlertManager.getInstance().launchAlertBox(
                            Alert.AlertType.INFORMATION,
                            "Jogo salvo de:\n" + destiny.getCanonicalPath(),
                            "Sucesso",
                            ""
                    );
                } catch (IOException ex) {
                    AlertManager.getInstance().launchAlertBox(
                            Alert.AlertType.ERROR,
                            "Erro",
                            "Erro ao guardar jogo.",
                            ex.getMessage()
                    );
                }
            }
        }
    }

    private void handleImportGame(){

        File selectedFile = FileChooserService.showImportGame();

        if(selectedFile != null){
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                AlertManager.getInstance().launchAlertBox(Alert.AlertType.ERROR, "Erro", "Ficheiro inválido", "O ficheiro selecionado não é um .txt válido.");
                return;
            }
            try {
                if(facade.importGame(selectedFile)){
                    if(!initGame(false))
                        return;
                    if(facade.isEmptyTurn())
                        return;
                    boolean isWhiteTurn = facade.isWhiteTurn();
                    String playerName = facade.getPlayerName(isWhiteTurn);
                    if (playerName.isEmpty())
                        return;
                    AlertManager.getInstance().launchAlertBox(Alert.AlertType.INFORMATION, "Jogo importado de:\n" + selectedFile.getCanonicalPath(), "Sucesso", "");
                }
            } catch (IOException ex) {
                AlertManager.getInstance().launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao importar jogo.", ex.getMessage());
            }
        }
    }

    private void handleExportGame() {
        if (facade.isEditModeActive()) {
            String whiteNameCurrent = facade.getPlayerName(true);
            String blackNameCurrent = facade.getPlayerName(false);

            ButtonType whiteTurnBtn = new ButtonType("White's Turn");
            ButtonType blackTurnBtn = new ButtonType("Black's Turn");
            Alert turnDialog = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Who will play next?",
                    whiteTurnBtn, blackTurnBtn, ButtonType.CANCEL
            );
            turnDialog.setTitle("Choose Next Turn");
            turnDialog.setHeaderText("Select which side moves first in the exported configuration:");
            Optional<ButtonType> turnResult = turnDialog.showAndWait();
            if (turnResult.isEmpty() || turnResult.get() == ButtonType.CANCEL) {
                return;
            }
            boolean isWhiteTurn = (turnResult.get() == whiteTurnBtn);

            facade.configureEditedGameForSave(whiteNameCurrent, blackNameCurrent, isWhiteTurn);

            File destiny = FileChooserService.showExportGame();
            if (destiny != null) {
                try {
                    if (facade.exportGame(destiny)) {
                        AlertManager.getInstance().launchAlertBox(
                                Alert.AlertType.INFORMATION,
                                "Game exported to:\n" + destiny.getCanonicalPath(),
                                "Success",
                                ""
                        );
                    }
                } catch (IOException ex) {
                    AlertManager.getInstance().launchAlertBox(
                            Alert.AlertType.ERROR,
                            "Error",
                            "Failed to export game.",
                            ex.getMessage()
                    );
                }
            }
            return;
        }

        if (isGameStarted(true)) {
            File destiny = FileChooserService.showExportGame();
            if (destiny != null) {
                try {
                    if (facade.exportGame(destiny)) {
                        AlertManager.getInstance().launchAlertBox(
                                Alert.AlertType.INFORMATION,
                                "Jogo exportado para:\n" + destiny.getCanonicalPath(),
                                "Sucesso",
                                ""
                        );
                    }
                } catch (IOException ex) {
                    AlertManager.getInstance().launchAlertBox(
                            Alert.AlertType.ERROR,
                            "Erro",
                            "Erro ao exportar jogo.",
                            ex.getMessage()
                    );
                }
            }
        }
    }

    private void handleQuitGame() {
        facade.quitGame();
        editModeText = "Activate Edit Mode";
        editMode.setText(editModeText);
    }

    private void handleLearningMode(){
        learningModeActive = !learningModeActive;
        facade.changeLearningMode(learningModeActive);
        if(learningModeActive){
            learningModeText = "Deactivate";
            learningSubMenu.getItems().addAll(undoMove, redoMove);
        }else{
            learningModeText = "Activate";
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

    private void handleEditMode() {
        if (!facade.isEditModeActive()) {
            facade.setEditModeActive(true);
            editModeText = "Deactivate Edit Mode";
            editMode.setText(editModeText);

            if (facade.getState() != null && facade.getState() != EChessState.NOT_STARTED) {
                boolean saveBefore = AlertManager.getInstance().confirmSaveBeforeExit(
                        "Save Current Game",
                        "Game in Progress",
                        "Do you want to save the current game before entering Edit Mode?"
                );
                if (saveBefore) {
                    handleSaveGame();
                }
                handleQuitGame();
            }

            facade.startEditMode();
            AlertManager.getInstance().launchAlertBox(
                    Alert.AlertType.INFORMATION,
                    "Edit Mode Activated",
                    "You are now in edit mode.",
                    "Click on squares to add or remove pieces."
            );
            return;
        }

        ButtonType saveEditsBtn    = new ButtonType("Save");
        ButtonType exportEditsBtn  = new ButtonType("Export");
        ButtonType discardEditsBtn = new ButtonType("Discard");
        Alert exitDialog = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Choose an action:",
                saveEditsBtn, exportEditsBtn, discardEditsBtn, ButtonType.CANCEL
        );
        exitDialog.setTitle("Exit Edit Mode");
        exitDialog.setHeaderText("Save changes, export, or discard and exit edit mode?");

        Optional<ButtonType> result = exitDialog.showAndWait();
        if (result.isEmpty() || result.get() == ButtonType.CANCEL) {
            return;
        }

        if (result.get() == discardEditsBtn) {
            facade.endEditMode();
            facade.setEditModeActive(false);
            editModeText = "Activate Edit Mode";
            editMode.setText(editModeText);
        }
        else if (result.get() == saveEditsBtn) {
            handleSaveGame();
            facade.endEditMode();
        }
        else {
            handleExportGame();
            facade.endEditMode();
        }
    }

    private boolean isGameStarted(boolean export) {
        boolean result = true;
        if(!facade.isStarted()){
            String  headerText = "Erro ao " + (export ? "exportar" : "guardar") + " o jogo";
            AlertManager.getInstance().launchAlertBox(Alert.AlertType.ERROR, "Erro", headerText, "Jogo não iniciado!");
            result = false;
        }
        return result;
    }

    private boolean initGame(boolean newGame) {
        if (facade.isEditModeActive()) {
            AlertManager.getInstance().launchAlertBox(
                    Alert.AlertType.WARNING,
                    "Edit Mode Active",
                    "Cannot start a new or imported game while Edit Mode is active.",
                    "Please deactivate Edit Mode first."
            );
            return false;
        }

        TextInputDialog dialogWhite = AlertManager.getInstance().launchDialogBox(true);
        String whiteName = dialogWhite.showAndWait().orElse(null);
        if (whiteName == null || whiteName.isEmpty())
            return false;

        TextInputDialog dialogBlack = AlertManager.getInstance().launchDialogBox(false);
        String blackName = dialogBlack.showAndWait().orElse(null);
        if (blackName == null || blackName.isEmpty())
            return false;

        if (newGame)
            facade.newGame(whiteName, blackName);
        else
            facade.setImportGameData(whiteName, blackName);

        return true;
    }

    private void handleToggleSound() {
        soundMuted = !soundMuted;
        SoundManager.setMuted(soundMuted);

        toggleSoundText = soundMuted ? "Enable Sound"
                : "Disable Sound";
        toggleSound.setText(toggleSoundText);
    }

    public MenuBar getMenuBar(){
        return menuBar;
    }
}
