package pt.isec.pa.chess.model.data.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import untrusted.payload.UnexpectedPayload;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameSerializationTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void saveRoundTripRestoresPlayersAndTurn() throws Exception {
        ChessGame game = new ChessGame();
        game.newGame("White", "Black");
        assertTrue(game.movePiece(4, 1, 4, 3));
        Path save = temporaryDirectory.resolve("game.dat");

        ChessGameSerialization.saveGame(game, save.toString());
        ChessGame loaded = ChessGameSerialization.loadGame(save.toString());

        assertEquals("White", loaded.getPlayerName(true));
        assertEquals("Black", loaded.getPlayerName(false));
        assertFalse(loaded.getCurrentPlayer().team().name().startsWith("WHITE"));
        assertNotNull(loaded.getPieceAt(new Position(4, 3)));
    }

    @Test
    void loadRejectsClassesOutsideTheSaveAllowList() throws IOException {
        Path save = temporaryDirectory.resolve("untrusted.dat");
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(save))) {
            output.writeObject(new UnexpectedPayload("not a chess game"));
        }

        assertThrows(IOException.class, () -> ChessGameSerialization.loadGame(save.toString()));
    }
}
