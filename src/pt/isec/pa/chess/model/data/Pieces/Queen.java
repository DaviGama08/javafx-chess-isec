package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.IPlayable;

public class Queen extends Piece implements IPlayable {
    public Queen(boolean isWhiteTeam, int column, int row) {
        super(isWhiteTeam ? 'Q' : 'q', column, row, isWhiteTeam, EPieceType.QUEEN);
    }
    public Queen(boolean isWhiteTeam, int column, int row, boolean wasMoved) {
        super(isWhiteTeam ? 'Q' : 'q', column, row, isWhiteTeam, EPieceType.QUEEN, wasMoved);
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
                board.setLastError("Caminho bloqueado para a Rainha");
                return false;
            }

            if (destPiece != null)
                board.removePiece(destPiece);
            board.board.get(pos.getRow()).set(pos.getCol(), null);
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

        boolean isDiagonal = (colDiff == rowDiff && colDiff > 0);
        boolean isLinear = (colDiff > 0 && rowDiff == 0) || (colDiff == 0 && rowDiff > 0);

        return isDiagonal || isLinear;
    }
}
