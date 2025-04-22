package pt.isec.pa.chess.model.data.Game;

import java.io.*;

public class ChessGameSerialization {
    private ChessGameSerialization() {}

    public static void saveGame(ChessGame game, String path) throws IOException{
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        out.writeObject(game);
    }

    public static ChessGame loadGame(String path) throws IOException, ClassNotFoundException  {
       ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
       return (ChessGame) in.readObject();
    }
}
