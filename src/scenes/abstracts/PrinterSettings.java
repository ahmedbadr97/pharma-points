package scenes.abstracts;
import javafx.stage.Stage;
import scenes.main.WindowAbstract;
public class PrinterSettings extends WindowAbstract<scenes.controller.PrinterSettings> {
    public PrinterSettings(Stage stage) {
        loadFxmlOnly("PrinterSettings",370,160);
        getController().ini(this);
        setStage(stage);
    }

}
