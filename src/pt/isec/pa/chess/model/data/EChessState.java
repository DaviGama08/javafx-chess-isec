package pt.isec.pa.chess.model.data;

//TODO: Provisory ENUM UNTIL IMPLEMENT FSM
public enum EChessState {
    IN_PROGRESS,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW_BY_REPETITION,
    DRAW_BY_FIFTY_MOVES,
    DRAW_BY_INSUFFICIENT_MATERIAL

}
