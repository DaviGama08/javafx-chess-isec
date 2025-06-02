package pt.isec.pa.chess.model.data.Factories;

import pt.isec.pa.chess.model.data.Pieces.*;

public abstract class PieceFactoryText {
    public static Piece createPiece(String piece){
        boolean wasMoved = true;
        if (piece.endsWith("*")) {
            wasMoved = false;
            piece = piece.substring(0, piece.length()-1);
        }

        int col = piece.charAt(1) - 'a';
        int row = Character.getNumericValue(piece.charAt(2));

        boolean isWhiteTeam = Character.isUpperCase(piece.charAt(0));

        switch (Character.toUpperCase(piece.charAt(0))) {
            case 'R':
                return new Rook(isWhiteTeam, col, row, wasMoved);
            case 'N':
                return new Knight(isWhiteTeam, col, row, wasMoved);
            case 'K':
                return new King(isWhiteTeam, col, row, wasMoved);
            case 'Q':
                return new Queen(isWhiteTeam, col, row, wasMoved);
            case 'B':
                return new Bishop(isWhiteTeam, col, row, wasMoved);
            case 'P':
                int initialRow = isWhiteTeam ? 1 : 6;
                if (row == initialRow && wasMoved) {
                    wasMoved = false;
                }
                return new Pawn(isWhiteTeam, col, row, wasMoved);
            default:
                return null;
        }
    }
}
