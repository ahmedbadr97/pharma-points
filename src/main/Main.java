package main;

import database.DBOperations;

import database.DBStatement;
import database.DBconnection;
import database.entities.ClientComputer;
import database.entities.Customer;
import database.entities.SystemUser;
import exceptions.DataNotFound;
import exceptions.SystemError;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import scenes.abstracts.CustomerDataPane;
import scenes.abstracts.OrderDataPane;
import scenes.main.*;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    public  static DBconnection dBconnection;
    public static ClientComputer connectedComputer;
    public static ExecutorService mainThreadsPool;
    public static Stage mainStage;
    public static AppSettings appSettings;
    //TODO add running screens

    public static void main(String[] args) throws SQLException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws SQLException, DataNotFound {
        Main.mainStage=primaryStage;
        primaryStage.setTitle("Fayed Pharmacy");
        mainThreadsPool= Executors.newCachedThreadPool();
        try {
            appSettings=new AppSettings();
            dBconnection=new DBconnection(appSettings.getServer_ip());
            dBconnection.Connect();
            Login login=new Login();
            login.showStage();
        }
        catch (SystemError s)
        {
            new Alerts(s.getMessage(), Alert.AlertType.ERROR);
            System.exit(-1);
        }



    }
    public static void shutdownSystem()
    {
        System.exit(-1);
    }
}
