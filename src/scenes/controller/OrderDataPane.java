package scenes.controller;

import database.DBStatement;
import database.entities.Order;
import database.entities.OrderTransaction;
import exceptions.InvalidTransaction;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import scenes.abstracts.OrderDataPane.OrderSettings;
import database.entities.OrderTransaction.TransactionType;
import scenes.images.ImageLoader;
import scenes.main.Alerts;
import utils.DateTime;

public class OrderDataPane {

    @FXML
    private ComboBox<OrderTransaction.TransactionType> trans_type_cb;

    @FXML
    private TextField trans_amount_tf;

    @FXML
    private TableView<OrderDataTableRow> order_trans_tv;

    @FXML
    private TableColumn<OrderDataTableRow, Integer> trans_id_col;

    @FXML
    private TableColumn<OrderDataTableRow, TransactionType> trans_type_col;

    @FXML
    private TableColumn<OrderDataTableRow, Float> money_amount_col;

    @FXML
    private TableColumn<OrderDataTableRow, DateTime> trans_time_col;

    @FXML
    private TableColumn<OrderDataTableRow, Button> remove_btn_col;

    @FXML
    private Label money_in_lb;

    @FXML
    private Label money_out_lb;

    @FXML
    private Label total_money_lb;

    @FXML
    private Label total_credit_in_lb;

    @FXML
    private Label total_credit_out_lb;

    @FXML
    private Label total_credit_lb;

    @FXML
    private TextArea order_notes_ta;

    @FXML
    private HBox edit_data_hbox;

    @FXML
    private Button cancel_edit_btn;

    @FXML
    private Button edit_cus_data_btn;

    @FXML
    private Button save_data_btn;
    private scenes.abstracts.OrderDataPane main_screen;
    private ObservableList<OrderDataTableRow> transactions_tv_list;
    float cusBalanceCredit;

    public void ini(scenes.abstracts.OrderDataPane main_screen) {
        this.main_screen = main_screen;
        OrderSettings orderSettings = main_screen.getOrderSettings();
        edit_data_hbox.setVisible(orderSettings == OrderSettings.viewAndEdit);
        if (orderSettings == OrderSettings.newOrder)
            trans_type_cb.getItems().addAll(TransactionType.money_in, TransactionType.credit_out);
        else {
            trans_type_cb.getItems().addAll(TransactionType.money_in, TransactionType.credit_out, TransactionType.money_out, TransactionType.credit_in);
            edit_data_hbox.setVisible(true);

        }
        trans_type_cb.getSelectionModel().select(0);


        //----------------------------------------------------//
        trans_tv_ini();


    }

    public void clearData() {
        order_notes_ta.clear();
        money_in_lb.setText(Integer.toString(0));
        money_out_lb.setText(Integer.toString(0));
        total_money_lb.setText(Integer.toString(0));

        total_credit_in_lb.setText(Integer.toString(0));
        total_credit_out_lb.setText(Integer.toString(0));
        total_credit_lb.setText(Integer.toString(0));
        trans_amount_tf.clear();
        transactions_tv_list.clear();


    }

    public void initializeOrder() {

        cusBalanceCredit = main_screen.getOrder().getCustomer().getActive_credit();

        // -------initialize on add transaction to order calculating total credit and money-----------
        main_screen.getOrder().addOnAddAction(this::updateOrderSummaryFields);
        Platform.runLater(() -> {
            trans_amount_tf.requestFocus();
        });
        if (main_screen.getOrderSettings() == OrderSettings.viewAndEdit) {
            for (OrderTransaction transaction : main_screen.getOrder().getOrderTransactions()) {
                transactions_tv_list.add(new OrderDataTableRow(transaction));

            }
            updateOrderSummaryFields();

        }
    }

    public void updateOrderSummaryFields() {
        String greenTextStyle = "green_summary_field";
        String redTextStyle = "red_summary_field";
        Order current_order = main_screen.getOrder();
        total_credit_in_lb.setText(Float.toString(current_order.getTotal_credit_in()));
        total_credit_out_lb.setText(Float.toString(current_order.getTotal_credit_out()));
        float total_credit = (current_order.getTotal_credit_in() - current_order.getTotal_credit_out());
        if (total_credit < 0) {
            total_credit_lb.getStyleClass().clear();
            total_credit_lb.getStyleClass().add(redTextStyle);
            total_credit *= -1;
        } else {
            total_credit_lb.getStyleClass().clear();
            total_credit_lb.getStyleClass().add(greenTextStyle);
        }
        total_credit_lb.setText(Float.toString(total_credit));


        money_in_lb.setText(Float.toString(current_order.getTotal_money_in()));
        money_out_lb.setText(Float.toString(current_order.getTotal_money_out()));
        float total_money = (current_order.getTotal_money_in() - current_order.getTotal_money_out());

        if (total_money < 0) {
            total_money_lb.getStyleClass().remove(greenTextStyle);
            total_money_lb.getStyleClass().add(redTextStyle);
            total_money *= -1.0;
        } else {
            total_money_lb.getStyleClass().remove(redTextStyle);
            total_money_lb.getStyleClass().add(greenTextStyle);
        }
        total_money_lb.setText(Float.toString(total_money));
    }

    public void trans_tv_ini() {
        transactions_tv_list = FXCollections.observableArrayList();
        trans_id_col.setCellValueFactory((new PropertyValueFactory<>("trans_id")));
        trans_type_col.setCellValueFactory((new PropertyValueFactory<>("trans_type")));
        money_amount_col.setCellValueFactory((new PropertyValueFactory<>("money_amount")));
        trans_time_col.setCellValueFactory((new PropertyValueFactory<>("trans_time")));
        remove_btn_col.setCellValueFactory((new PropertyValueFactory<>("remove_btn")));
        order_trans_tv.setItems(transactions_tv_list);
    }

    @FXML
    void add_transaction(ActionEvent event) {
        // validation
        float amount = -1;
        try {
            amount = Float.parseFloat(trans_amount_tf.getText());
        } catch (NumberFormatException n) {
            new Alerts("يجب كتابه القيمه بشكل صحيح xxx.xxx", Alert.AlertType.ERROR);
            Platform.runLater(() -> {
                trans_amount_tf.requestFocus();
            });
            return;
        }
        OrderTransaction orderTransaction = new OrderTransaction(main_screen.getOrder(), trans_type_cb.getValue(), amount);
        try {


            switch (orderTransaction.getTrans_type()) {

                case money_in:
                    addMoneyInTransaction(orderTransaction);
                    break;
                case credit_out:
                    addCreditOutTransaction(orderTransaction);
                    break;
                case money_out:
                    addMoneyOutTransaction(orderTransaction);
                    break;
                case credit_in:
                    addCreditInTransaction(orderTransaction);

            }
        } catch (InvalidTransaction e) {
            new Alerts(e.getMessage(), Alert.AlertType.ERROR) {
                @Override
                public void alertOnClose() {
                    trans_amount_tf.clear();
                }
            };
        }
        trans_amount_tf.clear();

    }

    void addMoneyInTransaction(OrderTransaction transaction) throws InvalidTransaction {
        // no validation for adding credit to customer, credit added with current sale money to credit
        main_screen.getOrder().addTransaction(transaction);
        transactions_tv_list.add(new OrderDataTableRow(transaction));
        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);
    }

    void addCreditOutTransaction(OrderTransaction transaction) throws InvalidTransaction {
        // validation--> check there is enough balance to give to the customer
        if (transaction.getMoney_amount() > main_screen.getCustomer().getActive_credit()) {
            new Alerts("لا يوجد رصيد كافى", Alert.AlertType.ERROR) {
                @Override
                public void alertOnClose() {
                    Platform.runLater(() -> {
                        trans_amount_tf.setText(Float.toString(main_screen.getCustomer().getActive_credit()));
                        trans_amount_tf.requestFocus();
                    });
                }
            };
            return;
        }
        main_screen.getOrder().addTransaction(transaction);
        transactions_tv_list.add(new OrderDataTableRow(transaction));
        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);

    }

    void addMoneyOutTransaction(OrderTransaction transaction) throws InvalidTransaction {
        main_screen.getOrder().addTransaction(transaction);
        transactions_tv_list.add(new OrderDataTableRow(transaction));
        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);

    }
    void addCreditInTransaction(OrderTransaction transaction)throws InvalidTransaction{
        main_screen.getOrder().addTransaction(transaction);
        transactions_tv_list.add(new OrderDataTableRow(transaction));
        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);
    }

    @FXML
    void cancel_edit(ActionEvent event) {

    }

    @FXML
    void edit_order(ActionEvent event) {

    }

    @FXML
    void save_order_Data(ActionEvent event) {

    }

    public String getOrderNotes() {
        if (order_notes_ta.getText() != null || !order_notes_ta.getText().isEmpty())
            return order_notes_ta.getText();
        else
            return null;
    }

    public class OrderDataTableRow {
        OrderTransaction transaction;
        Button remove_btn;

        public OrderDataTableRow(OrderTransaction transaction) {
            this.transaction = transaction;
            this.remove_btn = new Button();
            remove_btn.setOnAction((actionEvent) -> {
                removeBtnAction();
            });
            ImageLoader.icoButton(remove_btn, "deleteButton.png", 10);
        }

        public void removeBtnAction() {
            OrderSettings orderSettings = main_screen.getOrderSettings();
            if (orderSettings == OrderSettings.newOrder) {
                try {
                    main_screen.getOrder().removeTransaction(transaction);
                    transactions_tv_list.remove(this);
                    main_screen.getDbOperations().remove(transaction, DBStatement.Type.DELETE);
                } catch (InvalidTransaction e) {
                    new Alerts(e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }

        public int getTrans_id() {
            return transaction.getTrans_id();
        }

        public TransactionType getTrans_type() {
            return transaction.getTrans_type();
        }

        public float getMoney_amount() {
            return transaction.getMoney_amount();
        }

        public DateTime getTrans_time() {
            return transaction.getTrans_time();
        }

        public OrderTransaction getTransaction() {
            return transaction;
        }

        public Button getRemove_btn() {
            return remove_btn;
        }
    }

}
