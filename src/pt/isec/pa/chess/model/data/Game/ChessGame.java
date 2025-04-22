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

    private final GameBoard board;
    EChessState currentState;

    private Player whitePlayer;
    private Player blackPlayer;
    private boolean isWhiteTurn;

    public ChessGame() {
        board = new GameBoard();
    }

    public void setPlayers(String nameWhite, String nameBlack) {
        this.whitePlayer = new Player(nameWhite, ETeamColor.WHITE_TEAM);
        this.blackPlayer = new Player(nameBlack, ETeamColor.BLACK_TEAM);
        this.isWhiteTurn = true;
    }


    public boolean movePiece(int sourceColumn, int sourceRow, int destColumn, int destRow) {
        boolean success = board.movePiece(sourceColumn, sourceRow, destColumn, destRow);
        if(success){
            isWhiteTurn = !isWhiteTurn;
        }
        return success;
    }

    public EChessState getState() {
        return currentState;
    }

    public PlayerData getCurrentPlayer() {
        Player current = isWhiteTurn ? whitePlayer : blackPlayer;
        return new PlayerData(current.getName(), current.getTeam(), current.getScore());
    }

    public boolean gameOver() {
        //TODO
        return false;
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

    public Position validatePawnPromotion(){
        return board.validatePawnPromotion();
    }

    public boolean promotePawn(Position pos, EPieceType newType){
        return board.promotePawn(pos, newType);
    }

    public GameBoard getBoard() {
        return board;
    }
}
