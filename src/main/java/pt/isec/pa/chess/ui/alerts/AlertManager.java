package pt.isec.pa.chess.ui.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pt.isec.pa.chess.ui.res.ImageManager;

public class AlertManager implements IAlertManager {
    private static final AlertManager instance = new AlertManager();

    private AlertManager(){}

    public static AlertManager getInstance(){
        return instance;
    }
    @Override
    public Alert launchAlertBox(Alert.AlertType alertType, String title, String headerText, String contextText) {
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
        return alert;
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

    @Override
    public boolean confirmSaveBeforeExit(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType yes = new ButtonType("Sim");
        ButtonType no = new ButtonType("Não");
        ButtonType cancel = new ButtonType("Cancelar", ButtonType.CANCEL.getButtonData());

        alert.getButtonTypes().setAll(yes, no, cancel);

        var result = alert.showAndWait();
        return result.isPresent() && result.get() == yes;
    }
}
