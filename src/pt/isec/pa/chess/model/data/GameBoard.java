package pt.isec.pa.chess.model.data;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    public static final int NUM_ROWS = 8;
    public static final int NUM_COLS = 8;

    private String lastError = "";

    private final List<List<Piece>> board;

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
        addPiece(new Rook   (true, 'a', 1));
        addPiece(new Knight (true, 'b', 1));
        addPiece(new Bishop (true, 'c', 1));
        addPiece(new Queen  (true, 'd', 1));
        addPiece(new King   (true, 'e', 1));
        addPiece(new Bishop (true, 'f', 1));
        addPiece(new Knight (true, 'g', 1));
        addPiece(new Rook   (true, 'h', 1));

        for (char col = 'a'; col <= 'h'; col++)
            addPiece(new Pawn(true, col, 2));

        // Black Pieces
        addPiece(new Rook   (false, 'a', 8));
        addPiece(new Knight (false, 'b', 8));
        addPiece(new Bishop (false, 'c', 8));
        addPiece(new Queen  (false, 'd', 8));
        addPiece(new King   (false, 'e', 8));
        addPiece(new Bishop (false, 'f', 8));
        addPiece(new Knight (false, 'g', 8));
        addPiece(new Rook   (false, 'h', 8));

        for (char col = 'a'; col <= 'h'; col++)
            addPiece(new Pawn(false, col, 7));
    }

    public void addPiece(Piece piece) {
        if (isInvalidPosition(piece.getColumn(), piece.getRow())) {
            lastError = "Posição inválida para a peça: " + piece;
            return;
        }
        int rowIndex = piece.getRow() - 1;
        int colIndex = columnToIndex(piece.getColumn());
        if (board.get(rowIndex).get(colIndex) != null) {
            lastError = "Posição já ocupada: " + piece.getColumn() + piece.getRow();
            return;
        }
        board.get(rowIndex).set(colIndex, piece);
    }

    private int columnToIndex(char column) {
        return Character.toLowerCase(column) - 'a';
    }

    public boolean isInvalidPosition(char column, int row) {
        int colIndex = columnToIndex(column);
        return row < 1 || row > NUM_ROWS || colIndex < 0 || colIndex >= NUM_COLS;
    }

    public Piece getPiece(char column, int row) {
        if (isInvalidPosition(column, row)) return null;

        return board.get(row - 1).get(columnToIndex(column));
    }

    private boolean isPathUnclearLinear(int srcRow, int srcColIndex, int destRow, int destColIndex) {
        boolean isHorizontal = srcRow == destRow;
        boolean isVertical = srcColIndex == destColIndex;

        if (!isHorizontal && !isVertical) return false;

        int stepRow = Integer.signum(destRow - srcRow);
        int stepCol = Integer.signum(destColIndex - srcColIndex);

        int currentRow = srcRow + stepRow;
        int currentCol = srcColIndex + stepCol;

        while (currentRow != destRow || currentCol != destColIndex) {
            if (board.get(currentRow - 1).get(currentCol) != null) return true;

            currentRow += stepRow;
            currentCol += stepCol;
        }

        return false;
    }

    private boolean isPathUnclearDiagonal(int srcRow, int srcColIndex, int destRow, int destColIndex) {
        if (Math.abs(destRow - srcRow) != Math.abs(destColIndex - srcColIndex)) return false;

        int stepRow = Integer.signum(destRow - srcRow);
        int stepCol = Integer.signum(destColIndex - srcColIndex);

        int currentRow = srcRow + stepRow;
        int currentCol = srcColIndex + stepCol;

        while (currentRow != destRow && currentCol != destColIndex) {
            if (board.get(currentRow - 1).get(currentCol) != null) return true;

            currentRow += stepRow;
            currentCol += stepCol;
        }
        return false;
    }

    private boolean checkPawnMove(Piece piece, Piece destPiece, char sourceColumn, int sourceRow, char destColumn, int destRow) {
        int srcCol = columnToIndex(sourceColumn);
        int destCol = columnToIndex(destColumn);
        int colDiff = destCol - srcCol;
        int rowDiff = destRow - sourceRow;
        int direction = piece.isWhiteTeam ? 1 : -1;
        if (colDiff == 0) {
            if (destPiece != null) return false;

            if (rowDiff == 2 * direction)
                return getPiece(sourceColumn, sourceRow + direction) == null;

            return true;
        }

        if (Math.abs(colDiff) == 1 && rowDiff == direction) {
            if (destPiece != null) {
                if(destPiece.isWhiteTeam == piece.isWhiteTeam)
                    return false;
                removePiece(destPiece);
                piece.updatePosition(destColumn, destRow);
                return true;
            }

            // Check en passant move
            int expectedRow = piece.isWhiteTeam ? 5 : 4;
            if (sourceRow == expectedRow) {
                Piece sidePiece = getPiece(destColumn, sourceRow);

                if (sidePiece instanceof Pawn sidePawn &&
                        sidePawn.isWhiteTeam != piece.isWhiteTeam &&
                        sidePawn.getPawnStatus() == EPawnMoved.ONCE) {
                    removePiece(sidePawn);
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public boolean movePiece(char sourceColumn, int sourceRow, char destColumn, int destRow) {
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
        if (piece.isInvalidMove(destColumn, destRow, this)){
            lastError = "Movimento inválido da peça";
            return false;
        }


        Piece destPiece = getPiece(destColumn, destRow);

        if (destPiece != null && destPiece.isWhiteTeam == piece.isWhiteTeam) {
            lastError = "Destino ocupado pela própria equipa: " + destColumn + destRow;
            return false;
        }

        switch(piece.ePieceType){
            case BISHOP -> {
                if(isPathUnclearDiagonal(sourceRow, columnToIndex(sourceColumn), destRow, columnToIndex(destColumn))){
                    lastError = "Caminho bloqueado para o Bispo";
                    return false;
                }
            }
            case KING -> {
                int srcCol = columnToIndex(sourceColumn);
                int destCol = columnToIndex(destColumn);

                // Castling/roque
                if (Math.abs(destCol - srcCol) == 2) {
                    boolean isKingside = destCol > srcCol;

                    char rookSourceCol = isKingside ? 'h' : 'a';
                    char rookDestCol = isKingside ? (char) (destColumn - 1) : (char) (destColumn + 1);

                    if (isPathUnclearLinear(sourceRow, srcCol, sourceRow, columnToIndex(rookSourceCol))){
                        lastError = "Caminho bloqueado para o Rei";
                        return false;
                    }

                    Piece rook = getPiece(rookSourceCol, sourceRow);
                    if (!(rook instanceof Rook)){
                        lastError = "O roque só pode acontecer entre o Rei e uma Torre";
                        return false;
                    }

                    if(rook.wasMoved){
                        lastError = "A torre já se moveu. Impossível fazer o roque.";
                        return false;
                    }

                    movePieceOnBoard(rook, rookDestCol, sourceRow);
                }
            }
            case PAWN -> {
                if (!checkPawnMove(piece, destPiece, sourceColumn, sourceRow, destColumn, destRow)){
                    lastError = "Caminho bloqueado para o Peão";
                    return false;
                }

                if (destPiece != null) {
                    if (destPiece.isWhiteTeam == piece.isWhiteTeam) {
                        lastError = "Destino ocupado pela própria equipa: " + destColumn + destRow;
                        return false;
                    }
                    removePiece(destPiece);
                }            }
            case QUEEN -> {
                boolean isHorizontal = sourceRow == destRow;
                boolean isVertical = columnToIndex(sourceColumn) == columnToIndex(destColumn);

                if (isHorizontal || isVertical) {
                    if(isPathUnclearLinear(sourceRow, columnToIndex(sourceColumn), destRow, columnToIndex(destColumn))){
                        lastError = "Caminho bloqueado para a Rainha";
                        return false;
                    }
                }
                else {
                    if(isPathUnclearDiagonal(sourceRow, columnToIndex(sourceColumn), destRow, columnToIndex(destColumn))){
                        lastError = "Caminho bloqueado para a Rainha";
                        return false;
                    }
                }
            }
            case ROOK -> {
                if(isPathUnclearLinear(sourceRow, columnToIndex(sourceColumn), destRow, columnToIndex(destColumn))){
                    lastError = "Caminho bloqueado para a Torre";
                    return false;
                }
            }
        }

        if (destPiece != null)
            removePiece(destPiece);

        //03/04
        movePieceOnBoard(piece, destColumn, destRow);
        return true;
    }

    public void removePiece(Piece piece) {
        int rowIndex = piece.getRow() - 1;
        int colIndex = columnToIndex(piece.getColumn());
        board.get(rowIndex).set(colIndex, null);
    }
    private void movePieceOnBoard(Piece piece, char newColumn, int newRow) {
        board.get(piece.getRow() - 1).set(columnToIndex(piece.getColumn()), null);

        piece.updatePosition(newColumn, newRow);

        board.get(newRow - 1).set(columnToIndex(newColumn), piece);
    }
    public String getLastError(){
        return lastError;
    }
}