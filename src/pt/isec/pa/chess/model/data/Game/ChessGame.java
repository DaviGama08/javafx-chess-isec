package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Factories.PieceFactoryText;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ChessGame implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private GameBoard board;
    private EChessState currentState;
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean isWhiteTurn;
    private boolean isGameOver;
    private Player winner;

    public ChessGame() {
        board = new GameBoard();
    }
    public List<Position> getValidMoves(Position pos) {
        return board.getValidMoves(pos, isWhiteTurn);
    }

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

        // Aplica jogada
        board.movePiece(sourceColumn, sourceRow, destColumn, destRow);

        boolean checkMate = board.isCheckMate(!isWhiteTurn);
        boolean check     = board.isKingInCheck(!isWhiteTurn);

        if (checkMate) {
            isGameOver = true;
            winner = isWhiteTurn ? whitePlayer : blackPlayer;
            currentState = isWhiteTurn
                    ? EChessState.CHECKMATE_WHITE_WON
                    : EChessState.CHECKMATE_BLACK_WON;
        } else if (check) {
            currentState = EChessState.CHECK;
        } else {
            currentState = EChessState.IN_PROGRESS;
        }

        isWhiteTurn = !isWhiteTurn; // só inverter depois de tudo avaliado

        return true;
    }

    public boolean gameOver() {
        if(isGameOver){
            if(winner.getTeam() == ETeamColor.WHITE_TEAM)
                currentState = EChessState.CHECKMATE_WHITE_WON;
            else
                currentState = EChessState.CHECKMATE_BLACK_WON;
        }
        return isGameOver;
    }
    public boolean isCheck() {
        return board.isKingInCheck(isWhiteTurn);

    }
    public boolean isStarted() {return currentState != EChessState.NOT_STARTED;}

    public PositionData validatePawnPromotion(){
        return board.validatePawnPromotion();
    }

    public void promotePawn(Position pos, EPieceType newType){
        board.promotePawn(pos, newType);
    }

    // Menu Options

    public void newGame(String nameWhite, String nameBlack) {
        this.board = new GameBoard();
        setPlayersNewGame(nameWhite, nameBlack);
        this.isGameOver = false;
        this.winner = null;
        currentState = EChessState.IN_PROGRESS;
    }

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

    // Getters

    public GameBoard getBoard() {
        return board.clone();
    }

    public Piece getPieceAt(Position pos){
        return board.getPieceAt(pos);
    }

    public String getPlayerName(ETeamColor team) {
        return team == ETeamColor.WHITE_TEAM ? whitePlayer.getName()
                : blackPlayer.getName();
    }

    public EChessState getState() {
        return currentState;
    }

    public PlayerData getCurrentPlayer() {
        Player current = isWhiteTurn ? whitePlayer : blackPlayer;
        return new PlayerData(current.getName(), current.getTeam(), current.getScore());
    }

    public PlayerData getWinner() {
        if (winner == null) return null;
        return new PlayerData(winner.getName(), winner.getTeam(), winner.getScore());
    }

    // Setters
    public void setPlayersNewGame(String nameWhite, String nameBlack) {
        this.whitePlayer = new Player(nameWhite, ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player(nameBlack, ETeamColor.BLACK_TEAM);
        this.isWhiteTurn = true;
        this.isGameOver = false;
        this.winner = null;
    }

    public void setImportGameData(String nameWhite, String nameBlack){
        this.whitePlayer = new Player(nameWhite, ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player(nameBlack, ETeamColor.BLACK_TEAM);
        this.currentState = EChessState.IN_PROGRESS;
        this.isGameOver = false;
        this.winner = null;
    }
}
