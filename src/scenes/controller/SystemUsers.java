package scenes.controller;

import database.DBOperations;
import database.DBStatement;
import database.entities.SystemUser;
import exceptions.DataEntryError;
import exceptions.DataNotFound;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import scenes.abstracts.EditHBox;
import scenes.images.ImageLoader;
import scenes.main.Alerts;
import utils.Validator;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class SystemUsers {
    @FXML
    private TableView<SystemUsersRow> systemUsers_tv;

    @FXML
    private TableColumn<SystemUsersRow, TextField> username_col;

    @FXML
    private TableColumn<SystemUsersRow, TextField> loginName_col;

    @FXML
    private TableColumn<SystemUsersRow, TextField> password_col;

    @FXML
    private TableColumn<SystemUsersRow, CheckBox> isAdmin_col;

    @FXML
    private TableColumn<SystemUsersRow, HBox> editHBox_col;
    @FXML
    private TableColumn<SystemUsersRow,TextField> del_col;

    @FXML
    private TextField username_tf;

    @FXML
    private TextField login_name_tf;

    @FXML
    private PasswordField password_pf;

    @FXML
    private ToggleButton isAdmin_tb;
    DBOperations dbOperations;
    ObservableList<SystemUsersRow> systemUserRows;
    public void ini() {
        dbOperations=new DBOperations();
        systemUserRows=FXCollections.observableArrayList();

        try {
            ArrayList<SystemUser> systemUsers=SystemUser.getSystemUsers();
            for (SystemUser user:systemUsers) {
                systemUserRows.add(new SystemUsersRow(user));
            }

        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
        }
        systemUsers_tv_ini();


    }

    private void systemUsers_tv_ini() {
        username_col.setCellValueFactory((new PropertyValueFactory<>("username_tf")));
        loginName_col.setCellValueFactory((new PropertyValueFactory<>("login_name_tf")));
        password_col.setCellValueFactory((new PropertyValueFactory<>("password_pf")));
        isAdmin_col.setCellValueFactory((new PropertyValueFactory<>("isAdmin_cb")));
        editHBox_col.setCellValueFactory((new PropertyValueFactory<>("editHBox")));
        del_col.setCellValueFactory((new PropertyValueFactory<>("delBtn")));
        systemUsers_tv.setItems(systemUserRows);
    }
    void clearData()
    {
        username_tf.clear();
        login_name_tf.clear();
        password_pf.clear();
        isAdmin_tb.setSelected(false);

    }

    @FXML
    void addUser(ActionEvent event) {

        try {
            String username=Validator.getText(username_tf,"اسم المستخدم");
            String loginName=Validator.getText(login_name_tf,"اسم الدخول");
            String password=Validator.getText(password_pf,"اسم كلمه السر");
            SystemUser systemUser=new SystemUser(username,loginName,password,isAdmin_tb.isSelected() ?1:0);
            systemUserRows.add(new SystemUsersRow(systemUser));
            dbOperations.add(systemUser, DBStatement.Type.ADD);
            clearData();

        }
        catch (DataEntryError d)
        {
            new Alerts(d);
        }

    }

    public class SystemUsersRow {
        TextField username_tf;
        TextField login_name_tf;
        PasswordField password_pf;
        CheckBox isAdmin_cb;
        EditHBox editHBox;
        SystemUser systemUser;
        Button delBtn;

        public SystemUsersRow(SystemUser systemUser) {

            this.systemUser = systemUser;
            // fields initialization
            username_tf=new TextField();
            username_tf.setAlignment(Pos.CENTER);
            login_name_tf=new TextField();
            login_name_tf.setAlignment(Pos.CENTER);

            password_pf=new PasswordField();
            password_pf.setAlignment(Pos.CENTER);

            isAdmin_cb=new CheckBox();
            delBtn=new Button();
            delBtn.setGraphicTextGap(0);
            ImageLoader.icoButton(delBtn,"deleteButton.png",10);
            delBtn.setOnAction(event -> {
                if(systemUser.getAdmin()==1 &&systemAdmins()==1)
                {
                    new Alerts("لا يمكن ازاله المستخدم المسئول الوحيد", Alert.AlertType.ERROR);
                    return;
                }
                dbOperations.add(systemUser, DBStatement.Type.DELETE);
                systemUserRows.remove(this);
            });
            editHBox=new EditHBox();


            setMutability(false);
            setEditHBoxActions();
            loadFieldsData();

        }
        public void setEditHBoxActions()
        {
            editHBox.setEditAction((event -> {
                setMutability(true);
            }));
            editHBox.setCancelAction((event -> {
                setMutability(false);
                loadFieldsData();
            }));
            editHBox.setSaveAction((event -> {
                setMutability(false);
                if(systemUser.getAdmin()==1 && !isAdmin_cb.isSelected()&&systemAdmins()==1)
                {
                    // you changed the only admin to be not admin
                    new Alerts("لا يمكن تغير المستخدم المسئول الوحيد لغير مسئول", Alert.AlertType.ERROR);
                    loadFieldsData();

                }
                else {
                    try {
                        systemUser.setUsername(Validator.getText(username_tf,"اسم المستخدم"));
                        systemUser.setLogin_name(Validator.getText(login_name_tf,"اسم الدخول"));
                        systemUser.setLogin_password(Validator.getText(password_pf,"اسم كلمه السر"));
                    }
                    catch (DataEntryError d)
                    {
                        new Alerts(d);
                        editHBox.editableMod();
                        setMutability(true);
                        return;
                    }
                    systemUser.setAdmin(isAdmin_cb.isSelected() ? 1:0 );
                    dbOperations.add(systemUser, DBStatement.Type.UPDATE);

                }


            }));

        }
        public void loadFieldsData()
        {
            username_tf.setText(systemUser.getUsername());
            login_name_tf.setText(systemUser.getLogin_name());
            password_pf.setText(systemUser.getLogin_password());
            isAdmin_cb.setSelected(systemUser.getAdmin()==1);

        }
        public void setMutability(boolean isMutable)
        {
            setTextFieldMutability(username_tf,isMutable);
            setTextFieldMutability(login_name_tf,isMutable);
            setTextFieldMutability(password_pf,isMutable);

            isAdmin_cb.setDisable(!isMutable);

            isAdmin_cb.setStyle("-fx-opacity: 1");

        }
        public void setTextFieldMutability(TextField textField,boolean isMutable)
        {
            if (isMutable)
            {
                textField.getStyleClass().clear();
                textField.getStyleClass().addAll("text-field", "text-input");
                textField.setEditable(true);
            }
            else{
                textField.getStyleClass().clear();
                textField.getStyleClass().add("text_field_label");
                textField.setEditable(false);
            }

        }

        public TextField getUsername_tf() {
            return username_tf;
        }

        public TextField getLogin_name_tf() {
            return login_name_tf;
        }

        public PasswordField getPassword_pf() {
            return password_pf;
        }

        public CheckBox getIsAdmin_cb() {
            return isAdmin_cb;
        }

        public HBox getEditHBox() {
            return editHBox.getButtonsHBox();
        }

        public Button getDelBtn() {
            return delBtn;
        }

    }
    public int systemAdmins()
    {
        int admins=0;
        for (SystemUsersRow row:systemUserRows)
            admins+=row.systemUser.getAdmin();

        return admins;

    }
    @FXML
    void cancel(ActionEvent event) {
        dbOperations.clear();
        ini();
    }

    @FXML
    void save(ActionEvent event) {
        try {
            dbOperations.execute();
            new Alerts("تم حفظ البيانات بنجاح", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            new Alerts(e);
            ini();
        }
    }


}
