package scenes.main;

import database.entities.SystemUser;

public class Home extends WindowAbstract<scenes.controller.Home>{
    private final SystemUser logged_in_user;
    public Home(SystemUser logged_in_user) {
        this.logged_in_user=logged_in_user;
        load("home",720,350);
        this.getController().init(this);


    }

    public SystemUser getLogged_in_user() {
        return logged_in_user;
    }


}
