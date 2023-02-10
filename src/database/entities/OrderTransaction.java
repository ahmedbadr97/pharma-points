package database.entities;

import database.DBStatement;
import database.DBconnection;
import exceptions.DataNotFound;
import javafx.util.Pair;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderTransaction implements TablesOperations<OrderTransaction> {

    private int trans_id, order_id;
    private TransactionType trans_type;
    private float money_amount;
    private DateTime trans_time;
    private Order order;

    public OrderTransaction(int trans_id, int order_id, TransactionType trans_type, float money_amount, DateTime trans_time) {
        this.trans_id = trans_id;
        this.order_id = order_id;
        this.trans_type = trans_type;
        this.money_amount = money_amount;
        this.trans_time = trans_time;
    }

    public OrderTransaction(Order order, TransactionType trans_type, float money_amount) {
        this(0, order.getOrder_id(), trans_type, money_amount, new DateTime());
        this.order = order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.order_id = order.getOrder_id();
    }

    public void setMoney_amount(float money_amount) {
        this.money_amount = money_amount;
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

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    @Override
    public DBStatement<OrderTransaction> addRow() {
        // TRANS_ID ORDER_ID TRANS_TYPE MONEY_AMOUNT TRANS_TIME
        String sql_statement = "INSERT INTO ORDER_TRANSACTION (TRANS_ID,ORDER_ID,TRANS_TYPE,MONEY_AMOUNT) VALUES(?,?,?,?)";
        DBStatement<OrderTransaction> dbStatement = new DBStatement<OrderTransaction>(sql_statement, this, DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                Statement s = DBconnection.getInstance().getConnection().createStatement();
                ResultSet r = s.executeQuery(" SELECT ORDER_TRANSACTION_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setTrans_id(r.getInt(1));
                r.close();
                s.close();
                this.getPreparedStatement().setInt(1, getStatement_table().getTrans_id());
                this.getPreparedStatement().setInt(2, getStatement_table().getOrder().getOrder_id());
                this.getPreparedStatement().setInt(3, getStatement_table().getTrans_type().getDatabaseValue());
                this.getPreparedStatement().setFloat(4, getStatement_table().getMoney_amount());
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

        String sql_statement = "DELETE FROM ORDER_TRANSACTION WHERE TRANS_ID=?";
        DBStatement<OrderTransaction> dbStatement = new DBStatement<OrderTransaction>(sql_statement, this, DBStatement.Type.DELETE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setInt(1, getStatement_table().getTrans_id());
            }

            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<OrderTransaction> update() {
        // TRANS_ID ORDER_ID TRANS_TYPE MONEY_AMOUNT TRANS_TIME

        String sql_statement="UPDATE ORDER_TRANSACTION set MONEY_AMOUNT=?  where TRANS_ID=?";
        DBStatement<OrderTransaction> dbStatement=new DBStatement<OrderTransaction>(sql_statement,this,DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setFloat(1,getStatement_table().getMoney_amount());
                this.getPreparedStatement().setInt(2,getStatement_table().getTrans_id());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    public static ArrayList<OrderTransaction> getOrderTransactions(Order order) throws SQLException, DataNotFound {
        ArrayList<OrderTransaction> orderTransactions = new ArrayList<>();
        String sql_statement = "SELECT *  FROM ORDER_TRANSACTION WHERE ORDER_ID= ?";
        PreparedStatement p = DBconnection.getInstance().getConnection().prepareStatement(sql_statement);
        p.setInt(1, order.getOrder_id());
        ResultSet r = p.executeQuery();
        while (r.next()) {
            OrderTransaction orderTransaction = fetch_resultSet(r);
            orderTransaction.setOrder(order);
            orderTransactions.add(orderTransaction);
        }
        p.close();r.close();
        if (orderTransactions.isEmpty())
            throw new DataNotFound("no order transactions found for this order id " + order.getOrder_id());
        return orderTransactions;
    }

    private static OrderTransaction fetch_resultSet(ResultSet r) throws SQLException {
        String dateTimeStr = r.getString(5);
        DateTime transactionDateTime = null;
        if (dateTimeStr != null && !dateTimeStr.isEmpty())
            transactionDateTime = DateTime.from_timeStamp(dateTimeStr);
        return new OrderTransaction(r.getInt(1), r.getInt(2), TransactionType.fromDatabaseValue(r.getInt(3)), r.getFloat(4), transactionDateTime);

    }

    public enum TransactionType {
        money_in("فاتورة شراء", 0), credit_out("صرف نقاط", 1), money_out("مرتجع شراء", 2), credit_in("مرتجع نقاط", 3), money_in_settlement("تسويه حساب", 4);
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

        public static TransactionType fromDatabaseValue(int trans_type) {
            switch (trans_type) {

                case 1:
                    return TransactionType.credit_out;
                case 2:
                    return TransactionType.money_out;
                case 3:
                    return TransactionType.credit_in;
                case 4:
                    return TransactionType.money_in_settlement;
                default:
                    return TransactionType.money_in;
            }
        }
    }
    public static ArrayList<OrderTransaction> getTransactionsByDate(DateTime from ,DateTime to)throws SQLException ,DataNotFound
    {

        ArrayList<OrderTransaction> orderTransactions = new ArrayList<>();
        Map<Integer,Order> orderMap=new HashMap<>();
        String sql_statement = "SELECT *  FROM ORDER_TRANSACTION WHERE TRANS_TIME BETWEEN TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS') AND TO_TIMESTAMP(?,'YYYY-MM-DD HH24:MI:SS')";
        PreparedStatement p = DBconnection.getInstance().getConnection().prepareStatement(sql_statement);
        p.setString(1, from.getTimeStamp());
        p.setString(2, to.getTimeStamp());
        ResultSet r = p.executeQuery();
        while (r.next()) {
            OrderTransaction orderTransaction = fetch_resultSet(r);
            Order order=orderMap.get(orderTransaction.getOrder_id());
            if(order==null)
                order=Order.getOrderByID(orderTransaction.getOrder_id(),false);
            orderTransaction.setOrder(order);
            orderTransactions.add(orderTransaction);
        }
        p.close();r.close();
        if (orderTransactions.isEmpty())
            throw new DataNotFound("no order transactions found in this interval from " + from + " to " +to);
        return orderTransactions;
    }

}
