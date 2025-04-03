package pt.isec.pa.chess;

import pt.isec.pa.chess.model.data.*;
import pt.isec.pa.chess.ui.ChessUI;

public class ChessMain {
    public static void main(String[] args) {
        GameBoard board = new GameBoard();
        ChessUI ui = new ChessUI(board);

        ui.printBoard();
        System.out.println("------------------------------------------------------\n");
        board.removePiece(board.getPiece('b', 1));
        board.removePiece(board.getPiece('c', 1));
        board.removePiece(board.getPiece('d', 1));
        System.out.println("Peças removidas para liberar o roque queenside (b1, c1 e d1).\n");
        ui.printBoard();
        System.out.println("------------------------------------------------------\n");

        move(board, ui, 'e', 1, 'c', 1);
    }

    private static void move(GameBoard board, ChessUI ui, char fromCol, int fromRow, char toCol, int toRow) {
        System.out.printf("Movendo de %c%d para %c%d...\n", fromCol, fromRow, toCol, toRow);
        boolean success = board.movePiece(fromCol, fromRow, toCol, toRow);
        if (!success) {
            System.out.println(">> ERRO: " + board.getLastError());
        } else {
            System.out.println("Movimento realizado com sucesso!");
            ui.printBoard();
        }
        System.out.println("------------------------------------------------------\n");
    }
}
