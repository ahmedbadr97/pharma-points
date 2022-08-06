package scenes.abstracts;
import scenes.main.WindowAbstract;
public class PrintSettings extends WindowAbstract<scenes.controller.PrintSettings> {
    public PrintSettings() {
        load("PrintSettings",370,160);
        getController().ini(this);
    }
    public void print()
    {

    }

}
