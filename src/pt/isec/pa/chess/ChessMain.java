package pt.isec.pa.chess;

import javafx.application.Application;
import pt.isec.pa.chess.model.data.Game.ChessGameManager;
import pt.isec.pa.chess.ui.ChessUI;
import pt.isec.pa.chess.ui.MainJFX;

public class ChessMain {
    public static void main(String[] args) {
        Application.launch(MainJFX.class, args);

        ChessGameManager chessGameManager = new ChessGameManager();
        ChessUI ui = new ChessUI(chessGameManager);

        // Definir jogadores
        chessGameManager.setPlayers("Nunes", "Cid");

        System.out.println("=== JOGO INICIADO ===");
        ui.printCurrentPlayer();
        ui.printBoard();

        // Fazer algumas jogadas de exemplo
        System.out.println("Movendo peão branco de e2 para e4 (col=4, row=1 -> col=4, row=3)");
        ui.move(4, 1, 4, 3);
        ui.printBoard();

        System.out.println("Movendo peão preto de d7 para d5 (col=3, row=6 -> col=3, row=4)");
        ui.move(3, 6, 3, 4);
        ui.printBoard();

        // 1) Salvar o jogo atual no arquivo "game.txt"
        System.out.println("\n==> Exportando jogo para 'game.txt'...");
        boolean exportOk = chessGameManager.exportGame();
        if (!exportOk) {
            System.out.println("Falha ao exportar o jogo.");
        }

        // 2) Remover todas as peças usando removePiece diretamente
        System.out.println("\n==> Removendo todas as peças do tabuleiro...");
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ui.removePiece(col, row);
            }
        }
        ui.printBoard(); // Tabuleiro deve ficar vazio

        // 3) Importar o jogo de "game.txt" para restaurar o estado
        System.out.println("\n==> Importando jogo de 'game.txt'...");
        boolean importOk = chessGameManager.importGame();
        if (importOk) {
            System.out.println("Jogo importado com sucesso. Tabuleiro restaurado:");
            ui.printBoard();
        } else {
            System.out.println("Falha ao importar o jogo.");
        }

        System.out.println("=== FIM ===");


    }
}