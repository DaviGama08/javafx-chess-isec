package pt.isec.pa.chess.model.memento.Interfaces;

public interface IOriginator {
    IMemento save();
    void restore(IMemento memento);
}
