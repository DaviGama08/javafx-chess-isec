package pt.isec.pa.chess.ui;

import javafx.scene.layout.HBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import pt.isec.pa.chess.model.ChessGameManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StatusBarPane extends HBox implements PropertyChangeListener {
    private final ChessGameManager facade;
    private final PlayerInfoPane whiteBottomPane;
    private final PlayerInfoPane blackTopPane;
    private final Label lblTurn = new Label("Turno: –");

    public StatusBarPane(ChessGameManager facade, PlayerInfoPane whiteBottomPane, PlayerInfoPane blackTopPane) {
        super(20);
        this.facade = facade;
        this.whiteBottomPane = whiteBottomPane;
        this.blackTopPane = blackTopPane;
        createViews();
        registerHandlers();
        facade.addPropertyChangeListener(ChessGameManager.PROP_TURN_CHANGED, this);
    }

    public void createViews(){
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER);
        getChildren().add(lblTurn);
        lblTurn.setText("Turno: –");
    }

    public void registerHandlers(){
    }
    public void setTurn(String playerName, String playerTeam) {
        if (!playerName.isEmpty())
            lblTurn.setText("Turno: " + playerName + " (" + playerTeam + ")");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        whiteBottomPane.setPlayerInfo(facade.getPlayerName(true), true);
        blackTopPane.setPlayerInfo(facade.getPlayerName(false), false);
        if(!facade.isEmptyTurn()) {
            boolean isWhiteTurn = facade.isWhiteTurn();
            String playerName = facade.getPlayerName(isWhiteTurn);
            String playerTeam = isWhiteTurn ? "brancas" : "pretas";
            setTurn(playerName, playerTeam);
        }
    }
}