package pt.isec.pa.chess.ui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;
import pt.isec.pa.chess.model.data.Game.PlayerData;
import pt.isec.pa.chess.model.data.Game.Position;
import pt.isec.pa.chess.model.data.Game.PositionData;

public class RootPane extends BorderPane { //View-Controller
    ChessGameManager facade;
    BoardCanvas boardCanvas;
    MenuUI menuBar;

    HBox statusBar;
    Label lblWhite, lblBlack, lblTurn;

    public RootPane(ChessGameManager facade) {
        this.facade = facade;
        this.menuBar = new MenuUI(facade, this);
        createViews();
        registerHandlers();
    }

    private void createViews() {
        setTop(menuBar.createMenu());

        boardCanvas = new BoardCanvas(facade,this::update);
        /*  começa bloqueado – só é libertado depois de New game  */
        boardCanvas.setDisable(true);

        Pane areaPane = new Pane(boardCanvas);
        setCenter(areaPane);

        lblWhite = new Label("White: –");
        lblBlack = new Label("Black: –");
        lblTurn  = new Label("Turn: –");

        lblWhite.setStyle("-fx-font-weight: bold;");
        lblBlack.setStyle("-fx-font-weight: bold;");
        lblTurn .setStyle("-fx-font-weight: bold;");

        statusBar = new HBox(20, lblWhite, lblBlack, lblTurn);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(5));

        setBottom(statusBar);

        areaPane.widthProperty().addListener( obs ->
                boardCanvas.updateSize(areaPane.getWidth(), areaPane.getHeight()) );
        areaPane.heightProperty().addListener( obs ->
                boardCanvas.updateSize(areaPane.getWidth(), areaPane.getHeight()) );
    }

    private void registerHandlers() {

        menuBar.getNewGame().setOnAction(e -> {
            menuBar.newGame();
        });

        menuBar.getOpenGame().setOnAction(e -> {
            menuBar.openGame();
        });

        menuBar.getSaveGame().setOnAction(e -> {
            menuBar.saveGame();
        });

        menuBar.getImportGame().setOnAction(e -> {
            facade.importGame();
        });

        menuBar.getExportGame().setOnAction(e -> {
            facade.exportGame();
        });

        menuBar.getQuitGame().setOnAction(e -> {
            System.exit(0);
        });
    }

    public void update() {
        try {
            boardCanvas.draw();
            // AINDA NAO HA JOGADORES
            PlayerData cur = facade.getCurrentPlayer();
            if (cur == null) {
                lblWhite.setText("White: –");
                lblBlack.setText("Black: –");
                lblTurn .setText("Turn:  –");
                return;
            }

            //NOME DOS JOGADOR PRETO E BRANCO | NOME DO JOGADOR ATUAL
            PlayerData current = facade.getCurrentPlayer();
            lblWhite.setText("White: " + (facade.getPlayerName(ETeamColor.WHITE_TEAM)));
            lblBlack.setText("Black: " + (facade.getPlayerName(ETeamColor.BLACK_TEAM)));
            lblTurn .setText("Turn:  " + current.name() + " (" + current.team() + ")");

            PositionData pp = facade.validatePawnPromotion();
            if (pp != null) {
                Dialog<EPieceType> dlg = new Dialog<>();
                dlg.setTitle("Promoção de Peão");
                dlg.setHeaderText("Escolha a peça para promoção:");

                ButtonType btQ = new ButtonType("Dama",  ButtonBar.ButtonData.OK_DONE);
                ButtonType btR = new ButtonType("Torre", ButtonBar.ButtonData.OK_DONE);
                ButtonType btB = new ButtonType("Bispo", ButtonBar.ButtonData.OK_DONE);
                ButtonType btN = new ButtonType("Cavalo",ButtonBar.ButtonData.OK_DONE);
                dlg.getDialogPane().getButtonTypes()
                        .setAll(btQ, btR, btB, btN, ButtonType.CANCEL);

                dlg.setResultConverter(btn -> {
                    if (btn == btQ) return EPieceType.QUEEN;
                    if (btn == btR) return EPieceType.ROOK;
                    if (btn == btB) return EPieceType.BISHOP;
                    if (btn == btN) return EPieceType.KNIGHT;
                    return null;
                });

                EPieceType choice = dlg.showAndWait().orElse(null);
                if (choice != null)
                    facade.promotePawn(new Position(pp.col(), pp.row()), choice);

                boardCanvas.draw();
            }

            if (facade.gameOver()) {
                boardCanvas.setDisable(true);

                String winner = facade.getWinner().orElse("Ninguém");
                String state = facade.getState() != null ? facade.getState().toString() : "Desconhecido";

                Alert end = new Alert(Alert.AlertType.INFORMATION,
                        "Fim de jogo!\nVencedor: " + winner +
                                "\nEstado: " + state);
                end.setHeaderText(null);
                end.showAndWait();
            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error drawing board.");
            alert.setContentText("Error trying to draw the board.\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    public void boardEnabled(boolean on) {
        boardCanvas.setDisable(!on);
    }
}
