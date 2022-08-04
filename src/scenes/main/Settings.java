package scenes.main;

public class Settings extends WindowAbstract<scenes.controller.Settings>{
    public Settings() {
        load("Settings",720,480);
        getController().ini(this);
    }
}
