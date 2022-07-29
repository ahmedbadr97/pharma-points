package scenes.controller;

import database.entities.Order;
import exceptions.DataNotFound;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import scenes.abstracts.CustomerDataPane;
import scenes.main.Alerts;

import java.sql.SQLException;

public class CustomerData {

    @FXML
    private StackPane customer_data_sp;
    @FXML
    private TableView<Order> orders_tv;

    @FXML
    private TableColumn<Order, Integer> order_id_col;

    @FXML
    private TableColumn<Order, Float> total_money_in_col;

    @FXML
    private TableColumn<Order, Float> total_money_out_col;

    @FXML
    private TableColumn<Order, Float> total_money_col;

    @FXML
    private TableColumn<Order, Float> total_credit_in_col;

    @FXML
    private TableColumn<Order, Float> total_credit_out_col;

    @FXML
    private TableColumn<Order, Float> total_Credit_Col;

    @FXML
    private TableColumn<Order, Float> order_time_col;
    private scenes.main.CustomerData main_screen;
    private scenes.abstracts.CustomerDataPane customerDataPane;
    private ObservableList<Order> orders_tv_list;


    public void ini(scenes.main.CustomerData main_screen)
    {
        this.main_screen=main_screen;
        orders_tv_list = FXCollections.observableArrayList();
        // customerData Pane
        customerDataPane=new CustomerDataPane(true);
        customerDataPane.setCustomer(main_screen.getCustomer());
        customerDataPane.getController().loadCustomerData();
        customer_data_sp.getChildren().add(customerDataPane.getParent());
        try {
            orders_tv_list.addAll(Order.getCustomerOrders(main_screen.getCustomer()));
        }
        catch (SQLException s)
        {
            new Alerts(s);
        }
        catch (DataNotFound d)
        {
            //Do nothing
        }
        ordersTableIni();


    }
    public void ordersTableIni()
    {
        order_id_col.setCellValueFactory((new PropertyValueFactory<>("order_id")));
        total_money_in_col.setCellValueFactory((new PropertyValueFactory<>("total_money_in")));
        total_money_out_col.setCellValueFactory((new PropertyValueFactory<>("total_money_out")));
        total_money_col.setCellValueFactory((new PropertyValueFactory<>("total_money")));

        total_credit_in_col.setCellValueFactory((new PropertyValueFactory<>("total_credit_in")));
        total_credit_out_col.setCellValueFactory((new PropertyValueFactory<>("total_credit_out")));
        total_Credit_Col.setCellValueFactory((new PropertyValueFactory<>("total_credit")));
        order_time_col.setCellValueFactory((new PropertyValueFactory<>("order_time")));

        orders_tv.setItems(orders_tv_list);
    }

    @FXML
    void close(ActionEvent event) {
        main_screen.closeStage();

    }

}
