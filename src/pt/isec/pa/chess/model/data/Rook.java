package pt.isec.pa.chess.model.data;

public class Rook extends Piece implements IPlayable {
    public Rook(boolean isWhiteTeam, char column, int row) {
        super(isWhiteTeam ? 'R' : 'r', column, row, isWhiteTeam, EPieceType.ROOK);
    }

    @Override
    public boolean isInvalidMove(char newColumn, int newRow, GameBoard board) {
        if (board == null) return true;

        int colDiff = Math.abs(newColumn - this.column);
        int rowDiff = Math.abs(newRow - this.row);

        boolean isHorizontal = (rowDiff == 0 && colDiff > 0);
        boolean isVertical = (colDiff == 0 && rowDiff > 0);

        if (!isHorizontal && !isVertical)
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
