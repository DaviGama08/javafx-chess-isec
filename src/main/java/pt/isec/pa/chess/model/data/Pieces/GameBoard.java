package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.Factories.PieceFactory;
import pt.isec.pa.chess.model.data.game.Position;
import pt.isec.pa.chess.model.data.game.PositionData;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameBoard implements Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 1L;
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 8;

    private final List<Piece> pieces = new ArrayList<>();
    private int enPassantTargetCol = -1;
    private int enPassantTargetRow = -1;
    private int enPassantPawnCol = -1;
    private int enPassantPawnRow = -1;

    public GameBoard() {
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

    public int getNumRows() { return NUM_ROWS; }
    public int getNumCols() { return NUM_COLS; }

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

    public Piece getPieceAt(Position pos) {
        if (isInvalidPosition(pos.getCol(), pos.getRow()))
            return null;

        for (Piece p : pieces) {
            if (p.getColumn()==pos.getCol() && p.getRow()== pos.getRow())
                return p.clone();
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
        if (isInvalidPosition(sourceColumn, sourceRow)) return false;

        if (isInvalidPosition(destColumn, destRow)) return false;

        Piece piece = getPiece(sourceColumn, sourceRow);
        if (piece == null)return false;

        boolean moved = piece.move(destColumn, destRow, this);
        if (!moved)
            return false;

        if (piece instanceof Pawn && Math.abs(destRow - sourceRow) == 2) {
            enPassantTargetCol = destColumn;
            enPassantTargetRow = (sourceRow + destRow) / 2;
            enPassantPawnCol = destColumn;
            enPassantPawnRow = destRow;
        } else {
            clearEnPassantWindow();
        }
        return true;
    }

    boolean isEnPassantAvailable(Pawn pawn, int destColumn, int destRow) {
        if (pawn == null || destColumn != enPassantTargetCol || destRow != enPassantTargetRow)
            return false;

        Piece adjacent = getPiece(enPassantPawnCol, enPassantPawnRow);
        return adjacent instanceof Pawn && adjacent.isWhiteTeam() != pawn.isWhiteTeam();
    }

    boolean canCastle(King king, int destColumn, int destRow) {
        if (king == null || king.wasMoved || king.getColumn() != 4)
            return false;

        int homeRow = king.isWhiteTeam() ? 0 : 7;
        if (king.getRow() != homeRow || destRow != homeRow || (destColumn != 2 && destColumn != 6))
            return false;

        int rookColumn = destColumn == 6 ? 7 : 0;
        Piece rook = getPiece(rookColumn, homeRow);
        if (!(rook instanceof Rook) || rook.isWhiteTeam() != king.isWhiteTeam() || rook.wasMoved)
            return false;
        if (!isPathClear(king.getColumn(), homeRow, rookColumn, homeRow))
            return false;

        boolean attackedByWhite = !king.isWhiteTeam();
        int intermediateColumn = destColumn == 6 ? 5 : 3;
        return !isSquareAttacked(king.getColumn(), homeRow, attackedByWhite)
                && !isSquareAttacked(intermediateColumn, homeRow, attackedByWhite)
                && !isSquareAttacked(destColumn, homeRow, attackedByWhite);
    }

    private boolean isSquareAttacked(int column, int row, boolean byWhite) {
        for (Piece attacker : pieces) {
            if (attacker.isWhiteTeam() != byWhite)
                continue;

            int columnDistance = Math.abs(column - attacker.getColumn());
            int rowDelta = row - attacker.getRow();
            if (attacker instanceof Pawn) {
                int direction = attacker.isWhiteTeam() ? 1 : -1;
                if (columnDistance == 1 && rowDelta == direction)
                    return true;
            } else if (attacker instanceof King) {
                if (columnDistance <= 1 && Math.abs(rowDelta) <= 1
                        && (columnDistance != 0 || rowDelta != 0))
                    return true;
            } else if (attacker.isValidMove(column, row, this)) {
                return true;
            }
        }
        return false;
    }

    private void clearEnPassantWindow() {
        enPassantTargetCol = -1;
        enPassantTargetRow = -1;
        enPassantPawnCol = -1;
        enPassantPawnRow = -1;
    }

    public void addPiece(Piece p) {
        if (getPiece(p.getColumn(),p.getRow())!=null) return;

        pieces.add(p);
    }

    public void addPiece(EPieceType type, boolean isWhite, int col, int row){
        if (getPiece(col, row) == null)
            pieces.add(PieceFactory.createPiece(type,   isWhite, col, row));
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
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

    public void promotePawn(Position pos, EPieceType newType) {
        if (pos == null || newType == null || isInvalidPosition(pos.getCol(), pos.getRow())) {
            return;
        }

        Piece pawn = getPiece(pos.getCol(), pos.getRow());
        if (pawn == null || pawn.getEPieceType() != EPieceType.PAWN
                || (pawn.isWhiteTeam() ? pos.getRow() != 7 : pos.getRow() != 0)
                || (newType != EPieceType.QUEEN && newType != EPieceType.ROOK
                && newType != EPieceType.BISHOP && newType != EPieceType.KNIGHT)) {
            return;
        }

        removePiece(pawn);
        Piece nova = PieceFactory.createPiece(newType, pawn.isWhiteTeam(), pos.getCol(), pos.getRow());

        addPiece(nova);
    }

    public List<Position> getValidMoves(Position pos, boolean whiteTurn) {

        Piece p = getPiece(pos.getCol(), pos.getRow());
        if (p == null || p.isWhiteTeam() != whiteTurn)
            return List.of();

        List<Position> raw   = p.getValidMoves(this);
        List<Position> legal = new ArrayList<>();

        for (Position dest : raw) {
            GameBoard clone = this.clone();
            clone.movePiece(p.getColumn(), p.getRow(),
                    dest.getCol(), dest.getRow());

            if (!clone.isKingInCheck(whiteTurn))
                legal.add(dest);
        }
        return legal;
    }

    public boolean isStalemate(boolean whiteTurn) {
        if (isKingInCheck(whiteTurn))
            return false;
        for (Piece p : pieces) {
            if (p.isWhiteTeam() != whiteTurn)
                continue;
            List<Position> moves = getValidMoves(new Position(p.getColumn(), p.getRow()), whiteTurn);
            if (!moves.isEmpty())
                return false;
        }
        return true;
    }

    public boolean isInsufficientMaterial() {
        int queens  = 0, rooks = 0, pawns = 0;
        int bishopsWhite = 0, bishopsBlack = 0;
        int knightsWhite = 0, knightsBlack = 0;

        for (Piece p : pieces) {
            switch (p.getEPieceType()) {
                case QUEEN  -> queens++;
                case ROOK   -> rooks++;
                case PAWN   -> pawns++;
                case BISHOP -> {
                    if (p.isWhiteTeam()) bishopsWhite++; else bishopsBlack++;
                }
                case KNIGHT -> {
                    if (p.isWhiteTeam()) knightsWhite++; else knightsBlack++;
                }
                default -> {}
            }
            if (queens > 0 || rooks > 0 || pawns > 0)
                return false;
        }

        int bishops = bishopsWhite + bishopsBlack;
        int knights = knightsWhite + knightsBlack;

        if (bishops == 0 && knights == 0)
            return true;
        if (bishops == 1 && knights == 0)
            return true;
        if (knights == 1 && bishops == 0)
            return true;

        return false;
    }

    public boolean isCheckMate(boolean whiteTurn) {
        if (!isKingInCheck(whiteTurn))
            return false;
        for (Piece p : pieces) {
            if (p.isWhiteTeam() != whiteTurn)
                continue;

            List<Position> validMoves = getValidMoves(new Position(p.getColumn(), p.getRow()), whiteTurn);
            if (!validMoves.isEmpty())
                return false;
        }
        return true;
    }

    public boolean isKingInCheck(boolean whiteKing) {
        Piece king = pieces.stream()
                .filter(p -> p.getEPieceType() == EPieceType.KING
                        && p.isWhiteTeam() == whiteKing)
                .findFirst()
                .orElse(null);

        if (king == null) {
            return true;
        }

        Position kingPos = new Position(king.getColumn(), king.getRow());

        for (Piece enemy : pieces) {
            if (enemy.isWhiteTeam() == whiteKing)
                continue;

            for (Position dest : enemy.getValidMoves(this))
                if (dest.equals(kingPos))
                    return true;
        }
        return false;
    }

    @Override public GameBoard clone() {
        GameBoard copy = new GameBoard();
        copy.pieces.clear();
        for (Piece p : this.pieces) {
            copy.pieces.add(p.clone());
        }
        copy.enPassantTargetCol = this.enPassantTargetCol;
        copy.enPassantTargetRow = this.enPassantTargetRow;
        copy.enPassantPawnCol = this.enPassantPawnCol;
        copy.enPassantPawnRow = this.enPassantPawnRow;
        return copy;
    }

    public void clearBoard(){
        pieces.clear();
        clearEnPassantWindow();
    }
}
