package pt.isec.pa.chess.ui.views;

import javafx.scene.layout.HBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import pt.isec.pa.chess.model.data.Game.PlayerData;

public class StatusBarPane extends HBox {
    private final Label lblTurn = new Label("Turno: –");

    public StatusBarPane() {
        super(20);
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER);
        getChildren().add(lblTurn);
    }

    public void setTurn(PlayerData player) {
        if (player == null) {
            lblTurn.setText("Turno: –");
        } else {
            lblTurn.setText("Turno: " + player.name() + " (" + player.team() + ")");
        }
    }
}