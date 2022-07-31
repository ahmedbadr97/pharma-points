package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreditArchiveTransaction implements TablesOperations<CreditArchiveTransaction>{
    public  enum TransactionType{
        activeToArchive(1,"من ساريه الى متجمده"),archiveToActive(2,"من متجكده الى ساريه");
      private   final int databaseCode;
        private final String description;
        TransactionType(int databaseCode, String description) {
            this.databaseCode = databaseCode;
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public int getDatabaseCode() {
            return databaseCode;
        }
        public static TransactionType fromDatabaseCode(int databaseCode)
        {
            if(databaseCode==1)
                return TransactionType.activeToArchive;
            else
                return TransactionType.archiveToActive;

        }
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    private int trans_id,cus_id;
    private TransactionType transactionType;
    private int system_trans;
    private float credit;
    private DateTime trans_date;
    private Customer customer;

    private CreditArchiveTransaction(int trans_id, int cus_id, TransactionType transactionType, int system_trans, float credit, DateTime trans_date) {
        this.trans_id = trans_id;
        this.cus_id = cus_id;
        this.transactionType = transactionType;
        this.system_trans = system_trans;
        this.credit = credit;
        this.trans_date = trans_date;
    }

    public CreditArchiveTransaction( Customer customer,float amount) {
        // auto transfer all credit from system
        this(0, customer.getId(), TransactionType.activeToArchive,1,amount,new DateTime());
        this.customer = customer;
    }

    public CreditArchiveTransaction(TransactionType transactionType, int system_trans, float credit, Customer customer) {
      this(0, customer.getId(), transactionType,system_trans,credit,new DateTime());
      this.customer=customer;
    }


    public void setTrans_id(int trans_id) {
        this.trans_id = trans_id;
    }

    public int getTrans_id() {
        return trans_id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getSystem_trans() {
        return system_trans;
    }

    public float getCredit() {
        return credit;
    }

    public DateTime getTrans_date() {
        return trans_date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public DBStatement<CreditArchiveTransaction> addRow() {
        //"TRANS_ID" ,"CUS_ID" ,"TRANS_TYPE","SYSTEM_TRANS" ,"CREDIT" ,"TRANS_DATE",
        String sql_statement = "INSERT INTO CREDIT_ARCHIVE_TRANSACTION (TRANS_ID,CUS_ID,TRANS_TYPE,SYSTEM_TRANS,CREDIT) VALUES(?,?,?,?,?)";
        DBStatement<CreditArchiveTransaction> dbStatement = new DBStatement<CreditArchiveTransaction>(sql_statement, this, DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                Statement s = Main.dBconnection.getConnection().createStatement();
                ResultSet r = s.executeQuery(" SELECT CREDIT_ARCHIVE_TRANSACTION_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setTrans_id(r.getInt(1));
                r.close();
                s.close();
                this.getPreparedStatement().setInt(1, getStatement_table().getTrans_id());
                this.getPreparedStatement().setInt(2, getStatement_table().getCustomer().getId());
                this.getPreparedStatement().setInt(3, getStatement_table().getTransactionType().getDatabaseCode());
                this.getPreparedStatement().setInt(4, getStatement_table().getSystem_trans());
                this.getPreparedStatement().setFloat(5, getStatement_table().getCredit());
            }
            @Override
            public void after_execution_action() throws SQLException {

            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<CreditArchiveTransaction> DeleteRow() {
        return null;
    }

    @Override
    public DBStatement<CreditArchiveTransaction> update() {
        return null;
    }
    private static CreditArchiveTransaction fetch_resultSet(ResultSet r) throws SQLException
    {
        String dateTimestamp=r.getString(6);
        DateTime trans_date=null;
        if (dateTimestamp!=null)
            trans_date=DateTime.from_timeStamp(dateTimestamp);
        return new CreditArchiveTransaction(r.getInt(1),r.getInt(2),TransactionType.fromDatabaseCode(r.getInt(3)),r.getInt(4),r.getFloat(5),trans_date);
    }

    public static CreditArchiveTransaction getLastArchivedBySystem(Customer customer)throws SQLException ,DataNotFound
    {
        CreditArchiveTransaction creditArchiveTransaction=null;
        String sql_statement="SELECT * FROM (SELECT *  FROM CREDIT_ARCHIVE_TRANSACTION WHERE CUS_ID= ? AND SYSTEM_TRANS=1 AND TRANS_TYPE=1 ORDER BY TRANS_DATE DESC) WHERE ROWNUM=1";
        PreparedStatement p=Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setInt(1,customer.getId());
        ResultSet r=p.executeQuery();
        while (r.next())
        {
            creditArchiveTransaction=fetch_resultSet(r);
            creditArchiveTransaction.setCustomer(customer);

        }
        if (creditArchiveTransaction==null)
            throw new DataNotFound("no Archived credit from this customer name="+ customer.getName()+" ID="+customer.getId());
        return creditArchiveTransaction;
    }
}
