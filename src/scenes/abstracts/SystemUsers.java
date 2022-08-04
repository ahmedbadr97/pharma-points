package scenes.abstracts;

import scenes.main.WindowAbstract;

public class SystemUsers extends WindowAbstract<scenes.controller.SystemUsers> {
    public SystemUsers() {
        loadFxmlOnly("SystemUsers",575,400);
        getController().ini();
    }
}
