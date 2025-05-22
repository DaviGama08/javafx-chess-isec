package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import pt.isec.pa.chess.ui.res.ImageManager;

public class IntroPane extends VBox {
    public IntroPane() {
        super(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));

        Label lblUC = new Label("Programação Avançada");
        lblUC.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label lblProjeto = new Label("Jogo de Xadrez");
        lblProjeto.setStyle("-fx-font-size: 20px; -fx-font-style: italic;");

        ImageView image = new ImageView(ImageManager.getImage("isec.jpg"));
        image.setFitWidth(200);
        image.setPreserveRatio(true);

        Label lblAutores = new Label("Davi Nasser, Miguel Lopes, Ruben Seco");
        lblAutores.setStyle("-fx-font-size: 14px;");

        getChildren().addAll(lblUC, lblProjeto, image, lblAutores);
    }

}
