package pt.isec.pa.chess.model.data;

public interface IPlayable {
    boolean isInvalidMove(char newColumn, int newRow, GameBoard board);
    void updatePosition(char newColumn, int newRow);
}
