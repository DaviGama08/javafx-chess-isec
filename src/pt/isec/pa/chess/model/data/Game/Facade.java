package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;

public class Facade {
    private final String PATH = "game.dat";
    private ChessGame game;
    //private final ChessUI ui;

    public Facade() {
        this.game = new ChessGame();
        //this.ui = new ChessUI(this);
    }

    public boolean movePiece(int sourceColumn, int sourceRow, int destColumn, int destRow){
        return game.movePiece(sourceColumn, sourceRow, destColumn, destRow);
    }

    public EChessState getState() {
        return game.getState();
    }

    public PlayerData getPlayer() {
        return game.getCurrentPlayer();
    }

    //PARA TESTES...
    public void setPlayers(String whitePlayerName, String blackPlayerName) {
        game.setPlayers(whitePlayerName, blackPlayerName);
    }
    //PARA TESTES...
    public GameBoard getBoard() {
        return game.getBoard();
    }
    //PARA TESTES...
    public PlayerData getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    public boolean gameOver() {
        return game.gameOver();
    }

    public void saveGame() {
        ChessGameSerialization.saveGame(game, PATH);
    }

    public boolean loadGame() {
        this.game = ChessGameSerialization.loadGame(PATH);
        return game != null;
    }

}
