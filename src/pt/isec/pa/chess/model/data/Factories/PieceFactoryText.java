package pt.isec.pa.chess.model.data.Factories;

import pt.isec.pa.chess.model.data.Pieces.*;

public abstract class PieceFactoryText {
    public static Piece createPiece(String piece){
        char col = piece.charAt(1);
        int row = Character.getNumericValue(piece.charAt(2));
        boolean isWhiteTeam = Character.isUpperCase(piece.charAt(0));
        boolean wasMoved = piece.length() < 4 ? piece.charAt(3) != '*' : false;

        return switch(Character.toUpperCase(piece.charAt(0))){
            case 'R' -> new Rook(isWhiteTeam, col, row, wasMoved);
            case 'N' -> new Knight(isWhiteTeam, col, row, wasMoved);
            case 'K' -> new King(isWhiteTeam, col, row, wasMoved);
            case 'Q' -> new Queen(isWhiteTeam, col, row, wasMoved);
            case 'P' -> new Pawn(isWhiteTeam, col, row, wasMoved);
            case 'B' -> new Bishop(isWhiteTeam, col, row, wasMoved);
            default -> null;
        };
    }
}
