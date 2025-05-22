package pt.isec.pa.chess.ui.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.isec.pa.chess.ui.res.ImageManager;

public class AlertManager implements IAlertManager {
    @Override
    public void launchAlertBox(Alert.AlertType alertType, String title, String headerText, String contextText) {
        Alert alert;
        if(alertType == Alert.AlertType.INFORMATION) {
            alert = new Alert(alertType, title);
        }
        else {
            alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(contextText);
        }
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
    @Override
    public TextInputDialog launchDialogBox(boolean isWhite){
        String header = "Indique o nome do jogador " + (isWhite?"branco":"preto");
        TextInputDialog dialogBox = new TextInputDialog();
        dialogBox.setTitle("Novo Jogo");
        dialogBox.setHeaderText(header);
        dialogBox.setContentText("Nome:");
        dialogBox.setGraphic(getImageView(isWhite));
        return dialogBox;
    }

    @Override
    public ImageView getImageView (boolean isWhite) {
        String img =  (isWhite ? "kingW" : "kingB") + ".png";
        Image image = ImageManager.getImage(img);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(60);
        imageView.setFitWidth(60);
        return imageView;
    }

}
