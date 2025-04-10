package pt.isec.pa.chess;

import pt.isec.pa.chess.model.data.Game.Facade;
import pt.isec.pa.chess.ui.ChessUI;

public class ChessMain {
    public static void main(String[] args) {
        Facade facade = new Facade();
        ChessUI ui = new ChessUI(facade);

        // Definir jogadores
        facade.setPlayers("Nunes", "Cid");

        System.out.println("=== JOGO INICIADO ===");

        // Mostrar jogador atual e tabuleiro
        ui.printCurrentPlayer();
        ui.printBoard();

        // Fazer jogadas de exemplo
        System.out.println("Movendo peão branco de e2 para e4");
        ui.move(4, 1, 4, 3); // e2 -> e4
        ui.printBoard();

        System.out.println("Movendo peão preto de d7 para d5");
        ui.move(3, 6, 3, 4); // d7 -> d5
        ui.printBoard();

        // Remover peças para roque (exemplo)
        ui.removePiece(1, 0); // b1
        ui.removePiece(2, 0); // c1
        ui.removePiece(3, 0); // d1
        ui.printBoard();

        System.out.println("Tentando roque grande (Rei branco e Torre a0)");
        ui.move(4, 0, 2, 0); // e1 -> c1
        ui.move(5, 0, 4, 1); // e1 -> c1
        ui.move(4, 3, 3, 4); // e1 -> c1

        ui.printBoard();
        //Testar o save
        System.out.println("A guardar jogo em 'game.dat'");
        facade.saveGame();

        // Testar o load
        System.out.println("A carregar jogo de 'game.dat'");
        boolean loaded = facade.loadGame();
        if (loaded) {
            System.out.println("Jogo carregado com sucesso.");
            ui.printBoard();
        } else {
            System.out.println("Falha ao carregar jogo.");
        }

        System.out.println("=== FIM ===");
    }
}
