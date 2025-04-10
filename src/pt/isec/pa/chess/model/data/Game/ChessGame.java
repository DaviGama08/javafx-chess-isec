package pt.isec.pa.chess.model.data.Game;

import pt.isec.pa.chess.model.data.Enumerations.EChessState;
import pt.isec.pa.chess.model.data.Enumerations.ETeamColor;
import pt.isec.pa.chess.model.data.Factories.PieceFactoryText;
import pt.isec.pa.chess.model.data.Pieces.GameBoard;
import pt.isec.pa.chess.model.data.Pieces.Piece;

import java.io.Serial;
import java.io.Serializable;

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

    public String exportGameState(){
        StringBuilder sb = new StringBuilder();

        String currentTeam = isWhiteTurn ? whitePlayer.getTeam().toString() : blackPlayer.getTeam().toString();
        sb.append(currentTeam).append(",");

        for(int row = 0; row < GameBoard.NUM_ROWS; row++){
            for(int col = 0; col < GameBoard.NUM_COLS; col++){
                Piece piece = board.getPiece((char) ('a' + col), row + 1);
                if(piece != null){
                    sb.append(piece.toString()).append(",");
                }
            }
        }
        return sb.toString();
    }
    public boolean importGameState(String state) {
        if (state == null || state.isEmpty())
            return false;

        String [] tokens = state.split(",");
        if (tokens.length == 0)
            return false;

        String currentTeam = tokens[0];
        isWhiteTurn = currentTeam.equalsIgnoreCase("WHITE_TEAM");

        for (int row = 0; row < GameBoard.NUM_ROWS; row++) {
            for (int col = 0; col < GameBoard.NUM_COLS; col++) {
                board.removePiece(board.getPiece((char) ('a' + col), row + 1));
            }
        }

        for (int i = 1; i < tokens.length; i++) {
            String pieceText = tokens[i];
            if (!pieceText.isEmpty()) {
                Piece p = PieceFactoryText.createPiece(pieceText);
                if (p != null) {
                    board.addPiece(p);
                }
            }
        }
        return true;
    }
    //PARA TESTES...
    public GameBoard getBoard() { return board; }
}
