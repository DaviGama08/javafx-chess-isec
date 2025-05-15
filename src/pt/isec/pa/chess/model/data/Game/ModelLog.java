package pt.isec.pa.chess.model.data.Game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ModelLog {
    private static ModelLog instance;
    private final ObservableList<String> logs;
    private final PropertyChangeSupport pcs;

    private ModelLog() {
        logs = FXCollections.observableArrayList();
        pcs = new PropertyChangeSupport(this);
    }

    public static synchronized ModelLog getInstance() {
        if (instance == null)
            instance = new ModelLog();
        return instance;
    }

    public void addLog(String entry) {
        logs.add(entry);
        pcs.firePropertyChange("logAdded", null, entry);
    }

    public void clearLogs() {
        logs.clear();
        pcs.firePropertyChange("logsCleared", null, null);
    }

    public ObservableList<String> getLogs() {
        return FXCollections.unmodifiableObservableList(logs);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
}

