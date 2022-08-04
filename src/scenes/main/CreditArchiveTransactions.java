package scenes.main;

import database.entities.Customer;

public class CreditArchiveTransactions extends WindowAbstract<scenes.controller.CreditArchiveTransactions>{
    private Customer customer;
    public CreditArchiveTransactions(Customer customer) {
        load("CreditArchiveTransactions",610,525);
        this.customer=customer;
        getController().ini(this);
    }

    public Customer getCustomer() {
        return customer;
    }
}
