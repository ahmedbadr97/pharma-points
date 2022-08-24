package scenes.controller;

import database.entities.Customer;
import exceptions.DataNotFound;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import scenes.main.Alerts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetCustomerBar {


    @FXML
    private HBox searchHbox;

    @FXML
    private ComboBox<SearchWith> cus_search_cb;

    @FXML
    private TextField cus_search_tf;

    @FXML
    private Button cus_search_btn;

    public enum SearchWith {
        CUSTOMER_BARCODE("باركود"), CUSTOMER_PHONE("رقم التليفون"), CUSTOMER_NAME("الاسم");
        private final String label;

        SearchWith(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private ArrayList<Customer> searchCustomers = null;
    private Customer searchCustomer = null;
    private scenes.abstracts.GetCustomerBar main_scene;

    public void ini(scenes.abstracts.GetCustomerBar main_scene) {
        this.main_scene = main_scene;
        // --------------initialize combo boxes----------------//
        // search with combo box
        List<SearchWith> searchWithList = Arrays.asList(SearchWith.values());
        cus_search_cb.setItems(FXCollections.observableList(searchWithList));
        cus_search_cb.setValue(SearchWith.CUSTOMER_BARCODE);
        cus_search_cb.setOnAction(event -> {
            cus_search_tf.clear();
        });
        Platform.runLater(() -> {
            cus_search_tf.requestFocus();
        });

    }


    @FXML
    void search_btn_action(ActionEvent event) {
        String cus_search_value = cus_search_tf.getText();
        if (cus_search_value == null || cus_search_value.isEmpty()) {
            new Alerts("برجاء كتابه بيانات البحث اولا", Alert.AlertType.ERROR);
            Platform.runLater(() -> {
                cus_search_tf.requestFocus();
            });
            return;
        }
        searchCustomers = null;
        searchCustomer = null;

        try {

            switch (cus_search_cb.getValue()) {
                case CUSTOMER_NAME:
                    searchCustomers = Customer.getCustomersByName(cus_search_tf.getText());
                    break;
                case CUSTOMER_BARCODE:
                    searchCustomer = Customer.getCustomer(cus_search_value, Customer.QueryFilter.BARCODE);
                    break;
                case CUSTOMER_PHONE:
                    searchCustomer = Customer.getCustomer(cus_search_value, Customer.QueryFilter.PHONE);
                    break;

            }
        } catch (SQLException e) {
            new Alerts(e);
            return;
        } catch (DataNotFound e) {
            new Alerts(e);
            return;
        }
        if (searchCustomer != null && main_scene.getSearchWithIdAction()!=null)
            main_scene.getSearchWithIdAction().searchAction();
        if (searchCustomers != null && main_scene.getSearchWithNameAction()!=null)
            main_scene.getSearchWithNameAction().searchAction();

    }
    public void searchByNameOnly()
    {
       cus_search_cb.setValue(SearchWith.CUSTOMER_NAME);
       cus_search_cb.setDisable(true);
        cus_search_cb.setStyle("-fx-opacity: 1");
        cus_search_cb.getEditor().setStyle("-fx-opacity: 1");
    }
    public void updateSearchTf(String value)
    {
        cus_search_tf.setText(value);
    }
    public void clearData()
    {
        cus_search_tf.clear();
        cus_search_cb.setValue(SearchWith.CUSTOMER_BARCODE);
        Platform.runLater(()->{
            cus_search_tf.requestFocus();
        });
    }


    public ArrayList<Customer> getSearchCustomers() {
        return searchCustomers;
    }
    public Customer getSearchCustomer() {
        return searchCustomer;
    }
    public HBox getSearchHbox() {
        return searchHbox;
    }
}
