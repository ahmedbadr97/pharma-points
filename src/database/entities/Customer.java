package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Customer implements TablesOperations<Customer>{
   public enum QueryFilter{
        ID,NAME,PHONE,BARCODE
    }
    private int id;
    private String name,phone,barcode;
    private float points,balance;

    public Customer(int id, String name, String phone, String barcode, float points, float balance) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.barcode = barcode;
        this.points = points;
        this.balance = balance;
    }
    public Customer(String name,String phone){
        this(0,name,phone,"",0,0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
    public static Customer getCustomer(String value,QueryFilter filter) throws SQLException, DataNotFound {
        String sql_statement="SELECT *  FROM CUSTOMER WHERE ";
        switch (filter){
            case ID:
                sql_statement+="CUS_ID=?";
                break;
            case BARCODE:
                sql_statement+="CUS_BARCODE=?";
                break;
            case PHONE:
                sql_statement+="CUS_PHONE=?";
                break;
            default:
                throw new DataNotFound("Invalid Query Filter");
        }
        PreparedStatement p=Main.dBconnection.getConnection().prepareStatement(sql_statement);
        if (filter==QueryFilter.ID)
        {
            int id=Integer.getInteger(value);
            p.setInt(1,id);
        }
        else
            p.setString(1,value);
        ResultSet r=p.executeQuery();
        Customer customer=null;
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE CUS_POINTS CUS_BALANCE  <-- 6 cols
        while (r.next())
            customer=fetch_resultSet(r);
        r.close();p.close();
        if (customer==null)
            throw new DataNotFound("no Customer found with value ="+value);
        return customer;

    }
    private static Customer fetch_resultSet(ResultSet r) throws SQLException
    {
        return new Customer(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getFloat(5),r.getFloat(6));
    }
    public static ArrayList<Customer> getCustomersByName(String name)throws SQLException,DataNotFound
    {
        ArrayList<Customer> customers=new ArrayList<>();
        String sql_statement="SELECT *  FROM CUSTOMER WHERE CUS_NAME LIKE ?";
        PreparedStatement p=Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setString(1,name);
        ResultSet r=p.executeQuery();
        while (r.next())
            customers.add(fetch_resultSet(r));
        if (customers.isEmpty())
            throw new DataNotFound("no customer found with this name "+ name);
        return customers;
    }

    @Override
    public DBStatement<Customer> addRow()  {
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE CUS_POINTS CUS_BALANCE  <-- 6 cols
        String sql_statement="INSERT INTO CUSTOMER VALUES(?,?,?,?,?,?)";
        DBStatement<Customer> dbStatement=new DBStatement<Customer>(sql_statement,this,DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                Statement s= Main.dBconnection.getConnection().createStatement();
                ResultSet r=s.executeQuery(" SELECT CUSTOMER_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setId(r.getInt(1));
                r.close();s.close();
                this.getPreparedStatement().setInt(1,getStatement_table().getId());
                this.getPreparedStatement().setString(2,getStatement_table().getName());
                this.getPreparedStatement().setString(3,getStatement_table().getPhone());
                this.getPreparedStatement().setString(4,getStatement_table().getBarcode());
                this.getPreparedStatement().setFloat(5,getStatement_table().getPoints());
                this.getPreparedStatement().setFloat(6,getStatement_table().getBalance());
            }
            @Override
            public void after_execution_action() {

            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<Customer> DeleteRow() {
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE CUS_POINTS CUS_BALANCE  <-- 6 cols
        String sql_statement="DELETE FROM CUSTOMER WHERE CUS_ID=?";
        DBStatement<Customer> dbStatement=new DBStatement<Customer>(sql_statement,this,DBStatement.Type.DELETE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setInt(1,getStatement_table().getId());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<Customer> update() {
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE CUS_POINTS CUS_BALANCE  <-- 6 cols
        String sql_statement="UPDATE CUSTOMER set CUS_NAME=? , CUS_PHONE=? ,CUS_BARCODE=? , CUS_POINTS=? , CUS_BALANCE=? where CUS_ID=?";
        DBStatement<Customer> dbStatement=new DBStatement<Customer>(sql_statement,this,DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setString(1,getStatement_table().getName());
                this.getPreparedStatement().setString(2,getStatement_table().getPhone());
                this.getPreparedStatement().setString(3,getStatement_table().getBarcode());
                this.getPreparedStatement().setFloat(4,getStatement_table().getPoints());
                this.getPreparedStatement().setFloat(5,getStatement_table().getBalance());
                this.getPreparedStatement().setInt(6,getStatement_table().getId());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public String toString() {
        return String.format("Customer (ID= %d , Name= %s , Phone= %s , Barcode= %s , Points %f , Balance %f )", id,name,phone,barcode,points,balance);
    }
}
