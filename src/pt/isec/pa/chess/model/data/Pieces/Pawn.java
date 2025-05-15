package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPawnMoved;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

public class Pawn extends Piece{
    //To make possible other pawns to check en passant move
    private EPawnMoved pawnStatus = EPawnMoved.NEVER;
    public Pawn(boolean isWhiteTeam, int column, int row) {
        super(isWhiteTeam ? 'P' : 'p', column, row, isWhiteTeam, EPieceType.PAWN);
    }
    public Pawn(boolean isWhiteTeam, int column, int row, boolean wasMoved) {
        super(isWhiteTeam ? 'P' : 'p', column, row, isWhiteTeam, EPieceType.PAWN, wasMoved);
    }

    @Override
    public boolean move(int destColumn, int destRow, GameBoard board){
        if(isValidMove(destColumn, destRow, board)){
            Piece destPiece = board.getPiece(destColumn, destRow);

            if (destPiece != null && destPiece.isWhiteTeam == this.isWhiteTeam) {
                board.setLastError("Destino ocupado pela própria equipa: " + destColumn + destRow);
                return false;
            }

            if (!checkPawnMove(destPiece, pos.getCol(), pos.getRow(), destColumn, destRow, board)){
                board.setLastError("Caminho bloqueado para o Peão");
                return false;
            }

            updatePosition(destColumn, destRow);
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidMove(int newColumn, int newRow, GameBoard board) {
        if (board == null || board.isInvalidPosition(newColumn, newRow))
            return false;

        int direction = this.isWhiteTeam ? 1 : -1;
        int col = pos.getCol();
        int row = pos.getRow();

        int colDiff = newColumn - col;
        int rowDiff = newRow - row;

        Piece target = board.getPiece(newColumn, newRow);

        switch (pawnStatus) {
            case NEVER -> {
                if (colDiff == 0 && rowDiff == direction && target == null)
                    return true;

                if (colDiff == 0 && rowDiff == 2 * direction && target == null &&
                        board.getPiece(col, row + direction) == null)
                    return true;
            }
            case ONCE, MORETHANONCE -> {
                if (colDiff == 0 && rowDiff == direction && target == null)
                    return true;
            }
        }

        if (Math.abs(colDiff) == 1 && rowDiff == direction && target != null && target.isWhiteTeam != this.isWhiteTeam)
            return true;

        if (Math.abs(colDiff) == 1 && rowDiff == direction && target == null) {
            int expectedRow = isWhiteTeam ? 4 : 3;
            if (row == expectedRow) {
                Piece side = board.getPiece(newColumn, row);
                if (side instanceof Pawn sidePawn &&
                        sidePawn.isWhiteTeam != this.isWhiteTeam &&
                        sidePawn.getPawnStatus() == EPawnMoved.ONCE)
                    return true;
            }
        }

        return false;
    }


    @Override
    public void updatePosition(int newColumn, int newRow){
        this.pos.setCol(newColumn);
        this.pos.setRow(newRow);
        this.pawnStatus = pawnStatus == EPawnMoved.NEVER ? EPawnMoved.ONCE : EPawnMoved.MORETHANONCE;
        this.wasMoved = true;
    }

    private boolean checkPawnMove(Piece destPiece, int srcCol, int sourceRow, int destCol, int destRow, GameBoard board) {
        int colDiff = destCol - srcCol;
        int rowDiff = destRow - sourceRow;
        int direction = this.isWhiteTeam ? 1 : -1;
        if (colDiff == 0) {
            if (destPiece != null) return false;

            if (rowDiff == 2 * direction)
                return board.getPiece(srcCol, sourceRow + direction) == null;
            return true;
        }

        if (Math.abs(colDiff) == 1 && rowDiff == direction) {
            if (destPiece != null) {
                if(destPiece.isWhiteTeam == this.isWhiteTeam)
                    return false;
                board.removePiece(destPiece);
                updatePosition(destCol, destRow);
                return true;
            }

            // Check en passant move
            int expectedRow = this.isWhiteTeam ? 4 : 3;
            if (sourceRow == expectedRow) {
                Piece sidePiece = board.getPiece(destCol, sourceRow);

                if (sidePiece instanceof Pawn sidePawn &&
                        sidePawn.isWhiteTeam != this.isWhiteTeam &&
                        sidePawn.getPawnStatus() == EPawnMoved.ONCE) {
                    board.removePiece(sidePawn);
                    updatePosition(destCol, destRow);
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public EPawnMoved getPawnStatus(){
        return pawnStatus;
    }
}
