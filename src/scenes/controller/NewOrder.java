package scenes.controller;

import database.DBOperations;
import database.DBStatement;
import database.entities.Customer;
import database.entities.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import scenes.abstracts.CustomerDataPane;
import scenes.abstracts.GetCustomerBar;
import scenes.abstracts.OrderDataPane;
import scenes.main.Alerts;
import scenes.main.CustomerSearch;

import java.sql.SQLException;

public class NewOrder {

    @FXML
    private VBox mainVbox;
    private GetCustomerBar getCustomerBar;
    private CustomerDataPane customerDataPane;
    private OrderDataPane orderDataPane;
    private Customer customer;
    private Order order;
    private DBOperations dbOperations;
    private CustomerSearch customerSearch;
    public void ini()
    {
        customer=null;
        order=null;
        dbOperations=new DBOperations();
        customerDataPane=new CustomerDataPane(false,dbOperations);
        customerSearch=new CustomerSearch(true);
        //---------------------------- add search HBox -------------------------------//
        getCustomerBar=new GetCustomerBar();
        getCustomerBar.setSearchWithIdAction(()->{
            customer=getCustomerBar.getController().getSearchCustomer();
            customerDataPane.setCustomer(customer);
            customerDataPane.getController().loadCustomerData();
            orderDataPane.initialize_order(customer);
            order=orderDataPane.getOrder();
            orderDataPane.getOrder().addOnAddAction(()->{
                customerDataPane.getController().loadCustomerData();
            });

        });
        getCustomerBar.setSearchWithNameAction(()->{
            customerSearch.getController().update_cusTv_data(getCustomerBar.getController().getSearchCustomers());
            customerSearch.setOnCustomerSelection(()->{
                customer=customerSearch.getController().getSelectedCustomer();
                customerDataPane.setCustomer(customer);
                customerDataPane.getController().loadCustomerData();
                orderDataPane.initialize_order(customer);
                order=orderDataPane.getOrder();

                orderDataPane.getOrder().addOnAddAction(()->{
                customerDataPane.getController().loadCustomerData();

                });
                customerSearch.closeStage();


            });
            customerSearch.showStage();

        });
        mainVbox.getChildren().add(getCustomerBar.getParent());
        //----------------- add customer Data ---------------------//
        mainVbox.getChildren().add(customerDataPane.getParent());

        //------------------ OrderData ---------------------------//
        orderDataPane=new OrderDataPane(dbOperations);

        mainVbox.getChildren().add(orderDataPane.getParent());





    }
    public void clearData()
    {
        customerDataPane.getController().clearData();
        orderDataPane.getController().clearData();
        getCustomerBar.getController().clearData();

        customer=null;
        order=null;
    }

    @FXML
    void cancelNewOrder(ActionEvent event) {
        clearData();
        dbOperations.clear();
    }

    @FXML
    void saveNewOrder(ActionEvent event) {
        try {
            order.setNotes(orderDataPane.getController().getOrderNotes());
            dbOperations.execute();
            clearData();
        }
        catch (SQLException s)
        {
            new Alerts(s);
        }

    }

}
