package scenes.abstracts;

import database.DBOperations;
import database.entities.Customer;
import scenes.main.WindowAbstract;

public class CustomerDataPane  extends WindowAbstract<scenes.controller.CustomerDataPane>
{
    private final boolean mutable;
    DBOperations dbOperations;
    private  Customer customer;
    public CustomerDataPane( boolean mutable, DBOperations dbOperations) {
        this.mutable=mutable;
        this.dbOperations=dbOperations;
        load("CustomerDataPane",860,290);
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


    public boolean isMutable() {
        return mutable;
    }

    @Override
    public void closeAction() {

    }
}
