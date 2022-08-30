package scenes.controller;

        import database.DBOperations;
        import database.DBStatement;
        import database.entities.SaleHistory;
        import database.entities.SystemConfiguration;
        import exceptions.DataEntryError;
        import exceptions.DataNotFound;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
        import javafx.scene.layout.GridPane;
        import main.AppSettings;
        import main.Main;
        import scenes.abstracts.EditHBox;
        import scenes.images.ImageLoader;
        import scenes.main.Alerts;
        import utils.DateTime;
        import utils.Validator;

        import java.sql.SQLException;

public class OrderSettings {

    @FXML
    private TextField o_years_tf;

    @FXML
    private TextField o_months_tf;

    @FXML
    private TextField o_days_tf;



    @FXML
    private TextField money_tf;

    @FXML
    private TextField credit_tf;
    @FXML
    private GridPane order_expiry_gp;

    @FXML
    private GridPane sale_settings_gp;

    @FXML
    private TableView<SaleHistory> saleHistoryTv;
    @FXML
    private TableColumn<SaleHistory, DateTime> sale_date_col;
    @FXML
    private TableColumn<SaleHistory, Float> sale_credit_money_col;

    private AppSettings.CreditExpirySettings creditExpirySettings;
    private AppSettings.CreditExpirySettings newCreditExpirySettings;
    private SaleHistory currentSaleHistory;
    private SaleHistory newSaleHistory;
    private DBOperations dbOperations;
    ObservableList<SaleHistory> saleHistoryObservableList;
    private EditHBox editOrderExpiryHBox;
    private EditHBox editSaleSettingsHBox;



    public void ini()
    {
        creditExpirySettings= AppSettings.getInstance().getCreditExpirySettings();
        currentSaleHistory=AppSettings.getInstance().getCurrent_sale();
        newCreditExpirySettings=null;
        setExpiryDataMutability(false);
        updateExpiryDataFields();
        editOrderExpiryHBox=new EditHBox();
        order_expiry_gp.add(editOrderExpiryHBox.getButtonsHBox(),3,0);
        setOrderExpiryHBoxActions();

        editSaleSettingsHBox=new EditHBox();
        sale_settings_gp.add(editSaleSettingsHBox.getButtonsHBox(),2,0);
        setSaleSettingsHBoxActions();

        updateCurrentSaleFields();
        setCurrentSaleMutability(false);

        try {
            saleHistoryObservableList= FXCollections.observableList(SaleHistory.getSaleHistory());
        } catch (SQLException e) {
            new Alerts(e);
        } catch (DataNotFound e) {
            new Alerts(e);
        }
        dbOperations=new DBOperations();
        saleHistoryTvIni();

    }
    private void saleHistoryTvIni()
    {
        sale_date_col.setCellValueFactory((new PropertyValueFactory<>("creation_time")));
        sale_credit_money_col.setCellValueFactory((new PropertyValueFactory<>("creditToMoney")));
        saleHistoryTv.setItems(saleHistoryObservableList);
    }

    public void setExpiryDataMutability(boolean mutable)
    {
        o_years_tf.setEditable(mutable);
        o_months_tf.setEditable(mutable);
        o_days_tf.setEditable(mutable);
    }
    public void updateExpiryDataFields()
    {
        o_years_tf.setText(Integer.toString(creditExpirySettings.getYears()));
        o_months_tf.setText(Integer.toString(creditExpirySettings.getMonths()));
        o_days_tf.setText(Integer.toString(creditExpirySettings.getDays()));
    }
    public void updateCurrentSaleFields()
    {
        money_tf.setText(Float.toString(1.0f/currentSaleHistory.getMoney_to_credit()));
        credit_tf.setText("1");

    }
    public void setCurrentSaleMutability(boolean mutable)
    {
        money_tf.setEditable(mutable);
        credit_tf.setEditable(mutable);
    }
    void setOrderExpiryHBoxActions()
    {

        editOrderExpiryHBox.setCancelAction((actionEvent)->{
            setExpiryDataMutability(false);
            updateExpiryDataFields();
        });
        editOrderExpiryHBox.setEditAction((actionEvent)->{
            setExpiryDataMutability(true);
        });
        editOrderExpiryHBox.setSaveAction((actionEvent)->{
            int years=0,months=0,days=0;
            try {
                years = Validator.getInteger(o_years_tf,"السنين ",0,100);
                months= Validator.getInteger(o_months_tf,"الشهور ",0,12);
                days=Validator.getInteger(o_days_tf," الايام",0,Integer.MAX_VALUE);
                if(years+months+days==0)
                {
                    new Alerts("جميع الخانات تحتوى على اصفار", Alert.AlertType.ERROR);
                    return;
                }
                setExpiryDataMutability(false);
                newCreditExpirySettings=new AppSettings.CreditExpirySettings(years,months,days);
                dbOperations.add(SystemConfiguration.fromCreditExpirySettings(newCreditExpirySettings), DBStatement.Type.UPDATE);
            }
            catch (DataEntryError n)
            {
                new Alerts(n);

            }
        });

    }
    void setSaleSettingsHBoxActions()
    {
        editSaleSettingsHBox.setCancelAction((event)->{
            setCurrentSaleMutability(false);
            updateCurrentSaleFields();
        });
        editSaleSettingsHBox.setEditAction((event)->{
            setCurrentSaleMutability(true);
        });
        editSaleSettingsHBox.setSaveAction((event)->{
            try {
                float no_pts=Validator.getInteger(credit_tf,"عدد النقاط",1,(int)1e5);
                float money_amount=Validator.getInteger(money_tf,"عدد الجنيهات",1,(int)1e5);
                float sale_value=no_pts/money_amount;
                newSaleHistory=new SaleHistory(sale_value);
                dbOperations.add(newSaleHistory, DBStatement.Type.ADD);
                SystemConfiguration currentSaleConfig=new SystemConfiguration(SystemConfiguration.SystemAttribute.CURRENT_SALE,()-> Integer.toString(newSaleHistory.getSale_id()));
                dbOperations.add(currentSaleConfig, DBStatement.Type.UPDATE);
                currentSaleHistory=newSaleHistory;
                saleHistoryObservableList.add(currentSaleHistory);
                setCurrentSaleMutability(false);
            }
            catch (DataEntryError d)
            {
                new Alerts(d);
            }
        });
    }


    @FXML
    void cancelChanges(ActionEvent event) {
        ini();
        dbOperations.clear();
    }

    @FXML
    void saveData(ActionEvent event) {
        try {
            dbOperations.execute();
            if(newCreditExpirySettings!=null)
            {
                AppSettings.getInstance().setCreditExpirySettings(newCreditExpirySettings);
            }
            if(newSaleHistory!=null)
            {
                AppSettings.getInstance().setCurrent_sale(newSaleHistory);
            }

        } catch (SQLException e) {
            new Alerts(e);
        }
        new Alerts("تم حفظ البيانات بنجاح", Alert.AlertType.INFORMATION);
        ini();


    }




}

