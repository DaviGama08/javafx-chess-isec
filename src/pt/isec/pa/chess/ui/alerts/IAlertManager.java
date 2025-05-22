package pt.isec.pa.chess.ui.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;

public interface IAlertManager {
    void launchAlertBox(Alert.AlertType alertType, String title, String headerText, String contextText);
    TextInputDialog launchDialogBox(boolean isWhite);
    ImageView getImageView (boolean isWhite);
}
