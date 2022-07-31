package scenes.controller;

import database.DBStatement;
import database.entities.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import scenes.main.Alerts;
import java.sql.SQLException;

public class CustomerDataPane {

    @FXML
    private TextField cus_name_tf;

    @FXML
    private TextField cus_phone_tf;

    @FXML
    private TextField cus_barcode_tf;

    @FXML
    private TextArea cus_address_tf;

    @FXML
    private Label cus_active_credit_lb;


    @FXML
    private HBox edit_data_hbox;

    @FXML
    private Button cancel_edit_btn;

    @FXML
    private Button edit_cus_data_btn;


    @FXML
    private Button save_data_btn;
    private scenes.abstracts.CustomerDataPane main_screen;

    public void ini(scenes.abstracts.CustomerDataPane main_screen) {
        this.main_screen = main_screen;
        edit_data_hbox.setVisible(main_screen.isMutable());
        setEditMode(false);


    }

    public void loadCustomerData() {
        Customer customer = main_screen.getCustomer();
        customer.addActiveCreditChangeAction(()->{
            cus_active_credit_lb.setText(Float.toString(customer.getActive_credit()));
        });
        if (customer.getName() != null)
            cus_name_tf.setText(customer.getName());
        if (customer.getPhone() != null)
            cus_phone_tf.setText(customer.getPhone());
        if (customer.getBarcode() != null)
            cus_barcode_tf.setText(customer.getBarcode());
        if (customer.getAddress() != null)
            cus_address_tf.setText(customer.getAddress());

        cus_active_credit_lb.setText(Float.toString(customer.getActive_credit()));

    }
    public void clearData()
    {
        cus_name_tf.clear();
        cus_phone_tf.clear();
        cus_barcode_tf.clear();
        cus_address_tf.clear();
        edit_data_hbox.setVisible(main_screen.isMutable());
        setEditMode(false);
        cus_active_credit_lb.setText(Float.toString(0));

    }

    public void setEditMode(boolean edit_mode) {
        //customer data fields
        cus_name_tf.setEditable(edit_mode);
        cus_phone_tf.setEditable(edit_mode);
        cus_address_tf.setEditable(edit_mode);
        cus_barcode_tf.setEditable(edit_mode);
        //TODO set update expiry date from admin only



        // edit buttons Hbox
        cancel_edit_btn.setVisible(edit_mode);
        save_data_btn.setVisible(edit_mode);
        edit_cus_data_btn.setVisible(!edit_mode);
        if (edit_mode)
            edit_cus_data_btn.setPrefWidth(0);
        else
            edit_cus_data_btn.setPrefWidth(120);

    }


    @FXML
    void cancel_edit(ActionEvent event) {
        setEditMode(false);
        loadCustomerData();
    }

    @FXML
    void edit_cus_data(ActionEvent event) {
        setEditMode(true);

    }



    @FXML
    void open_cus_arch_credit(ActionEvent event) {

    }

    @FXML
    void save_cus_Data(ActionEvent event) {
        //validation
        Customer customer = main_screen.getCustomer();
        String cus_name = cus_name_tf.getText();
        if (cus_name == null || cus_name.isEmpty()) {
            new Alerts("برجاء كتابه اسم العميل", Alert.AlertType.ERROR);
            return;

        }
        String cus_phone = cus_phone_tf.getText();
        if (cus_phone == null || cus_phone.isEmpty()) {
            new Alerts("برجاء كتابه رقم التليفون", Alert.AlertType.ERROR);
            return;
        }
        try {
            Integer.parseInt(cus_phone_tf.getText());
        } catch (NumberFormatException n) {
            new Alerts("برجاء كتابه ارقم التليفون ارقام فقط", Alert.AlertType.ERROR);
            return;
        }
        main_screen.getCustomer().setName(cus_name);
        main_screen.getCustomer().setPhone(cus_phone_tf.getText());
        if(cus_barcode_tf.getText()!=null && !cus_barcode_tf.getText().isEmpty())
            main_screen.getCustomer().setBarcode(cus_barcode_tf.getText());
        if(cus_address_tf.getText()!=null && !cus_address_tf.getText().isEmpty())
            main_screen.getCustomer().setAddress(cus_address_tf.getText());

        main_screen.getDbOperations().add(main_screen.getCustomer(), DBStatement.Type.UPDATE);
        setEditMode(false);
        try {
            main_screen.getDbOperations().execute();
        } catch (SQLException e) {
            new Alerts(e);
        }
    }

}
