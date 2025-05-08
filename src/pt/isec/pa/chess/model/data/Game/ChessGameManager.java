package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

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

    public void setPlayers(String whitePlayerName, String blackPlayerName) {
        game.setPlayers(whitePlayerName, blackPlayerName);
    }

    public GameBoard getBoard() throws CloneNotSupportedException {
        return game.getBoard();
    }

    public PlayerData getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    public void newGame(String nameWhite, String nameBlack) {
        game.newGame(nameWhite, nameBlack);
    }

    public boolean gameOver() {
        return game.gameOver();
    }

    public java.util.Optional<String> getWinner() {
        PlayerData winner = game.getWinner();
        return winner != null ? Optional.of(winner.name()) : Optional.empty();
    }

    public boolean isStarted() { return game.getState() != null; }

    public void saveGame() throws IOException {
        ChessGameSerialization.saveGame(game, PATH);
    }
    public void saveGame(File destiny) throws IOException {
        ChessGameSerialization.saveGame(game, destiny.getAbsolutePath());
    }
    public boolean loadGame() throws IOException, ClassNotFoundException {
        this.game = ChessGameSerialization.loadGame(PATH);
        return game != null;
    }
    public boolean loadGame(File source) throws IOException, ClassNotFoundException {
        this.game = ChessGameSerialization.loadGame(source.getAbsolutePath());
        return game != null;
    }
    public boolean exportGame(){
        return game.exportGameState();
    }

    public boolean importGame(){
        return game.importGameState();
    }

    public PositionData validatePawnPromotion(){
        return game.validatePawnPromotion();
    }

    public boolean promotePawn(Position pos, EPieceType newType){
        return game.promotePawn(pos, newType);
    }

    public String getPlayerName(ETeamColor team) {
        return game.getPlayerName(team);
    }
}
