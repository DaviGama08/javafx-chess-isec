package pt.isec.pa.chess.ui.res;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;

public class ImageManager{
    private ImageManager() { }
    private static final HashMap<String, Image> images= new HashMap<>();
    public static Image getImage(String filename) {
        Image image= images.get(filename);
        if(image== null)
            try(InputStream is= ImageManager.class.getClassLoader().getResourceAsStream("images/pieces/"+filename)) {
                image= new Image(is);
                images.put(filename,image);
            } catch(Exception e) { return null; }
        return image;
    }
}
