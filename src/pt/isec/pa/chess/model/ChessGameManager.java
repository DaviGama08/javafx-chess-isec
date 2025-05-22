package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;
import pt.isec.pa.chess.model.data.game.*;
import pt.isec.pa.chess.model.memento.CareTaker;
import pt.isec.pa.chess.model.memento.Interfaces.IMemento;
import pt.isec.pa.chess.model.memento.Interfaces.IOriginator;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessGameManager implements IOriginator {
    public static final String PROP_UNDO_PERFORMED  = "undoPerformed";
    public static final String PROP_REDO_PERFORMED  = "redoPerformed";
    public static final String PROP_BOARD_CHANGED   = "boardChanged";
    public static final String PROP_TURN_CHANGED    = "turnChanged";
    public static final String PROP_GAME_STARTED    = "gameStarted";
    public static final String PROP_GAME_OVER       = "gameOver";
    public static final String PROP_CHECK           = "check";
    private transient final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ChessGame game;
    private final CareTaker caretaker;

    public ChessGameManager() {
        this.game = new ChessGame();
        this.caretaker = new CareTaker(this);
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    public void addLogPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        ModelLog.getInstance().addPropertyChangeListener(propertyName, listener);
    }

    public void clearLogs() {
        ModelLog.getInstance().clearLogs();
    }

    public boolean movePiece(int sourceColumn, int sourceRow, int destColumn, int destRow){
        caretaker.save();
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
                pcs.firePropertyChange(PROP_CHECK, null, game.getCurrentPlayer().team() == ETeamColor.WHITE_TEAM);
                pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());
            }
        }
        return moved;
    }

    public int[] validatePawnPromotion(){
        PositionData pd = game.validatePawnPromotion();
        return (pd == null) ? null : new int[]{ pd.col(), pd.row() };
    }

    public void promotePawn(int col, int row, EPieceType newType){
        Position pos = new Position(col, row);
        game.promotePawn(pos, newType);
        pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
        ModelLog.getInstance().addLog("Peão promovido para " + newType + " em " +
                (char)('a' + pos.getCol()) + (pos.getRow() + 1));
    }

    public boolean availableMoveWithoutSelecedPiece(int col, int row) {
        return game.availableMoveWithoutSelecedPiece(col, row);
    }

    public boolean availableMoveWithSelectedPiece(int col, int row){
        return game.availableMoveWithSelectedPiece(col, row);
    }

    public boolean isWhiteTurn(){
        return game.getCurrentPlayer().team() == ETeamColor.WHITE_TEAM;
    }
    public boolean isEmptyTurn(){
        return game.getCurrentPlayer().team() == ETeamColor.EMPTY;
    }
    public boolean isStarted() { return game.getState() != null; }
    public boolean isCheck() { return game.isCheck(); }
    public boolean isMoveValid(int fromCol, int fromRow, int toCol, int toRow) {
        Position from = new Position(fromCol, fromRow);
        Position to   = new Position(toCol,   toRow);

        List<Position> list = game.getValidMoves(from);

        return list.contains(to);
    }

    public void newGame(String nameWhite, String nameBlack) {
        game.newGame(nameWhite, nameBlack);
        caretaker.reset();
        caretaker.save();
        pcs.firePropertyChange(PROP_GAME_STARTED, null, null);
        pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
        pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());
        ModelLog.getInstance().addLog("Novo jogo iniciado entre " + nameWhite + " e " + nameBlack);
    }
    public boolean loadGame(File source) throws IOException, ClassNotFoundException {
        ChessGame loaded = ChessGameSerialization.loadGame(source.getAbsolutePath());
        if (loaded == null) return false;

        this.game = loaded;
        caretaker.reset();
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
            caretaker.reset();
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
    public void setImportGameData(String whitePlayerName, String blackPlayerName) {
        game.setImportGameData(whitePlayerName, blackPlayerName);
    }

    //MEMENTO
    private static class GameMemento implements IMemento {
        private final ChessGame snapshot;

        public GameMemento(ChessGame game) {
            this.snapshot = game.clone();
        }
        @Override
        public ChessGame getSnapshot() {
            return snapshot.clone();
        }
    }
    @Override
    public IMemento save() {
        return new GameMemento(game.clone());
    }

    @Override
    public void restore(IMemento memento) {
        if (memento instanceof GameMemento gm) {
            this.game = gm.getSnapshot();

            pcs.firePropertyChange(PROP_BOARD_CHANGED, null, game.getBoard());
            pcs.firePropertyChange(PROP_TURN_CHANGED, null, game.getCurrentPlayer());

            if (game.gameOver()) {
                pcs.firePropertyChange(PROP_GAME_OVER, null, game.getWinner());
            }
        }
    }

    public void undo() {
        if (caretaker.hasUndo()) {
            caretaker.undo();
            pcs.firePropertyChange(PROP_UNDO_PERFORMED, null, null);
        }
    }

    public void redo() {
        if (caretaker.hasRedo()) {
            caretaker.redo();
            pcs.firePropertyChange(PROP_REDO_PERFORMED, null, null);
        }
    }

    //Getters
    public EChessState getState() {
        return game.getState();
    }

    public String getPlayerName(boolean isWhiteTeam) {
        return game.getPlayerName(isWhiteTeam);
    }

    public GameBoard getBoard() {
        return game.getBoard();
    }

    public EPieceType getPieceTypeAt(int col, int row) {
        Piece piece = game.getPieceAt(new Position(col, row));
        return piece != null ? piece.getEPieceType() : null;
    }
    public List<int[]> getValidMoves(int col, int row) {
        List<Position> list = game.getValidMoves(new Position(col, row));

        List<int[]> moves = new ArrayList<>(list.size());
        for (Position p : list)
            moves.add(new int[]{ p.getCol(), p.getRow() });
        return moves;
    }

    public java.util.Optional<String> getWinner() {
        PlayerData winner = game.getWinner();
        return winner != null ? Optional.of(winner.name()) : Optional.empty();
    }

    public boolean hasPiece(int col, int row) {
        return game.getBoard().getPiece(col, row) != null;
    }

}
