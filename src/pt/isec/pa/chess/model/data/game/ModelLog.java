package pt.isec.pa.chess.model.data.game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ModelLog {
    private static ModelLog instance;

    private final List<String> logs;
    private final PropertyChangeSupport pcs;

    private ModelLog() {
        logs = new ArrayList<>();
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

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }
}
