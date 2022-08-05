package scenes.controller;

import database.DBOperations;
import database.DBStatement;
import database.entities.Customer;
import database.entities.Order;
import exceptions.InvalidTransaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scenes.abstracts.OrderDataPane;
import scenes.main.Alerts;

import java.sql.SQLException;

public class OrderData {

    @FXML
    private Button deleteOrder_btn;

    @FXML
    private Button cancel_edit_btn;

    @FXML
    private Button edit_order_data_btn;
    @FXML
    private Button save_data_btn;

    @FXML
    private Label cus_active_credit_lb;
    @FXML
    private VBox main_Vbox;
    boolean mutable;

    private OrderDataPane orderDataPane;
    private DBOperations dbOperations;

    private scenes.main.OrderData main_screen;
    public void ini(scenes.main.OrderData main_screen){
        this.main_screen=main_screen;
        cus_active_credit_lb.setText(Float.toString(main_screen.getOrder().getCustomer().getActive_credit()));
        dbOperations=new DBOperations();
        this.orderDataPane=new OrderDataPane(dbOperations,main_screen.getOrder());
        main_Vbox.getChildren().add(  orderDataPane.getParent());
        Customer customer=    main_screen.getOrder().getCustomer();
        customer.addActiveCreditChangeAction(()->cus_active_credit_lb.setText(Float.toString(customer.getActive_credit())));
        orderDataPane.getController().setInitializeAction(()->orderDataPane.getCustomer().addActiveCreditChangeAction(()->cus_active_credit_lb.setText(Float.toString(orderDataPane.getCustomer().getActive_credit()))));
        setMutable(false);
    }

    @FXML
    void delete_order(ActionEvent event)
    {
        //TODO delete the order in more efficent way

        try {

            orderDataPane.getController().deleteOrder();
            new Alerts("تم حذف الفاتوره بنجاح", Alert.AlertType.INFORMATION);
            dbOperations.execute();
            main_screen.closeStage();

        }
        catch (SQLException s)
        {
            new Alerts(s);
            orderDataPane.getController().reloadFromDatabase();
            dbOperations.clear();
        }
        catch (InvalidTransaction i)
        {
            new Alerts(i.getMessage(), Alert.AlertType.ERROR);
            orderDataPane.getController().reloadFromDatabase();

        }
        dbOperations.clear();

    }
    @FXML
    void cancel_edit(ActionEvent event) {
        dbOperations.clear();
        orderDataPane.getController().reloadFromDatabase();
        orderDataPane.getController().initializeOrder();
        setMutable(false);



    }

    @FXML
    void edit_order(ActionEvent event) {
        setMutable(true);
        dbOperations.add(main_screen.getOrder().getCustomer(), DBStatement.Type.UPDATE);

    }

    @FXML
    void save_order_Data(ActionEvent event) {
        try {
            dbOperations.execute();
            setMutable(false);

        } catch (SQLException e) {
            new Alerts(e);
        }

    }
    public void setMutable(boolean mutable) {
        this.mutable = mutable;
        orderDataPane.getController().setMutable(mutable);
        save_data_btn.setVisible(mutable);
        cancel_edit_btn.setVisible(mutable);
        deleteOrder_btn.setVisible(!mutable);

        edit_order_data_btn.setVisible(!mutable);
        if(mutable)
        {
            edit_order_data_btn.setMaxWidth(0);
            edit_order_data_btn.setPrefWidth(0);
            deleteOrder_btn.setPrefWidth(0);
            deleteOrder_btn.setMaxWidth(0);
        }
        else
        {
            edit_order_data_btn.setMaxWidth(120);
            edit_order_data_btn.setPrefWidth(120);

            deleteOrder_btn.setPrefWidth(60);
            deleteOrder_btn.setMaxWidth(60);

        }
    }
}
