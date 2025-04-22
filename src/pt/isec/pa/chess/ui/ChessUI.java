package pt.isec.pa.chess.ui;

import pt.isec.pa.chess.model.data.Game.ChessGameManager;
import pt.isec.pa.chess.model.data.Game.PlayerData;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

public class ChessUI {
    private final ChessGameManager facade;

    public ChessUI(ChessGameManager facade) {
        this.facade = facade;
    }

    public void printCurrentPlayer() {
        PlayerData p = facade.getCurrentPlayer();
        if (p != null) {
            System.out.println("Jogador atual: " + p.name() + " (" + p.team() + ") [score=" + p.score() + "]");
        }
    }

    public void printBoard() {
        GameBoard b = facade.getBoard();
        System.out.print("   ");
        for (char c = 'a'; c < 'a' + GameBoard.NUM_COLS; c++) {
            System.out.printf("%-8c", c);
        }
        System.out.println();
        for (int row = GameBoard.NUM_ROWS - 1; row >= 0; row--) {
            System.out.printf("%2d ", row + 1);
            for (int col = 0; col < GameBoard.NUM_COLS; col++) {
                Piece p = b.getPiece(col, row);
                String s = p == null ? "    " : p.toString();
                System.out.printf("%-8s", "[" + s + "]");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean move(int sc, int sr, int dc, int dr) {
        boolean result = facade.movePiece(sc, sr, dc, dr);
        if (!result) {
            System.out.println("Falha ao mover peca.");
        } else {
            System.out.println("Movimento realizado com sucesso.");
        }
        return result;
    }

    public void removePiece(int c, int r) {
        Piece p = facade.getBoard().getPiece(c, r);
        if (p == null) {
            System.out.println("Nenhuma peca em " + (char) ('a' + c) + (r + 1));
        } else {
            facade.getBoard().removePiece(p);
            System.out.println("Removida peca " + p + " de " + (char) ('a' + c) + (r + 1));
        }
    }
}
