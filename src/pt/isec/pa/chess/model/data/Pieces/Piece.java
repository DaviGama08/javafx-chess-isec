package pt.isec.pa.chess.model.data.Pieces;

import pt.isec.pa.chess.model.data.Enumerations.EPieceType;
import pt.isec.pa.chess.model.data.IPlayable;
import pt.isec.pa.chess.model.data.Game.Position;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

abstract public class Piece implements IPlayable, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected char type;
    protected Position pos;
    protected boolean isWhiteTeam;
    protected boolean wasMoved = false;
    protected EPieceType ePieceType;

    public Piece(char type, int column, int row, boolean isWhiteTeam, EPieceType ePieceType) {
        this.type = type;
        this.pos = new Position(column, row);
        this.isWhiteTeam = isWhiteTeam;
        this.ePieceType = ePieceType;
    }
    public Piece(char type, int column, int row, boolean isWhiteTeam, EPieceType ePieceType, boolean wasMoved) {
        this.type = type;
        this.pos = new Position(column, row);
        this.isWhiteTeam = isWhiteTeam;
        this.ePieceType = ePieceType;
        this.wasMoved = wasMoved;
    }
    @Override
    public String toString() {
        String list = "KRkr";
        return "" + type + indexToColumn(pos.getCol()) + pos.getRow() + (!wasMoved && list.contains(String.valueOf(type)) ? "*" : " ");
    }

    protected void updatePosition(int newColumn, int newRow) {
        this.pos.setCol(newColumn);
        this.pos.setRow(newRow);
        this.wasMoved = true;
    }

    private char indexToColumn(int index) {
        return (char) ('a' + index);
    }

    // ####### Getters #######
    public int getColumn() {
        return pos.getCol();
    }
    public int getRow() {
        return pos.getRow();
    }
    public boolean isWhiteTeam() {return isWhiteTeam;}
    public EPieceType getEPieceType() {return ePieceType;}

    public List<Position> getValidMoves(GameBoard board){
        List<Position> validMoves = new ArrayList<>();

        for(int row = 0; row < GameBoard.NUM_ROWS; row++){
            for(int col = 0; col < GameBoard.NUM_COLS; col++){
                if(row == pos.getRow() && col == pos.getCol())
                    continue;
                if(isValidMove(col, row, board))
                    validMoves.add(new Position(col, row));
            }
        }
        return validMoves;
    }

    public abstract boolean isValidMove(int newColumn, int newRow, GameBoard board);

    public abstract boolean move(int destColumn, int destRow, GameBoard board);

}
