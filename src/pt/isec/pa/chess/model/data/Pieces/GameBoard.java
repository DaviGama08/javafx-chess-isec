package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Factories.PieceFactory;
import pt.isec.pa.chess.model.data.Game.Position;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 8;

    private String lastError = "";

    public final List<List<Piece>> board;

    public GameBoard() {
        board = new ArrayList<>(NUM_ROWS);
        for (int i = 0; i < NUM_ROWS; i++) {
            List<Piece> row = new ArrayList<>(NUM_COLS);
            for (int j = 0; j < NUM_COLS; j++) {
                row.add(null);
            }
            board.add(row);
        }
        initializePieces();
    }

    public void initializePieces() {
        // White Pieces
        createPiece(EPieceType.ROOK    , true,  0, 0);
        createPiece(EPieceType.KNIGHT  , true,  1, 0);
        createPiece(EPieceType.BISHOP  , true,  2, 0);
        createPiece(EPieceType.QUEEN   , true,  3, 0);
        createPiece(EPieceType.KING    , true,  4, 0);
        createPiece(EPieceType.BISHOP  , true,  5, 0);
        createPiece(EPieceType.KNIGHT  , true,  6, 0);
        createPiece(EPieceType.ROOK    , true,  7, 0);

        for (int col = 0; col <= 7; col++)
            createPiece(EPieceType.PAWN, true, col, 1);

        // Black Pieces
        createPiece(EPieceType.ROOK    , false, 0, 7);
        createPiece(EPieceType.KNIGHT  , false, 1, 7);
        createPiece(EPieceType.BISHOP  , false, 2, 7);
        createPiece(EPieceType.QUEEN   , false, 3, 7);
        createPiece(EPieceType.KING    , false, 4, 7);
        createPiece(EPieceType.BISHOP  , false, 5, 7);
        createPiece(EPieceType.KNIGHT  , false, 6, 7);
        createPiece(EPieceType.ROOK    , false, 7, 7);

        for (int col = 0; col <= 7; col++)
            createPiece(EPieceType.PAWN, false, col, 6);


    }

    public boolean createPiece(EPieceType type, boolean isWhiteTeam, int col, int row) {
        if (isInvalidPosition(col, row)){
            lastError = "Posição inválida para criação da peça "+type+" "+col+" "+row;
            return false;
        }

        if (board.get(row).get(col) != null) {
            lastError = "Posição já ocupada: " + col + " " + row;
            return false;
        }
        board.get(row).set(col, PieceFactory.createPiece(type , isWhiteTeam, col, row));
        return true;
    }

    public boolean isInvalidPosition(int col, int row) {
        return row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS;
    }

    public Piece getPiece(int col, int row) {
        if (isInvalidPosition(col, row))
            return null;

        return board.get(row).get(col);
    }

    public boolean isPathClear(int srcColumn, int srcRow, int destColumn, int destRow) {
        int rowDiff = destRow - srcRow;
        int colDiff = destColumn - srcColumn;

        if (rowDiff == 0 || colDiff == 0 || Math.abs(rowDiff) == Math.abs(colDiff)) {
            int stepRow = Integer.signum(rowDiff);
            int stepCol = Integer.signum(colDiff);
            int currentRow = srcRow + stepRow;
            int currentCol = srcColumn + stepCol;

            while (currentRow != destRow || currentCol != destColumn) {
                if (board.get(currentRow).get(currentCol) != null) {
                    return false;
                }
                currentRow += stepRow;
                currentCol += stepCol;
            }
            return true;
        }
        return true;
    }

    public boolean movePiece(int sourceColumn, int sourceRow, int destColumn, int destRow) {
        if (isInvalidPosition(sourceColumn, sourceRow)) {   
            lastError = "Source Position Invalid: " + sourceColumn + " " + sourceRow;
            return false;
        }

        if (isInvalidPosition(destColumn, destRow)) {
            lastError = "Destiny Position Invalid: " + destColumn + " " + destRow;
            return false;
        }

        Piece piece = getPiece(sourceColumn, sourceRow);
        if (piece == null) {
            lastError = "Nenhuma peça na posição de origem: " + sourceColumn + sourceRow;
            return false;
        }
        if (!piece.move(destColumn, destRow, this)){
            lastError = "Movimento inválido da peça";
            return false;
        }
        return true;
    }
    public void addPiece(Piece piece) {
        int row = piece.getRow();
        int col = piece.getColumn();

        if (isInvalidPosition(col, row)) {
            lastError = "Posição inválida para a peça: col=" + col + ", row=" + row;
            return;
        }

        if (board.get(row).get(col) != null) {
            lastError = "Posição já ocupada: col=" + col + ", row=" + row;
            return;
        }

        board.get(row).set(col, piece);
    }

    public void removePiece(Piece piece) {
        board.get(piece.getRow()).set(piece.getColumn(), null);
    }

    public void movePieceOnBoard(Piece piece, int newColumn, int newRow) {
        board.get(piece.getRow()).set(piece.getColumn(), null);

        piece.updatePosition(newColumn, newRow);

        board.get(newRow).set(newColumn, piece);
    }
    public String getLastError(){
        return lastError;
    }

    public void setLastError(String e){
        this.lastError = e;
    }

    public Position validatePawnPromotion() {
        for (int col = 0; col < NUM_COLS; col++) {
            Piece piece = getPiece(col, 7);
            if (piece != null && piece.getEPieceType() == EPieceType.PAWN && piece.isWhiteTeam()) {
                return new Position(col, 7);
            }
        }

        for (int col = 0; col < NUM_COLS; col++) {
            Piece piece = getPiece(col, 0);
            if (piece != null && piece.getEPieceType() == EPieceType.PAWN && !piece.isWhiteTeam()) {
                return new Position(col, 0);
            }
        }
        return null;
    }

    public boolean promotePawn(Position pos, EPieceType newType) {
        if (isInvalidPosition(pos.getCol(), pos.getRow())) {
            return false;
        }

        Piece pawn = getPiece(pos.getCol(), pos.getRow());
        if (pawn == null || pawn.getEPieceType() != EPieceType.PAWN) {
            return false;
        }

        removePiece(pawn);

        Piece nova = PieceFactory.createPiece(newType, pawn.isWhiteTeam(), pos.getCol(), pos.getRow());

        addPiece(nova);
        return true;
    }

}