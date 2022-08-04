package main;

import database.DBOperations;
import database.DBStatement;
import database.entities.*;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
import exceptions.SystemError;
import utils.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import database.entities.SystemConfiguration.SystemAttribute;
public class AppSettings {

    private SystemUser logged_in_user;
    private String server_ip;
    private CreditExpirySettings creditExpirySettings;
    private SaleHistory current_sale;
    private ConfigurationFile configurationFile;

    public AppSettings() throws SystemError {
        configurationFile=new ConfigurationFile("config");
        configurationFile.openSettingsFile();
        server_ip=configurationFile.getValue("serverip");
        configurationFile.closeSettingsFile();
    }
    public void setServer_ip(String server_ip)throws SystemError
    {
        configurationFile.openSettingsFile();
        configurationFile.addValue("serverip",server_ip);
        configurationFile.saveData();
        this.server_ip=server_ip;
    }
    private void getCurrentSaleSettings()throws SQLException,DataNotFound
    {
       SystemConfiguration currentSaleConfig=SystemConfiguration.getSystemConfiguration(SystemAttribute.CURRENT_SALE);
       current_sale=SaleHistory.getSaleBy_id(Integer.parseInt(currentSaleConfig.getAttrib_value()));

    }


    public void loadAppSettings(SystemUser logged_in_user)throws SQLException,DataNotFound
    {
        this.logged_in_user=logged_in_user;
        getCurrentSaleSettings();
        this.creditExpirySettings=SystemConfiguration.get_CreditExpirySettings();
        update_expired_orders();


    }

    public void setCreditExpirySettings(CreditExpirySettings creditExpirySettings) {
        this.creditExpirySettings = creditExpirySettings;
    }

    public void update_expired_orders()throws SQLException,DataNotFound
    {
        DBOperations dbOperations=new DBOperations();
        SystemConfiguration updateExpiredOrdersConf=SystemConfiguration.getSystemConfiguration(SystemAttribute.EXPIRED_ORDERS_LAST_CHECK);
        dbOperations.add(updateExpiredOrdersConf, DBStatement.Type.UPDATE);
        Statement s = Main.dBconnection.getConnection().createStatement();
        ResultSet r = s.executeQuery("SELECT TO_CHAR(SYSDATE,'DD-MM-YYYY') FROM dual");
        DateTime current_time=null;
        while (r.next()) {
            String dateTimeStr = r.getString(1).substring(0,10);
            current_time = DateTime.fromDate(dateTimeStr);
        }
        if (current_time==null)
            throw new DataNotFound("can't bring database date");
        DateTime last_check=DateTime.fromDate(updateExpiredOrdersConf.getAttrib_value());
        if(last_check.getLocalDate().isEqual(current_time.getLocalDate()))
            return;
        ArrayList<Order> expiredOrders=null;
        try {
             expiredOrders=Order.getExpiredOrders(current_time);
        }
        catch (DataNotFound d){
            // update updateExpiredOrdersConf date
            dbOperations.execute();
            return;}

        HashMap<Integer,Customer> processedCustomers=new HashMap<>();

        for (Order order:expiredOrders) {
            Customer customer=processedCustomers.get(order.getCus_id());
            if(customer==null)
            {
                customer=Customer.getCustomer(Integer.toString(order.getCus_id()), Customer.QueryFilter.ID);
                processedCustomers.put(customer.getId(),customer);
            }
            float amount_to_transfer=Math.max(order.getTotal_credit(),0.0f);
            if(amount_to_transfer!=0)
            {

                amount_to_transfer=Math.min(amount_to_transfer,customer.getActive_credit());
                try {
                    customer.fromActiveToArchive(amount_to_transfer);
                    dbOperations.add(new CreditArchiveTransaction(customer,amount_to_transfer), DBStatement.Type.ADD);

                }
                catch (InvalidTransaction i)
                {
                    //TO DO throw invalid trans
                }

            }
            order.setOrder_archived(true);
            dbOperations.add(order, DBStatement.Type.UPDATE);
        }
        for (Customer cus:processedCustomers.values()) {
            dbOperations.add(cus, DBStatement.Type.UPDATE);
        }
        dbOperations.execute();


    }

    public void setCurrent_sale(SaleHistory current_sale) {
        this.current_sale = current_sale;
    }

    public SystemUser getLogged_in_user() {
        return logged_in_user;
    }

    public String getServer_ip() {
        return server_ip;
    }


    public CreditExpirySettings getCreditExpirySettings() {
        return creditExpirySettings;
    }

    public SaleHistory getCurrent_sale() {
        return current_sale;
    }

    public static class CreditExpirySettings{
        private int years,months,days;

        public CreditExpirySettings(int years, int months, int days) {
            this.years = years;
            this.months = months;
            this.days = days;
        }

        public int getYears() {
            return years;
        }

        public int getMonths() {
            return months;
        }

        public int getDays() {
            return days;
        }
    }}
