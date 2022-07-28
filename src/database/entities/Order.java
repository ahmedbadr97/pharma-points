package database.entities;

import database.DBStatement;
import exceptions.InvalidTransaction;
import main.Main;
import utils.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Order implements TablesOperations<Order>{
    private int order_id,cus_id,sale_id;
    private DateTime order_time;
    private float total_money_in,total_money_out,total_credit_in,total_credit_out;
    private String notes;
    private Customer customer;
    SaleHistory orderSale;
    private ArrayList<OrderTransaction> orderTransactions;
    public interface OnAddTransactionsAction {
        void transactionAction();
    }
    private ArrayList<OnAddTransactionsAction> onAddTransactionsListener;

    private Order(int order_id, int cus_id, DateTime order_time,int sale_id,String notes) {
        this.order_id = order_id;
        this.cus_id = cus_id;
        this.sale_id=sale_id;
        this.order_time = order_time;
        this.total_money_in=total_money_out= total_credit_in=total_credit_out= 0;
        this.notes = notes;
        orderTransactions=new ArrayList<>();
        onAddTransactionsListener =new ArrayList<>();
    }
    public Order(Customer customer,SaleHistory orderSale){
        this(0,customer.getId(),null, orderSale.getSale_id(),null);
        this.customer=customer;
        this.orderSale = orderSale;
    }

    public int getOrder_id() {
        return order_id;
    }


    public void addOnAddAction(OnAddTransactionsAction onAddTransactionsAction) {
        this.onAddTransactionsListener.add(onAddTransactionsAction)  ;
    }
    public void executeOnAddTransActions()
    {
        for (OnAddTransactionsAction action:onAddTransactionsListener) {
            action.transactionAction();
        }
    }
    public void addTransaction(OrderTransaction orderTransaction)throws InvalidTransaction
    {
        changeOrderTotal(orderTransaction.getMoney_amount(),orderTransaction.getTrans_type());
        orderTransactions.add(orderTransaction);

        executeOnAddTransActions();
    }
    private void changeOrderTotal(float trans_amount, OrderTransaction.TransactionType transactionType)throws InvalidTransaction
    {
        float customer_credit=0;
        float total_credit_in_change=0,total_money_in_change=0,total_credit_out_change=0,total_money_out_change=0;
        switch (transactionType)
        {
            case money_in:
                if(total_money_in+trans_amount<0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.invalidMoneyIn);
                total_money_in_change=trans_amount;
                customer_credit=trans_amount*getOrderSale().getMoney_to_credit();
                break;
            case credit_out:
                if(total_credit_out+trans_amount<0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.inValidCreditOut);
                total_credit_out_change=trans_amount;
                customer_credit=-trans_amount;
                break;
            case credit_in:
                if(total_credit_in+trans_amount<0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.invalidCreditIn);
                total_credit_in_change=trans_amount;
                customer_credit=trans_amount;
                break;
            case money_out:
                if(total_money_out+trans_amount<0)
                    throw new InvalidTransaction(InvalidTransaction.ErrorType.invalidMoneyOut);
                total_money_out_change=trans_amount;
                customer_credit=-trans_amount*getOrderSale().getMoney_to_credit();
                break;
            case money_in_settlement:
                total_money_in_change=trans_amount;
                customer_credit=trans_amount;

        }
        getCustomer().addToActiveCredit(customer_credit);
        total_credit_in+=total_credit_in_change;
        total_credit_out+=total_credit_out_change;
        total_money_in+=total_money_in_change;
        total_money_out+=total_money_out_change;
    }
    public void removeTransaction(OrderTransaction orderTransaction)throws InvalidTransaction
    {
        changeOrderTotal(-orderTransaction.getMoney_amount(),orderTransaction.getTrans_type());
        orderTransactions.remove(orderTransaction);
        executeOnAddTransActions();

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

    @Override
    public DBStatement<Order> addRow() {
        //"ORDER_ID","CUS_ID" ,"ORDER_TIME","SALE_ID" ,"NOTES" ,
        String sql_statement="INSERT INTO CUS_ORDER (ORDER_ID,CUS_ID,SALE_ID,NOTES) VALUES(?,?,?,?)";
        DBStatement<Order> dbStatement=new DBStatement<Order>(sql_statement,this,DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                Statement s= Main.dBconnection.getConnection().createStatement();
                ResultSet r=s.executeQuery(" SELECT CUS_ORDER_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setOrder_id(r.getInt(1));
                r.close();s.close();
                this.getPreparedStatement().setInt(1,getStatement_table().getOrder_id());
                this.getPreparedStatement().setInt(2,getStatement_table().getCustomer().getId());
                this.getPreparedStatement().setInt(3,getStatement_table().getSale_id());
                this.getPreparedStatement().setString(4,getStatement_table().getNotes());
            }
            @Override
            public void after_execution_action()throws SQLException {
                customer.updateCustomerCredit();

            }
        };
        return dbStatement;

    }


    @Override
    public DBStatement<Order> DeleteRow() {
        //"ORDER_ID","CUS_ID" ,"ORDER_TIME","SALE_ID" ,"NOTES" ,


        String sql_statement="DELETE FROM CUS_ORDER WHERE ORDER_ID=?";
        DBStatement<Order> dbStatement=new DBStatement<Order>(sql_statement,this,DBStatement.Type.DELETE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setInt(1,getStatement_table().getOrder_id());
            }
            @Override
            public void after_execution_action() {
            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<Order> update() {
        return null;
    }
}
