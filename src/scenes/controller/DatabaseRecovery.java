package scenes.controller;

import exceptions.DataEntryError;
import exceptions.SystemError;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.AppSettings;
import main.Main;
import scenes.main.Alerts;
import utils.Validator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseRecovery {


    @FXML
    private TextField autoBackupPath_tf;

    @FXML
    private TextField every_years_tf;

    @FXML
    private TextField every_months_tf;

    @FXML
    private TextField every_days_tf;

    @FXML
    private TextField backupNowPath_tf;

    @FXML
    private TextField oldBackupPath_tf;

    AppSettings.DataRecoverySettings dataRecoverySettings;
    scenes.abstracts.DatabaseRecovery main_screen;
    DirectoryChooser autoSavingDirChooser ;
    DirectoryChooser saveNowDirChooser;
    FileChooser backupNowFileChooser;
    public void ini(scenes.abstracts.DatabaseRecovery main_screen)
    {
        this.main_screen=main_screen;
        dataRecoverySettings=null;
        autoSavingDirChooser = new DirectoryChooser();
        saveNowDirChooser=new DirectoryChooser();
        backupNowFileChooser= new FileChooser();
        dataRecoverySettings=Main.appSettings.getDataRecoverySettings();
        loadData();




    }
    public void loadData()
    {
        autoBackupPath_tf.setText(dataRecoverySettings.getBackupPath());
        every_years_tf.setText(Integer.toString(dataRecoverySettings.getYears()));
        every_months_tf.setText(Integer.toString(dataRecoverySettings.getMonths()));
        every_days_tf.setText(Integer.toString(dataRecoverySettings.getDays()));

    }

    @FXML
    void backupNow(ActionEvent event) {
        try {
            if(autoBackupPath_tf.getText()==null||autoBackupPath_tf.getText().isEmpty())
                throw new DataEntryError("يجب اختيار مكان حفط الملافات");
            AppSettings.saveBackupData(backupNowPath_tf.getText());

        }
        catch (DataEntryError | SQLException | IOException d)
        {
            new Alerts(d.getMessage(), Alert.AlertType.ERROR);
        }

    }

    @FXML
    void cancel(ActionEvent event) {
        loadData();
    }

    @FXML
    void save(ActionEvent event) {

        try {
            if(autoBackupPath_tf.getText()==null||autoBackupPath_tf.getText().isEmpty())
                throw new DataEntryError("يجب اختيار مكان حفط الملافات");

            String dataPath=autoBackupPath_tf.getText();
            int years= Validator.getInteger(every_years_tf,"السنين",0,10);
            int months=Validator.getInteger(every_months_tf,"الشهور",0,12);
            int days=Validator.getInteger(every_days_tf,"الايام",0,366);
            if(years+months+days==0)
                throw new DataEntryError("يجب اضافه فتره زمانيه");

            AppSettings.DataRecoverySettings dataRecoverySettings=Main.appSettings.new DataRecoverySettings(dataPath,years,months,days);
            dataRecoverySettings.saveDataToConfigFile();
            if(!Main.appSettings.isMainDevice())
                Main.appSettings.setMainDevice(true);
            this.dataRecoverySettings=dataRecoverySettings;
            new Alerts("تم حفط ظ البيانات بنجاح", Alert.AlertType.INFORMATION);
        }
        catch (DataEntryError d)
        {
            new Alerts(d);
            loadData();
        }
        catch (SystemError s)
        {
            new Alerts(s.getMessage(), Alert.AlertType.ERROR);
            loadData();

        }

    }

    @FXML
    void selectAutoBackupPath(ActionEvent event) {
        File selectedDirectory = autoSavingDirChooser.showDialog(main_screen.getStage());
        if(selectedDirectory != null)
            autoBackupPath_tf.setText(selectedDirectory.getAbsolutePath());

    }

    @FXML
    void selectBackupNowPath(ActionEvent event) {
        File selectedDirectory = saveNowDirChooser.showDialog(main_screen.getStage());

        if(selectedDirectory != null)
            backupNowPath_tf.setText(selectedDirectory.getAbsolutePath());
    }
    @FXML
    void selectOldBackupPath(ActionEvent event) {

        backupNowFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Database Files", "*.DMP"));
        File selectedDirectory = backupNowFileChooser.showOpenDialog(main_screen.getStage());


        if(selectedDirectory != null)
            oldBackupPath_tf.setText(selectedDirectory.getAbsolutePath());

    }

    @FXML
    void backup(ActionEvent event) {

            try {
                if(oldBackupPath_tf.getText()==null||oldBackupPath_tf.getText().isEmpty())
                    throw new DataEntryError("يجب اختيار مكان الملاف");
                AppSettings.loadBackupData(oldBackupPath_tf.getText());
            } catch (DataEntryError | SQLException | IOException e) {
                new Alerts(e.getMessage(), Alert.AlertType.ERROR);
            }


    }

}
