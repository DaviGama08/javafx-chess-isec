package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.ui.alerts.AlertManager;

import java.beans.PropertyChangeEvent;

public class RootPane extends BorderPane { //View-Controller
    //ANTES:
    //private final BoardCanvas boardCanvas;
    //private final StackPane boardPane;
    //private final IntroPane introPane;
    //private final PlayerInfoPane whiteBottomPane;
    //private final PlayerInfoPane blackTopPane;

    //DEPOIS:
    private final ChessGameManager facade;
    private final AlertManager alertManager;
    private BoardCanvas boardCanvas;

    VBox centerPane;
    HBox topPlayerInfo;
    HBox bottomPlayerInfo;

    public RootPane(ChessGameManager facade, AlertManager alertManager) {
        this.facade = facade;
        this.alertManager = alertManager;

        createViews();
        registerHandlers();
    }

    private void createViews() {
        IntroPane       introPane        = new IntroPane();
        boardCanvas                      = new BoardCanvas(facade, alertManager);
        StackPane       boardPane        = new StackPane(boardCanvas);
        PlayerInfoPane  whiteBottomPane  = new PlayerInfoPane();
        PlayerInfoPane  blackTopPane     = new PlayerInfoPane();

        topPlayerInfo    = new HBox(10, blackTopPane);
        topPlayerInfo.setAlignment(Pos.CENTER);

        bottomPlayerInfo = new HBox(10, whiteBottomPane);
        bottomPlayerInfo.setAlignment(Pos.CENTER);

        centerPane = new VBox(5, topPlayerInfo, boardPane, bottomPlayerInfo);
        centerPane.setAlignment(Pos.CENTER);

        new MenuUI(
                facade, alertManager, this,
                whiteBottomPane, blackTopPane,
                new StatusBarPane(facade, whiteBottomPane, blackTopPane),
                boardCanvas, introPane, centerPane
        );

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

        boardPane.widthProperty() .addListener((o,oldV,n)-> { if (n.doubleValue() > 0) boardCanvas.draw(); });
        boardPane.heightProperty().addListener((o,oldV,n)-> { if (n.doubleValue() > 0) boardCanvas.draw(); });

        boardCanvas.widthProperty() .addListener((o,oldV,n)-> boardCanvas.draw());
        boardCanvas.heightProperty().addListener((o,oldV,n)-> boardCanvas.draw());
    }

    private void registerHandlers() {
        facade.addPropertyChangeListener(ChessGameManager.PROP_GAME_OVER, this::onGameOver);
        facade.addPropertyChangeListener(ChessGameManager.PROP_CHECK, this::onCheck);
    }

    private void onGameOver(PropertyChangeEvent evt) {
        boardCanvas.setDisable(true);
        boardCanvas.clearHighlights();
        boardCanvas.setSelected(-1, -1);

        String winner = facade.getWinner().orElse("Empate");
        String state = facade.getState() != null ? facade.getState().toString() : "Desconhecido";
        String title = "Fim de jogo!\nVencedor: " + winner + "\nEstado: " + state;

        alertManager.launchAlertBox(Alert.AlertType.INFORMATION, title, "", "");
    }

    private void onCheck(PropertyChangeEvent evt) {
        boolean isWhiteTeam = (boolean)evt.getNewValue();
        Platform.runLater(() -> {
            alertManager.launchAlertBox(Alert.AlertType.WARNING, "Xeque!", "Rei em perigo", "O rei das peças "
                    + (isWhiteTeam ? "Brancas" : "Pretas")
                    + " está em xeque.");
        });
    }
}
