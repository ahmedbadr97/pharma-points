package scenes.abstracts;

import database.DBOperations;
import database.DBStatement;
import database.entities.Customer;
import database.entities.Order;
import database.entities.SaleHistory;
import database.entities.SystemUser;
import exceptions.DataNotFound;
import main.Main;
import scenes.main.WindowAbstract;

import java.sql.SQLException;

public class OrderDataPane extends WindowAbstract<scenes.controller.OrderDataPane> {
    private Customer customer;
    private DBOperations dbOperations;
    private Order order;
    OrderSettings orderSettings;
    public enum OrderSettings
    {
        newOrder,returnOrder,viewAndEdit
    }
    public OrderDataPane(Customer customer, DBOperations dbOperations) {
        //new order
        load("OrderDataPane",860,585);
        this.customer=customer;
        this.dbOperations=dbOperations;
        this.orderSettings=OrderSettings.newOrder;
        SaleHistory orderSale= Main.appSettings.getCurrent_sale();
        this.order=new Order(customer,orderSale);
        getController().ini(this);
        dbOperations.add(order, DBStatement.Type.ADD);

    }


    public Customer getCustomer() {
        return customer;
    }

    public DBOperations getDbOperations() {
        return dbOperations;
    }


    public Order getOrder() {
        return order;
    }

    public OrderSettings getOrderSettings() {
        return orderSettings;
    }

    @Override
    public void closeAction() {

    }
}
