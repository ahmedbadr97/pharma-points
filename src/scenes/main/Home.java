package scenes.main;

import database.DBconnection;
import database.entities.SystemUser;
import main.Main;

public class Home extends WindowAbstract<scenes.controller.Home>{
    private final SystemUser logged_in_user;
    public Home(SystemUser logged_in_user) {
        this.logged_in_user=logged_in_user;
        load("home",720,350);
        this.getController().init(this);
        DBconnection.ConnectionAction connectionAction=connected -> {
            getController().setConnected(connected);
        };
        Main.dBconnection.addConnectionAction(connectionAction);
        addOnCloseAction(() -> Main.dBconnection.removeConnectionAction(connectionAction));

    }

    public SystemUser getLogged_in_user() {
        return logged_in_user;
    }


}
