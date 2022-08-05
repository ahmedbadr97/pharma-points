package scenes.controller;

import database.entities.SaleHistory;
import exceptions.DataNotFound;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import main.Main;
import scenes.abstracts.OrderSettings;
import scenes.abstracts.SystemUsers;
import scenes.main.Alerts;

import java.sql.SQLException;

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
    private Button printerSetBtn;

    @FXML
    private Button usersSetBtn;
    Button selectedButton;
    scenes.abstracts.DataBaseSettings dataBaseSettingsScene;
    scenes.abstracts.OrderSettings orderSettings;
    scenes.abstracts.SystemUsers systemUsers;
//    PrinterSettings printerSettingsGui;
    scenes.main.Settings main_scene;

    public void ini(scenes.main.Settings main_scene)
    {
        this.main_scene =main_scene;
        dataBaseSettingsScene =new scenes.abstracts.DataBaseSettings();
        selectedButton=null;
        if(Main.appSettings.getLogged_in_user().getAdmin()==1)
        {
            orderSettings=new OrderSettings();
            systemUsers=new SystemUsers();

        }
        else{
            orderSetBtn.setDisable(true);
            usersSetBtn.setDisable(true);
        }



        serverSetAction(null);

    }

    @FXML
    void printerSetAction(ActionEvent event)
    {
//        setSelectedButton(printerSetBtn);
//        if(printerSettingsGui==null)
//            printerSettingsGui=new PrinterSettings(invokingClass.getStage());
//        mainSpane.getChildren().clear();
//        mainSpane.getChildren().add(printerSettingsGui.getParent());
//        setSelectedButton(printerSetBtn);
//        topBarText.setText("اعدادت الطبعه");
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

}
