package scenes.controller;

import database.entities.Customer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import scenes.main.CustomerData;
import scenes.main.CustomerSearch;
import scenes.main.Login;
import scenes.main.NewOrder;
import scenes.main.Settings;
import scenes.main.TransactionsLog;

public class Home {
    @FXML
    private Text system_user_t;
    @FXML
    private Circle connection_circle;
    private scenes.main.Home main_screen;
    private scenes.main.CustomerSearch customerSearchScene;
    private scenes.main.TransactionsLog transactionsLogScene;
    private scenes.main.Settings settings;
    public void init(scenes.main.Home main_screen){
        this.main_screen=main_screen;
        system_user_t.setText(main_screen.getLogged_in_user().getUsername());
        main_screen.getScene().setOnKeyPressed(event -> {
            if(event.getText().equals("1"))
            {
                new_operation_btn_action(null);
            }
            else if(event.getText().equals("2"))
            {
                customer_data_btn_action(null);
            }
            else if(event.getText().equals("3"))
            {
                transactions_log_btn_action(null);
            }
        });

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
        main_screen.closeStage();
        Login login=new scenes.main.Login();
        login.showStage();


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
        if(this.settings==null)
        {
            this.settings=new Settings();
            this.settings.addOnCloseAction(()->{this.settings=null;});
            this.settings.showStage();
        }
        else
            Platform.runLater(()->this.settings.getStage().requestFocus());

    }

    @FXML
    void transactions_log_btn_action(ActionEvent event) {
        transactionsLogScene=new TransactionsLog();
        transactionsLogScene.showStage();

    }
    public void setConnected(boolean connected)
    {
        if(connected)
            connection_circle.setFill(Color.GREEN);
        else
            connection_circle.setFill(Color.GRAY);

    }

}
