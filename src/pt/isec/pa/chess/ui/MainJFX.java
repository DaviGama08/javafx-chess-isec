package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;

public class MainJFX extends Application {
    private final ChessGameManager facade;
    public MainJFX() {
        facade = new ChessGameManager();
    }
    @Override
    public void start(Stage stage){
        RootPane root = new RootPane(facade);
        Scene scene = new Scene(root,590,590);
        stage.setScene(scene);
        stage.setTitle("ChessGame");
        stage.show();
    }
}
