package pt.isec.pa.chess.ui.controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.data.Game.ModelLog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class LogWindow extends Stage implements PropertyChangeListener {

    private final ListView<String> logList;

    public LogWindow() {
        setTitle("Model Logs");

        logList = new ListView<>(ModelLog.getInstance().getLogs());

        Button btnClear = new Button("Limpar logs");
        btnClear.setOnAction(e -> ModelLog.getInstance().clearLogs());

        VBox root = new VBox(10, logList, btnClear);
        root.setPadding(new Insets(10));
        VBox.setVgrow(logList, Priority.ALWAYS); // 🔁 Faz o logList expandir com a janela

        Scene scene = new Scene(root);
        setScene(scene);
        setMinWidth(400);
        setMinHeight(400);

        ModelLog.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("logAdded".equals(evt.getPropertyName())) {
            Platform.runLater(() -> {
                int size = logList.getItems().size();
                if (size > 0)
                    logList.scrollTo(size - 1);
            });
        }
        if ("logsCleared".equals(evt.getPropertyName())) {
            Platform.runLater(() -> logList.scrollTo(0));
        }
    }
}
