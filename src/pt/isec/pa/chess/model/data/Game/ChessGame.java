package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Factories.PieceFactoryText;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

import java.io.*;
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

    public void setPlayers(String nameWhite, String nameBlack) {
        this.whitePlayer = new Player(nameWhite, ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player(nameBlack, ETeamColor.BLACK_TEAM);
        this.isWhiteTurn = true;
        this.isGameOver = false;
        this.winner = null;
    }

    public boolean movePiece(int sourceColumn, int sourceRow, int destColumn, int destRow) {
        if (!isStarted() || isGameOver)
            return false;

        Piece mover = board.getPiece(sourceColumn, sourceRow);
        if (mover == null || mover.isWhiteTeam() != isWhiteTurn) {
            return false;
        }

        boolean success = board.movePiece(sourceColumn, sourceRow, destColumn, destRow);
        if (success) {
            // Verifica se o adversário ainda tem o rei em jogo
            boolean kingExists = board.doesKingExist(!isWhiteTurn); // verifica o REI da equipa adversária
            if (!kingExists) {
                isGameOver = true;
                winner = isWhiteTurn ? whitePlayer : blackPlayer;
            }
            isWhiteTurn = !isWhiteTurn;
        }
        return success;
    }

    public PlayerData getWinner() {
        if (winner == null) return null;
        return new PlayerData(winner.getName(), winner.getTeam(), winner.getScore());
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

    public EChessState getState() {
        return currentState;
    }

    public PlayerData getCurrentPlayer() {
        Player current = isWhiteTurn ? whitePlayer : blackPlayer;
        return new PlayerData(current.getName(), current.getTeam(), current.getScore());
    }

    public void newGame(String nameWhite, String nameBlack) {
        this.board = new GameBoard();
        setPlayers(nameWhite, nameBlack);
        this.isGameOver = false;
        this.winner = null;
        currentState = EChessState.IN_PROGRESS;
    }

    public boolean isStarted() {return whitePlayer != null && blackPlayer != null;
    }

    public boolean exportGameState() {
        File file = new File("game.txt");
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
                    sb.append(piece.toString()).append(",");
                }
            }
        }

        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',') {
            sb.setLength(sb.length() - 1);
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean importGameState() {
        File file = new File("game.txt");
        if (!file.exists()) {
            System.err.println("Ficheiro game.txt não existe!");
            return false;
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (   Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                contentBuilder.append(scanner.nextLine());
                contentBuilder.append("\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        String content = contentBuilder.toString();
        content = content.replace("\n", "").replace("\r", "");

        String[] tokens = content.split(",");

        if (tokens.length == 0) {
            System.err.println("Nenhum dado para importar.");
            return false;
        }

        String currentTeam = tokens[0].trim();
        isWhiteTurn = currentTeam.equalsIgnoreCase("WHITE_TEAM");

        for (int row = 0; row < GameBoard.NUM_ROWS; row++) {
            for (int col = 0; col < GameBoard.NUM_COLS; col++) {
                Piece p = board.getPiece(col, row);
                if (p != null) {
                    board.removePiece(p);
                }
            }
        }

        for (int i = 1; i < tokens.length; i++) {
            String pieceText = tokens[i].trim();
            if (!pieceText.isEmpty()) {
                Piece p = PieceFactoryText.createPiece(pieceText);
                if (p != null) {
                    board.addPiece(p);
                } else {
                    System.err.println("Falha ao criar peça: " + pieceText);
                }
            }
        }
        return true;
    }

    public PositionData validatePawnPromotion(){
        return board.validatePawnPromotion();
    }

    public boolean promotePawn(Position pos, EPieceType newType){
        return board.promotePawn(pos, newType);
    }

    public GameBoard getBoard() throws CloneNotSupportedException {
        return board.clone();
    }
    public String getPlayerName(ETeamColor team) {
        return team == ETeamColor.WHITE_TEAM ? whitePlayer.getName()
                : blackPlayer.getName();
    }
}
