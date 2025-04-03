package pt.isec.pa.chess.model.data;



abstract public class Piece implements IPlayable{
    protected char type;
    protected char column;
    protected int row;
    protected boolean isWhiteTeam;
    protected boolean wasMoved = false;
    protected EPieceType ePieceType;

    public Piece(char type, char column, int row, boolean isWhiteTeam, EPieceType ePieceType) {
        this.type = type;
        this.column = column;
        this.row = row;
        this.isWhiteTeam = isWhiteTeam;
        this.ePieceType = ePieceType;
    }
    
    @Override
    public String toString() {
        String list = "KRkr";
        return "" + type + column + row + (!wasMoved && list.contains(String.valueOf(type)) ? "*" : " ");
    }

    // ####### Getters #######

    public char getType() {
        return type;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public EPieceType getEPieceType(){return ePieceType;}

    // ####### Setters #######

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(char column) {
        this.column = column;
    }

    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }
}
