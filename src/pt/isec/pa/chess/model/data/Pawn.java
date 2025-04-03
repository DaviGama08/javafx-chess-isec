package pt.isec.pa.chess.model.data;


public class Pawn extends Piece{
    //To make possible other pawns to check en passant move
    private EPawnMoved pawnStatus = EPawnMoved.NEVER;
    public Pawn(boolean isWhiteTeam, char column, int row) {
        super(isWhiteTeam ? 'P' : 'p', column, row, isWhiteTeam, EPieceType.PAWN);
    }

    @Override
    public boolean isInvalidMove(char newColumn, int newRow, GameBoard board) {
        if(board == null) return true;

        int direction = this.isWhiteTeam ? 1 : -1;
        switch (pawnStatus){
            case NEVER -> {
                if( column == newColumn && (row + direction == newRow || row + (2 * direction) == newRow) ){
                    return false;
                }
                else if( (column + 1 == newColumn || column - 1 == newColumn) && row + direction == newRow ){
                    return false;
                }
            }
            case ONCE,MORETHANONCE ->{
                if( column == newColumn && row+direction==newRow){
                    return false;
                }
                else if( (column + 1 == newColumn || column - 1 == newColumn) && row+direction==newRow){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void updatePosition(char newColumn, int newRow){
        this.row = newRow;
        this.column = newColumn;
        this.pawnStatus = pawnStatus == EPawnMoved.NEVER ? EPawnMoved.ONCE : EPawnMoved.MORETHANONCE;
        this.wasMoved = true;
    }

    public EPawnMoved getPawnStatus(){
        return pawnStatus;
    }
}
