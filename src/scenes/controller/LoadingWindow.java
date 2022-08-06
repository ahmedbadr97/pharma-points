package scenes.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public  class LoadingWindow {
    @FXML
    private Label title_lb;
    public void ini(String title)
    {
        title_lb.setText(title);

    }
}

