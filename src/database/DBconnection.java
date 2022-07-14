package database;

import main.Main;
import scenes.main.Alerts;
import database.entities.ClientComputer;
import javafx.scene.control.Alert;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.*;
import java.util.Enumeration;

public class DBconnection {
   private Connection connection=null;
    private String serverip;
    private final String username;
   private final String password;
   private boolean conntected;
   private boolean clientSet;
    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public DBconnection(String serverip)throws SQLException {
          this.serverip = serverip;
        this.username = "fayedpharmacy";
        this.password = "fayed203046";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver Class Loaded");

        }
        catch (ClassNotFoundException e1)
        {

            new Alerts(" Error loading Database drivers (Missing files)",Alert.AlertType.ERROR);
        }

    }
    public void Connect() throws SQLException
    {
            if (connection!=null&&!connection.isClosed())
                return;
            connection=DriverManager.getConnection("jdbc:oracl:thin:@"+serverip+":1521:xe",username,password);
            connection.setAutoCommit(false);
            conntected=true;
        if(!clientSet)
        {
            setClientData();
            clientSet =true;
        }
        connectionAction();
    }
    public void setClientData(){
        try {
            boolean valid=false;
//            String mbSerial=getWindowsMotherboard_SerialNumber();
            PreparedStatement p=connection.prepareStatement("SELECT * FROM \"FAYEDADMIN\".CLIENTCOMPUTER WHERE  MAC_ADDRESS=?");

            clientSet =true;
            Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
            while (networkInterface.hasMoreElements()) {
                NetworkInterface network = networkInterface.nextElement();
                byte[] macAddressBytes = network.getHardwareAddress();
                if (macAddressBytes != null) {
                    StringBuilder macAddressStr = new StringBuilder();
                    for (int i = 0; i < macAddressBytes.length; i++) {
                        macAddressStr.append(String.format("%02X", macAddressBytes[i]));
                    }
                    p.setString(1,macAddressStr.toString());
                    ResultSet r=p.executeQuery();
                    while (r.next())
                    {
                        Main.connectedComputer=new ClientComputer(r.getInt(1),r.getString(2));
                        valid=true;
                    }
                    r.close();
                    if(valid)break;
                }
            }
            p.close();

            if(!valid)System.exit(-1);
        }
        catch(SocketException e){
            new Alerts(e.getMessage(), Alert.AlertType.ERROR);
            System.exit(-1);
        } catch (SQLException sqlException) {
            new Alerts(sqlException);
            System.exit(-1);
        }
    }

    public Connection getConnection() {

        return connection;
    }

    public void DisConnect()
    {
        try {
            if (connection.isClosed()||connection==null)
                return;

            connection.close();
        }
        catch (SQLException s)
        {
            new Alerts(s);
        }
        conntected=false;
        connectionAction();

    }

    public boolean isConntected() {
        return conntected;
    }

    public void connectionAction()
    {

    }
}
