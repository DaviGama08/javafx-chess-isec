package pt.isec.pa.chess.model.data.game;

import java.io.*;

public class ChessGameSerialization {
    private static final ObjectInputFilter SAVE_FILTER = ObjectInputFilter.Config.createFilter(
            "maxdepth=32;maxrefs=10000;maxbytes=1048576;pt.isec.pa.chess.**;java.base/*;!*"
    );

    private ChessGameSerialization() {}

    public static void saveGame(ChessGame game, String path) throws IOException{
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(game);
        }
    }

    public static ChessGame loadGame(String path) throws IOException, ClassNotFoundException  {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            in.setObjectInputFilter(SAVE_FILTER);
            Object value = in.readObject();
            if (!(value instanceof ChessGame game))
                throw new InvalidObjectException("Save file does not contain a ChessGame");
            return game;
        }
    }
}
