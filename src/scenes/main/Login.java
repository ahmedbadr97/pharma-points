package scenes.main;

import database.entities.SystemUser;

public class Login extends WindowAbstract<scenes.controller.Login>{

    private SystemUser logged_in_user;
    public Login(){
        logged_in_user=null;
        load("login_page",570,320);
        this.getController().init(this);
    }
    @Override
    public void CloseAction() {

    }

    public SystemUser getLogged_in_user() {
        return logged_in_user;
    }

    public void setLogged_in_user(SystemUser logged_in_user) {
        this.logged_in_user = logged_in_user;
    }
}
