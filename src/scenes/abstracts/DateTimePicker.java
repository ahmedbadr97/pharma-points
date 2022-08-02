package scenes.abstracts;

import javafx.scene.Node;
import scenes.main.WindowAbstract;
import utils.DateTime;

public class DateTimePicker extends WindowAbstract<scenes.controller.DateTimePicker> {
    public DateTimePicker() {
        loadFxmlOnly("DateTimePicker",345,30);
        getController().ini();

    }
    public DateTime getDateTime()
    {
        return getController().getDateTime();
    }


}
