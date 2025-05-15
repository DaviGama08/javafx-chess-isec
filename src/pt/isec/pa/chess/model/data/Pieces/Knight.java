package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

public class Knight extends Piece {
    public Knight(boolean isWhiteTeam, int column, int row) {
        super(isWhiteTeam ? 'N' : 'n', column, row, isWhiteTeam, EPieceType.KNIGHT);
    }
    public Knight(boolean isWhiteTeam, int column, int row, boolean wasMoved) {
        super(isWhiteTeam ? 'N' : 'n', column, row, isWhiteTeam, EPieceType.KNIGHT, wasMoved);
    }

    @Override
    public boolean move(int destColumn, int destRow, GameBoard board){
        if (!isValidMove(destColumn, destRow, board))
            return false;

        Piece destPiece = board.getPiece(destColumn, destRow);
        if (destPiece != null) {
            if (destPiece.isWhiteTeam == this.isWhiteTeam) {
                board.setLastError("Destino ocupado pela própria equipa: " + destColumn + destRow);
                return false;
            }
            board.removePiece(destPiece);
        }

        updatePosition(destColumn, destRow);
        return true;
    }

    @Override
    public boolean isValidMove(int newColumn, int newRow, GameBoard board) {
        if (board == null)
            return false;

        int colDiff = Math.abs(newColumn - this.pos.getCol());
        int rowDiff = Math.abs(newRow - this.pos.getRow());

        return (colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2);
    }
}
