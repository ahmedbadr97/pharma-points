package scenes.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import scenes.main.Alerts;
import utils.DateTime;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimePicker {
    enum dayLight{
        am,pm;
    }

    @FXML
    private HBox DeadLineHB;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ChoiceBox<dayLight> timeCB;

    @FXML
    private TextField minuteTF;

    @FXML
    private TextField hourTF;

    private int hour,minute;

    public void ini()
    {
        timeCB.getItems().addAll(dayLight.values());
        timeCB.setValue(dayLight.pm);
        datePicker.setValue(LocalDate.now());
        hour=0;
        minute=0;
    }
    public void setTime(int hour,int minute)
    {
        this.hour=hour;
        this.minute=minute;

        minuteTF.setText(Integer.toString(this.minute));
        if(hour>12)
        {
            this.hour-=12;
            timeCB.setValue(dayLight.pm);
        }

        else
            timeCB.setValue(dayLight.am);
        hourTF.setText(Integer.toString(this.hour));

    }
    public DateTime getDateTime()
    {
        int hour24=hour;
        if(timeCB.getValue()==dayLight.pm)
            hour24+=12;
        return  DateTime.getDateTimeFromDatePicker(datePicker, LocalTime.of(hour24, minute, 0));
    }

    @FXML
    void CheckHour(KeyEvent event) {
        try {
            if (hourTF.getText().length() > 2)
                throw new Exception();
            hour = Integer.parseInt(hourTF.getText());
            if (hour > 12||timeCB.getValue()==dayLight.pm&&hour==12)
                throw new Exception();
        } catch (Exception e) {
            new Alerts("خانت الساعه يجب ان تحتوي علي رقم من 1 للى 12 ", Alert.AlertType.ERROR);
            hourTF.clear();

        }
    }

    @FXML
    void CheckMinute(KeyEvent event) {
        if (minuteTF.getText().equals(""))
            return;
        try {
            if (minuteTF.getText().length() > 2)
                throw new Exception();
            minute = Integer.parseInt(minuteTF.getText());
            if (minute > 59) {
                throw new Exception();
            }
        } catch (Exception e) {
            new Alerts("خانت الدقائق يجب ان تحتوي علي رقم من 0 الى 59", Alert.AlertType.ERROR);
            minuteTF.clear();

        }
    }
}
