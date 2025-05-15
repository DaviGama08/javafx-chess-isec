package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

public class Rook extends Piece {
    public Rook(boolean isWhiteTeam, int column, int row) {
        super(isWhiteTeam ? 'R' : 'r', column, row, isWhiteTeam, EPieceType.ROOK);
    }
    public Rook(boolean isWhiteTeam, int column, int row, boolean wasMoved) {
        super(isWhiteTeam ? 'R' : 'r', column, row, isWhiteTeam, EPieceType.ROOK, wasMoved);
    }

    @Override
    public boolean move(int destColumn, int destRow, GameBoard board){
        if(isValidMove(destColumn, destRow, board)){
            Piece destPiece = board.getPiece(destColumn, destRow);

            if (destPiece != null && destPiece.isWhiteTeam == this.isWhiteTeam) {
                board.setLastError("Destino ocupado pela própria equipa: " + destColumn + destRow);
                return false;
            }

            if(!board.isPathClear(pos.getCol(), pos.getRow(), destColumn, destRow)){
                board.setLastError("Caminho bloqueado para a Torre");
                return false;
            }

            if (destPiece != null)
                board.removePiece(destPiece);
            updatePosition(destColumn, destRow);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidMove(int destColumn, int destRow, GameBoard board) {
        if (board == null)
            return false;

        int colDiff = Math.abs(destColumn - this.pos.getCol());
        int rowDiff = Math.abs(destRow - this.pos.getRow());

        boolean isLinear = (rowDiff == 0 && colDiff > 0) || (colDiff == 0 && rowDiff > 0);
        if (!isLinear)
            return false;

        return board.isPathClear(this.pos.getCol(), this.pos.getRow(), destColumn, destRow);
    }
}
