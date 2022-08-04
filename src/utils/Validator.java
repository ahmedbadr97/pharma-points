package utils;

import exceptions.DataEntryError;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import scenes.main.Alerts;

public class Validator {

    public static int getInteger(TextField textField,String fieldName,int start,int end)throws DataEntryError
    {
        int value;
        try {
            value =Integer.parseInt(textField.getText());
        }
        catch (NumberFormatException n)
        {
            Platform.runLater(textField::requestFocus);
            throw  new DataEntryError(fieldName+" يجب ان يحتوى على ارقام فقط");
        }
        if (value<start || value >end)
        {
            Platform.runLater(textField::requestFocus);
            throw  new DataEntryError(fieldName+" يجب ان يحتوى على ارقام من "+start+" الى "+end);

        }
        return value;

    }
    public static float getFloat(TextField textField,String fieldName,float start,float end)throws DataEntryError
    {
        float value;
        try {
            value =Float.parseFloat(textField.getText());
        }
        catch (NumberFormatException n)
        {
            Platform.runLater(textField::requestFocus);

            throw  new DataEntryError(fieldName+" يجب ان يحتوى على ارقام فقط");
        }
        if (value<start || value >end)
        {
            Platform.runLater(textField::requestFocus);
            throw  new DataEntryError(fieldName+" يجب ان يحتوى على ارقام من "+start+" الى "+end);

        }
        return value;

    }
    public  static String getText(TextField textField,String fieldName)throws DataEntryError
    {
        if(textField.getText()==null||textField.getText().isEmpty()){
            Platform.runLater(textField::requestFocus);
            throw  new DataEntryError( "يجب كتابه "+fieldName);

        }
        return textField.getText();

    }
}
