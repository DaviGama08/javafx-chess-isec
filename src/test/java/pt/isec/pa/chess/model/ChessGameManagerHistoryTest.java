package pt.isec.pa.chess.model;

import org.junit.jupiter.api.Test;
import pt.isec.pa.chess.model.data.Enumerations.EPieceType;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameManagerHistoryTest {

    @Test
    void invalidMoveDoesNotConsumeUndoHistoryAndRedoRestoresTheMove() {
        ChessGameManager manager = new ChessGameManager();
        manager.newGame("White", "Black");

        assertTrue(manager.movePiece(4, 1, 4, 3));
        assertFalse(manager.movePiece(4, 1, 4, 2));

        manager.undo();
        assertEquals(EPieceType.PAWN, manager.getPieceTypeAt(4, 1));
        assertFalse(manager.hasPiece(4, 3));
        assertTrue(manager.isWhiteTurn());

        manager.redo();
        assertFalse(manager.hasPiece(4, 1));
        assertEquals(EPieceType.PAWN, manager.getPieceTypeAt(4, 3));
        assertFalse(manager.isWhiteTurn());
    }
}
