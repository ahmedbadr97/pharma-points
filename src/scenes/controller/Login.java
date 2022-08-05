
package scenes.controller;

import database.entities.SystemUser;
import exceptions.DataNotFound;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.Main;
import scenes.main.Alerts;
import scenes.abstracts.DataBaseSettings;
import scenes.main.Home;

import java.sql.SQLException;

public class Login {
    scenes.main.Login scene_main;
    VBox loginVbox;
    DataBaseSettings dataBaseSettings;

    public void init(scenes.main.Login scene_main){
        this.scene_main=scene_main;
        dataBaseSettings=null;
        String lastLoginName=Main.appSettings.getLastLoginName();
        if(lastLoginName!=null)
        {
            login_user_tf.setText(lastLoginName);
            Platform.runLater(()->{
                login_password_pf.requestFocus();
            });
        }
    }
    @FXML
    private StackPane main_Spane;

    @FXML
    private TextField login_user_tf;

    @FXML
    private PasswordField login_password_pf;

    @FXML
    void login_btn_action(ActionEvent event) {
        String login_user=login_user_tf.getText();
        String login_password=login_password_pf.getText();
        try {
            SystemUser logged_in_user=SystemUser.get_user(login_user,login_password);
            scene_main.setLogged_in_user(logged_in_user);
            System.out.println("Welcome");
           Home home= new Home(logged_in_user);
           home.showStage();


        }
        catch (SQLException s)
        {
            new Alerts(s);
        }
        catch (DataNotFound d)
        {
            new Alerts(d.getMessage(), Alert.AlertType.ERROR);
            login_password_pf.clear();
        }
        try {
            Main.appSettings.loadAppSettings(scene_main.getLogged_in_user());
            Main.appSettings.setLastLoginName(login_user);
        } catch (Exception e) {
            new Alerts(e.getMessage()+" unable to load app settings .. app will shutdown", Alert.AlertType.ERROR);
            Main.shutdownSystem();

        }
        scene_main.closeStage();

    }

    @FXML
    void open_database_settings(ActionEvent event) {
        if (dataBaseSettings==null)
        {
            dataBaseSettings=new DataBaseSettings();
            loginVbox=(VBox) main_Spane.getChildren().get(0);
            main_Spane.getChildren().set(0,dataBaseSettings.getParent());
            dataBaseSettings.addOnCloseAction(()->{
                dataBaseSettings=null;
                main_Spane.getChildren().set(0,loginVbox);
            });
        }


    }

}
