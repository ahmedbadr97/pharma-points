package scenes.controller;

import database.entities.Customer;
import database.entities.Order;
import exceptions.DataNotFound;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import scenes.abstracts.CustomerDataPane;
import scenes.main.Alerts;
import utils.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerData {

    @FXML
    private StackPane customer_data_sp;
    @FXML
    private TableView<OrdersTableRows> orders_tv;

    @FXML
    private TableColumn<OrdersTableRows, Integer> order_id_col;

    @FXML
    private TableColumn<OrdersTableRows, Float> total_money_in_col;

    @FXML
    private TableColumn<OrdersTableRows, Float> total_money_out_col;

    @FXML
    private TableColumn<OrdersTableRows, Label> total_money_col;

    @FXML
    private TableColumn<OrdersTableRows, Float> total_credit_in_col;

    @FXML
    private TableColumn<OrdersTableRows, Float> total_credit_out_col;

    @FXML
    private TableColumn<OrdersTableRows, Label> total_Credit_Col;

    @FXML
    private TableColumn<Order, Float> order_time_col;
    private scenes.main.CustomerData main_screen;
    private scenes.abstracts.CustomerDataPane customerDataPane;
    private ObservableList<OrdersTableRows> orders_tv_list;

    public void ini(scenes.main.CustomerData main_screen)
    {
        this.main_screen=main_screen;
        orders_tv_list = FXCollections.observableArrayList();
        // customerData Pane
        customerDataPane=new CustomerDataPane(true);
        customer_data_sp.getChildren().add(customerDataPane.getParent());
        ordersTableIni();
        loadData();


    }
    public void loadData()
    {
        customerDataPane.setCustomer(main_screen.getCustomer());
        customerDataPane.getController().loadCustomerData();
        try {
            ArrayList<Order> orders=Order.getCustomerOrders(main_screen.getCustomer());
            for (Order order:orders)
                orders_tv_list.add(new OrdersTableRows(order));
        }
        catch (SQLException s)
        {
            new Alerts(s);
        }
        catch (DataNotFound d)
        {
            //Do nothing
        }
    }
    public void ordersTableIni()
    {
        order_id_col.setCellValueFactory((new PropertyValueFactory<>("order_id")));
        total_money_in_col.setCellValueFactory((new PropertyValueFactory<>("total_money_in")));
        total_money_out_col.setCellValueFactory((new PropertyValueFactory<>("total_money_out")));
        total_money_col.setCellValueFactory((new PropertyValueFactory<>("total_money_lb")));

        total_credit_in_col.setCellValueFactory((new PropertyValueFactory<>("total_credit_in")));
        total_credit_out_col.setCellValueFactory((new PropertyValueFactory<>("total_credit_out")));
        total_Credit_Col.setCellValueFactory((new PropertyValueFactory<>("total_credit_lb")));
        order_time_col.setCellValueFactory((new PropertyValueFactory<>("order_time")));

        orders_tv.setItems(orders_tv_list);
    }
    public class OrdersTableRows{
        Label total_money_lb,total_credit_lb;
        Order order;

        public OrdersTableRows(Order order) {
            this.order=order;
            float total_money=order.getTotal_money();
            this.total_money_lb=new Label();
            if(total_money<0)
            {
                total_money*=-1;
                total_money_lb.getStyleClass().add("red_col_field");
            }
            else if(total_money>0)
                total_money_lb.getStyleClass().add("green_col_field");
            total_money_lb.setText(Float.toString(total_money));


            float total_credit=order.getTotal_credit();
            this.total_credit_lb=new Label();
            if(total_credit<0)
            {
                total_credit*=-1;
                total_credit_lb.getStyleClass().add("red_col_field");
            }
            else if(total_credit>0)
                total_credit_lb.getStyleClass().add("green_col_field");
            total_credit_lb.setText(Float.toString(total_credit));

        }


        public int getOrder_id() {
            return order.getOrder_id();
        }

        public float getTotal_money_in() {
            return order.getTotal_money_in();
        }

        public float getTotal_money_out() {
            return order.getTotal_money_out();
        }

        public float getTotal_credit_in() {
            return order.getTotal_credit_in();
        }

        public float getTotal_credit_out() {
            return order.getTotal_credit_out();
        }

        public DateTime getOrder_time() {
            return order.getOrder_time();
        }

        public Label getTotal_money_lb() {
            return total_money_lb;
        }

        public Label getTotal_credit_lb() {
            return total_credit_lb;
        }

        public Order getOrder() {
            return order;
        }
    }

    @FXML
    void close(ActionEvent event) {
        main_screen.closeStage();

    }

}
