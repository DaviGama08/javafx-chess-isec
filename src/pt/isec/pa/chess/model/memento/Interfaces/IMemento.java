package pt.isec.pa.chess.model.memento.Interfaces;

public interface IMemento {
    default Object getSnapshot() { return null; }
}
