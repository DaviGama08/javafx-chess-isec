package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ChessGameManager {
    public static final String PROP_BOARD_CHANGED = "boardChanged";
    public static final String PROP_TURN_CHANGED  = "turnChanged";
    public static final String PROP_GAME_STARTED  = "gameStarted";
    public static final String PROP_GAME_OVER     = "gameOver";
    public static final String PROP_CHECK         = "check";
    private transient final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ChessGame game;

    public ChessGameManager() {
        this.game = new ChessGame();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public List<Position> getValidMoves(Position pos) {
        return game.getValidMoves(pos);
    }

    public boolean movePiece(int sourceColumn, int sourceRow, int destColumn, int destRow){
        boolean moved = game.movePiece(sourceColumn, sourceRow, destColumn, destRow);
        if (moved) {
            pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
            pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());

            ModelLog.getInstance().addLog("Movimento de " + (char)('a' + sourceColumn) + (sourceRow + 1) +
                    " para " + (char)('a' + destColumn) + (destRow + 1));

            if (game.gameOver()) {
                pcs.firePropertyChange(PROP_GAME_OVER, null, game.getWinner());
                String winner = getWinner().orElse("Empate");
                ModelLog.getInstance().addLog("Fim de jogo. Vencedor: " + winner);
            }
            if (game.isCheck()) {
                pcs.firePropertyChange(PROP_CHECK, null, game.getCurrentPlayer().team());
            }
        }
        return moved;
    }

    public PositionData validatePawnPromotion(){
        return game.validatePawnPromotion();
    }

    public void promotePawn(Position pos, EPieceType newType){
        game.promotePawn(pos, newType);
        pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
        ModelLog.getInstance().addLog("Peão promovido para " + newType + " em " +
                (char)('a' + pos.getCol()) + (pos.getRow() + 1));
    }

    public EChessState getState() {
        return game.getState();
    }

    public void setImportGameData(String whitePlayerName, String blackPlayerName) {
        game.setImportGameData(whitePlayerName, blackPlayerName);
    }

    public Piece getPieceAt(Position pos)
    {
        return game.getPieceAt(pos);
    }
    public GameBoard getBoard() {
        return game.getBoard();
    }

    public PlayerData getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    public java.util.Optional<String> getWinner() {
        PlayerData winner = game.getWinner();
        return winner != null ? Optional.of(winner.name()) : Optional.empty();
    }

    public boolean isStarted() { return game.getState() != null; }

    public String getPlayerName(ETeamColor team) {
        return game.getPlayerName(team);
    }

    public void newGame(String nameWhite, String nameBlack) {
        game.newGame(nameWhite, nameBlack);
        pcs.firePropertyChange(PROP_GAME_STARTED, null, null);
        pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
        pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());
        ModelLog.getInstance().addLog("Novo jogo iniciado entre " + nameWhite + " e " + nameBlack);
    }

    public boolean loadGame(File source) throws IOException, ClassNotFoundException {
        ChessGame loaded = ChessGameSerialization.loadGame(source.getAbsolutePath());
        if (loaded == null) return false;

        this.game = loaded;

        pcs.firePropertyChange(PROP_GAME_STARTED, null, null);
        pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
        pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());
        ModelLog.getInstance().addLog("Jogo carregado com sucesso de: " + source.getAbsolutePath());
        return true;
    }

    public void saveGame(File destiny) throws IOException {
        ChessGameSerialization.saveGame(game, destiny.getAbsolutePath());
        ModelLog.getInstance().addLog("Jogo guardado em: " + destiny.getAbsolutePath());
    }

    public boolean importGame(File source){
        boolean result = game.importGameState(source);
        if (result) {
            pcs.firePropertyChange(PROP_GAME_STARTED, null, null);
            pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
            pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());

            ModelLog.getInstance().addLog("Jogo importado de: " + source.getAbsolutePath());
        }
        return result;
    }

    public boolean exportGame(File destiny){
        ModelLog.getInstance().addLog("Jogo exportado para: " + destiny.getAbsolutePath());
        return game.exportGameState(destiny);
    }
}
