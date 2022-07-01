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
   private Alert dbalert;
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
//    private  String getWindowsMotherboard_SerialNumber() {
//        String result = "";
//        try {
//            File file = File.createTempFile("realhowto",".vbs");
//            file.deleteOnExit();
//            FileWriter fw = new java.io.FileWriter(file);
//
//            String vbs =
//                    "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
//                            + "Set colItems = objWMIService.ExecQuery _ \n"
//                            + "   (\"Select * from Win32_BaseBoard\") \n"
//                            + "For Each objItem in colItems \n"
//                            + "    Wscript.Echo objItem.SerialNumber \n"
//                            + "    exit for  ' do the first cpu only! \n"
//                            + "Next \n";
//
//            fw.write(vbs);
//            fw.close();
//
//            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
//            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while ((line = input.readLine()) != null) {
//                result += line;
//            }
//            input.close();
//        }
//        catch(Exception E){
//            System.err.println("Windows MotherBoard Exp : "+E.getMessage());
//        }
//        return result.trim();
//    }

    public void connectionAction()
    {

    }
}
