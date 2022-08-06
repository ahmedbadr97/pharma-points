package scenes.controller;

import database.entities.OrderTransaction;
import exceptions.DataNotFound;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import main.Main;
import scenes.abstracts.LoadingWindow;
import scenes.main.Alerts;
import utils.DateTime;
import utils.TreasurySummaryPrint;

import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionsLog {

    @FXML
    private StackPane from_spane;

    @FXML
    private StackPane to_spane;

    @FXML
    private TableView<OrderTransaction> transactions_tv;

    @FXML
    private TableColumn<OrderTransaction, Integer> order_id_col;

    @FXML
    private TableColumn<OrderTransaction, Integer> trans_id_col;

    @FXML
    private TableColumn<OrderTransaction, OrderTransaction.TransactionType> trans_type_col;

    @FXML
    private TableColumn<OrderTransaction, Integer> amount_col;

    @FXML
    private TableColumn<OrderTransaction, DateTime> date_time_col;

    @FXML
    private Label money_out_lb;

    @FXML
    private Label total_credit_in_lb;

    @FXML
    private Label money_in_lb;

    @FXML
    private Label total_credit_out_lb;

    @FXML
    private Label total_money_lb;

    @FXML
    private Label total_credit_lb;

    @FXML
    private Label cus_credit_out_lb;

    @FXML
    private Label cus_credit_in_lb;

    @FXML
    private Label total_cus_credit_lb;
    private scenes.abstracts.DateTimePicker fromDateTime;
    private scenes.abstracts.DateTimePicker toDateTime;
    ObservableList<OrderTransaction> ordersTvList;
    private float total_money_in, total_money_out, totalSystemCreditIn, totalSystemCreditOut, totalOrderCreditOut, totalOrderCreditIn;
    String greenTextStyle = "green_summary_field";
    String redTextStyle = "red_summary_field";
    scenes.main.TransactionsLog main_screen;

    public void ini(scenes.main.TransactionsLog main_screen) {
        this.main_screen = main_screen;
        fromDateTime = new scenes.abstracts.DateTimePicker();
        fromDateTime.getController().setTime(0, 0);
        from_spane.getChildren().add(fromDateTime.getParent());
        toDateTime = new scenes.abstracts.DateTimePicker();
        toDateTime.getController().setTime(23, 59);
        to_spane.getChildren().add(toDateTime.getParent());
        ordersTvList = FXCollections.observableArrayList();
        trans_tv_ini();

    }


    @FXML
    void filterTransactions(ActionEvent event) {
        clearData();
        try {
            ArrayList<OrderTransaction> orderTransactions = OrderTransaction.getTransactionsByDate(fromDateTime.getDateTime(), toDateTime.getDateTime());
            ordersTvList.addAll(orderTransactions);
            setSummaryLabels(orderTransactions);
            transactions_tv.sort();
        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
        }

    }

    public void trans_tv_ini() {
        transactions_tv.setItems(ordersTvList);
        order_id_col.setCellValueFactory((new PropertyValueFactory<>("order_id")));
        trans_id_col.setCellValueFactory((new PropertyValueFactory<>("trans_id")));
        trans_type_col.setCellValueFactory((new PropertyValueFactory<>("trans_type")));
        amount_col.setCellValueFactory((new PropertyValueFactory<>("money_amount")));
        date_time_col.setCellValueFactory((new PropertyValueFactory<>("trans_time")));
        date_time_col.setSortType(TableColumn.SortType.ASCENDING);
        transactions_tv.getSortOrder().add(date_time_col);
    }

    public void clearData() {
        ordersTvList.clear();
        setSummaryLabels(null);

    }

    public void setSummaryLabels(ArrayList<OrderTransaction> orderTransactions) {

        total_money_in = total_money_out = totalSystemCreditIn = totalOrderCreditOut = totalOrderCreditIn = totalSystemCreditOut = 0;
        if (orderTransactions != null)
            for (OrderTransaction transaction : orderTransactions) {
                float trans_amount = transaction.getMoney_amount();
                float order_sale = transaction.getOrder().getOrderSale().getMoney_to_credit();
                switch (transaction.getTrans_type()) {
                    case money_in:
                        total_money_in += trans_amount;
                        totalSystemCreditIn += trans_amount * order_sale;
                        break;
                    case credit_out:
                        totalOrderCreditOut += trans_amount;
                        totalSystemCreditOut += trans_amount;
                        break;
                    case credit_in:
                        totalOrderCreditIn += trans_amount;
                        totalSystemCreditIn += trans_amount;
                        break;
                    case money_out:
                        total_money_out += trans_amount;
                        totalSystemCreditOut += trans_amount * order_sale;
                        break;
                    case money_in_settlement:
                        total_money_in += trans_amount;
                        totalSystemCreditIn += trans_amount;
                }

            }
        money_in_lb.setText(Float.toString(total_money_in));
        money_out_lb.setText(Float.toString(total_money_out));
        float total_money = total_money_in - total_money_out;
        setTotalLabel(total_money, total_money_lb);

        total_credit_in_lb.setText(Float.toString(totalOrderCreditIn));
        total_credit_out_lb.setText(Float.toString(totalOrderCreditOut));
        float total_credit = totalOrderCreditIn - totalOrderCreditOut;
        setTotalLabel(total_credit, total_credit_lb);

        cus_credit_in_lb.setText(Float.toString(totalSystemCreditIn));
        cus_credit_out_lb.setText(Float.toString(totalOrderCreditOut));
        float total_cus_credit = totalSystemCreditIn - totalSystemCreditOut;
        setTotalLabel(total_cus_credit, total_cus_credit_lb);


    }

    public void setTotalLabel(float amount, Label label) {
        if (amount < 0) {
            label.getStyleClass().clear();
            label.getStyleClass().add(redTextStyle);
            amount *= -1;
        } else {
            label.getStyleClass().clear();
            label.getStyleClass().add(greenTextStyle);
        }
        label.setText(Float.toString(amount));
    }

    @FXML
    void print(ActionEvent event) {
        try {
            DateTime currentDate = Main.dBconnection.getCurrentDatabaseTime();
            TreasurySummaryPrint.TreasurySummary treasurySummary = new TreasurySummaryPrint.TreasurySummary(Main.appSettings.getLogged_in_user().getUsername(),
                    total_money_in - total_money_out, totalOrderCreditIn - totalOrderCreditOut,
                    fromDateTime.getDateTime(), toDateTime.getDateTime(), currentDate);
            TreasurySummaryPrint treasurySummaryPrint=new TreasurySummaryPrint(treasurySummary);
            LoadingWindow loadingWindow=new LoadingWindow("printing transactions log");
            loadingWindow.startProcess(()->{
                try {
                    treasurySummaryPrint.print();
                }
                catch (Exception e)
                {
                    Platform.runLater(()->{new Alerts(e.getMessage(), Alert.AlertType.ERROR);});
                }
                Platform.runLater(loadingWindow::closeStage);

            });

            loadingWindow.showStage();


        } catch (SQLException e) {
            new Alerts(e);
        }
    }

    @FXML
    void close(ActionEvent event) {


    }

}

