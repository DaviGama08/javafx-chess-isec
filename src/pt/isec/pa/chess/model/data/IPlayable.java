package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.Pieces.GameBoard;

public interface IPlayable {

    boolean move(int newCol, int newRow, GameBoard board);
    boolean isValidMove(int newColumn, int newRow, GameBoard board);
}
