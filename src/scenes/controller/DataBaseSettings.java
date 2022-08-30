package scenes.controller;

import database.DBconnection;
import exceptions.SystemError;
import javafx.application.Platform;
import main.AppSettings;
import main.Main;
import scenes.abstracts.LoadingWindow;
import scenes.main.Alerts;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBaseSettings {
    public ToggleGroup IPorPcname;
    public TextField ip1, ip2, ip3, ip4, PCName;
    public RadioButton IPRB, PCNameRB;
    public static String connectiondata;

    scenes.abstracts.DataBaseSettings main_scene;
    String ip;

    public void ini(scenes.abstracts.DataBaseSettings main_scene) {
        this.main_scene = main_scene;
        ip = AppSettings.getInstance().getServer_ip();
        if ((ip != null) && !ip.isEmpty()) {
            Pattern p = Pattern.compile("\\d{1,3}.\\d{1,3}.\\d{1,3}+.\\d{1,3}");
            Matcher m = p.matcher(ip);
            if (m.matches()) {
                byIp();
                String[] iplist = ip.split("\\.");
                ip1.setText(iplist[0]);
                ip2.setText(iplist[1]);
                ip3.setText(iplist[2]);
                ip4.setText(iplist[3]);
            } else {
                byPCName();
                PCName.setText(ip);
            }
        }


    }

    public void byIp() {
        IPRB.setSelected(true);
        ip1.clear();
        ip1.setDisable(false);
        ip2.clear();
        ip2.setDisable(false);
        ip3.clear();
        ip3.setDisable(false);
        ip4.clear();
        ip4.setDisable(false);
        PCName.clear();
        PCName.setDisable(true);
    }


    public void byPCName() {
        PCNameRB.setSelected(true);
        PCName.clear();
        PCName.setDisable(false);
        ip1.clear();
        ip1.setDisable(true);
        ip2.clear();
        ip2.setDisable(true);
        ip3.clear();
        ip3.setDisable(true);
        ip4.clear();
        ip4.setDisable(true);
    }

    public void connect() {

        if (IPRB.isSelected()) {
            boolean validdata = (ip1.getText() != null) && (ip2.getText() != null) && (ip3.getText() != null) && (ip4.getText() != null);
            validdata = validdata && (ip1.getText().length() <= 3) && (ip2.getText().length() <= 3) && (ip3.getText().length() <= 3) && (ip4.getText().length() <= 3);
            try {
                validdata = validdata && Integer.parseInt(ip1.getText()) <= 255;
                validdata = validdata && Integer.parseInt(ip2.getText()) <= 255;
                validdata = validdata && Integer.parseInt(ip3.getText()) <= 255;
                validdata = validdata && Integer.parseInt(ip4.getText()) <= 255;
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid IP Address");
                alert.show();
                ip1.clear();
                ip2.clear();
                ip3.clear();
                ip4.clear();
                validdata = false;
            }
            if (validdata) {
                connectiondata = ip1.getText() + "." + ip2.getText() + "." + ip3.getText() + "." + ip4.getText();
            } else {
                connectiondata = null;
            }
        } else
            connectiondata = PCName.getText();


        if (connectiondata != null && !connectiondata.equals("")) {


            scenes.abstracts.LoadingWindow loadingWindow = new LoadingWindow("connecting to database");
            loadingWindow.startProcess(() -> {
                try {
                    DBconnection.getInstance().setServerip(connectiondata);
                    DBconnection.getInstance().disConnect();
                    DBconnection.getInstance().Connect();
                    AppSettings.getInstance().setServer_ip(connectiondata);

                    if (DBconnection.getInstance().getConnection().isValid(10000)) {
                        Platform.runLater(()->new Alerts("Connected successfully", Alert.AlertType.INFORMATION));

                    }


                } catch (SQLException s) {
                    Platform.runLater(() -> {
                        new Alerts("Failed to connect to the database please enter the database server IP or network name correctly", Alert.AlertType.ERROR);
                        loadingWindow.closeStage();
                    });

                } catch (SystemError s) {

                    Platform.runLater(() -> new Alerts(s.getMessage(), Alert.AlertType.ERROR));

                }

                Platform.runLater(() -> {
                    loadingWindow.closeStage();
                    main_scene.closeStage();
                });
            });
            loadingWindow.setOnTop();
            loadingWindow.showStage();
        }


    }
}

