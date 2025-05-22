package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;

import java.beans.PropertyChangeEvent;

public class LogWindow extends Stage {

    private final ChessGameManager facade;

    private ListView<String> logList;
    private Button btnClear;

    public LogWindow(ChessGameManager facade) {
        this.facade = facade;
        createViews();
        registerHandlers();
    }

    private void createViews() {
        setTitle("Model Logs");
        logList = new ListView<>();
        btnClear = new Button("Limpar logs");

        VBox root = new VBox(10, logList, btnClear);
        root.setPadding(new Insets(10));
        VBox.setVgrow(logList, Priority.ALWAYS);

        Scene scene = new Scene(root);
        setScene(scene);
        setMinWidth(400);
        setMinHeight(400);
    }

    private void registerHandlers() {
        facade.addLogPropertyChangeListener("logAdded", this::onLogAdded);
        facade.addLogPropertyChangeListener("logsCleared", this::onLogsCleared);

        btnClear.setOnAction(e -> facade.clearLogs());
    }

    private void onLogAdded(PropertyChangeEvent evt) {
        String novaEntrada = (String) evt.getNewValue();
        Platform.runLater(() -> {
            logList.getItems().add(novaEntrada);
            logList.scrollTo(logList.getItems().size() - 1);
        });
    }

    private void onLogsCleared(PropertyChangeEvent evt) {
        Platform.runLater(() -> logList.getItems().clear());
    }
}
