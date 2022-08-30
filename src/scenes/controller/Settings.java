package scenes.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import main.AppSettings;
import main.Main;
import scenes.abstracts.DatabaseRecovery;
import scenes.abstracts.OrderSettings;
import scenes.abstracts.SystemUsers;

public class Settings {

    @FXML
    private Text topBarText;

    @FXML
    private StackPane mainSpane;

    @FXML
    private Button serverSetBtn;

    @FXML
    private Button orderSetBtn;
    @FXML
    private Button databaseRecovery_btn;

    @FXML
    private Button usersSetBtn;
    Button selectedButton;
    scenes.abstracts.DataBaseSettings dataBaseSettingsScene;
    scenes.abstracts.OrderSettings orderSettings;
    scenes.abstracts.SystemUsers systemUsers;
    scenes.main.Settings main_scene;
    scenes.abstracts.DatabaseRecovery databaseRecovery;

    public void ini(scenes.main.Settings main_scene)
    {
        this.main_scene =main_scene;
        dataBaseSettingsScene =new scenes.abstracts.DataBaseSettings();


        selectedButton=null;
        if(AppSettings.getInstance().getLogged_in_user().getAdmin()==1)
        {
            orderSettings=new OrderSettings();
            systemUsers=new SystemUsers();

        }
        else{
            orderSetBtn.setDisable(true);
            usersSetBtn.setDisable(true);
            databaseRecovery_btn.setDisable(true);
        }
        if(!AppSettings.getInstance().isMainDevice())
            databaseRecovery_btn.setDisable(true);
        else{
            databaseRecovery=new DatabaseRecovery();
        }



        serverSetAction(null);

    }



    @FXML
    void serverSetAction(ActionEvent event) {
        mainSpane.getChildren().clear();
        mainSpane.getChildren().add(dataBaseSettingsScene.getParent());
        setSelectedButton(serverSetBtn);
        topBarText.setText("اعدادت السرفر");


    }

    @FXML
    void userSetAction(ActionEvent event) {
        mainSpane.getChildren().clear();
        mainSpane.getChildren().add(systemUsers.getParent());
        setSelectedButton(usersSetBtn);
        topBarText.setText("المستخدمين");


    }
    @FXML
    void orderSetAction(ActionEvent event) {
        mainSpane.getChildren().clear();
        mainSpane.getChildren().add(orderSettings.getParent());
        setSelectedButton(orderSetBtn);
        topBarText.setText("اعدادت الفاتوره");

    }
    private void setSelectedButton(Button newSelectedButton)
    {
        if(selectedButton!=null)
        {
            selectedButton.getStyleClass().clear();
            selectedButton.getStyleClass().add("navButtons_default");
        }

        selectedButton=newSelectedButton;
        newSelectedButton.getStyleClass().clear();
        selectedButton.getStyleClass().add("navButtons_selected");
    }


    @FXML
    void databaseRecovery(ActionEvent event) {
        mainSpane.getChildren().clear();
        mainSpane.getChildren().add(databaseRecovery.getParent());
        setSelectedButton(databaseRecovery_btn);
        topBarText.setText("اعدادت النسخه الاحطياطيه");

    }

}
