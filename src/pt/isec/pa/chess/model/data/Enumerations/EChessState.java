package pt.isec.pa.chess.model.data.Enumerations;

//TODO: Provisory ENUM UNTIL IMPLEMENT FSM
public enum EChessState {
    IN_PROGRESS,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW_BY_REPETITION,
    DRAW_BY_FIFTY_MOVES,
    CHECKMATE_BLACK_WON,
    CHECKMATE_WHITE_WON,
    DRAW_BY_INSUFFICIENT_MATERIAL
}
