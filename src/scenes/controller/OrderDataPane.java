package scenes.controller;

import database.DBStatement;
import database.entities.CreditArchiveTransaction;
import database.entities.Customer;
import database.entities.Order;
import database.entities.OrderTransaction;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import main.Main;
import scenes.abstracts.OrderDataPane.OrderSettings;
import database.entities.OrderTransaction.TransactionType;
import scenes.images.ImageLoader;
import scenes.main.Alerts;
import utils.DateTime;

import java.sql.SQLException;

public class OrderDataPane {

    @FXML
    private Label order_id_lb,order_time_lb,order_expiry_date_lb;
    @FXML
    private Button delete_order_btn;
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
    private TableColumn<OrderDataTableRow, TextField> money_amount_col;

    @FXML
    private TableColumn<OrderDataTableRow, DateTime> trans_time_col;

    @FXML
    private TableColumn<OrderDataTableRow, HBox> edit_save_btn_col;

    @FXML
    private TableColumn<OrderDataTableRow, Button> remove_btn_col;

    @FXML
    private Label money_in_lb;

    @FXML
    private Label money_out_lb;

    @FXML
    private Label total_money_lb;
    @FXML
    private Label amount_needed_lb;

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
    private Button edit_order_data_btn;

    @FXML
    private Button save_data_btn;
    private scenes.abstracts.OrderDataPane main_screen;
    private ObservableList<OrderDataTableRow> transactions_tv_list;
    private float amount_needed_before_edit = 0;
    float cusBalanceCredit;
    private boolean mutable;
    interface InitializeAction{
        public void action();
    }
    private InitializeAction initializeAction;

    public void ini(scenes.abstracts.OrderDataPane main_screen) {
        this.main_screen = main_screen;
        initializeAction=null;
        OrderSettings orderSettings = main_screen.getOrderSettings();
        ImageLoader.icoButton(delete_order_btn,"deleteButton.png",10);

        edit_data_hbox.setVisible(orderSettings == OrderSettings.viewAndEdit);
        if (orderSettings == OrderSettings.newOrder)
        {
            mutable=true;
            trans_type_cb.getItems().addAll(TransactionType.money_in, TransactionType.credit_out);
            delete_order_btn.setDisable(true);

        }
        else {
            trans_type_cb.getItems().addAll(TransactionType.money_in, TransactionType.credit_out, TransactionType.money_out, TransactionType.credit_in);
            edit_data_hbox.setVisible(true);

        }
        trans_type_cb.getSelectionModel().select(0);


        //----------------------------------------------------//
        trans_tv_ini();


    }

    public void setInitializeAction(InitializeAction initializeAction) {
        this.initializeAction = initializeAction;
    }

    public void clearData() {
        order_notes_ta.clear();
        money_in_lb.setText(Integer.toString(0));
        money_out_lb.setText(Integer.toString(0));
        total_money_lb.setText(Integer.toString(0));
        order_time_lb.setText("");
        order_expiry_date_lb.setText("");
        order_id_lb.setText("");

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

            transactions_tv_list.clear();
            for (OrderTransaction transaction : main_screen.getOrder().getOrderTransactions()) {
                transactions_tv_list.add(new OrderDataTableRow(transaction));

            }
            float total_money = (main_screen.getOrder().getTotal_money_in() - main_screen.getOrder().getTotal_money_out());
            order_id_lb.setText(Integer.toString(main_screen.getOrder().getOrder_id()));
            amount_needed_before_edit = total_money - main_screen.getOrder().getTotal_credit_out();
            order_time_lb.setText(main_screen.getOrder().getOrder_time().toString());
            order_expiry_date_lb.setText(main_screen.getOrder().getExpiry_date().get_Date());
            updateOrderSummaryFields();
            setMutable(false);
        }else {
            order_id_lb.setText("0");
            order_time_lb.setText(new DateTime().toString());
            DateTime expected_expiry=new DateTime();
            int years=Main.appSettings.getCreditExpirySettings().getYears();
            int months=Main.appSettings.getCreditExpirySettings().getMonths();
            int days=Main.appSettings.getCreditExpirySettings().getDays();
            expected_expiry.add_to_date(years,months,days);
            order_expiry_date_lb.setText(expected_expiry.get_Date());
        }
        if(initializeAction!=null)
            initializeAction.action();
    }
    public void reloadFromDatabase()
    {
        try {
            main_screen.setCustomer(Customer.getCustomer(Integer.toString(main_screen.getCustomer().getId()), Customer.QueryFilter.ID));
            main_screen.setOrder(Order.getOrderByID(main_screen.getOrder().getOrder_id()));
            main_screen.getOrder().setCustomer(main_screen.getCustomer());

        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);

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

        // --------------------------Amount needed text field--------------------------------------//
        float amount_needed = Math.max(total_money - current_order.getTotal_credit_out(), 0.0f);
        amount_needed_lb.getStyleClass().clear();
        amount_needed_lb.getStyleClass().add(greenTextStyle);
        if (main_screen.getOrderSettings() == OrderSettings.viewAndEdit) {
            amount_needed = total_money - current_order.getTotal_credit_out() - amount_needed_before_edit;
            if (amount_needed < 0) {
                amount_needed*=-1;
                amount_needed_lb.getStyleClass().clear();
                amount_needed_lb.getStyleClass().add(redTextStyle);
            }
        }
        amount_needed_lb.setText(Float.toString(amount_needed));


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
        money_amount_col.setCellValueFactory((new PropertyValueFactory<>("money_amount_tf")));
        trans_time_col.setCellValueFactory((new PropertyValueFactory<>("trans_time")));
        remove_btn_col.setCellValueFactory((new PropertyValueFactory<>("remove_btn")));
        edit_save_btn_col.setCellValueFactory((new PropertyValueFactory<>("edit_HBox")));
        order_trans_tv.setItems(transactions_tv_list);
    }

    public void setMutable(boolean mutable) {
        trans_amount_tf.setEditable(mutable);
        this.mutable = mutable;
        save_data_btn.setVisible(mutable);
        cancel_edit_btn.setVisible(mutable);
        delete_order_btn.setDisable(!mutable);
        edit_order_data_btn.setVisible(!mutable);
        if(mutable)
        {
            edit_order_data_btn.setMaxWidth(0);
            edit_order_data_btn.setPrefWidth(0);
        }
        else
        {
            edit_order_data_btn.setMaxWidth(120);
            edit_order_data_btn.setPrefWidth(120);
        }
    }
    @FXML
    void delete_order(ActionEvent event)
    {

    }


    @FXML
    void add_transaction(ActionEvent event) {
        if(!mutable)return;
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
        main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction));
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
        main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction));
        transactions_tv_list.add(new OrderDataTableRow(transaction));
        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);


    }

    void addMoneyOutTransaction(OrderTransaction transaction) throws InvalidTransaction {
        // money return check if there is no credit
        Order order = main_screen.getOrder();
        Customer customer = main_screen.getCustomer();
        float transfer_amount = 0;
        try {
            main_screen.getOrder().addTransaction(transaction);
            main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction));
            transactions_tv_list.add(new OrderDataTableRow(transaction));
            main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);

        } catch (InvalidTransaction inv) {
            if (inv.getErrorType() == InvalidTransaction.ErrorType.noEnoughCusCredit) {
                // if the problem that there is no enough credit in customer account
//                boolean noEnoughCusCredit = true;
//                if (order.isOrder_archived()) {
//                    if (customer.getArchived_credit() > 0) {
//                        transfer_amount = Math.min(customer.getArchived_credit(), order.getSettlementTransaction(transaction).getMoney_amount());
//                        CreditArchiveTransaction fromArchivedToActiveTransfer = new CreditArchiveTransaction(CreditArchiveTransaction.TransactionType.archiveToActive, 1, transfer_amount, customer);
//                        customer.fromArchiveToActive(fromArchivedToActiveTransfer.getCredit());
//                        main_screen.getDbOperations().add(fromArchivedToActiveTransfer, DBStatement.Type.ADD);
//                        new Alerts(String.format("تم تحويل نقاط من النقاط المجمده بقيمه %.2f", fromArchivedToActiveTransfer.getCredit()), Alert.AlertType.INFORMATION);
//                        //TODO update customer credit text filed
//                    }
//                    try {
//                        order.checkTransactionValidation(transaction.getMoney_amount(), transaction.getTrans_type());
//                        noEnoughCusCredit = false;
//                    } catch (InvalidTransaction e) {
//                    }
//                }
//                if (noEnoughCusCredit) {

                OrderTransaction settlementTransaction = order.getSettlementTransaction(transaction);
                // if customer expiry date after order date take from balance the change
                Alerts.ConfBtnAction confBtnAction = new Alerts.ConfBtnAction() {
                    @Override
                    public void yesAction() {
                        // add settlement transaction first then return value
                        try {
                            main_screen.getOrder().addTransaction(settlementTransaction);
                            main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(settlementTransaction));

                            main_screen.getDbOperations().add(settlementTransaction, DBStatement.Type.ADD);

                            main_screen.getOrder().addTransaction(transaction);
                            main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction));
                            main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);
                            transactions_tv_list.addAll(new OrderDataTableRow(settlementTransaction), new OrderDataTableRow(transaction));

                        } catch (InvalidTransaction e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void noAction() {
                        trans_amount_tf.clear();

                    }
                };
                new Alerts(confBtnAction, "لا يوجد رصيد كافى لاسترجاع القيمه المطلوبه كامله حيث انه تم صرف النقاط المكتسبه من هذه القيمه هل تريد استرجاع الفرق");
                return;

//                } else {
//                    try {
//                        main_screen.getOrder().addTransaction(transaction);
//                        main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction));
//                        transactions_tv_list.add(new OrderDataTableRow(transaction));
//                        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);
//                    } catch (InvalidTransaction e) {
//                        new Alerts(e.getMessage(), Alert.AlertType.ERROR);
//
//                    }
//                }
            } else {
                new Alerts(inv.getMessage(), Alert.AlertType.ERROR);
            }

        }


    }

    void addCreditInTransaction(OrderTransaction transaction) throws InvalidTransaction {
        main_screen.getOrder().addTransaction(transaction);
        main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction));
        transactions_tv_list.add(new OrderDataTableRow(transaction));
        main_screen.getDbOperations().add(transaction, DBStatement.Type.ADD);
    }

    @FXML
    void cancel_edit(ActionEvent event) {
        main_screen.getDbOperations().clear();
        reloadFromDatabase();
        initializeOrder();
        setMutable(false);


    }

    @FXML
    void edit_order(ActionEvent event) {
        setMutable(true);
        main_screen.getDbOperations().add(main_screen.getCustomer(), DBStatement.Type.UPDATE);


    }

    @FXML
    void save_order_Data(ActionEvent event) {
        try {
            main_screen.getDbOperations().execute();
            setMutable(false);

        } catch (SQLException e) {
            new Alerts(e);
        }

    }

    public String getOrderNotes() {
        if (order_notes_ta.getText() != null || !order_notes_ta.getText().isEmpty())
            return order_notes_ta.getText();
        else
            return null;
    }

    public class OrderDataTableRow {
        OrderTransaction transaction;
        TextField money_amount_tf;
        Button remove_btn;
        Button editButton;
        Button cancelButton;
        Button saveButton;
        CreditArchiveTransaction fromArchivedToActiveTransfer;
        OrderTransaction settlement_added;
        HBox edit_HBox;
        float amount_backup;

        public OrderDataTableRow(OrderTransaction transaction) {

            this.transaction = transaction;
            this.money_amount_tf = new TextField();
            this.money_amount_tf.setText(Float.toString(transaction.getMoney_amount()));
            this.money_amount_tf.setAlignment(Pos.CENTER);
            this.money_amount_tf.setOnAction(event -> {
                if(saveButton.isVisible())
                    saveButtonAction();
            });
            this.remove_btn = new Button();
            ImageLoader.icoButton(remove_btn, "deleteButton.png", 10);

            remove_btn.setOnAction((actionEvent) -> {
                removeBtnAction();
            });

            this.editButton = new Button();
            editButton.setGraphicTextGap(0);
            ImageLoader.icoButton(editButton, "editButton.png", 10);
            this.editButton.setOnAction((actionEvent) -> {
                editButtonAction();
            });
            if (main_screen.getOrderSettings() == OrderSettings.newOrder)
                this.editButton.setDisable(true);


            this.saveButton = new Button();
            ImageLoader.icoButton(saveButton, "saveButton.png", 10);
            this.saveButton.setOnAction(event -> {
                saveButtonAction();
            });

            this.cancelButton = new Button();
            ImageLoader.icoButton(cancelButton, "ban-solid.png", 10);
            this.cancelButton.setOnAction((actionEvent) -> {
                cancelButtonAction();
            });
            if (transaction.getTrans_type() == TransactionType.money_in_settlement)
                editButton.setDisable(true);


            edit_HBox = new HBox(cancelButton, editButton, saveButton);
            edit_HBox.setAlignment(Pos.CENTER);

            immutable_mode();

        }

        public void setSettlement_added(OrderTransaction settlement_added) {
            this.settlement_added = settlement_added;
        }

        public CreditArchiveTransaction getFromArchivedToActiveTransfer() {
            return fromArchivedToActiveTransfer;
        }

        public void setFromArchivedToActiveTransfer(CreditArchiveTransaction fromArchivedToActiveTransfer) {
            this.fromArchivedToActiveTransfer = fromArchivedToActiveTransfer;
        }

        public HBox getEdit_HBox() {
            return edit_HBox;
        }

        public TextField getMoney_amount_tf() {
            return money_amount_tf;
        }

        public Button getEditButton() {
            return editButton;
        }

        public void immutable_mode() {
            editButton.setPrefWidth(10);
            ImageLoader.icoButton(editButton, "editButton.png", 10);
            editButton.setVisible(true);
            cancelButton.setVisible(false);
            saveButton.setVisible(false);
            money_amount_tf.getStyleClass().clear();
            money_amount_tf.getStyleClass().add("text_field_label");
            money_amount_tf.setEditable(false);


        }

        public void mutable_mode() {
            amount_backup = transaction.getMoney_amount();
            cancelButton.setVisible(true);
            editButton.setGraphic(null);
            saveButton.setVisible(true);
            editButton.setVisible(false);
            editButton.setMaxWidth(0);
            editButton.setPrefWidth(0);
            money_amount_tf.getStyleClass().clear();
            money_amount_tf.getStyleClass().addAll("text-field", "text-input");
            money_amount_tf.setEditable(true);
        }


        public void editButtonAction() {
            if(!mutable)return;
            mutable_mode();

        }

        public void saveButtonAction() {
            float new_amount = Float.parseFloat(money_amount_tf.getText());
            float change = new_amount - transaction.getMoney_amount();

            try {
                main_screen.getOrder().updateTransaction(transaction, new_amount);
                main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(change, transaction.getTrans_type(), false));
                main_screen.getDbOperations().add(transaction, DBStatement.Type.UPDATE);

            } catch (InvalidTransaction inv) {
                new Alerts(inv.getMessage(), Alert.AlertType.ERROR);
                money_amount_tf.setText(Float.toString(transaction.getMoney_amount()));
            }
            immutable_mode();

        }

        public void cancelButtonAction() {
            immutable_mode();
            money_amount_tf.setText(Float.toString(transaction.getMoney_amount()));
        }

        public void removeBtnAction() {
            OrderSettings orderSettings = main_screen.getOrderSettings();

            try {
                main_screen.getOrder().removeTransaction(transaction);
                main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction, true));
                main_screen.getDbOperations().add(transaction, DBStatement.Type.DELETE);
                transactions_tv_list.remove(this);

                if (settlement_added != null) {
                    main_screen.getOrder().removeTransaction(settlement_added);
                    main_screen.getCustomer().addToActiveCredit(main_screen.getOrder().getCustomerBalanceChange(transaction, true));
                    main_screen.getDbOperations().add(settlement_added, DBStatement.Type.DELETE);

                }


            } catch (InvalidTransaction e) {
                new Alerts(e.getMessage(), Alert.AlertType.ERROR);
            }

        }


        public int getTrans_id() {
            return transaction.getTrans_id();
        }

        public TransactionType getTrans_type() {
            return transaction.getTrans_type();
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
