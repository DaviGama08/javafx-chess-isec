package pt.isec.pa.chess.ui;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import pt.isec.pa.chess.model.ChessGameManager;

import java.beans.PropertyChangeEvent;

public class PlayerInfoPane extends HBox {
    ChessGameManager facade;
    boolean isWhitePlayer;
    private final Label lblInfo = new Label();

    public PlayerInfoPane(ChessGameManager facade, boolean isWhitePlayer) {
        super(5);
        this.facade = facade;
        this.isWhitePlayer = isWhitePlayer;
        createViews();
        registerHandlers();
    }

    public void createViews(){
        setAlignment(Pos.CENTER);
        setPadding(new Insets(2));
        getChildren().add(lblInfo);
        setMaxHeight(Region.USE_PREF_SIZE); // altura máxima
    }

    public void registerHandlers(){
        facade.addPropertyChangeListener(ChessGameManager.PROP_UNDO_PERFORMED, this::handleNewGame);
        facade.addPropertyChangeListener(ChessGameManager.PROP_TURN_CHANGED, this::handleNewGame);
    }

    public void setPlayerInfo(String name, boolean isWhiteTeam) {
        lblInfo.setText("Nome: " + name + " | Peças: " + (isWhiteTeam ? "Brancas" : "Pretas"));
    }

    public void handleNewGame(PropertyChangeEvent evt){
        setPlayerInfo(facade.getPlayerName(isWhitePlayer),isWhitePlayer);
    }
}
