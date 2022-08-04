package scenes.main;

public class Settings extends WindowAbstract<scenes.controller.Settings>{
    public Settings() {
        load("Settings",790,500);
        getController().ini(this);
    }
}
