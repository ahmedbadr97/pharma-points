package database.entities;

import database.DBOperations;
import database.DBStatement;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Order implements TablesOperations<Order> {
    private int order_id, cus_id, sale_id;
    private DateTime order_time;
    private float total_money_in, total_money_out, total_credit_in, total_credit_out, returned_credit;
    private String notes;
    private DateTime expiry_date;
    private boolean order_archived;
    private Customer customer;
    SaleHistory orderSale;
    private ArrayList<OrderTransaction> orderTransactions;

    public interface OnAddTransactionsAction {
        void transactionAction();
    }


    private ArrayList<OnAddTransactionsAction> onAddTransactionsListener;

    private Order(int order_id, int cus_id, DateTime order_time, int sale_id, String notes, DateTime expiry_date, boolean order_archived) {
        this.order_id = order_id;
        this.cus_id = cus_id;
        this.sale_id = sale_id;
        this.order_time = order_time;
        this.total_money_in = total_money_out = total_credit_in = total_credit_out = returned_credit = 0;
        this.notes = notes;
        orderTransactions = new ArrayList<>();
        onAddTransactionsListener = new ArrayList<>();
        this.order_archived = order_archived;
        this.expiry_date = expiry_date;
    }

    public Order(Customer customer, SaleHistory orderSale) {
        this(0, customer.getId(), null, orderSale.getSale_id(), null, null, false);
        this.customer = customer;
        this.orderSale = orderSale;
    }

    public boolean isOrder_archived() {
        return order_archived;
    }
    public void clearListeners()
    {
        onAddTransactionsListener.clear();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getOrder_id() {
        return order_id;
    }

    public float getTotal_money() {
        return total_money_in - total_money_out;
    }

    public float getTotal_credit() {
        return total_credit_in - total_credit_out;
    }

    public void addOnAddAction(OnAddTransactionsAction onAddTransactionsAction) {
        this.onAddTransactionsListener.add(onAddTransactionsAction);
    }


    public void executeOnAddTransActions() {
        for (OnAddTransactionsAction action : onAddTransactionsListener) {
            action.transactionAction();
        }
    }

    public void addTransaction(OrderTransaction orderTransaction) throws InvalidTransaction {
        checkTransactionValidation(orderTransaction.getMoney_amount(), orderTransaction.getTrans_type());
        changeOrderTotal(orderTransaction.getMoney_amount(), orderTransaction.getTrans_type());
        orderTransactions.add(orderTransaction);
        executeOnAddTransActions();
    }

    public void removeTransaction(OrderTransaction orderTransaction) throws InvalidTransaction {
        checkTransactionValidation(-orderTransaction.getMoney_amount(), orderTransaction.getTrans_type());
        changeOrderTotal(-orderTransaction.getMoney_amount(), orderTransaction.getTrans_type());
        orderTransactions.remove(orderTransaction);
        executeOnAddTransActions();

    }
    public void updateTransaction(OrderTransaction orderTransaction,float new_amount)throws InvalidTransaction
    {
        float change=new_amount-orderTransaction.getMoney_amount();
        checkTransactionValidation(change, orderTransaction.getTrans_type());
        orderTransaction.setMoney_amount(new_amount);
        changeOrderTotal(change,orderTransaction.getTrans_type());
    }

    public void checkTransactionValidation(float trans_amount, OrderTransaction.TransactionType transactionType) throws InvalidTransaction {
        float customer_credit = 0;
        switch (transactionType) {
            case money_in:
                if (total_money_in + trans_amount < 0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.invalidMoneyIn);
                customer_credit = trans_amount * getOrderSale().getMoney_to_credit();
                break;
            case credit_out:
                // produces error no enough balance in customer balance , or remove credit greater than put credit
                if (total_credit_out + trans_amount < 0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.inValidCreditOut);
                customer_credit = -trans_amount;
                break;
            case credit_in:
                // return given credit ,
                // may throw error returned credit greater than given credit
                // may throw error remove from credit in greater than given
                float new_returned_value = returned_credit + trans_amount;
                float new_c_in_value = total_credit_in + trans_amount;
                if (new_c_in_value < 0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.invalidCreditIn);
                if (new_returned_value > total_credit_out)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.returnGreaterThanIn);
                customer_credit = trans_amount;
                break;
            case money_out:
                float new_returned_money = total_money_out + trans_amount;
                if (new_returned_money < 0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.invalidMoneyOut);
                if (new_returned_money > total_money_in)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.returnGreaterThanIn);
                customer_credit = -trans_amount * getOrderSale().getMoney_to_credit();
                break;
            case money_in_settlement:
                customer_credit = trans_amount;

        }
        if (customer.getActive_credit() + customer_credit < 0)
            throw new InvalidTransaction(InvalidTransaction.ErrorType.noEnoughCusCredit);
    }
    public static ArrayList<Order> getExpiredOrders(DateTime current_time)throws SQLException,DataNotFound
    {
        ArrayList<Order> orders = new ArrayList<>();
        String sql_statement ="SELECT * FROM CUS_ORDER WHERE ORDER_ARCHIVED=0 AND EXPIRY_DATE< TO_DATE(?,'DD-MM-YYYY')";
        PreparedStatement p = Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setString(1, current_time.get_Date());
        ResultSet  r = p.executeQuery();
        while (r.next()) {
            Order order = fetch_resultSet(r);
            //TODO improve performance by saving order data
            order.orderSale=SaleHistory.getSaleBy_id(order.getSale_id());
            order.setOrderTransactions(OrderTransaction.getOrderTransactions(order));
            orders.add(order);

        }
        if (orders.isEmpty())
            throw new DataNotFound("no expired orders found  ");
        return orders;

    }



    private void changeOrderTotal(float trans_amount, OrderTransaction.TransactionType transactionType){
        switch (transactionType) {
            case money_in:
                total_money_in += trans_amount;
                total_credit_in += trans_amount*orderSale.getMoney_to_credit();
                break;
            case credit_out:
                total_credit_out += trans_amount;
                break;
            case credit_in:
                total_credit_in += trans_amount;
                returned_credit += trans_amount;
                break;
            case money_out:
                total_money_out += trans_amount;
                total_credit_in += -trans_amount*orderSale.getMoney_to_credit();
                break;
            case money_in_settlement:
                total_money_in += trans_amount;
        }
    }

    public void setOrder_archived(boolean order_archived) {
        this.order_archived = order_archived;
    }

    public float getCustomerBalanceChange(OrderTransaction orderTransaction)
    {
        return this.getCustomerBalanceChange(orderTransaction,false);
    }
    public float getCustomerBalanceChange(OrderTransaction orderTransaction,boolean remove_trans){
        return getCustomerBalanceChange(orderTransaction.getMoney_amount(),orderTransaction.getTrans_type(),remove_trans);
    }
    public float getCustomerBalanceChange(float trans_amount, OrderTransaction.TransactionType transactionType, boolean remove_trans)
    {
         trans_amount *= (remove_trans ? -1.0f:1.0f);
        float customer_credit = 0.0f;
        switch (transactionType) {
            case money_in:
                customer_credit = trans_amount * getOrderSale().getMoney_to_credit();
                break;
            case credit_out:
                customer_credit = -trans_amount;
                break;
            case credit_in:
            case money_in_settlement:
                customer_credit = trans_amount;
                break;
            case money_out:
                customer_credit = -trans_amount * getOrderSale().getMoney_to_credit();
                break;
        }
        return customer_credit;
    }


    public void setOrderTransactions(ArrayList<OrderTransaction> orderTransactions) {
        this.orderTransactions = orderTransactions;
        for (OrderTransaction transaction : orderTransactions) {

            switch (transaction.getTrans_type()) {
                case money_in:
                    total_money_in += transaction.getMoney_amount();
                    total_credit_in += transaction.getMoney_amount() * getOrderSale().getMoney_to_credit();
                    break;
                case credit_out:
                    total_credit_out += transaction.getMoney_amount();
                    break;
                case credit_in:
                    total_credit_in += transaction.getMoney_amount();
                    break;
                case money_out:
                    total_money_out += transaction.getMoney_amount();
                    total_credit_in -= transaction.getMoney_amount() * getOrderSale().getMoney_to_credit();
                    break;
                case money_in_settlement:
                    total_money_in += transaction.getMoney_amount();
                    break;
            }

        }
    }

    public OrderTransaction getSettlementTransaction(OrderTransaction orderTransaction) {
        assert (orderTransaction.getTrans_type() == OrderTransaction.TransactionType.money_out);
        //this method gets a settlement transaction for return transaction to be accepted
        float cus_credit_change = customer.getActive_credit() - orderTransaction.getMoney_amount() * getOrderSale().getMoney_to_credit();

        return new OrderTransaction(this, OrderTransaction.TransactionType.money_in_settlement, cus_credit_change * -1);


    }

    public Customer getCustomer() {
        return customer;
    }

    public SaleHistory getOrderSale() {
        return orderSale;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public DateTime getOrder_time() {
        return order_time;
    }

    public void setOrder_time(DateTime order_time) {
        this.order_time = order_time;
    }

    public String getNotes() {
        return notes;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public float getTotal_money_in() {
        return total_money_in;
    }

    public float getTotal_money_out() {
        return total_money_out;
    }

    public float getTotal_credit_in() {
        return total_credit_in;
    }

    public float getTotal_credit_out() {
        return total_credit_out;
    }

    public ArrayList<OrderTransaction> getOrderTransactions() {
        return orderTransactions;
    }

    public DateTime getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(DateTime expiry_date) {
        this.expiry_date = expiry_date;
    }

    @Override
    public DBStatement<Order> addRow() {
        //TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS')
        //"ORDER_ID","CUS_ID" ,"ORDER_TIME","SALE_ID" ,"NOTES" ,
        String sql_statement = "INSERT INTO CUS_ORDER (ORDER_ID,CUS_ID,SALE_ID,NOTES,EXPIRY_DATE) VALUES(?,?,?,?,TO_DATE(?, 'DD-MM-YYYY'))";
        DBStatement<Order> dbStatement = new DBStatement<Order>(sql_statement, this, DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {

                // get order id
                Statement s = Main.dBconnection.getConnection().createStatement();
                ResultSet r = s.executeQuery(" SELECT CUS_ORDER_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setOrder_id(r.getInt(1));
                r.close();
                s.close();
                //get system time
                s = Main.dBconnection.getConnection().createStatement();
                r = s.executeQuery("SELECT CURRENT_TIMESTAMP FROM dual");
                while (r.next()) {
                    String dateTimeStr = r.getString(1);
                    DateTime current_time = DateTime.from_timeStamp(dateTimeStr);
                    int years = SystemConfiguration.get_CreditExpirySettings().getYears();
                    int months = SystemConfiguration.get_CreditExpirySettings().getMonths();
                    int days = SystemConfiguration.get_CreditExpirySettings().getDays();
                    current_time.add_to_date(years, months, days + 1);
                    current_time.toMinTime();
                    this.getStatement_table().setExpiry_date(current_time);
                }
                r.close();
                s.close();
                this.getPreparedStatement().setInt(1, getStatement_table().getOrder_id());
                this.getPreparedStatement().setInt(2, getStatement_table().getCustomer().getId());
                this.getPreparedStatement().setInt(3, getStatement_table().getSale_id());
                this.getPreparedStatement().setString(4, getStatement_table().getNotes());
                this.getPreparedStatement().setString(5, getStatement_table().expiry_date.get_Date());
            }

            @Override
            public void after_execution_action() throws SQLException {

            }
        };
        return dbStatement;

    }


    @Override
    public DBStatement<Order> DeleteRow() {
        //"ORDER_ID","CUS_ID" ,"ORDER_TIME","SALE_ID" ,"NOTES" ,
        String sql_statement = "DELETE FROM CUS_ORDER WHERE ORDER_ID=?";
        DBStatement<Order> dbStatement = new DBStatement<Order>(sql_statement, this, DBStatement.Type.DELETE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setInt(1, getStatement_table().getOrder_id());
            }

            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<Order> update() {
        //"ORDER_ID","CUS_ID" ,"ORDER_TIME","SALE_ID" ,"NOTES" ,
        String sql_statement = "UPDATE  CUS_ORDER set Notes=? ,ORDER_ARCHIVED=? WHERE ORDER_ID=?";
        DBStatement<Order> dbStatement = new DBStatement<Order>(sql_statement, this, DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setString(1, getStatement_table().getNotes());
                this.getPreparedStatement().setInt(2, getStatement_table().isOrder_archived() ? 1:0);
                this.getPreparedStatement().setInt(3, getStatement_table().getOrder_id());
            }

            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }


    public static ArrayList<Order> getCustomerOrders(Customer customer) throws SQLException, DataNotFound {
        ArrayList<Order> orders = new ArrayList<>();
        String sql_statement = "SELECT *  FROM CUS_ORDER WHERE CUS_ID= ?";
        PreparedStatement p = Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setInt(1, customer.getId());
        ResultSet r = p.executeQuery();
        while (r.next()) {
            Order order = fetch_resultSet(r);
            order.setCustomer(customer);
            order.orderSale = SaleHistory.getSaleBy_id(order.getSale_id());
            order.setOrderTransactions(OrderTransaction.getOrderTransactions(order));
            orders.add(order);

        }
        if (orders.isEmpty())
            throw new DataNotFound("no orders found for this customer " + customer.getName());
        return orders;
    }
    public static Order getOrderByID(int id)throws SQLException,DataNotFound
    {
        Order order = null;
        String sql_statement = "SELECT *  FROM CUS_ORDER WHERE ORDER_ID= ?";
        PreparedStatement p = Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setInt(1, id);
        ResultSet r = p.executeQuery();
        while (r.next()) {
             order = fetch_resultSet(r);
            order.orderSale = SaleHistory.getSaleBy_id(order.getSale_id());
            order.setOrderTransactions(OrderTransaction.getOrderTransactions(order));

        }
        if (order==null)
            throw new DataNotFound("no orders found with id " + id);
        return order;
    }

    private static Order fetch_resultSet(ResultSet r) throws SQLException {
        String dateTimeStr = r.getString(3);
        DateTime orderDateTime = null;
        if (dateTimeStr != null && !dateTimeStr.isEmpty())
            orderDateTime = DateTime.from_timeStamp(dateTimeStr);
        DateTime expiryDateTime  = new DateTime(LocalDateTime.of(r.getDate(6).toLocalDate(), LocalTime.MIN));

        return new Order(r.getInt(1), r.getInt(2), orderDateTime, r.getInt(4), r.getString(5), expiryDateTime, r.getInt(7) == 1);

    }
}
