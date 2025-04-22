package pt.isec.pa.chess.model.data.Factories;

import pt.isec.pa.chess.model.data.Pieces.*;

public abstract class PieceFactoryText {
    public static Piece createPiece(String piece){
        int col = piece.charAt(1) - 'a';
        int row = Character.getNumericValue(piece.charAt(2));
        boolean isWhiteTeam = Character.isUpperCase(piece.charAt(0));
        boolean wasMoved = true;
        if (piece.endsWith("*")) {
            wasMoved = false;
            piece = piece.substring(0, piece.length()-1);
        }

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
