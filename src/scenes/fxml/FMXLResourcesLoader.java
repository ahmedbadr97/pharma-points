package scenes.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;


import java.io.FileReader;

public class FMXLResourcesLoader {
    public  static FXMLLoader FXML(String fileName){
        FXMLLoader fxmlLoader=null;
        try {
             fxmlLoader=new FXMLLoader(FMXLResourcesLoader.class.getResource(fileName));
        }
        catch (Exception e){
            FmxlLoadAlert();
        }

        return fxmlLoader;

    }
    public static FileReader ReadFile(String filename)
    {
        FileReader fileReader=null;
        try {
           fileReader =new FileReader(filename);
        }
        catch (Exception e){}
       return fileReader;
    }

    public static void FmxlLoadAlert()
    {
        Alert alert=new Alert(Alert.AlertType.ERROR,"Error loading FXML files Program going to shutdown");
        alert.show();
        alert.setOnCloseRequest(event -> System.exit(2));
    }
}
