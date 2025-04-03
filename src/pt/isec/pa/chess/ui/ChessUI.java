package pt.isec.pa.chess.ui;

import pt.isec.pa.chess.model.data.GameBoard;
import pt.isec.pa.chess.model.data.Piece;

public class ChessUI {
    private GameBoard board;

    public ChessUI(GameBoard board){
        this.board = board;
    }
    public void printBoard() {
        System.out.println("Board:");
        for (int row = board.NUM_ROWS; row >= 1; row--) {
            System.out.print(row + " ");
            for (char col = 'a'; col <= 'h'; col++) {
                Piece p = board.getPiece(col, row);
                if (p != null)
                    System.out.print("[" + p + "] ");
                else
                    System.out.print("[    ] ");
            }
            System.out.println();
        }
        System.out.print("  ");
        for (char col = 'a'; col <= 'h'; col++)
            System.out.print("  " + col + "    ");

        System.out.println();
    }
}
