package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.IPlayable;

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

            if(!board.isPathClear(pos.getCol(), pos.getRow(), destRow, destColumn)){
                board.setLastError("Caminho bloqueado para a Torre");
                return false;
            }

            if (destPiece != null)
                board.removePiece(destPiece);

            board.board.get(pos.getRow()).set(pos.getCol(), null);
            board.movePieceOnBoard(this, destColumn, destRow);
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

        boolean isHorizontal = (rowDiff == 0 && colDiff > 0);
        boolean isVertical = (colDiff == 0 && rowDiff > 0);

        return isHorizontal || isVertical;
    }
}
