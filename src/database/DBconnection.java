package database;

import main.AppSettings;
import main.Main;
import scenes.main.Alerts;
import database.entities.ClientComputer;
import javafx.scene.control.Alert;
import utils.DateTime;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class DBconnection {
    private static DBconnection uniqueInstance;
    private Connection connection = null;
    private String serverip;
    private final String username;
    private final String password;
    private boolean connected;
    private boolean clientSet;
    public interface ConnectionAction{
         void isConnected(boolean connected);
    }

    ArrayList<ConnectionAction> connectionActions;

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }
    public static DBconnection getInstance()
    {
        if (uniqueInstance==null)
            uniqueInstance=new DBconnection();
        return uniqueInstance;
    }

    private DBconnection()  {
        this.serverip = AppSettings.getInstance().getServer_ip();
        this.username = AppSettings.getInstance().getDbUsername();
        this.password = AppSettings.getInstance().getDbPassword();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver Class Loaded");

        } catch (ClassNotFoundException e1) {

            new Alerts(" Error loading Database drivers (Missing files)", Alert.AlertType.ERROR);
        }
        connectionActions=new ArrayList<>();

    }

    public void Connect() throws SQLException {
        if (connection != null && !connection.isClosed())
            return;

        connection = DriverManager.getConnection("jdbc:oracl:thin:@" + serverip + ":1521:xe", username, password);
        connection.setAutoCommit(false);
        connected = true;
        if (!clientSet) {
            setClientData();
            clientSet = true;
        }
        executeConnectionChangeActions();
    }
    public void addConnectionAction(ConnectionAction action)
    {
        connectionActions.add(action);
    }
    public void clearConnectionActions(){
        connectionActions.clear();
    }
    public boolean removeConnectionAction(ConnectionAction action)
    {
        return connectionActions.remove(action);
    }

    public void setClientData() {
        try {
            boolean valid = false;
//            String mbSerial=getWindowsMotherboard_SerialNumber();
            PreparedStatement p = connection.prepareStatement("SELECT * FROM \"ADMIN\".CLIENTCOMPUTER WHERE  MAC_ADDRESS=?");

            clientSet = true;
            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
            while (networkInterface.hasMoreElements()) {
                NetworkInterface network = networkInterface.nextElement();
                byte[] macAddressBytes = network.getHardwareAddress();
                if (macAddressBytes != null) {
                    StringBuilder macAddressStr = new StringBuilder();
                    for (int i = 0; i < macAddressBytes.length; i++) {
                        macAddressStr.append(String.format("%02X", macAddressBytes[i]));
                    }
                    p.setString(1, macAddressStr.toString());
                    ResultSet r = p.executeQuery();
                    while (r.next()) {
                        Main.connectedComputer = new ClientComputer(r.getInt(1), r.getString(2));
                        valid = true;
                    }
                    r.close();
                    if (valid) break;
                }
            }
            p.close();

            if (!valid) System.exit(-1);
        } catch (SocketException e) {
            new Alerts(e.getMessage(), Alert.AlertType.ERROR);
            System.exit(-1);
        } catch (SQLException sqlException) {
            new Alerts(sqlException);
            System.exit(-1);
        }
    }
    public void executeConnectionChangeActions()
    {
        for (ConnectionAction action:connectionActions)
            action.isConnected(connected);

    }

    public void setConnected(boolean connected) {
        if(connected!=this.connected)
        {
            this.connected = connected;
            executeConnectionChangeActions();
        }
    }

    public Connection getConnection() throws SQLException{
        if(connection!=null)
            setConnected(connection.isValid(25));

        return connection;
    }

    public void disConnect() {
        try {
            if (connection == null||connection.isClosed())
                return;

            connection.close();
        } catch (SQLException s) {
            new Alerts(s);
        }
        connected = false;
        executeConnectionChangeActions();
    }

    public boolean isConnected() {
        return connected;
    }


    public DateTime getCurrentDatabaseDate() throws SQLException {
        Statement s = getConnection().createStatement();
        ResultSet r = s.executeQuery("SELECT TO_CHAR(SYSDATE,'DD-MM-YYYY') FROM dual");
        DateTime current_time = null;
        while (r.next()) {
            String dateTimeStr = r.getString(1).substring(0, 10);
            current_time = DateTime.fromDate(dateTimeStr);
        }
        return current_time;
    }
    public DateTime getCurrentDatabaseTime() throws SQLException {
        Statement s = getConnection().createStatement();
        ResultSet r = s.executeQuery("SELECT CURRENT_TIMESTAMP FROM dual");
        DateTime current_time = null;
        while (r.next()) {
            String dateTimeStr = r.getString(1);
            current_time = DateTime.from_timeStamp(dateTimeStr);
        }
        return current_time;
    }
}
