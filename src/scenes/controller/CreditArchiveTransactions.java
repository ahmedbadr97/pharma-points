package scenes.controller;

import database.DBOperations;
import database.DBStatement;
import database.entities.CreditArchiveTransaction;
import exceptions.DataEntryError;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scenes.main.Alerts;
import utils.DateTime;
import utils.Validator;

import java.sql.SQLException;
import java.util.ArrayList;

public class CreditArchiveTransactions {

    @FXML
    private Label archived_credit_lb;

    @FXML
    private Label active_credit_lb;

    @FXML
    private TableView<CreditArchiveTransaction> creditTransactions_tv;

    @FXML
    private TableColumn<CreditArchiveTransaction, Integer> creditTrans_id;

    @FXML
    private TableColumn<CreditArchiveTransaction, CreditArchiveTransaction.TransactionType> creditTransType_col;

    @FXML
    private TableColumn<CreditArchiveTransaction, Float> creditTrans_amount;

    @FXML
    private TableColumn<CreditArchiveTransaction, String> manual_op_col;

    @FXML
    private TableColumn<CreditArchiveTransaction, DateTime> dateTime_col;

    @FXML
    private ComboBox<CreditArchiveTransaction.TransactionType> creditTransType_cb;

    @FXML
    private TextField creditTransAmount_tf;
    private ObservableList<CreditArchiveTransaction> archiveTransactionsObList;
    private scenes.main.CreditArchiveTransactions main_screen;
    private DBOperations dbOperations;
    public void ini(scenes.main.CreditArchiveTransactions main_screen)
    {
        this.main_screen=main_screen;
        dbOperations=new DBOperations();
        dbOperations.add(main_screen.getCustomer(), DBStatement.Type.UPDATE);
        creditTransType_cb.getItems().addAll(CreditArchiveTransaction.TransactionType.values());
        archiveTransactionsObList=FXCollections.observableArrayList();
        main_screen.getCustomer().addActiveCreditChangeAction(() -> {
            active_credit_lb.setText(Float.toString(main_screen.getCustomer().getActive_credit()));
        });
        main_screen.getCustomer().addArchivedCreditChangeAction(() -> {
            archived_credit_lb.setText(Float.toString(main_screen.getCustomer().getArchived_credit()));
        });
        systemUsers_tv_ini();
        loadData();

    }
    public void loadData()
    {
        clearData();
        archiveTransactionsObList.clear();
        try {
           archiveTransactionsObList.addAll(CreditArchiveTransaction.getArchiveTransactions(main_screen.getCustomer()));
            active_credit_lb.setText(Float.toString(main_screen.getCustomer().getActive_credit()));
            archived_credit_lb.setText(Float.toString(main_screen.getCustomer().getArchived_credit()));


        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
        }

    }

    private void systemUsers_tv_ini() {
        creditTrans_id.setCellValueFactory((new PropertyValueFactory<>("trans_id")));
        creditTransType_col.setCellValueFactory((new PropertyValueFactory<>("transactionType")));
        creditTrans_amount.setCellValueFactory((new PropertyValueFactory<>("credit")));
        manual_op_col.setCellValueFactory((new PropertyValueFactory<>("isManualTransaction")));
        dateTime_col.setCellValueFactory((new PropertyValueFactory<>("trans_date")));
        creditTransactions_tv.setItems(archiveTransactionsObList);
    }
    void clearData()
    {
        creditTransType_cb.getSelectionModel().select(0);
        creditTransAmount_tf.clear();

    }
    @FXML
    void cancel(ActionEvent event) {
        dbOperations.clear();
        main_screen.getStage().close();
    }

    @FXML
    void save(ActionEvent event) {
        try {
            dbOperations.execute();
            new Alerts("تم حفظ البيانات بنجاح", Alert.AlertType.INFORMATION);
            main_screen.closeStage();
        } catch (SQLException e) {
            new Alerts(e);
        }
    }

    @FXML
    void transCredit(ActionEvent event) {
        try {
            float amount= Validator.getFloat(creditTransAmount_tf,"قيمه النقاط",0,(int)1e5);

            CreditArchiveTransaction.TransactionType transactionType=creditTransType_cb.getValue();
            switch (transactionType){
                case activeToArchive:
                    main_screen.getCustomer().fromActiveToArchive(amount);
                    break;
                case archiveToActive:
                    main_screen.getCustomer().fromArchiveToActive(amount);
                    break;
            }
            CreditArchiveTransaction archiveTransaction=new CreditArchiveTransaction(transactionType,0,amount,main_screen.getCustomer());
            dbOperations.add(archiveTransaction, DBStatement.Type.ADD);
            archiveTransactionsObList.add(archiveTransaction);
            clearData();
        }
        catch (InvalidTransaction inv)
        {
            new Alerts(inv.getMessage(), Alert.AlertType.ERROR);
        }
        catch (DataEntryError d)
        {
            new Alerts(d);
        }

    }

}
