package scenes.main;

import database.entities.Customer;

public class NewCustomer extends WindowAbstract<scenes.controller.NewCustomer>{
    private Customer newCustomer;
    public NewCustomer() {
        load("NewCustomer",700,300);
        getController().ini(this);
        newCustomer=null;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }
}
