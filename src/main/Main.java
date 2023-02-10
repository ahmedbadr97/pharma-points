package main;


import database.DBconnection;
import database.entities.ClientComputer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import scenes.abstracts.LoadingWindow;

import scenes.main.*;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    public static DBconnection dBconnection;
    public static ClientComputer connectedComputer;
    public static ExecutorService mainThreadsPool;
    public static Stage mainStage;
    public static AppSettings appSettings;
    //TODO add running screens

    public static void main(String[] args) throws SQLException {

        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        Main.mainStage = primaryStage;
        primaryStage.setTitle("Fayed Pharmacy");
        mainThreadsPool = Executors.newCachedThreadPool();
        try {
            AppSettings.InstantiateInstance();
            Login login = new Login();
            login.showStage();
            dBconnection = DBconnection.getInstance();
            LoadingWindow loadingWindow = new LoadingWindow("connecting to database");
            loadingWindow.startProcess(() -> {
                try {
                    dBconnection.Connect();
                    Platform.runLater(loadingWindow::closeStage);
                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        loadingWindow.closeStage();
                        new Alerts(e);
                    });
                }

            });
            loadingWindow.setOnTop();
            loadingWindow.showStage();
        } catch (Exception s) {
            new Alerts(s.getMessage(), Alert.AlertType.ERROR);
            System.exit(-1);
        }

    }

    public static void shutdownSystem() {
        System.exit(-1);
    }
}
