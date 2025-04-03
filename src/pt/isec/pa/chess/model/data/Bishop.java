package pt.isec.pa.chess.model.data;

public class Bishop extends Piece implements IPlayable {
    public Bishop(boolean isWhiteTeam, char column, int row) {
        super(isWhiteTeam ? 'B' : 'b', column, row, isWhiteTeam, EPieceType.BISHOP);
    }
    @Override
    public boolean isInvalidMove(char newColumn, int newRow, GameBoard board) {
        if (board == null) return true;

        int colDiff = Math.abs(newColumn - this.column);
        int rowDiff = Math.abs(newRow - this.row);

        if (colDiff != rowDiff || colDiff == 0)
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
