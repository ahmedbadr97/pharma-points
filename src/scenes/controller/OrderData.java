package scenes.controller;

import database.DBOperations;
import database.entities.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import scenes.abstracts.OrderDataPane;
import scenes.images.ImageLoader;

public class OrderData {

    @FXML
    private Label order_id_lb;

    @FXML
    private Button remove_order_btn;

    @FXML
    private Label cus_active_credit_lb;
    @FXML
    private VBox main_Vbox;

    private OrderDataPane orderDataPane;
    private DBOperations dbOperations;

    private scenes.main.OrderData main_screen;
    public void ini(scenes.main.OrderData main_screen){
        this.main_screen=main_screen;
        order_id_lb.setText(Integer.toString(main_screen.getOrder().getOrder_id()));
        ImageLoader.icoButton(remove_order_btn,"deleteButton.png",10);
        cus_active_credit_lb.setText(Float.toString(main_screen.getOrder().getCustomer().getActive_credit()));
        dbOperations=new DBOperations();
        this.orderDataPane=new OrderDataPane(dbOperations,main_screen.getOrder());
        main_Vbox.getChildren().add(  orderDataPane.getParent());
        Customer customer=    main_screen.getOrder().getCustomer();
        customer.addActiveCreditChangeAction(()->cus_active_credit_lb.setText(Float.toString(customer.getActive_credit())));
        orderDataPane.getController().setInitializeAction(()->orderDataPane.getCustomer().addActiveCreditChangeAction(()->cus_active_credit_lb.setText(Float.toString(orderDataPane.getCustomer().getActive_credit()))));


    }

    @FXML
    void open_cus_arch_credit(ActionEvent event) {

    }

    @FXML
    void remove_order(ActionEvent event) {

    }

}
