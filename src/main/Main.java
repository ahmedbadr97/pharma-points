package main;

import database.DBOperations;
import database.DBStatement;
import database.DBconnection;
import database.entities.ClientComputer;
import database.entities.Customer;
import javafx.application.Application;
import javafx.stage.Stage;
import scenes.main.Login;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    public  static DBconnection dBconnection;
    public static ClientComputer connectedComputer;
    public static ExecutorService mainThreadsPool;
    public static Stage mainStage;

    public static void main(String[] args) throws SQLException {

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws SQLException {
        Main.mainStage=primaryStage;
        primaryStage.setTitle("Fayed Pharmacy");
        Login login=new Login();
        mainThreadsPool= Executors.newCachedThreadPool();
        dBconnection=new DBconnection("127.0.0.1");
        dBconnection.Connect();
        DBOperations dbOperations=new DBOperations();
        Customer customer=new Customer("Ahmed Badr","01114242654");
        dbOperations.add(customer, DBOperations.Type.ADD);
        dbOperations.add(customer,DBOperations.Type.UPDATE);
        dbOperations.add(customer, DBOperations.Type.DELETE);
        dbOperations.add(customer, DBOperations.Type.ADD);
        dbOperations.execute();

        login.showStage();

    }
}
