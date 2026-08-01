package pt.isec.pa.chess.model.data.Enumerations;

public enum EChessState {
    NOT_STARTED,
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
