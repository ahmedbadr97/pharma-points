package scenes.controller;

import database.DBOperations;
import database.DBStatement;
import database.entities.CreditArchiveTransaction;
import database.entities.Customer;
import exceptions.DataEntryError;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import main.Main;
import scenes.abstracts.GetCustomerBar;
import scenes.main.Alerts;
import scenes.main.NewCustomer;
import utils.Validator;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerSearch {

    @FXML
    private TextField credit_end_tf;
    @FXML
    private TextField credit_start_tf;
    @FXML
    private TextField creditTransAmount_tf;

    @FXML
    private TableView<Customer> cus_tv;

    @FXML
    private TableColumn<Customer, String> cus_name_col;

    @FXML
    private TableColumn<Customer, String> cus_phone_col;

    @FXML
    private TableColumn<Customer, Integer> cus_credit_col;


    @FXML
    private Label tv_cnt_lb;

    @FXML
    private ComboBox<CreditArchiveTransaction.TransactionType> edit_customers_cb;

    @FXML
    private GridPane cus_edit_admin_panal;
    @FXML
    private StackPane cus_search_stack;


    private scenes.main.CustomerSearch main_screen;
    private ObservableList<Customer> customers_tv_list;
    private scenes.abstracts.GetCustomerBar getCustomerBar;
    private Customer selectedCustomer;
    private DBOperations dbOperations;

    public void init(scenes.main.CustomerSearch main_screen) {
        this.main_screen = main_screen;
        dbOperations=new DBOperations();

        // -------------- Search HBOX initialize---------------//

        getCustomerBar = new scenes.abstracts.GetCustomerBar();
        cus_search_stack.getChildren().add(getCustomerBar.getController().getSearchHbox());
        getCustomerBar.setSearchWithNameAction(() -> {
            update_cusTv_data(getCustomerBar.getController().getSearchCustomers());
        });
        getCustomerBar.setSearchWithIdAction(()->{
            new scenes.main.CustomerData(getCustomerBar.getController().getSearchCustomer()).showStage();
        });


        if(Main.appSettings.getLogged_in_user().getAdmin()==0)
            cus_edit_admin_panal.setVisible(false);
        // --------------initialize combo boxes----------------//

        edit_customers_cb.getItems().addAll(CreditArchiveTransaction.TransactionType.values());




        cus_table_init();


    }

    private void cus_table_init() {
        customers_tv_list = FXCollections.observableArrayList();
        cus_name_col.setCellValueFactory((new PropertyValueFactory<>("name")));
        cus_phone_col.setCellValueFactory((new PropertyValueFactory<>("phone")));
        cus_credit_col.setCellValueFactory((new PropertyValueFactory<>("active_credit")));

        cus_tv.setRowFactory(tv->
        {
            TableRow<Customer> roww=new  TableRow<Customer>();
            roww.setOnMouseClicked(event ->
            {
                if(roww.isEmpty()) return;
                if (event.getClickCount()==2) {
                    selectedCustomer=roww.getItem();
                    if(main_screen.getOnCustomerSelection()!=null)
                        main_screen.getOnCustomerSelection().customerSelectionAction();
                }
            });
            return roww ;
        } );
        cus_tv.setItems(customers_tv_list);

    }



    @FXML
    void filter_btn_action(ActionEvent event) {
        customers_tv_list.clear();
        ArrayList<Customer> filterCustomers = null;
        int credit_start = -1, credit_end = -1;

        try {
            if (credit_start_tf.getText() != null && !credit_start_tf.getText().isEmpty()) {
                credit_start = Integer.parseInt(credit_start_tf.getText());
                if (credit_start < 0)
                    throw new NumberFormatException();
            }
            if (credit_end_tf.getText() != null && !credit_end_tf.getText().isEmpty()) {
                credit_end = Integer.parseInt(credit_end_tf.getText());
                if (credit_end < 0)
                    throw new NumberFormatException();
            }
            if (credit_end!=-1&&(credit_end < credit_start)) {
                new Alerts("نهايه فتره النقاط اكبر من البدايه", Alert.AlertType.ERROR);
                return;
            }


        } catch (NumberFormatException nf) {
            new Alerts("النقاط يجب ان تكون ارقام فقط", Alert.AlertType.ERROR);
            return;
        }
        try {
            filterCustomers = Customer.getCustomersBy_Credit(credit_start, credit_end);
        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
        }
        if (filterCustomers != null) {
            update_cusTv_data(filterCustomers);
        }

    }
    @FXML
    void edit_cus_btn_action(ActionEvent event) {
        float amount=0;
        try {
             amount = Validator.getFloat(creditTransAmount_tf, "قيمه النقاط", 0, (int) 1e5);

        }   catch (DataEntryError d)
        {
            new Alerts(d);
            return;
        }

        for (Customer customer:customers_tv_list) {
            try {

                CreditArchiveTransaction.TransactionType transactionType = edit_customers_cb.getValue();
                switch (transactionType) {
                    case activeToArchive:
                        customer.fromActiveToArchive(amount);
                        break;
                    case archiveToActive:
                        customer.fromArchiveToActive(amount);
                        break;
                }
                CreditArchiveTransaction archiveTransaction = new CreditArchiveTransaction(transactionType, 0, amount, customer);
                dbOperations.add(archiveTransaction, DBStatement.Type.ADD);
                dbOperations.add(customer, DBStatement.Type.UPDATE);
            }
            catch (InvalidTransaction inv)
            {
                new Alerts(inv.getMessage()+" "+customer.getName(), Alert.AlertType.ERROR);
            }

        }
        cus_tv.refresh();

    }

    @FXML
    void save_cus_edit_action(ActionEvent event) {
        try {
            dbOperations.execute();
        } catch (SQLException e) {
            new Alerts(e);
        }
        new Alerts("تم حفظ البيانات بنجاح", Alert.AlertType.INFORMATION);


    }


    @FXML
    void new_customer_btn_action(ActionEvent event) {
       NewCustomer newCustomer =new NewCustomer();
       newCustomer.addOnCloseAction(() -> {
           if(newCustomer.getNewCustomer()!=null)
           {
               new scenes.main.CustomerData(newCustomer.getNewCustomer()).showStage();
           }
       });
       newCustomer.setOnTop();
       newCustomer.showStage();

    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public GetCustomerBar getGetCustomerBar() {
        return getCustomerBar;
    }

    public void update_cusTv_data(ArrayList<Customer> customerArrayList) {
        customers_tv_list.clear();
        customers_tv_list.addAll(customerArrayList);
        tv_cnt_lb.setText(Integer.toString(customerArrayList.size()));
    }




}

