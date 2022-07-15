package scenes.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Home {
    @FXML
    private Text system_user_t;
    @FXML
    private Circle connection_circle;
    private scenes.main.Home main_screen;
    public void init(scenes.main.Home main_screen){
        this.main_screen=main_screen;
        system_user_t.setText(main_screen.getLogged_in_user().getUsername());

    }

    @FXML
    void customer_data_btn_action(ActionEvent event) {

    }

    @FXML
    void logout_btn_action(ActionEvent event) {

    }

    @FXML
    void new_operation_btn_action(ActionEvent event) {

    }

    @FXML
    void return_operation_btn_action(ActionEvent event) {

    }

    @FXML
    void settings_btn_action(ActionEvent event) {

    }

    @FXML
    void transactions_log_btn_action(ActionEvent event) {

    }

}
