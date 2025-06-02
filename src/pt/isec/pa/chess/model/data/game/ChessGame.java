package pt.isec.pa.chess.model.data.game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Factories.PieceFactoryText;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;
import pt.isec.pa.chess.model.memento.IMemento;
import pt.isec.pa.chess.model.memento.IOriginator;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Represents and manages the core logic and state of a chess game.
 * <p>
 * This class is responsible for handling game rules, piece movements,
 * player turns, game state transitions (e.g., check, checkmate),
 * and providing an interface for game interaction. It also implements
 * IOriginator to support saving and restoring its state via the Memento pattern.
 * All board operations are managed through an aggregated GameBoard object.
 *
 * @author Davi Nasser, Miguel Lopes e Ruben Seco
 * @version 1.0.0
 */
public class ChessGame implements Serializable, Cloneable, IOriginator {
    @Serial
    private static final long serialVersionUID = 1L;
    private GameBoard board;
    private EChessState currentState;
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private boolean editModeActive;
    private Player winner;

    /**
     * Default constructor for ChessGame.
     * <p>
     * Initializes a new game board and sets the initial game state to NOT_STARTED.
     */
    public ChessGame() {
        this.board = new GameBoard();
        this.whitePlayer = new Player("", ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player("", ETeamColor.BLACK_TEAM);

    }

    /**
     * Checks if clicking on a square should not result in selecting a piece for the current player.
     * <p>
     * This is used by the UI when no piece is yet selected. If this method
     * returns true, it implies the clicked square is either empty or contains an opponent's piece,
     * thus, no piece belonging to the current player can be selected there.
     *
     * @param col The 0-indexed column of the square.
     * @param row The 0-indexed row of the square.
     * @return true if the square is empty or contains an opponent's piece;
     * false if it contains a piece belonging to the current player (which is then selectable).
     */
    public boolean availableMoveWithoutSelecedPiece(int col, int row) {
        Position pos = new Position(col, row);
        boolean result = false;
        Piece piece = getPieceAt(pos);
        PlayerData cp = getCurrentPlayer();
        if (piece == null || piece.getTeam() != cp.team())
            result = true;
        return result;
    }

    /**
     * Checks if a clicked square contains a piece belonging to the current player,
     * when another piece was already selected.
     * <p>
     * This is used by the UI to determine if the player is trying to switch their
     * selected piece to another one of their own pieces.
     *
     * @param col The 0-indexed column of the target square.
     * @param row The 0-indexed row of the target square.
     * @return true if the target square contains a piece belonging to the current player;
     * false otherwise (e.g., square is empty or contains an opponent's piece).
     */
    public boolean availableMoveWithSelectedPiece(int col, int row){
        Position pos = new Position(col, row);
        boolean result = false;
        Piece target = getPieceAt(pos);
        if (target != null && target.getTeam() == getCurrentPlayer().team())
            result = true;
        return result;
    }

    /**
     * Attempts to move a piece from a source position to a destination position.
     *<p>
     * First, it checks that the game is in progress (not in a “not started” state or already finished)
     * and that it is the correct player’s turn by verifying that the source square contains a piece
     * belonging to the player whose turn it is.
     *<p>
     * It then confirms that the move is legal for that piece type by delegating to the GameBoard’s movement logic.
     * Before applying the move to the real board, it makes the same move on a cloned board to ensure
     * that the moving player’s king would not be left in check.
     * If any of these validations fail, the method returns false and the board remains unchanged.
     * If all validations succeed, the move is applied to the real board and the game state is updated accordingly:
     *   – If the opponent is left in checkmate, the state becomes CHECKMATE_WHITE_WON or CHECKMATE_BLACK_WON.
     *   – If the opponent has no legal moves but is not in check, the state becomes STALEMATE.
     *   – If there is insufficient material remaining to force checkmate (for example, king versus king
     *     or king and knight versus king), the state becomes DRAW_BY_INSUFFICIENT_MATERIAL.
     *   – If none of those conditions apply but the opponent is in check, the state becomes CHECK.
     *   – Otherwise, the state remains IN_PROGRESS.
     * Finally, the turn flag is flipped so that it becomes the other player’s turn,
     * and the method returns true to indicate that the move was successfully applied.
     *
     * @param sourceColumn the 0-indexed column of the piece to move
     * @param sourceRow    the 0-indexed row of the piece to move
     * @param destColumn   the 0-indexed column of the destination square
     * @param destRow      the 0-indexed row of the destination square
     * @return true if the move was successfully applied; false if any validation failed
     */
    public boolean movePiece(int sourceColumn, int sourceRow,
                             int destColumn,   int destRow) {

        if (!isStarted() || isGameOver)
            return false;

        Piece mover = board.getPiece(sourceColumn, sourceRow);
        if (mover == null || mover.isWhiteTeam() != isWhiteTurn)
            return false;

        GameBoard tmp = board.clone();
        if (!tmp.movePiece(sourceColumn, sourceRow, destColumn, destRow))
            return false;

        if (tmp.isKingInCheck(isWhiteTurn))
            return false;

        board.movePiece(sourceColumn, sourceRow, destColumn, destRow);

        boolean stalemate    = board.isStalemate(!isWhiteTurn);
        boolean insufficient = board.isInsufficientMaterial();
        boolean checkMate = board.isCheckMate(!isWhiteTurn);
        boolean check     = board.isKingInCheck(!isWhiteTurn);

        if (checkMate) {
            isGameOver = true;
            winner = isWhiteTurn ? whitePlayer : blackPlayer;
            currentState = isWhiteTurn
                    ? EChessState.CHECKMATE_WHITE_WON
                    : EChessState.CHECKMATE_BLACK_WON;
        }else if(stalemate){
            currentState = EChessState.STALEMATE;
            isGameOver = true;
            winner = null;
        }else if(insufficient){
            isGameOver = true;
            winner = null;
            currentState = EChessState.DRAW_BY_INSUFFICIENT_MATERIAL;
        }
        else if (check) {
            currentState = EChessState.CHECK;
        } else {
            currentState = EChessState.IN_PROGRESS;
        }

        isWhiteTurn = !isWhiteTurn;

        return true;
    }

    /**
     * Checks if the game has concluded.
     * <p>
     * If the game is over and a winner was determined by checkmate, this method
     * also updates the current game state accordingly.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean gameOver() {
        if (isGameOver) {
            if (winner != null) {
                currentState = winner.getTeam() == ETeamColor.WHITE_TEAM
                        ? EChessState.CHECKMATE_WHITE_WON
                        : EChessState.CHECKMATE_BLACK_WON;
            }
        }
        return isGameOver;
    }

    /**
     * Determines if the current player's king is in check.
     *
     * @return true if the king of the player whose turn it is is currently under attack, false otherwise.
     */
    public boolean isCheck() {
        return board.isKingInCheck(isWhiteTurn);

    }

    /**
     * Checks if the game has been started.
     * A game is considered started if its state is not EChessState.NOT_STARTED.
     *
     * @return true if the game has started, false otherwise.
     */
    public boolean isStarted() {return currentState != EChessState.NOT_STARTED;}

    /**
     * Identifies if a pawn is eligible for promotion.
     * <p>
     * A pawn is eligible if it has reached the opponent's back rank.
     * This method delegates the check to the game board.
     *
     * @return A PositionData Record containing the coordinates of the promotable pawn,
     * or null if no pawn is currently eligible for promotion.
     */
    public PositionData validatePawnPromotion(){
        return board.validatePawnPromotion();
    }

    /**
     * Promotes a pawn at a given position to a specified new piece type.
     * <p>
     * This action is delegated to the game board.
     *
     * @param pos The Position of the pawn to be promoted.
     * @param newType The EPieceType (e.g., QUEEN, ROOK) the pawn will be promoted to.
     */
    public void promotePawn(Position pos, EPieceType newType){
        board.promotePawn(pos, newType);
    }

    /**
     * Activates the board editing mode.
     * <p>
     * In this mode, the game state is reset (board cleared, no winner, etc.),
     * and players are initialized with empty names. The game is marked as not over
     * and in a NOT_STARTED state, ready for custom piece placement.
     */
    public void startEditMode(){
        this.editModeActive = true;
        this.isGameOver = false;
        this.currentState = EChessState.NOT_STARTED;
        this.winner = null;
        setPlayersNewGame("", "");
        clearBoard();
    }

    /**
     * Configures the edited game state before saving or exporting.
     * <p>
     * Updates player names for white and black, resets the game state to NOT_STARTED,
     * and sets which side will move first in the edited configuration.
     *
     * @param nameWhite       Name of the player controlling the white pieces.
     * @param nameBlack       Name of the player controlling the black pieces.
     * @param isWhiteTeamTurn true if the white side should move first; false if the black side should move first.
     */
    public void configureEditedGameForSave(String nameWhite, String nameBlack, boolean isWhiteTeamTurn) {
        this.whitePlayer.setName(nameWhite);
        this.blackPlayer.setName(nameBlack);
        this.currentState = EChessState.NOT_STARTED;
        this.isWhiteTurn = isWhiteTeamTurn;
    }

    /**
     * Exits board editing mode without saving the changes.
     * <p>
     * The game state is set to NOT_STARTED, and edit mode is deactivated.
     * Any custom piece setup might be lost unless previously saved by other means
     * or if the intention is to revert to a pre-edit state (which is not explicitly handled here).
     */
    public void endEditModeWithoutSave(){
        this.currentState = EChessState.NOT_STARTED;
        this.editModeActive = false;
    }

    /**
     * Adds a piece to the board, typically during edit mode.
     * <p>
     * The piece is added only if edit mode is active, the position is valid, and the square is empty.
     *
     * @param type The EPieceType of the piece to add.
     * @param isWhite true if the piece should be white, false for black.
     * @param col The 0-indexed column to place the piece.
     * @param row The 0-indexed row to place the piece.
     * @return true if the piece was successfully added, false otherwise.
     */
    public boolean addPiece(EPieceType type, boolean isWhite, int col, int row){
        if (!editModeActive)
            return false;
        if (board.isInvalidPosition(col, row) || board.getPiece(col, row) != null)
            return false;

        board.addPiece(type, isWhite, col, row);
        return true;
    }

    /**
     * Removes a piece from the board at the specified coordinates, typically during edit mode.
     *
     * @param col The 0-indexed column of the piece to remove.
     * @param row The 0-indexed row of the piece to remove.
     * @return true if a piece was successfully removed, false if not in edit mode or no piece at the position.
     */
    public boolean removePiece(int col, int row){
        if (!editModeActive)
            return false;

        Piece piece = board.getPiece(col, row);
        if (piece != null) {
            board.removePiece(piece);
            return true;
        }
        return false;
    }

    /**
     * Clears all pieces from the game board. This action is only effective if edit mode is active.
     */
    public void clearBoard(){
        if (editModeActive)
            board.clearBoard();
    }

    /**
     * Resets the game to a non-started state.
     * <p>
     * Clears the board, player information, winner, and game status flags.
     * Edit mode is also deactivated. White is set as the starting player by default if a new game were to start.
     */
    public void quitGame(){
        currentState = EChessState.NOT_STARTED;
        board.clearBoard();
        whitePlayer = new Player("", ETeamColor.WHITE_TEAM);
        blackPlayer = new Player("", ETeamColor.BLACK_TEAM);
        winner = null;
        isWhiteTurn = false;
        isGameOver = false;
        editModeActive = false;
    }

    /**
     * Initializes a new standard chess game with the specified player names.
     * <p>
     * The board is set to the standard initial piece configuration.
     * Game flags (game over, winner, edit mode) are reset, and the state is set to IN_PROGRESS
     * with white to move.
     *
     * @param nameWhite The name for the white player.
     * @param nameBlack The name for the black player.
     */
    public void newGame(String nameWhite, String nameBlack) {
        this.board = new GameBoard();
        setPlayersNewGame(nameWhite, nameBlack);
        this.isGameOver = false;
        this.winner = null;
        currentState = EChessState.IN_PROGRESS;
    }

    /**
     * Imports a game state from a text file.
     * <p>
     * The file should contain comma-separated values. The first token indicates the current player's turn
     * (e.g., "WHITE_TEAM" or "BLACK_TEAM"), followed by textual representations of each piece on the board.
     * The existing board is cleared before loading the new pieces. Player names are not loaded from the file
     * and should be set using setImportGameData if required.
     *
     * @param source The File object for the text file containing the game state.
     * @return true if the game state was successfully imported, false on error (e.g., file not found, invalid format).
     */
    public boolean importGameState(File source) {
        if (!source.exists())
            return false;

        StringBuilder contentBuilder = new StringBuilder();
        try ( Scanner scanner = new Scanner(source)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine());
                contentBuilder.append("\n");
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        String content = contentBuilder.toString();
        content = content.replace("\n", "").replace("\r", "");

        String[] tokens = content.split(",");

        if (tokens.length == 0)
            return false;

        String currentTeam = tokens[0].trim();
        isWhiteTurn = currentTeam.equalsIgnoreCase("WHITE_TEAM");

        for (int row = 0; row < GameBoard.NUM_ROWS; row++)
            for (int col = 0; col < GameBoard.NUM_COLS; col++) {
                Piece p = board.getPiece(col, row);
                if (p != null)
                    board.removePiece(p);
            }

        for (int i = 1; i < tokens.length; i++) {
            String pieceText = tokens[i].trim();
            if (!pieceText.isEmpty()) {
                Piece p = PieceFactoryText.createPiece(pieceText);
                if (p != null)
                    board.addPiece(p);
            }
        }
        return true;
    }

    /**
     * Exports the current game state to a specified text file.
     * <p>
     * The state is saved as a comma-separated string. The first value is the color of the
     * current player to move (e.g., "WHITE_TEAM"), followed by the textual representation of each piece.
     * Black pieces are typically listed after white pieces, often starting on a new line in the file for readability.
     *
     * @param destiny The File to which the game state will be exported.
     * @return true if the game was exported successfully, false if an IOException occurs.
     */
    public boolean exportGameState(File destiny) {
        StringBuilder sb = new StringBuilder();
        String currentTeam = isWhiteTurn ? whitePlayer.getTeam().toString() : blackPlayer.getTeam().toString();
        sb.append(currentTeam).append(",\n");
        boolean flag = true;
        for(int row = 0; row < GameBoard.NUM_ROWS; row++){
            for(int col = 0; col < GameBoard.NUM_COLS; col++){
                Piece piece = board.getPiece(col, row);
                if(piece != null){
                    String pieceStr = piece.toString();
                    char firstChar = pieceStr.charAt(0);
                    if(Character.isLowerCase(firstChar) && flag){
                        sb.append("\n");
                        flag = false;
                    }
                    sb.append(piece).append(",");
                }
            }
        }

        if (!sb.isEmpty() && sb.charAt(sb.length() - 1) == ',')
            sb.setLength(sb.length() - 1);

        try (FileWriter writer = new FileWriter(destiny)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the current game board.
     *
     * @return A new GameBoard instance representing the current board configuration.
     */
    public GameBoard getBoard() {
        return board.clone();
    }

    /**
     * Retrieves a clone of the piece at the specified board position.
     *
     * @param pos The Position object indicating the square.
     * @return A clone of the Piece at the given position, or null if the square is empty or the position is invalid.
     */
    public Piece getPieceAt(Position pos){
        return board.getPieceAt(pos);
    }

    /**
     * Gets the name of the player for the specified team.
     *
     * @param isWhiteTeam true to retrieve the white player's name, false for the black player's name.
     * @return The name of the specified player. Returns an empty string if the player is not set.
     */
    public String getPlayerName(boolean isWhiteTeam) {
        return isWhiteTeam ? whitePlayer.getName()
                : blackPlayer.getName();
    }

    /**
     * Gets the current high-level state of the chess game.
     *
     * @return The EChessState enum value representing the current game status
     * (e.g., NOT_STARTED, IN_PROGRESS, CHECK, CHECKMATE_WHITE_WON).
     */
    public EChessState getState() {
        return currentState;
    }

    /**
     * Retrieves data for the player whose turn it currently is.
     *
     * @return A PlayerData record containing the name, team color, and score of the current player.
     * If players are not yet initialized, returns PlayerData with an empty name.
     */
    public PlayerData getCurrentPlayer() {
        Player current = isWhiteTurn ? whitePlayer : blackPlayer;
        return new PlayerData(current.getName(), current.getTeam(), current.getScore());
    }

    /**
     * Retrieves data for the winning player, if the game has concluded with a winner.
     *
     * @return A PlayerData record for the winner. Returns null if the game is not over,
     * is a draw, or if the winner is not set.
     */
    public PlayerData getWinner() {
        if (winner == null) return null;
        return new PlayerData(winner.getName(), winner.getTeam(), winner.getScore());
    }

    /**
     * Calculates and returns a list of all valid destination positions for a piece
     * currently at the given source position.
     * <p>
     * This method considers all chess rules, including preventing moves that would leave
     * the current player's king in check.
     *
     * @param pos The Position of the piece for which to find valid moves.
     * @return A List of Position objects, where each Position is a valid destination square.
     * Returns an empty list if there is no piece at the specified position,
     * it's not that piece's turn, or there are no legal moves.
     */
    public List<Position> getValidMoves(Position pos) {
        return board.getValidMoves(pos, isWhiteTurn);
    }


    /**
     * Returns whether edit mode is currently active.
     * <p>
     * In edit mode, pieces can be added or removed freely on the board.
     *
     * @return true if edit mode is active; false otherwise.
     */
    public boolean isEditModeActive(){
        return editModeActive;
    }

    /**
     * Activates or deactivates edit mode for this game instance.
     * <p>
     * When set to true, the game enters edit mode allowing piece placement and removal.
     *
     * @param editModeActive true to enter edit mode; false to exit edit mode.
     */
    public void setEditModeActive(boolean editModeActive){
        this.editModeActive = editModeActive;
    }

    /**
     * Sets up the player objects for a new game, including their names and teams.
     * <p>
     * Also initializes game state variables: sets white player's turn to true,
     * game over to false, and clears any previous winner.
     *
     * @param nameWhite The name for the white player.
     * @param nameBlack The name for the black player.
     */
    public void setPlayersNewGame(String nameWhite, String nameBlack) {
        this.whitePlayer = new Player(nameWhite, ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player(nameBlack, ETeamColor.BLACK_TEAM);
        this.isWhiteTurn = true;
        this.isGameOver = false;
        this.winner = null;
    }

    /**
     * Sets player names and initializes the game state, typically used after importing a game.
     * <p>
     * Configures player objects with the provided names and sets the game to IN_PROGRESS.
     * Resets game over status and winner. The current turn (isWhiteTurn) should have been
     * determined during the import process from the file.
     *
     * @param nameWhite The name for the white player.
     * @param nameBlack The name for the black player.
     */
    public void setImportGameData(String nameWhite, String nameBlack){
        this.whitePlayer = new Player(nameWhite, ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player(nameBlack, ETeamColor.BLACK_TEAM);
        this.currentState = EChessState.IN_PROGRESS;
        this.isGameOver = false;
        this.winner = null;
    }

    /**
     * Saves the current state of this ChessGame instance into an IMemento.
     * <p>
     * This method creates a snapshot of the game's current state, including the board,
     * player information, turn, and game status, by cloning the current instance.
     *
     * @return An IMemento object containing the snapshot of the game.
     */
    @Override
    public IMemento save() {
        // Cria um memento com um clone do estado atual do ChessGame
        return new GameMemento(this.clone());
    }

    /**
     * Restores the state of this ChessGame instance from the given IMemento.
     * <p>
     * If the memento is a valid GameMemento, this method updates the current
     * game's state to match the snapshot stored in the memento. This involves
     * replacing the board, player data, turn, and other relevant state variables.
     *
     * @param memento The IMemento from which to restore the state.
     */
    @Override
    public void restore(IMemento memento) {
        if (memento instanceof GameMemento gameMemento) {
            ChessGame snapshot = gameMemento.getSnapshot();
            this.board = snapshot.board;
            this.currentState = snapshot.currentState;
            this.whitePlayer = snapshot.whitePlayer;
            this.blackPlayer = snapshot.blackPlayer;
            this.isWhiteTurn = snapshot.isWhiteTurn;
            this.isGameOver = snapshot.isGameOver;
            this.editModeActive = snapshot.editModeActive;
            this.winner = snapshot.winner;
        }
    }

    /**
     * Nested class representing a Memento for the ChessGame state.
     * <p>
     * This class encapsulates a snapshot (a deep clone) of a ChessGame
     * object at a particular moment. It is used by the CareTaker to store
     * game history for undo/redo operations.
     */
    private static class GameMemento implements IMemento {
        private final ChessGame snapshot;

        /**
         * Creates a new GameMemento with a snapshot of the ChessGame.
         * It is expected that the 'game' parameter is already a clone of the
         * state to be saved, typically provided by ChessGame's save() method.
         *
         * @param game The ChessGame instance (a clone) to be stored as a snapshot.
         */
        public GameMemento(ChessGame game) {
            this.snapshot = game;
        }

        /**
         * Retrieves the game state snapshot stored in this memento.
         * <p>
         * Returns a clone of the stored ChessGame snapshot to ensure that the
         * memento's internal state is not modified externally and that the
         * recipient of the snapshot gets a distinct, modifiable copy.
         *
         * @return A clone of the ChessGame snapshot.
         */
        @Override
        public ChessGame getSnapshot() {
            return this.snapshot.clone();
        }
    }

    /**
     * Creates and returns a deep clone of this ChessGame instance.
     * <p>
     * This method is crucial for the Memento pattern to ensure that snapshots
     * are independent copies of the game state. It meticulously clones the game board
     * and player objects (if they are mutable and part of the state).
     *
     * @return A deep clone of this ChessGame.
     * @throws AssertionError if cloning is not supported, which should not occur as this class implements Cloneable.
     */
    @Override
    public ChessGame clone() {
        try {
            ChessGame copy = (ChessGame) super.clone();
            copy.board = this.board.clone();
            if (this.whitePlayer != null) {
                copy.whitePlayer = this.whitePlayer.clone();
            }
            if (this.blackPlayer != null) {
                copy.blackPlayer = this.blackPlayer.clone();
            }
            if (this.winner != null) {
                copy.winner = this.winner.clone();
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ChessGame.clone() failed", e);
        }
    }
}
