package main;

import database.DBOperations;

import database.DBStatement;
import database.DBconnection;
import database.entities.ClientComputer;
import database.entities.Customer;
import database.entities.SystemUser;
import exceptions.DataNotFound;
import javafx.application.Application;
import javafx.stage.Stage;
import scenes.abstracts.CustomerDataPane;
import scenes.main.Login;
import scenes.main.NewCustomer;
import utils.DateTime;

import java.sql.SQLException;
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
        appSettings=new AppSettings();
        //TODO get saved server ip , pass it to db connection or make db connection use it directly
        dBconnection=new DBconnection("127.0.0.1");
        dBconnection.Connect();
        Login login=new Login();
        login.showStage();

    }
    public static void shutdownSystem()
    {
        System.exit(-1);
    }
}
