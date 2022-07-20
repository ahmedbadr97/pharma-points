package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Customer implements TablesOperations<Customer>{
   public enum QueryFilter{
        ID,PHONE,BARCODE
    }
    private int id;
    private String name,phone,barcode,address;
    private float active_credit,archived_credit;
    DateTime expiry_date;

    public Customer(int id, String name, String phone, String barcode, String address, float active_credit,float archived_credit, DateTime expiry_date) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.barcode = barcode;
        this.active_credit = active_credit;
        this.archived_credit=archived_credit;
        this.address=address;
        this.expiry_date=expiry_date;
    }
    public Customer(String name,String phone,String barcode,String address){
        this(0,name,phone,barcode,address,0,0,null);
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

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public DateTime getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(DateTime expiry_date) {
        this.expiry_date = expiry_date;
    }

    public float getActive_credit() {
        return active_credit;
    }

    public float getArchived_credit() {
        return archived_credit;
    }

    public void setArchived_credit(float archived_credit) {
        this.archived_credit = archived_credit;
    }

    public void setActive_credit(float active_credit) {
        this.active_credit = active_credit;
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
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_ARCHIVED_CREDIT , EXPIRY_DATE <-- 7 cols
        while (r.next())
            customer=fetch_resultSet(r);
        r.close();p.close();
        if (customer==null)
            throw new DataNotFound("no Customer found with value ="+value);
        return customer;

    }
    private static Customer fetch_resultSet(ResultSet r) throws SQLException
    {
        String dateTimestamp=r.getString(8);
        DateTime expireDate=null;
        if (dateTimestamp!=null)
            expireDate=DateTime.from_timeStamp(dateTimestamp);
        return new Customer(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getFloat(6),r.getFloat(7),expireDate);
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

    public static ArrayList<Customer> getCustomersBy_CreditAndExpiry(DateTime from, DateTime to, int credit_start, int credit_end) throws SQLException,DataNotFound {
        ArrayList<Customer> customers=new ArrayList<>();
        String sql_statement="SELECT *  FROM CUSTOMER WHERE";
        int prep_col_idx=1;
        boolean filter_with_date=(from!=null && to!=null);
        boolean credit_between_value=(credit_start!=-1 && credit_end!=-1);
        boolean credit_start_only=!credit_between_value && credit_start!=-1;
        boolean credit_end_only=!credit_between_value && credit_end!=-1;


        if (!filter_with_date&& credit_start==-1 && credit_end==-1)
            throw new DataNotFound("please specify filter for search first");


        if (filter_with_date){
            sql_statement+=" EXPIRY_DATE BETWEEN  TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF') AND TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF')";
        }
        if (credit_between_value)
        {
            if (filter_with_date)
                sql_statement+=" AND ";
            // credit between interval and expiry date between to dates
            sql_statement+=" CUS_ACTIVE_CREDIT BETWEEN ? AND ?";
        }
        else if (credit_start_only){
            if (filter_with_date)
                sql_statement+=" AND ";
            sql_statement+=" CUS_ACTIVE_CREDIT>= ?";
        }
        else if(credit_end_only){
            if (filter_with_date)
                sql_statement+=" AND ";
            sql_statement+=" CUS_ACTIVE_CREDIT<= ?";
        }

        PreparedStatement p=Main.dBconnection.getConnection().prepareStatement(sql_statement);
        if (filter_with_date)
        {
            p.setString(prep_col_idx++,from.getTimeStamp());
            p.setString(prep_col_idx++,to.getTimeStamp());
        }
        if(credit_between_value)
        {
            p.setInt(prep_col_idx++,credit_start);
            p.setInt(prep_col_idx,credit_end);
        }
        else if(credit_start_only)
        {
            p.setInt(prep_col_idx,credit_start);
        }
        else if (credit_end_only)
        {
            p.setInt(prep_col_idx,credit_end);
        }




        ResultSet r=p.executeQuery();
        while (r.next())
            customers.add(fetch_resultSet(r));
        if (customers.isEmpty())
            throw new DataNotFound("no customers found");
        return customers;
    }


    @Override
    public DBStatement<Customer> addRow()  {
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_ARCHIVED_CREDIT , EXPIRY_DATE <-- 7 cols
        String sql_statement="INSERT INTO CUSTOMER VALUES(?,?,?,?,?,?,?,?)";
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
                this.getPreparedStatement().setString(5,getStatement_table().getAddress());
                this.getPreparedStatement().setFloat(6,getStatement_table().getActive_credit());
                this.getPreparedStatement().setFloat(7,getStatement_table().getArchived_credit());
                if (getExpiry_date()!=null)
                    this.getPreparedStatement().setString(8,getStatement_table().getExpiry_date().getTimeStamp());
                else
                    this.getPreparedStatement().setString(8,null);
            }
            @Override
            public void after_execution_action() {

            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<Customer> DeleteRow() {
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_archived_CREDIT , EXPIRY_DATE <-- 7 cols
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
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_ARCHIVED_CREDIT , EXPIRY_DATE <-- 8 cols
        String sql_statement="UPDATE CUSTOMER set CUS_NAME=? , CUS_PHONE=? ,CUS_BARCODE=? , CUS_ADDRESS=? , CUS_ACTIVE_CREDIT=?,CUS_ARCHIVED_CREDIT=? ,EXPIRY_DATE=TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS.FF') where CUS_ID=?";
        DBStatement<Customer> dbStatement=new DBStatement<Customer>(sql_statement,this,DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setString(1,getStatement_table().getName());
                this.getPreparedStatement().setString(2,getStatement_table().getPhone());
                this.getPreparedStatement().setString(3,getStatement_table().getBarcode());
                this.getPreparedStatement().setString(4,getStatement_table().getAddress());
                this.getPreparedStatement().setFloat(5,getStatement_table().getActive_credit());
                this.getPreparedStatement().setFloat(6,getStatement_table().getArchived_credit());
                if (getExpiry_date()!=null)
                    this.getPreparedStatement().setString(7,getStatement_table().getExpiry_date().getTimeStamp());
                else
                    this.getPreparedStatement().setString(7,null);

                this.getPreparedStatement().setInt(8,getStatement_table().getId());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public String toString() {
        return String.format("Customer (ID= %d , Name= %s , Phone= %s , Barcode= %s ,Address= %s ,active_credit= %f ,archived_credit= %f, Expiry Date= %s)", id,name,phone,barcode,address, active_credit,archived_credit,expiry_date==null ? "":expiry_date.get_Date());
    }
}
