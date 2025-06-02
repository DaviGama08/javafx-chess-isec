package pt.isec.pa.chess.ui;

import javafx.scene.layout.HBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import pt.isec.pa.chess.model.ChessGameManager;

import java.beans.PropertyChangeEvent;

public class StatusBarPane extends HBox  {
    private final ChessGameManager facade;
    private final Label lblTurn = new Label("Turno: –");

    public StatusBarPane(ChessGameManager facade) {
        super(20);
        this.facade = facade;
        createViews();
        registerHandlers();
    }

    public void createViews(){
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER);
        getChildren().add(lblTurn);
        lblTurn.setText("Turno: –");
    }

    public void registerHandlers(){
        facade.addPropertyChangeListener(ChessGameManager.PROP_TURN_CHANGED, this::handleTurnChanged);
        facade.addPropertyChangeListener(ChessGameManager.PROP_GAME_STARTED, this::handleNemGame);
    }
    public void setTurn(String playerName, String playerTeam) {
        if (!playerName.isEmpty())
            lblTurn.setText("Turno: " + playerName + " (" + playerTeam + ")");
    }

    private void handleTurnChanged(PropertyChangeEvent evt){
        if(!facade.isEmptyTurn()) {
            boolean isWhiteTurn = facade.isWhiteTurn();
            String playerName = facade.getPlayerName(isWhiteTurn);
            String playerTeam = isWhiteTurn ? "brancas" : "pretas";
            setTurn(playerName, playerTeam);
        }
    }

    private void handleNemGame(PropertyChangeEvent evt){
        setTurn(facade.getPlayerName(true), "brancas");
    }

}