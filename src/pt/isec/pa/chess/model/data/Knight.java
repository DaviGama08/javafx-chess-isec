package pt.isec.pa.chess.model.data;

public class Knight extends Piece implements IPlayable {
    public Knight(boolean isWhiteTeam, char column, int row) {
        super(isWhiteTeam ? 'N' : 'n', column, row, isWhiteTeam, EPieceType.KNIGHT);
    }

    @Override
    public boolean isInvalidMove(char newColumn, int newRow, GameBoard board) {
        if (board == null) return true;

        int colDiff = Math.abs(newColumn - this.column);
        int rowDiff = Math.abs(newRow - this.row);

        boolean isKnightMove = (colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2);
        if (!isKnightMove)
            return true;
        return false;
    }

    @Override
    public void updatePosition(char newColumn, int newRow) {
        this.column = newColumn;
        this.row = newRow;
        this.wasMoved = true;
    }
}
