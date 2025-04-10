package pt.isec.pa.chess.model.data.Game;

import java.io.*;

public class ChessGameSerialization {
    private ChessGameSerialization() {}

    public static void saveGame(ChessGame game, String path){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(game);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static ChessGame loadGame(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (ChessGame) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
