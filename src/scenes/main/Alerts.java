package scenes.main;

import exceptions.DataEntryError;
import exceptions.DataNotFound;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.SQLException;
import java.util.Optional;

public class Alerts {
    Alert alert;
    Exception current_exception;
    String oracleMessage;
   public static Alert.AlertType ifnoALert= Alert.AlertType.INFORMATION;

    public Alerts(SQLException s) {
        this.current_exception = s;
        iniOracleMessage(s.getErrorCode());
        alert=new Alert(Alert.AlertType.ERROR,oracleMessage+" "+s.getMessage());
        alert.setOnCloseRequest(event -> alertOnClose());
        alert.showAndWait();
    }

    public Alerts(DataNotFound dataNotFound) {
        this.current_exception = dataNotFound;
        alert=new Alert(Alert.AlertType.INFORMATION,dataNotFound.getMessage());
        alert.setOnCloseRequest(event -> alertOnClose());
        alert.showAndWait();
    }
    public Alerts(DataEntryError dataEntryError) {
        this.current_exception = dataEntryError;
        alert=new Alert(Alert.AlertType.INFORMATION,dataEntryError.getMessage());
        alert.setOnCloseRequest(event -> alertOnClose());
        alert.showAndWait();
    }

    public Alerts(ConfBtnAction action, String message)
    {
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,message);
        ButtonType yes= new ButtonType("نعم");
        ButtonType no= new ButtonType("لا");
        alert.getButtonTypes().setAll(yes,no);
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==yes)action.yesAction();
        else action.noAction();

    }
    public void alertOnClose()
    {

    }
    public void iniOracleMessage(int error_code)
    {
        switch (error_code)
        {
            case 1:
                oracleMessage="يوجد تشابه ببيانات تم اضافتها من قبل";
                break;
            case 2001:
                oracleMessage="No Items";
                break;
            default:
                oracleMessage="Database Server Error "+"code="+error_code;
        }
    }

    public Alerts(String message, Alert.AlertType type) {
        Alert alert=new Alert(type,message);
        alert.setOnCloseRequest( event -> alertOnClose());
        alert.showAndWait();
    }
    public static interface  ConfBtnAction
    {
        public abstract void yesAction();
        public abstract void noAction();
    }
}
