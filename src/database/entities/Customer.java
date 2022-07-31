package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
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
    public interface OnCreditChange {
        public void changeAction();
    }
    ArrayList<OnCreditChange> onActiveCreditChangeActions;
    ArrayList<OnCreditChange> onArchivedChangeCreditActions;

    public Customer(int id, String name, String phone, String barcode, String address, float active_credit,float archived_credit) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.barcode = barcode;
        this.active_credit = active_credit;
        this.archived_credit=archived_credit;
        this.address=address;
        onArchivedChangeCreditActions=new ArrayList<>();
        onActiveCreditChangeActions=new ArrayList<>();
    }
    public Customer(String name,String phone,String barcode,String address){
        this(0,name,phone,barcode,address,0,0);
    }
    public void addActiveCreditChangeAction(OnCreditChange onCreditChangeAction)
    {
     onActiveCreditChangeActions.add(onCreditChangeAction);
    }
    public void clearListeners()
    {
        onActiveCreditChangeActions.clear();
        onArchivedChangeCreditActions.clear();
    }
    public void addArchivedCreditChangeAction(OnCreditChange onCreditChangeAction)
    {
        onArchivedChangeCreditActions.add(onCreditChangeAction);
    }
    private void executeArchivedCreditChangeActions()
    {
        for (OnCreditChange action:onArchivedChangeCreditActions) {
            action.changeAction();
        }
    }
    private void executeActiveCreditChangeActions()
    {
        for (OnCreditChange action:onActiveCreditChangeActions) {
            action.changeAction();
        }
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




    public float getActive_credit() {
        return active_credit;
    }

    public float getArchived_credit() {
        return archived_credit;
    }
    public void fromActiveToArchive(float amount)
    {
        // TODO validate and throw exceptions
        if(active_credit-amount>=0)
        {
            active_credit-=amount;
            archived_credit+=amount;
        }

        executeActiveCreditChangeActions();
        executeArchivedCreditChangeActions();

    }
    public void fromArchiveToActive(float amount)
    {
        // TODO validate and throw exceptions

        if(archived_credit-amount>=0)
        {
            archived_credit-=amount;
            active_credit+=amount;
        }
        executeActiveCreditChangeActions();
        executeArchivedCreditChangeActions();


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
            int id=Integer.parseInt(value);
            p.setInt(1,id);
        }
        else
            p.setString(1,value);
        ResultSet r=p.executeQuery();
        Customer customer=null;
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_ARCHIVED_CREDIT  <-- 7 cols
        while (r.next())
            customer=fetch_resultSet(r);
        r.close();p.close();
        if (customer==null)
            throw new DataNotFound("no Customer found with value ="+value);
        return customer;

    }
    private static Customer fetch_resultSet(ResultSet r) throws SQLException
    {
        return new Customer(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getFloat(6),r.getFloat(7));
    }
    public static ArrayList<Customer> getCustomersByName(String name)throws SQLException,DataNotFound
    {
        ArrayList<Customer> customers=new ArrayList<>();
        String sql_statement="SELECT *  FROM CUSTOMER WHERE CUS_NAME LIKE ?";
        PreparedStatement p=Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setString(1,name+"%");
        ResultSet r=p.executeQuery();
        while (r.next())
            customers.add(fetch_resultSet(r));
        if (customers.isEmpty())
            throw new DataNotFound("no customer found with this name "+ name);
        return customers;
    }


    public static ArrayList<Customer> getCustomersBy_Credit(int credit_start, int credit_end) throws SQLException,DataNotFound {
        ArrayList<Customer> customers=new ArrayList<>();
        String sql_statement="SELECT *  FROM CUSTOMER WHERE";
        int prep_col_idx=1;
        boolean credit_between_value=(credit_start!=-1 && credit_end!=-1);
        boolean credit_start_only=!credit_between_value && credit_start!=-1;

        if (credit_start==-1 && credit_end==-1)
            throw new DataNotFound("please specify filter for search first");


        if (credit_between_value)
        {
            // credit between interval
            sql_statement+=" CUS_ACTIVE_CREDIT BETWEEN ? AND ?";
        }
        else if (credit_start_only){
            sql_statement+=" CUS_ACTIVE_CREDIT>= ?";
        }
        else {
            sql_statement+=" CUS_ACTIVE_CREDIT<= ?";
        }

        PreparedStatement p=Main.dBconnection.getConnection().prepareStatement(sql_statement);
        if(credit_between_value)
        {
            p.setInt(prep_col_idx++,credit_start);
            p.setInt(prep_col_idx,credit_end);
        }
        else if(credit_start_only)
        {
            p.setInt(prep_col_idx,credit_start);
        }
        else
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
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_ARCHIVED_CREDIT  <-- 7 cols
        String sql_statement="INSERT INTO CUSTOMER VALUES(?,?,?,?,?,?,?)";
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

            }
            @Override
            public void after_execution_action() {

            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<Customer> DeleteRow() {
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_archived_CREDIT  <-- 7 cols
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
        // CUS_ID CUS_NAME CUS_PHONE CUS_BARCODE, CUS_ADDRESS ,CUS_ACTIVE_CREDIT,CUS_ARCHIVED_CREDIT  <-- 7 cols
        String sql_statement="UPDATE CUSTOMER set CUS_NAME=? , CUS_PHONE=? ,CUS_BARCODE=? , CUS_ADDRESS=? , CUS_ACTIVE_CREDIT=?,CUS_ARCHIVED_CREDIT=? where CUS_ID=?";
        DBStatement<Customer> dbStatement=new DBStatement<Customer>(sql_statement,this,DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setString(1,getStatement_table().getName());
                this.getPreparedStatement().setString(2,getStatement_table().getPhone());
                this.getPreparedStatement().setString(3,getStatement_table().getBarcode());
                this.getPreparedStatement().setString(4,getStatement_table().getAddress());
                this.getPreparedStatement().setFloat(5,getStatement_table().getActive_credit());
                this.getPreparedStatement().setFloat(6,getStatement_table().getArchived_credit());
                this.getPreparedStatement().setInt(7,getStatement_table().getId());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    public void addToActiveCredit(float value)throws InvalidTransaction
    {
        if(this.active_credit+value<0)
            throw new InvalidTransaction(InvalidTransaction.ErrorType.noEnoughCusCredit);
        this.active_credit+=value;
        executeActiveCreditChangeActions();
    }
    public void addToArchivedCredit(float value)throws InvalidTransaction
    {
        if(this.archived_credit+value<0)
            throw new InvalidTransaction(InvalidTransaction.ErrorType.noEnoughCusCredit);
        this.archived_credit+=value;
        executeArchivedCreditChangeActions();
    }


    @Override
    public String toString() {
        return String.format("Customer (ID= %d , Name= %s , Phone= %s , Barcode= %s ,Address= %s ,active_credit= %f ,archived_credit= %f)", id,name,phone,barcode,address, active_credit,archived_credit);
    }

}
