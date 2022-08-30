package scenes.abstracts;

import database.DBOperations;
import database.DBStatement;
import database.entities.Customer;
import database.entities.Order;
import database.entities.SaleHistory;
import database.entities.SystemUser;
import exceptions.DataNotFound;
import main.AppSettings;
import main.Main;
import scenes.main.WindowAbstract;

public class OrderDataPane extends WindowAbstract<scenes.controller.OrderDataPane> {
    private Customer customer;
    private DBOperations dbOperations;
    private Order order;
    OrderSettings orderSettings;


    public enum OrderSettings {
        newOrder, returnOrder, viewAndEdit
    }

    public OrderDataPane(DBOperations dbOperations) {
        //new order
        loadFxmlOnly("OrderDataPane", 860, 543);
        this.dbOperations = dbOperations;
        this.orderSettings = OrderSettings.newOrder;
        getController().ini(this);

    }

    public OrderDataPane(DBOperations dbOperations, Order order) {
        this.dbOperations = dbOperations;
        this.order = order;
        this.customer=order.getCustomer();
        loadFxmlOnly("OrderDataPane", 860, 543);
        orderSettings=OrderSettings.viewAndEdit;
        getController().ini(this);
        getController().initializeOrder();
    }

    public void initialize_order(Customer customer) {
        this.customer = customer;
        this.order = new Order(customer, AppSettings.getInstance().getCurrent_sale());
        this.dbOperations.add(order, DBStatement.Type.ADD);
        dbOperations.add(customer, DBStatement.Type.UPDATE);

        getController().initializeOrder();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setOrder(Order order) {
        this.order = order;
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
    
}
