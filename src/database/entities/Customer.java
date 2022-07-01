package database.entities;

import database.DBStatement;
import main.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer implements TablesOperations<Customer>{
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
}
