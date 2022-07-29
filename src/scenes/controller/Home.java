package scenes.controller;

import database.entities.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import scenes.main.CustomerData;
import scenes.main.CustomerSearch;
import scenes.main.NewOrder;

public class Home {
    @FXML
    private Text system_user_t;
    @FXML
    private Circle connection_circle;
    private scenes.main.Home main_screen;
    private scenes.main.CustomerSearch customerSearchScene;
    public void init(scenes.main.Home main_screen){
        this.main_screen=main_screen;
        system_user_t.setText(main_screen.getLogged_in_user().getUsername());

    }

    @FXML
    void customer_data_btn_action(ActionEvent event) {
        customerSearchScene=new CustomerSearch(false);
        customerSearchScene.setOnCustomerSelection(()->{
            Customer selectedCustomer=customerSearchScene.getController().getSelectedCustomer();
            if (selectedCustomer!=null)
            {
                CustomerData customerData= new CustomerData(selectedCustomer);
                customerData.setOnTop();
                customerData.showStage();
            }
        });
        customerSearchScene.showStage();

    }

    @FXML
    void logout_btn_action(ActionEvent event) {

    }

    @FXML
    void new_operation_btn_action(ActionEvent event) {
        new NewOrder().showStage();

    }

//    @FXML
//    void return_operation_btn_action(ActionEvent event) {
//
//    }

    @FXML
    void settings_btn_action(ActionEvent event) {

    }

    @FXML
    void transactions_log_btn_action(ActionEvent event) {

    }

}
