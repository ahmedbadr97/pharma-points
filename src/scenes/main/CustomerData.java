package scenes.main;

import database.DBOperations;
import database.entities.Customer;

public class CustomerData extends WindowAbstract<scenes.controller.CustomerData> {
    private  Customer customer;
    private final DBOperations dbOperations;

    public CustomerData(Customer customer) {
        this.customer = customer;
        this.dbOperations=new DBOperations();
        load("CustomerData",860,600);
        getController().ini(this);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public DBOperations getDbOperations() {
        return dbOperations;
    }

}
