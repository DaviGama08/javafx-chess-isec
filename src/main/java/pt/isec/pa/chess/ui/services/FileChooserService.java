package pt.isec.pa.chess.ui.services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileChooserService {
    private static Stage stage;
    public static void setStage(Stage stage){
        if(stage != null)
            FileChooserService.stage = stage;
    }
    public static File showOpenGameDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Jogo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat")
        );
        return fileChooser.showOpenDialog(stage);
    }

    public static File showSaveGameDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Jogo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Jogo (*.dat)", "*.dat")
        );
        return fileChooser.showSaveDialog(stage);
    }

    public static File showImportGame(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Jogo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Jogo (*.txt)", "*.txt")
        );
        return fileChooser.showOpenDialog(stage);
    }

    public static File showExportGame(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Jogo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de Jogo (*.txt)", "*.txt")
        );
        return fileChooser.showSaveDialog(stage);
    }
}
