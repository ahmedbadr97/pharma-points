package scenes.abstracts;

import database.DBOperations;
import database.entities.Customer;
import scenes.main.WindowAbstract;

public class CustomerDataPane  extends WindowAbstract<scenes.controller.CustomerDataPane>
{
    private final boolean mutable;
    DBOperations dbOperations;
    private final Customer customer;
    public CustomerDataPane(Customer customer, boolean mutable, DBOperations dbOperations) {
        this.mutable=mutable;
        this.dbOperations=dbOperations;
        this.customer=customer;
        load("CustomerDataPane",860,290);
        getController().ini(this);

    }

    public Customer getCustomer() {
        return customer;
    }

    public DBOperations getDbOperations() {
        return dbOperations;
    }


    public boolean isMutable() {
        return mutable;
    }

    @Override
    public void closeAction() {

    }
}
