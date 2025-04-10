package pt.isec.pa.chess.model.data.Factories;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Pieces.*;

abstract public class PieceFactory {
    public static Piece createPiece(EPieceType type, boolean isWhiteTeam, int column, int row) {
        return switch (type) {
            case KING -> new King(isWhiteTeam, column, row);
            case QUEEN -> new Queen(isWhiteTeam, column, row);
            case ROOK -> new Rook(isWhiteTeam, column, row);
            case BISHOP -> new Bishop(isWhiteTeam, column, row);
            case KNIGHT -> new Knight(isWhiteTeam, column, row);
            case PAWN -> new Pawn(isWhiteTeam, column, row);
        };
    }
}
