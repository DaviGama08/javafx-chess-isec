package pt.isec.pa.chess.model.data;

public class King extends Piece implements IPlayable {
    public King(boolean isWhiteTeam, char column, int row) {
        super(isWhiteTeam ? 'K' : 'k', column, row, isWhiteTeam, EPieceType.KING);
    }

    @Override
    public boolean isInvalidMove(char newColumn, int newRow, GameBoard board) {
        if (board == null)return true;

        int colDiff = Math.abs(newColumn - this.column);
        int rowDiff = Math.abs(newRow - this.row);

        if (colDiff == 1 || rowDiff == 1 || (colDiff == 0 && rowDiff == 0))
            return false;
        return !(!wasMoved && colDiff == 2 && rowDiff == 0);

    }

    @Override
    public void updatePosition(char newColumn, int newRow) {
        this.column = newColumn;
        this.row = newRow;
        this.wasMoved = true;
    }
}
