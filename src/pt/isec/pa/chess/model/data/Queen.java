package pt.isec.pa.chess.model.data;

public class Queen extends Piece implements IPlayable {
    public Queen(boolean isWhiteTeam, char column, int row) {
        super(isWhiteTeam ? 'Q' : 'q', column, row, isWhiteTeam, EPieceType.QUEEN);
    }

    @Override
    public boolean isInvalidMove(char newColumn, int newRow, GameBoard board) {
        if (board == null) return true;

        int colDiff = Math.abs(newColumn - this.column);
        int rowDiff = Math.abs(newRow - this.row);

        boolean isDiagonal = (colDiff == rowDiff && colDiff > 0);
        boolean isLinear = (colDiff > 0 && rowDiff == 0) || (colDiff == 0 && rowDiff > 0);

        if (!isDiagonal && !isLinear)
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
