package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

public class Bishop extends Piece{
    public Bishop(boolean isWhiteTeam, int column, int row) {
        super(isWhiteTeam ? 'B' : 'b', column, row, isWhiteTeam, EPieceType.BISHOP);
    }
    public Bishop(boolean isWhiteTeam, int column, int row, boolean wasMoved) {
        super(isWhiteTeam ? 'B' : 'b', column, row, isWhiteTeam, EPieceType.BISHOP, wasMoved);
    }

    @Override
    public boolean move(int destColumn, int destRow, GameBoard board){
        if(isValidMove(destColumn, destRow, board)){

            Piece destPiece = board.getPiece(destColumn, destRow);

            if (destPiece != null && destPiece.isWhiteTeam == this.isWhiteTeam) return false;


            if(!board.isPathClear(pos.getCol(), pos.getRow(), destColumn, destRow)) return false;


            if (destPiece != null)
                board.removePiece(destPiece);

            updatePosition(destColumn, destRow);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidMove(int newColumn, int newRow, GameBoard board) {
        if (board == null)
            return false;

        int colDiff = Math.abs(newColumn - this.pos.getCol());
        int rowDiff = Math.abs(newRow - this.pos.getRow());

        if(colDiff != rowDiff)
            return false;
        return board.isPathClear(this.pos.getCol(), this.pos.getRow(), newColumn, newRow);
    }

}
