package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.ui.alerts.AlertManager;

import java.beans.PropertyChangeEvent;

public class RootPane extends BorderPane {
    private final ChessGameManager facade;
    private BoardCanvas boardCanvas;

    VBox centerPane;
    HBox topPlayerInfo;
    HBox bottomPlayerInfo;
    StatusBarPane statusBar;
    IntroPane introPane;

    public RootPane(ChessGameManager facade) {
        this.facade = facade;

        createViews();
        registerHandlers();
    }

    private void createViews() {
        introPane                        = new IntroPane();
        boardCanvas                      = new BoardCanvas(facade);
        StackPane       boardPane        = new StackPane(boardCanvas);
        PlayerInfoPane  whiteBottomPane  = new PlayerInfoPane(facade, true);
        PlayerInfoPane  blackTopPane     = new PlayerInfoPane(facade, false);


        topPlayerInfo    = new HBox(10, blackTopPane);
        topPlayerInfo.setAlignment(Pos.CENTER);

        bottomPlayerInfo = new HBox(10, whiteBottomPane);
        bottomPlayerInfo.setAlignment(Pos.CENTER);

        centerPane = new VBox(5, topPlayerInfo, boardPane, bottomPlayerInfo);
        centerPane.setAlignment(Pos.CENTER);

        statusBar = new StatusBarPane(facade);

        MenuUI menuUI = new MenuUI(facade);

        setTop(menuUI.getMenuBar());
        setCenter(introPane);
        setBottom(null);

        boardPane.setMinSize(100, 100);
        boardPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(boardPane, Priority.ALWAYS);

        whiteBottomPane.setAlignment(Pos.CENTER);
        blackTopPane .setAlignment(Pos.CENTER);

        boardCanvas.setDisable(true);
        boardCanvas.widthProperty() .bind(boardPane.widthProperty());
        boardCanvas.heightProperty().bind(boardPane.heightProperty());

        boardPane.widthProperty() .addListener((_, _, n)-> { if (n.doubleValue() > 0) boardCanvas.draw(); });
        boardPane.heightProperty().addListener((_,_,n)-> { if (n.doubleValue() > 0) boardCanvas.draw(); });

        boardCanvas.widthProperty() .addListener((_,_,_)-> boardCanvas.draw());
        boardCanvas.heightProperty().addListener((_,_,_)-> boardCanvas.draw());
    }

    private void registerHandlers() {
        facade.addPropertyChangeListener(ChessGameManager.PROP_CHECKMATE, this::handleCheckMate);
        facade.addPropertyChangeListener(ChessGameManager.PROP_CHECK, this::handleCheck);
        facade.addPropertyChangeListener(ChessGameManager.PROP_GAME_STARTED, this::handleNewGame);
        facade.addPropertyChangeListener(ChessGameManager.PROP_GAME_OVER, this::handleGameOver);
    }

    private void handleCheckMate(PropertyChangeEvent evt) {
        boardCanvas.setDisable(true);
        boardCanvas.clearHighlights();
        boardCanvas.setSelected(-1, -1);

        String winner = facade.getWinner().orElse("Empate");
        String state = facade.getState() != null ? facade.getState().toString() : "Desconhecido";
        String title = "Fim de jogo!\nVencedor: " + winner + "\nEstado: " + state;


        AlertManager.getInstance().launchAlertBox(Alert.AlertType.INFORMATION, title, "", "");
    }

    private void handleCheck(PropertyChangeEvent evt) {
        boolean isWhiteTeam = (boolean)evt.getNewValue();
        Platform.runLater(() -> AlertManager.getInstance().launchAlertBox(Alert.AlertType.WARNING, "Xeque!",
                "Rei em perigo", "O rei das peças " + (isWhiteTeam ? "Brancas" : "Pretas") + " está em xeque."));
    }

    private void handleNewGame(PropertyChangeEvent evt){
        this.setCenter(centerPane);
        this.setBottom(statusBar);
    }

    private void handleGameOver(PropertyChangeEvent evt){
        String estadoFinal = facade.getState() != null ? facade.getState().toString() : "Desconhecido";
        String titulo;
        String msg;

        switch (estadoFinal) {
            case "CHECKMATE_WHITE_WON" -> {
                titulo = "Fim de jogo – Xeque-mate";
                msg    = "Vitória das Brancas";
            }
            case "CHECKMATE_BLACK_WON" -> {
                titulo = "Fim de jogo – Xeque-mate";
                msg    = "Vitória das Pretas";
            }
            case "STALEMATE" -> {
                titulo = "Fim de jogo – Empate";
                msg    = "Rei afogado (stalemate)";
            }
            case "DRAW_BY_INSUFFICIENT_MATERIAL" -> {
                titulo = "Fim de jogo – Empate";
                msg    = "Material insuficiente para dar mate";
            }
            default -> {
                titulo = "Fim de jogo";
                msg    = "Estado: " + estadoFinal;
            }
        }

        Alert alert = AlertManager.getInstance().launchAlertBox(
                Alert.AlertType.INFORMATION,
                titulo,
                "",
                msg
        );
        if(alert.getResult() == ButtonType.OK){
            this.setCenter(introPane);
            setBottom(null);
        }
    }
}
