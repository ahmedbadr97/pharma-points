package scenes.controller;

import database.entities.Customer;
import database.entities.Order;
import exceptions.DataNotFound;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import scenes.abstracts.CustomerDataPane;
import scenes.main.Alerts;
import scenes.main.OrderData;
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
    private TableColumn<OrdersTableRows, String> order_exp_col;
    @FXML
    private TableColumn<Order, DateTime> order_time_col;

    private scenes.main.CustomerData main_screen;
    private scenes.abstracts.CustomerDataPane customerDataPane;
    private ObservableList<OrdersTableRows> orders_tv_list;
    private OrdersTableRows selectedRow;

    public void ini(scenes.main.CustomerData main_screen)
    {
        this.main_screen=main_screen;
        selectedRow=null;
        orders_tv_list = FXCollections.observableArrayList();
        // customerData Pane
        customerDataPane=new CustomerDataPane(true);
        customer_data_sp.getChildren().add(customerDataPane.getParent());
        ordersTableIni();
        loadData();


    }
    public void loadData()
    {
        orders_tv_list.clear();
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
    public void reloadCustomer(){
        try {
            main_screen.setCustomer(Customer.getCustomer(Integer.toString(main_screen.getCustomer().getId()), Customer.QueryFilter.ID));
        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
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
        order_exp_col.setCellValueFactory((new PropertyValueFactory<>("expiry_date")));

        orders_tv.setRowFactory(tv->
        {
            TableRow<OrdersTableRows> roww=new  TableRow<OrdersTableRows>();
            roww.setOnMouseClicked(event ->
            {
                if(roww.isEmpty()) return;
                if (event.getClickCount()==2 && roww.getItem().orderDataScreen==null) {
                    selectedRow=roww.getItem();

                    selectedRow.orderDataScreen=new OrderData(roww.getItem().getOrder());
                    selectedRow.orderDataScreen.addOnCloseAction(()->{
                        reloadCustomer();
                        loadData();
                        selectedRow.orderDataScreen=null;
                        //TODO clear listeners
//                        roww.getItem().order.clearListeners();
//                        main_screen.getCustomer().clearListeners();
                    });
                    selectedRow.orderDataScreen.showStage();
                }
            });
            return roww ;
        } );

        orders_tv.setItems(orders_tv_list);
    }
    public class OrdersTableRows{
        Label total_money_lb,total_credit_lb;
        Order order;
        OrderData orderDataScreen;


        public OrdersTableRows(Order order) {
            this.order=order;
            orderDataScreen=null;
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
            return order.getTotalSystemCreditIn();
        }

        public float getTotal_credit_out() {
            return order.getTotalOrderCreditOut();
        }

        public DateTime getOrder_time() {
            return order.getOrder_time();
        }

        public String getExpiry_date() {
            return order.getExpiry_date().get_Date();
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
