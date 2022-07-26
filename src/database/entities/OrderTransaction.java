package database.entities;

import database.DBStatement;
import main.Main;
import utils.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderTransaction implements TablesOperations<OrderTransaction>{

    private int trans_id,order_id;
    private TransactionType trans_type;
    private float money_amount;
    private DateTime trans_time;
    private Order order;

    public OrderTransaction(int trans_id, int order_id, TransactionType trans_type, float money_amount,DateTime trans_time) {
        this.trans_id = trans_id;
        this.order_id = order_id;
        this.trans_type = trans_type;
        this.money_amount = money_amount;
        this.trans_time = trans_time;
    }

    public OrderTransaction(Order order, TransactionType trans_type, float money_amount) {
        this(0,order.getOrder_id(),trans_type,money_amount,new DateTime());
        this.order=order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.order_id=order.getOrder_id();
    }

    public int getTrans_id() {
        return trans_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public DateTime getTrans_time() {
        return trans_time;
    }

    public TransactionType getTrans_type() {
        return trans_type;
    }

    public float getMoney_amount() {
        return money_amount;
    }

    public void setTrans_id(int trans_id) {
        this.trans_id = trans_id;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public DBStatement<OrderTransaction> addRow() {
        // TRANS_ID ORDER_ID TRANS_TYPE MONEY_AMOUNT TRANS_TIME
        String sql_statement="INSERT INTO ORDER_TRANSACTION (TRANS_ID,ORDER_ID,TRANS_TYPE,MONEY_AMOUNT) VALUES(?,?,?,?)";
        DBStatement<OrderTransaction> dbStatement=new DBStatement<OrderTransaction>(sql_statement,this,DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                Statement s= Main.dBconnection.getConnection().createStatement();
                ResultSet r=s.executeQuery(" SELECT ORDER_TRANSACTION_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setTrans_id(r.getInt(1));
                r.close();s.close();
                this.getPreparedStatement().setInt(1,getStatement_table().getTrans_id());
                this.getPreparedStatement().setInt(2,getStatement_table().getOrder().getOrder_id());
                this.getPreparedStatement().setInt(3,getStatement_table().getTrans_type().getDatabaseValue());
                this.getPreparedStatement().setFloat(4,getStatement_table().getMoney_amount());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<OrderTransaction> DeleteRow() {
        // TRANS_ID ORDER_ID TRANS_TYPE MONEY_AMOUNT TRANS_TIME

        String sql_statement="DELETE FROM ORDER_TRANSACTION WHERE TRANS_ID=?";
        DBStatement<OrderTransaction> dbStatement=new DBStatement<OrderTransaction>(sql_statement,this,DBStatement.Type.DELETE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setInt(1,getStatement_table().getTrans_id());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<OrderTransaction> update() {
       return null;
    }

    public enum TransactionType{
        money_in("فاتوره شراء",0),credit_out("صرف نقاط",1),money_out("مرتجع شراء",2),credit_in("مرتجع نقاط",3),money_in_settlement("تسويه حساب",4);
        private final String description;
        private final int databaseValue;

        TransactionType(String description, int databaseValue) {
            this.description = description;
            this.databaseValue = databaseValue;
        }

        @Override
        public String toString() {
            return description;
        }

        public int getDatabaseValue() {
            return databaseValue;
        }
        public static TransactionType fromDatabaseValue(int trans_type)
        {
            switch (trans_type)
            {
                case 1:
                    return TransactionType.credit_in;
                case 2:
                    return TransactionType.money_out;
                case 3:
                    return TransactionType.credit_out;
                case 4:
                    return TransactionType.money_in_settlement;
                default:
                    return TransactionType.money_in;
            }
        }
    }

}
