package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;

import java.io.IOException;

public class ChessGameManager {
    private final String PATH = "game.dat";
    private ChessGame game;

    public ChessGameManager() {
        this.game = new ChessGame();
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

    public void saveGame() throws IOException {
        ChessGameSerialization.saveGame(game, PATH);
    }

    public boolean loadGame() throws IOException, ClassNotFoundException {
        this.game = ChessGameSerialization.loadGame(PATH);
        return game != null;
    }

    public boolean exportGame(){
        return game.exportGameState();
    }
    public boolean importGame(){
        return game.importGameState();
    }

    public Position validatePawnPromotion(){
        return game.validatePawnPromotion();
    }

    public boolean promotePawn(Position pos, EPieceType newType){
        return game.promotePawn(pos, newType);
    }
}
