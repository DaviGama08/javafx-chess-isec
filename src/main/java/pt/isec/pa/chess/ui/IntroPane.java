package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class IntroPane extends VBox {
    public IntroPane() {
        super(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));

        Label title = new Label("JavaFX Chess");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label description = new Label("Local two-player chess");
        description.setStyle("-fx-font-size: 20px; -fx-font-style: italic;");

        getChildren().addAll(title, description);
    }

}
