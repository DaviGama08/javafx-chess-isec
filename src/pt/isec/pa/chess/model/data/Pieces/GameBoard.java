package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Factories.PieceFactory;
import pt.isec.pa.chess.model.data.Game.Position;
import pt.isec.pa.chess.model.data.Game.PositionData;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 8;

    private String lastError = "";

    public final List<Piece> pieces = new ArrayList<>();

    public GameBoard() {
        pieces.clear();
        initializePieces();
    }

    public void initializePieces() {
        // White Pieces
        pieces.add(PieceFactory.createPiece(EPieceType.ROOK,   true, 0, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.KNIGHT, true, 1, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.BISHOP, true, 2, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.QUEEN,  true, 3, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.KING,   true, 4, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.BISHOP, true, 5, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.KNIGHT, true, 6, 0));
        pieces.add(PieceFactory.createPiece(EPieceType.ROOK,   true, 7, 0));

        for (int col = 0; col < NUM_COLS; col++) {
            pieces.add(PieceFactory.createPiece(EPieceType.PAWN, true, col, 1));
        }

        // Black Pieces
        pieces.add(PieceFactory.createPiece(EPieceType.ROOK,   false, 0, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.KNIGHT, false, 1, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.BISHOP, false, 2, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.QUEEN,  false, 3, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.KING,   false, 4, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.BISHOP, false, 5, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.KNIGHT, false, 6, 7));
        pieces.add(PieceFactory.createPiece(EPieceType.ROOK,   false, 7, 7));

        for (int col = 0; col < NUM_COLS; col++) {
            pieces.add(PieceFactory.createPiece(EPieceType.PAWN, false, col, 6));
        }

    }

    public void createPiece(EPieceType type, boolean isWhiteTeam, int col, int row) {
        if (isInvalidPosition(col, row)) {
            lastError = "Invalid position for piece creation: " + col + "," + row;
            return;
        }
        if (getPiece(col, row) != null) {
            lastError = "Position already occupied: " + col + "," + row;
            return;
        }
        pieces.add(PieceFactory.createPiece(type, isWhiteTeam, col, row));
    }

    public boolean isInvalidPosition(int col, int row) {
        return row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS;
    }

    public Piece getPiece(int col, int row) {
        if (isInvalidPosition(col, row))
            return null;

        for (Piece p : pieces) {
            if (p.getColumn()==col && p.getRow()==row)
                return p;
        }
        return null;
    }

    public boolean isPathClear(int srcColumn, int srcRow, int destColumn, int destRow) {
        int rowDiff = destRow - srcRow;
        int colDiff = destColumn - srcColumn;

        if (rowDiff == 0 || colDiff == 0 || Math.abs(rowDiff) == Math.abs(colDiff)) {
            int stepRow = Integer.signum(rowDiff);
            int stepCol = Integer.signum(colDiff);
            int r = srcRow + stepRow, c = srcColumn + stepCol;
            while (r != destRow || c != destColumn) {
                if (getPiece(c, r) != null) return false;
                r += stepRow;
                c += stepCol;
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

    public void addPiece(Piece p) {
        if (getPiece(p.getColumn(),p.getRow())!=null) {
            lastError = "Posição já ocupada";
            return;
        }
        pieces.add(p);
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    public void movePieceOnBoard(Piece piece, int newColumn, int newRow) {
        piece.updatePosition(newColumn, newRow);
    }

    public String getLastError(){
        return lastError;
    }

    public void setLastError(String e){
        this.lastError = e;
    }

    public PositionData validatePawnPromotion() {
        for (int col = 0; col < NUM_COLS; col++) {
            Piece piece = getPiece(col, 7);
            if (piece != null && piece.getEPieceType() == EPieceType.PAWN && piece.isWhiteTeam()) {
                return new PositionData(col, 7);
            }
        }

        for (int col = 0; col < NUM_COLS; col++) {
            Piece piece = getPiece(col, 0);
            if (piece != null && piece.getEPieceType() == EPieceType.PAWN && !piece.isWhiteTeam()) {
                return new PositionData(col, 0);
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

    public boolean doesKingExist(boolean isWhiteTeam) {
        for (Piece p : pieces) {
            if (p.getEPieceType() == EPieceType.KING && p.isWhiteTeam() == isWhiteTeam) {
                return true;
            }
        }
        return false;
    }

    @Override public GameBoard clone() {
        GameBoard copy = new GameBoard();
        copy.pieces.clear();
        for (Piece p : pieces)
            copy.pieces.add(PieceFactory.createPiece(
                    p.getEPieceType(), p.isWhiteTeam(), p.getColumn(), p.getRow()));
        return copy;
    }

    public int getNumRows() { return NUM_ROWS; }
    public int getNumCols() { return NUM_COLS; }
}