package pt.isec.pa.chess.ui.controllers;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;
import pt.isec.pa.chess.model.data.Game.PlayerData;
import pt.isec.pa.chess.model.data.Game.Position;
import pt.isec.pa.chess.model.data.Game.PositionData;
import pt.isec.pa.chess.model.data.Pieces.Piece;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.views.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class RootPane extends BorderPane implements PropertyChangeListener { //View-Controller
    private final ChessGameManager facade;
    private final MenuUI menuBar;
    private final BoardCanvas boardCanvas;
    private final StackPane boardPane;
    private final IntroPane introPane;
    private Position selected = null;
    private final StatusBarPane statusBar;
    private final PlayerInfoPane whiteBottomPane;
    private final PlayerInfoPane blackTopPane;
    VBox centerPane;
    HBox topPlayerInfo;
    HBox bottomPlayerInfo;

    public RootPane(ChessGameManager facade) {
        this.facade = facade;
        this.facade.addPropertyChangeListener(this);
        this.menuBar = new MenuUI();
        this.introPane = new IntroPane();
        this.boardCanvas = new BoardCanvas();
        this.boardPane = new StackPane(boardCanvas);
        this.statusBar = new StatusBarPane();
        this.whiteBottomPane = new PlayerInfoPane();
        this.blackTopPane = new PlayerInfoPane();
        this.topPlayerInfo = new HBox(10, blackTopPane);
        topPlayerInfo.setAlignment(Pos.CENTER);
        this.bottomPlayerInfo = new HBox(10, whiteBottomPane);
        bottomPlayerInfo.setAlignment(Pos.CENTER);
        this.centerPane = new VBox(5, topPlayerInfo, boardPane, bottomPlayerInfo);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setAlignment(Pos.CENTER);

        createViews();
        registerHandlers();
    }

    private void createViews() {
        setTop(menuBar.getMenuBar());
        setCenter(introPane);
        setBottom(null);

        centerPane.setAlignment(Pos.CENTER);
        boardPane.setMinSize(100, 100);
        boardCanvas.setDisable(true);
        VBox.setVgrow(boardPane, Priority.ALWAYS);
        boardPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        whiteBottomPane.setAlignment(Pos.CENTER);
        blackTopPane.setAlignment(Pos.CENTER);

        boardCanvas.widthProperty().bind(boardPane.widthProperty());
        boardCanvas.heightProperty().bind(boardPane.heightProperty());

        boardPane.widthProperty().addListener((obs, o, n) -> {
            if (n.doubleValue() > 0) boardCanvas.draw();
        });
        boardPane.heightProperty().addListener((obs, o, n) -> {
            if (n.doubleValue() > 0) boardCanvas.draw();
        });

        boardCanvas.widthProperty().addListener((obs, o, n) -> boardCanvas.draw());
        boardCanvas.heightProperty().addListener((obs, o, n) -> boardCanvas.draw());
    }

    private void registerHandlers() {
        menuBar.getNewGame().setOnAction(e -> handleNewGame());
        menuBar.getOpenGame().setOnAction(e -> handleOpenGame());
        menuBar.getSaveGame().setOnAction(e -> handleSaveGame());
        menuBar.getImportGame().setOnAction(e -> handleImportGame());
        menuBar.getExportGame().setOnAction(e -> handleExportGame());
        menuBar.getQuitGame().setOnAction(e -> handleQuitGame());

        boardCanvas.setOnPositionSelected(this::handleBoardClick);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ChessGameManager.PROP_BOARD_CHANGED -> {
                try{
                    boardCanvas.setBoard(facade.getBoard());
                    boardCanvas.draw();

                    PlayerData current = facade.getCurrentPlayer();
                    statusBar.setTurn(current);
                    if (current == null)
                        return;

                    PositionData pp = facade.validatePawnPromotion();
                    if (pp != null) {
                        Dialog<EPieceType> dlg = new Dialog<>();
                        dlg.setTitle("Promoção de Peão");
                        dlg.setHeaderText("Escolha a peça para promoção:");

                        ButtonType btQ = new ButtonType("Dama",  ButtonBar.ButtonData.OK_DONE);
                        ButtonType btR = new ButtonType("Torre", ButtonBar.ButtonData.OK_DONE);
                        ButtonType btB = new ButtonType("Bispo", ButtonBar.ButtonData.OK_DONE);
                        ButtonType btN = new ButtonType("Cavalo",ButtonBar.ButtonData.OK_DONE);
                        dlg.getDialogPane().getButtonTypes().setAll(btQ, btR, btB, btN);

                        dlg.setResultConverter(btn -> {
                            if (btn == btQ) return EPieceType.QUEEN;
                            if (btn == btR) return EPieceType.ROOK;
                            if (btn == btB) return EPieceType.BISHOP;
                            if (btn == btN) return EPieceType.KNIGHT;
                            return null;
                        });

                        dlg.showAndWait().ifPresent(choice -> facade.promotePawn(new Position(pp.col(), pp.row()), choice));

                        boardCanvas.setBoard(facade.getBoard());
                        boardCanvas.draw();
                    }
                } catch (Exception e) {
                    launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao desenhar tabuleiro.", e.getMessage());
                }
            }
            case ChessGameManager.PROP_TURN_CHANGED -> {
                PlayerData current = facade.getCurrentPlayer();
                whiteBottomPane.setPlayerInfo(facade.getPlayerName(ETeamColor.WHITE_TEAM), ETeamColor.WHITE_TEAM);
                blackTopPane.setPlayerInfo(facade.getPlayerName(ETeamColor.BLACK_TEAM), ETeamColor.BLACK_TEAM);
                statusBar.setTurn(current);
            }
            case ChessGameManager.PROP_CHECK -> {
                ETeamColor sideInCheck = (ETeamColor)evt.getNewValue();
                statusBar.setTurn(facade.getCurrentPlayer());
                statusBar.getChildren().add(new Label(" – XEQUE!"));
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Xeque!");
                    alert.setHeaderText("Rei em perigo");
                    alert.setContentText("O rei das peças "
                            + (sideInCheck==ETeamColor.WHITE_TEAM ? "Brancas" : "Pretas")
                            + " está em xeque.");
                    alert.showAndWait();
                });
            }
            case ChessGameManager.PROP_GAME_OVER -> {
                boardCanvas.setDisable(true);
                selected = null;
                boardCanvas.clearHighlights();
                boardCanvas.setSelected(null);

                String winner = facade.getWinner().orElse("Empate");
                String state = facade.getState() != null ? facade.getState().toString() : "Desconhecido";
                String title = "Fim de jogo!\nVencedor: " + winner + "\nEstado: " + state;

                launchAlertBox(Alert.AlertType.INFORMATION, title, "", "");
            }

        }
    }

    // Menu Options Handles
    public void handleNewGame(){
        if(!initGame(true))
            return;
        boardCanvas.setBoard(facade.getBoard());
        boardCanvas.setDisable(false);
        setCenter(centerPane);
        setBottom(statusBar);
        Platform.runLater(boardCanvas::draw);

        String whiteName = facade.getPlayerName(ETeamColor.WHITE_TEAM);
        String blackName = facade.getPlayerName(ETeamColor.BLACK_TEAM);

        whiteBottomPane.setPlayerInfo(whiteName, ETeamColor.WHITE_TEAM);
        blackTopPane.setPlayerInfo(blackName, ETeamColor.BLACK_TEAM);

        boardCanvas.clearHighlights();
        boardCanvas.setBoard(facade.getBoard());
        boardCanvas.draw();
        // AINDA NAO HA JOGADORES
        PlayerData current = facade.getCurrentPlayer();
        statusBar.setTurn(current);
    }

    public void handleOpenGame(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Jogo");
        new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat");

        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if(selectedFile != null){
            if (!selectedFile.getName().toLowerCase().endsWith(".dat")) {
                launchAlertBox(Alert.AlertType.ERROR, "Erro", "Ficheiro inválido", "O ficheiro selecionado não é um .dat válido.");
                return;
            }
            try {
                if(facade.loadGame(selectedFile)){
                    boardCanvas.setBoard(facade.getBoard());
                    boardCanvas.setDisable(false);
                    setCenter(centerPane);
                    Platform.runLater(boardCanvas::draw);
                    setBottom(statusBar);
                    boardCanvas.clearHighlights();
                    boardCanvas.setBoard(facade.getBoard());
                    boardCanvas.draw();
                    launchAlertBox(Alert.AlertType.INFORMATION, "Jogo carregado de:\n" + selectedFile.getCanonicalPath(), "Sucesso", "");
                }
            } catch (IOException | ClassNotFoundException ex) {
                launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao abrir jogo.", ex.getMessage());
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

            File destiny = fileChooser.showSaveDialog(this.getScene().getWindow());
            if(destiny != null){
                try {
                    facade.saveGame(destiny);
                    launchAlertBox(Alert.AlertType.INFORMATION, "Jogo salvo de:\n" + destiny.getCanonicalPath(), "Sucesso", "");
                } catch (IOException ex) {
                    launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao guardar jogo.", ex.getMessage());
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

        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());

        if(selectedFile != null){
            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                launchAlertBox(Alert.AlertType.ERROR, "Erro", "Ficheiro inválido", "O ficheiro selecionado não é um .txt válido.");
                return;
            }
            try {
                if(facade.importGame(selectedFile)){
                    if(!initGame(false))
                        return;
                    boardCanvas.setBoard(facade.getBoard());
                    boardCanvas.setDisable(false);
                    setCenter(centerPane);
                    Platform.runLater(boardCanvas::draw);
                    setBottom(statusBar);
                    String whiteName = facade.getPlayerName(ETeamColor.WHITE_TEAM);
                    String blackName = facade.getPlayerName(ETeamColor.BLACK_TEAM);

                    whiteBottomPane.setPlayerInfo(whiteName, ETeamColor.WHITE_TEAM);
                    blackTopPane.setPlayerInfo(blackName, ETeamColor.BLACK_TEAM);

                    // AINDA NAO HA JOGADORES
                    PlayerData current = facade.getCurrentPlayer();
                    statusBar.setTurn(current);
                    if (current == null)
                        return;
                    boardCanvas.clearHighlights();
                    boardCanvas.setBoard(facade.getBoard());
                    boardCanvas.draw();
                    launchAlertBox(Alert.AlertType.INFORMATION, "Jogo importado de:\n" + selectedFile.getCanonicalPath(), "Sucesso", "");
                }
            } catch (IOException ex) {
                launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao importar jogo.", ex.getMessage());
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
            File destiny = fileChooser.showSaveDialog(this.getScene().getWindow());
            if(destiny != null){
                try {
                    if(facade.exportGame(destiny))
                        launchAlertBox(Alert.AlertType.INFORMATION, "Jogo exportado para:\n" + destiny.getCanonicalPath(), "Sucesso", "");
                } catch (IOException ex) {
                    launchAlertBox(Alert.AlertType.ERROR, "Erro", "Erro ao exportar jogo.", ex.getMessage());
                }
            }
        }
    }

    private void handleQuitGame() {
        setCenter(introPane);
        boardCanvas.setDisable(true);
        selected = null;
        boardCanvas.clearHighlights();
        boardCanvas.setSelected(null);
    }

    //Auxiliar Methods
    private void handleBoardClick(Position pos) {
        if (selected == null) {
            Piece piece = facade.getPieceAt(pos);
            PlayerData cp = facade.getCurrentPlayer();

            if (piece == null || piece.getTeam() != cp.team())
                return;

            selected = pos;
            boardCanvas.setSelected(pos);
            boardCanvas.setHighlights(facade.getValidMoves(pos));
            return;
        } else {

            Piece target = facade.getPieceAt(pos);
            if (target != null && target.getTeam() == facade.getCurrentPlayer().team()) {
                selected = pos;
                boardCanvas.setSelected(pos);
                boardCanvas.setHighlights(facade.getValidMoves(pos));
                return;
            }

            if (selected.equals(pos)) {
                selected = null;
                boardCanvas.setSelected(null);
                boardCanvas.clearHighlights();
                return;
            }

            if (!facade.getValidMoves(selected).contains(pos)) {
                boardCanvas.setHighlights(facade.getValidMoves(selected));
                return;
            }

            boolean moved = facade.movePiece(selected.getCol(), selected.getRow(),
                    pos.getCol(), pos.getRow());
            if (moved) {
                selected = null;
                boardCanvas.setSelected(null);
                boardCanvas.clearHighlights();
            }
        }
    }

    public boolean isGameStarted(boolean export) {
        boolean result = true;
        if(!facade.isStarted()){
            String  headerText = "Erro ao " + (export ? "exportar" : "guardar") + " o jogo";
            launchAlertBox(Alert.AlertType.ERROR, "Erro", headerText, "Jogo não iniciado!");
            result = false;
        }
        return result;
    }

    private void launchAlertBox(Alert.AlertType alertType, String title, String headerText, String contextText) {
        Alert alert;
        if(alertType == Alert.AlertType.INFORMATION) {
            alert = new Alert(alertType, title);
        }
        else {
            alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(contextText);
        }
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private TextInputDialog launchDialogBox(boolean isWhite){
        String header = "Indique o nome do jogador " + (isWhite?"branco":"preto");
        TextInputDialog dialogBox = new TextInputDialog();
        dialogBox.setTitle("Novo Jogo");
        dialogBox.setHeaderText(header);
        dialogBox.setContentText("Nome:");
        dialogBox.setGraphic(getImageView(isWhite));
        return dialogBox;
    }

    private ImageView getImageView (boolean isWhite) {
        String img =  (isWhite ? "kingW" : "kingB") + ".png";
        Image image = ImageManager.getImage(img);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        return imageView;
    }

    private boolean initGame (boolean newGame)
    {
        TextInputDialog dialogWhite = launchDialogBox(true);

        String whiteName = dialogWhite.showAndWait().orElse(null);
        if (whiteName == null || whiteName.isBlank())
            return false;

        TextInputDialog dialogBlack = launchDialogBox(false);

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
