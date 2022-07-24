package scenes.controller;

import database.entities.Customer;
import exceptions.DataNotFound;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import scenes.main.Alerts;
import scenes.main.NewCustomer;
import utils.DateTime;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerSearch {

    @FXML
    private ComboBox<SearchWith> cus_search_cb;


    @FXML
    private TextField cus_search_tf;

    @FXML
    private Button cus_search_btn;

    @FXML
    private TextField credit_end_tf;
    @FXML
    private TextField credit_start_tf;
    @FXML
    private CheckBox filter_by_expiry_cb;
    @FXML
    private DatePicker exp_from_db;

    @FXML
    private DatePicker exp_to_db;

    @FXML
    private Button filter_btn;

    @FXML
    private TableView<Customer> cus_tv;

    @FXML
    private TableColumn<Customer, String> cus_name_col;

    @FXML
    private TableColumn<Customer, String> cus_phone_col;

    @FXML
    private TableColumn<Customer, Integer> cus_credit_col;

    @FXML
    private TableColumn<Customer, DateTime> cus_exp_col;

    @FXML
    private Label tv_cnt_lb;

    @FXML
    private ComboBox<EditCustomer> edit_customers_cb;

    @FXML
    private GridPane cus_edit_admin_panel;






    private scenes.main.CustomerSearch main_screen;
    private ObservableList<Customer> customers_tv_list;

    public void init(scenes.main.CustomerSearch main_screen){
        this.main_screen=main_screen;

        // --------------initialize combo boxes----------------//
        // search with combo box
        List<SearchWith> searchWithList=Arrays.asList(SearchWith.values());
        cus_search_cb.setItems(FXCollections.observableList(searchWithList));
        cus_search_cb.setValue(SearchWith.CUSTOMER_BARCODE);
        cus_search_cb.setOnAction(event -> {
            cus_search_tf.clear();
        });

        // Edit Customer combo box
        List<EditCustomer> editCustomers=Arrays.asList(EditCustomer.values());
        edit_customers_cb.setItems(FXCollections.observableList(editCustomers));
        edit_customers_cb.setValue(EditCustomer.EXPIRY_DATE);



        //--------------- initialize DatePickers----------- //
        exp_from_db.setValue(LocalDate.now());
        exp_to_db.setValue(LocalDate.now());

        Platform.runLater(() -> {
            cus_search_tf.requestFocus();
        });
        cus_table_init();




    }

    private void cus_table_init(){
        customers_tv_list=FXCollections.observableArrayList();
        cus_name_col.setCellValueFactory((new PropertyValueFactory<>("name")));
        cus_phone_col.setCellValueFactory((new PropertyValueFactory<>("phone")));
        cus_credit_col.setCellValueFactory((new PropertyValueFactory<>("active_credit")));
        cus_exp_col.setCellValueFactory((new PropertyValueFactory<>("expiry_date")));

        //TODO add row on click action
        cus_tv.setItems(customers_tv_list);

    }

    @FXML
    void edit_cus_btn_action(ActionEvent event) {

    }

    @FXML
    void filter_by_expiry_cb_action(ActionEvent event) {
        if (filter_by_expiry_cb.isSelected())
        {
            exp_to_db.setDisable(false);
            exp_from_db.setDisable(false);
        }
        else {
            exp_to_db.setDisable(true);
            exp_from_db.setDisable(true);
        }
    }

    @FXML
    void filter_btn_action(ActionEvent event) {
        customers_tv_list.clear();
        ArrayList<Customer> filterCustomers=null;
        int credit_start=-1,credit_end=-1;

        try {
            if (credit_start_tf.getText()!=null&&!credit_start_tf.getText().isEmpty())
            {
                credit_start=Integer.parseInt(credit_start_tf.getText());
                if (credit_start<0)
                    throw new NumberFormatException();
            }
            if (credit_end_tf.getText()!=null&&!credit_end_tf.getText().isEmpty())
            {
                credit_end=Integer.parseInt(credit_end_tf.getText());
                if (credit_end<0)
                    throw new NumberFormatException();
            }


        }
        catch (NumberFormatException nf)
        {
            new Alerts("النقاط يجب ان تكون ارقام فقط", Alert.AlertType.ERROR);
            return;
        }
        DateTime fromDateTime=null,toDateTime=null;

        if (filter_by_expiry_cb.isSelected())
        {
            fromDateTime=DateTime.getDateTimeFromDatePicker(exp_from_db, LocalTime.MIN);
            toDateTime=DateTime.getDateTimeFromDatePicker(exp_to_db, LocalTime.MAX);
        }
        try {
            filterCustomers=Customer.getCustomersBy_CreditAndExpiry(fromDateTime,toDateTime,credit_start,credit_end);
        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
        }
        if (filterCustomers!= null)
        {
            update_cusTv_data(filterCustomers);
        }

    }

    @FXML
    void save_cus_edit_action(ActionEvent event) {

    }


    @FXML
    void search_btn_action(ActionEvent event) {
        String cus_search_value=cus_search_tf.getText();
        if (cus_search_value ==null || cus_search_value.isEmpty()){
            new Alerts("برجاء كتابه بيانات البحث اولا", Alert.AlertType.ERROR);
            Platform.runLater(() -> {
                cus_search_tf.requestFocus();
            });
            return;
        }
        ArrayList<Customer> searchCustomers=null;
        Customer SearchCustomer=null;
        try {

            switch (cus_search_cb.getValue()) {
                case CUSTOMER_NAME:
                    searchCustomers = Customer.getCustomersByName(cus_search_tf.getText());
                    break;
                case CUSTOMER_BARCODE:
                    searchCustomers=new ArrayList<Customer>();
                    SearchCustomer=Customer.getCustomer(cus_search_value, Customer.QueryFilter.BARCODE);
                    //TODO add search with phone and with barcode open customer screen
                    System.out.println(SearchCustomer);
                    break;
                case CUSTOMER_PHONE:
                    searchCustomers=new ArrayList<Customer>();
                    SearchCustomer=Customer.getCustomer(cus_search_value, Customer.QueryFilter.PHONE);
                    //TODO add search with phone and with barcode open customer screen
                    System.out.println(SearchCustomer);
                    break;

            }
        }
         catch (SQLException e) {
            new Alerts(e);
            return;
        } catch (DataNotFound e) {
            new Alerts(e);
            return;
        }
        if (searchCustomers!= null)
        {
            update_cusTv_data(searchCustomers);
        }


    }
    @FXML
    void new_customer_btn_action(ActionEvent event) {
        new NewCustomer().showStage();
        //TODO after save settings go to customer data

    }
    private void update_cusTv_data(ArrayList<Customer> customerArrayList){
        customers_tv_list.addAll(customerArrayList);
        tv_cnt_lb.setText(Integer.toString(customerArrayList.size()));
    }

    // -------------------------------------choice boxes enums -------------------------------------//

    enum SearchWith {
        CUSTOMER_BARCODE("باركود"),CUSTOMER_PHONE("رقم التليفون"),CUSTOMER_NAME("الاسم");
        private final String label;

        SearchWith(String label) {
            this.label = label;
        }
        @Override
        public String toString() {
            return label;
        }
    }
    enum PointsAre{
        GREATER_THAN("اكبر من"),SMALLER_THAN("اصغر من");
        private final String label;

        PointsAre(String label) {
            this.label = label;
        }
        @Override
        public String toString() {
            return label;
        }
    }

    enum EditCustomer{
        EXPIRY_DATE("تاريخ الانتهاء"),TRANSFER_POINTS("تحويل نقاط");
        private final String label;

        EditCustomer(String label) {
            this.label = label;
        }
        @Override
        public String toString() {
            return label;
        }
    }
}
