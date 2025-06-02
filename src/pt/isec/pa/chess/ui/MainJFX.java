package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.ui.services.FileChooserService;

public class MainJFX extends Application {
    private final ChessGameManager facade;
    public MainJFX() {facade = new ChessGameManager();}
    @Override
    public void start(Stage stage){
        stage.setMinWidth(450);
        stage.setMinHeight(450);
        RootPane root = new RootPane(facade);
        Scene scene = new Scene(root,590,590);
        FileChooserService.setStage(stage);
        stage.setScene(scene);
        stage.setTitle("ChessGame");
        stage.show();

        LogWindow logWindow = new LogWindow(facade);
        logWindow.setX(stage.getX() + stage.getWidth());
        logWindow.setY(stage.getY());
        logWindow.show();
    }
}
