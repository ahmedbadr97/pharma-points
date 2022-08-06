package scenes.abstracts;

import scenes.main.WindowAbstract;

public class DatabaseRecovery  extends WindowAbstract<scenes.controller.DatabaseRecovery> {
    public DatabaseRecovery() {
        loadFxmlOnly("DatabaseRecovery",600,377);
        getController().ini(this);
    }
}
