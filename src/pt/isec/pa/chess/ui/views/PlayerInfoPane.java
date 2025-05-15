package pt.isec.pa.chess.ui.views;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;

public class PlayerInfoPane extends HBox {
    private final Label lblInfo = new Label();

    public PlayerInfoPane() {
        super(5);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(2));
        getChildren().add(lblInfo);

        setMaxHeight(Region.USE_PREF_SIZE); // altura mínima
    }

    public void setPlayerInfo(String name, ETeamColor color) {
        lblInfo.setText("Nome: " + name + " | Peças: " + (color == ETeamColor.WHITE_TEAM ? "Brancas" : "Pretas"));
    }
}
