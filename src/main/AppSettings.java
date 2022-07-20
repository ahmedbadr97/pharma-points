package main;

import database.entities.*;
import exceptions.DataNotFound;

import java.sql.SQLException;

public class AppSettings {

    private SystemUser logged_in_user;
    private String server_ip;
    private CreditExpirySettings creditExpirySettings;
    private SaleHistory current_sale;

    public AppSettings(SystemUser logged_in_user) throws SQLException, DataNotFound {
        this.logged_in_user = logged_in_user;
        this.current_sale= SystemConfiguration.getCurrentSale();
        this.creditExpirySettings=SystemConfiguration.get_CreditExpirySettings();
        //TODO load server ip from config file
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
    }}
