package pt.isec.pa.chess.ui;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class PlayerInfoPane extends HBox {
    private final Label lblInfo = new Label();

    public PlayerInfoPane() {
        super(5);
        createViews();
        registerHandlers();
    }

    public void createViews(){

        setAlignment(Pos.CENTER);
        setPadding(new Insets(2));
        getChildren().add(lblInfo);
        setMaxHeight(Region.USE_PREF_SIZE); // altura mínima
    }

    public void registerHandlers(){

    }

    public void setPlayerInfo(String name, boolean isWhiteTeam) {
        lblInfo.setText("Nome: " + name + " | Peças: " + (isWhiteTeam ? "Brancas" : "Pretas"));
    }
}
