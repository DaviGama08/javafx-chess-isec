package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

public class King extends Piece{
    public King(boolean isWhiteTeam, int column, int row) {
        super(isWhiteTeam ? 'K' : 'k', column, row, isWhiteTeam, EPieceType.KING);
    }
    public King(boolean isWhiteTeam, int column, int row, boolean wasMoved) {
        super(isWhiteTeam ? 'K' : 'k', column, row, isWhiteTeam, EPieceType.KING, wasMoved);
    }
    @Override
    public boolean move(int destColumn, int destRow, GameBoard board){
        if(isValidMove(destColumn, destRow, board)){

            Piece destPiece = board.getPiece(destColumn, destRow);

            if (destPiece != null && destPiece.isWhiteTeam == this.isWhiteTeam) {
                board.setLastError("Destino ocupado pela própria equipa: " + destColumn + destRow);
                return false;
            }

            if (Math.abs(destColumn - pos.getCol()) == 2) {
                boolean isKingside = destColumn > pos.getCol();

                int rookSourceCol = isKingside ? 7 : 0;
                int rookDestCol = isKingside ? destColumn - 1 : destColumn + 1;

                if (board.isPathClear(pos.getCol(), pos.getRow(), rookSourceCol, pos.getRow())){
                    board.setLastError("Caminho bloqueado para o Rei");
                    return false;
                }

                Piece rook = board.getPiece(rookSourceCol, pos.getRow());
                if (!(rook instanceof Rook)){
                    board.setLastError("O roque só pode acontecer entre o Rei e uma Torre");
                    return false;
                }

                if(rook.wasMoved){
                    board.setLastError("A torre já se moveu. Impossível fazer o roque.");
                    return false;
                }

                rook.updatePosition(rookDestCol, pos.getRow());
                updatePosition(destColumn, destRow);

                return true;
            }

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
            return true;

        int colDiff = Math.abs(newColumn - this.pos.getCol());
        int rowDiff = Math.abs(newRow - this.pos.getRow());

        if ((colDiff <= 1 && rowDiff <= 1) && !(colDiff == 0 && rowDiff == 0))
            return true;

        if (!wasMoved && rowDiff == 0 && colDiff == 2) {
            boolean isKingside = newColumn > pos.getCol();
            int rookCol = isKingside ? 7 : 0;

            Piece rook = board.getPiece(rookCol, pos.getRow());
            if (!(rook instanceof Rook) || rook.isWhiteTeam != this.isWhiteTeam || rook.wasMoved)
                return false;

            if (!board.isPathClear(pos.getCol(), pos.getRow(), rookCol, pos.getRow()))
                return false;

            return true;
        }

        return false;
    }

}
