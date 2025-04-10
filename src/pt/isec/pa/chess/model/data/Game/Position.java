package pt.isec.pa.chess.model.data.Game;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int col;
    private int row;

    public Position(int col, int row){
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(this == o)
            return true;
        if(!(o instanceof Position p))
            return false;
        return this.col == p.col && this.row == p.row;
    }
    @Override
    public int hashCode(){
        return Objects.hash(col, row, this.getClass());
    }
}
