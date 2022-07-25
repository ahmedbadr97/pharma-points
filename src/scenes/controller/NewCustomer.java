package scenes.controller;

import database.DBOperations;
import database.DBStatement;
import database.entities.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scenes.main.Alerts;

import java.sql.SQLException;

public class NewCustomer {

    @FXML
    private TextField cus_barcode_tf;

    @FXML
    private TextField cus_name_tf;

    @FXML
    private TextField cus_phone_tf;

    @FXML
    private TextArea cus_address_tf;
    private scenes.main.NewCustomer main_screen;
    public void ini(scenes.main.NewCustomer main_screen)
    {
        this.main_screen=main_screen;
    }


    @FXML
    void cancel_btn_action(ActionEvent event) {
        main_screen.closeStage();

    }

    @FXML
    void save_btn_action(ActionEvent event) {

        // ---------------------------------- DATA VALIDATION----------------------------------------------//
        String cus_name=cus_name_tf.getText();
        if (cus_name==null || cus_name.isEmpty())
        {
            new Alerts("برجاء كتابه اسم العميل", Alert.AlertType.ERROR);
            return;

        }
        String cus_phone=cus_phone_tf.getText();
        if (cus_phone==null || cus_phone.isEmpty())
        {
            new Alerts("برجاء كتابه رقم التليفون", Alert.AlertType.ERROR);
            return;
        }
        try {
            Integer.parseInt(cus_phone_tf.getText());
        }
        catch (NumberFormatException n){
            new Alerts("برجاء كتابه ارقم التليفون ارقام فقط", Alert.AlertType.ERROR);
            return;
        }
        String cus_barcode=null,cus_address=null;
        if(cus_barcode_tf.getText()!=null && !cus_barcode_tf.getText().isEmpty())
             cus_barcode=cus_barcode_tf.getText();
        if(cus_address_tf.getText()!=null && !cus_address_tf.getText().isEmpty())
             cus_address=cus_address_tf.getText();

        Customer new_customer=new Customer(cus_name,cus_phone,cus_barcode,cus_address);
        new_customer.systemSetExpiryDate();

        DBOperations dbOperations=new DBOperations();
        dbOperations.add(new_customer, DBStatement.Type.ADD);
        try {
            dbOperations.execute();
            this.main_screen.closeStage();
        } catch (SQLException e) {
            new Alerts(e);
        }


    }

}
