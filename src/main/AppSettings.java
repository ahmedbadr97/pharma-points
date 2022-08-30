package main;

import database.DBOperations;
import database.DBStatement;
import database.DBconnection;
import database.entities.*;
import exceptions.DataNotFound;
import exceptions.InvalidTransaction;
import exceptions.SystemError;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import scenes.abstracts.LoadingWindow;
import scenes.main.Alerts;
import utils.DateTime;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import database.entities.SystemConfiguration.SystemAttribute;

public class AppSettings {
    private static AppSettings uniqueInstance;


    private SystemUser logged_in_user;
    private String server_ip;
    private CreditExpirySettings creditExpirySettings;
    private SaleHistory current_sale;
    private ConfigurationFile configurationFile;
    private String printerName;
    private String lastLoginName;
    private boolean isMainDevice;
    private DataRecoverySettings dataRecoverySettings;


    private AppSettings() throws SystemError {
        configurationFile = new ConfigurationFile("config");
        configurationFile.openSettingsFile();
        server_ip = configurationFile.getValue("serverip");
        lastLoginName = configurationFile.getValue("last_login_name");
        String main_device = configurationFile.getValue("main_device");
        printerName=configurationFile.getValue("printer_name");
        dataRecoverySettings = null;
        if (main_device != null) {
            isMainDevice = main_device.equals("1");
        }
        if (isMainDevice) {
            dataRecoverySettings = new DataRecoverySettings();
        }

        configurationFile.closeSettingsFile();
    }
    public static void InstantiateInstance()throws SystemError
    {
        if (uniqueInstance==null)
            uniqueInstance=new AppSettings();

    }
    public static AppSettings getInstance()  {

        return uniqueInstance;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) throws SystemError{
        configurationFile.openSettingsFile();
        configurationFile.addValue("printer_name", printerName);
        configurationFile.saveData();
        this.printerName = printerName;
    }

    public DataRecoverySettings getDataRecoverySettings() {
        return dataRecoverySettings;
    }

    public void setDataRecoverySettings(DataRecoverySettings dataRecoverySettings) {
        this.dataRecoverySettings = dataRecoverySettings;
    }

    public String getLastLoginName() {
        return lastLoginName;
    }

    public void setMainDevice(boolean mainDevice) throws SystemError {
        configurationFile.openSettingsFile();
        configurationFile.addValue("main_device", isMainDevice ? "1" : "0");
        configurationFile.saveData();
        isMainDevice = mainDevice;
    }


    public boolean isMainDevice() {
        return isMainDevice;
    }

    public void setServer_ip(String server_ip) throws SystemError {
        configurationFile.openSettingsFile();
        configurationFile.addValue("serverip", server_ip);
        configurationFile.saveData();
        this.server_ip = server_ip;
    }

    public void setLastLoginName(String lastLoginName) throws SystemError {
        configurationFile.openSettingsFile();
        configurationFile.addValue("last_login_name", lastLoginName);
        configurationFile.saveData();
        this.lastLoginName = lastLoginName;
    }

    private void getCurrentSaleSettings() throws SQLException, DataNotFound {
        SystemConfiguration currentSaleConfig = SystemConfiguration.getSystemConfiguration(SystemAttribute.CURRENT_SALE);
        current_sale = SaleHistory.getSaleBy_id(Integer.parseInt(currentSaleConfig.getAttrib_value()));

    }


    public void loadAppSettings(SystemUser logged_in_user) throws SQLException, DataNotFound, IOException {
        this.logged_in_user = logged_in_user;
        getCurrentSaleSettings();
        this.creditExpirySettings = SystemConfiguration.get_CreditExpirySettings();
        update_expired_orders();
        if (isMainDevice() && dataRecoverySettings.getBackupPath() != null)
            checkForDatabaseUpdate();


    }

    public void setCreditExpirySettings(CreditExpirySettings creditExpirySettings) {
        this.creditExpirySettings = creditExpirySettings;
    }

    public void checkForDatabaseUpdate() throws SQLException, IOException {
        DateTime current_time = DBconnection.getInstance().getCurrentDatabaseDate();
        DateTime check_time = current_time.clone();
        check_time.add_to_date(dataRecoverySettings.years, dataRecoverySettings.months, dataRecoverySettings.days);
        if (current_time.compareTo(check_time) >= 0) {
            saveBackupData(dataRecoverySettings.getBackupPath());
        }

    }

    public void update_expired_orders() throws SQLException, DataNotFound {
        DBOperations dbOperations = new DBOperations();
        SystemConfiguration updateExpiredOrdersConf = SystemConfiguration.getSystemConfiguration(SystemAttribute.EXPIRED_ORDERS_LAST_CHECK);
        dbOperations.add(updateExpiredOrdersConf, DBStatement.Type.UPDATE);
        DateTime current_time = DBconnection.getInstance().getCurrentDatabaseDate();
        if (current_time == null)
            throw new DataNotFound("can't bring database date");
        DateTime last_check = DateTime.fromDate(updateExpiredOrdersConf.getAttrib_value());
        if (last_check.getLocalDate().isEqual(current_time.getLocalDate()))
            return;
        updateExpiredOrdersConf.setAttrib_value(current_time.get_Date());
        ArrayList<Order> expiredOrders = null;
        try {
            expiredOrders = Order.getExpiredOrders(current_time);
        } catch (DataNotFound d) {
            // update updateExpiredOrdersConf date
            dbOperations.execute();
            return;
        }

        HashMap<Integer, Customer> processedCustomers = new HashMap<>();

        for (Order order : expiredOrders) {
            Customer customer = processedCustomers.get(order.getCus_id());
            if (customer == null) {
                customer = Customer.getCustomer(Integer.toString(order.getCus_id()), Customer.QueryFilter.ID);
                processedCustomers.put(customer.getId(), customer);
            }
            float amount_to_transfer = Math.max(order.getTotalSystemCreditIn() - order.getTotalSystemCreditOut(), 0.0f);
            if (amount_to_transfer != 0) {

                amount_to_transfer = Math.min(amount_to_transfer, customer.getActive_credit());
                try {
                    customer.fromActiveToArchive(amount_to_transfer);
                    dbOperations.add(new CreditArchiveTransaction(customer, amount_to_transfer), DBStatement.Type.ADD);

                } catch (InvalidTransaction i) {
                    //TO DO throw invalid trans
                }

            }
            order.setOrder_archived(true);
            dbOperations.add(order, DBStatement.Type.UPDATE);
        }
        for (Customer cus : processedCustomers.values()) {
            dbOperations.add(cus, DBStatement.Type.UPDATE);
        }
        dbOperations.execute();


    }

    public static void saveBackupData(String backupPath) throws SQLException, IOException {
        // add directory path to database
        String sql = String.format("CREATE OR REPLACE DIRECTORY backup_dir AS '%s'", backupPath);
        Statement s = DBconnection.getInstance().getConnection().createStatement();
        s.execute(sql);
        s.close();
        DBconnection.getInstance().getConnection().commit();


        DateTime dateTime = new DateTime();
        String fileName = String.format("%s %s .dmp", dateTime.get_Date(), dateTime.getTime("HH_mm_ss"));

        //database saving cmd command in the passed directory and file name is current datetime
        String command = String.format("expdp fayedpharmacy/fayed203046 schemas=fayedpharmacy directory=backup_dir dumpfile='%s' ", fileName);
        LoadingWindow loadingWindow = new LoadingWindow("Saving database backup");
        loadingWindow.setOnTop();
        loadingWindow.showStage();
        loadingWindow.startProcess(() -> {
            ProcessBuilder process = new ProcessBuilder("cmd.exe", "/c", command);
            try {
                Process resultP = process.start();
                while (resultP.isAlive()) {
                    while (resultP.isAlive()) {
                        Thread.sleep(100);
                        if (resultP.isAlive() && loadingWindow.isProcessCanceled())
                            resultP.destroy();
                    }
                }
                int result = resultP.exitValue();
                if (!loadingWindow.isProcessCanceled())
                    Platform.runLater(loadingWindow::closeStage);
                new ProcessBuilder("cmd.exe", "/c", String.format("del %s\\export.log", backupPath)).start();
                if (result == 1)
                    throw new Exception();
                Platform.runLater(() -> {
                    new Alerts("تم حفظ النسخه الاحتياطية بنجاح فى " + backupPath, Alert.AlertType.INFORMATION);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    new Alerts("حدث خطئ فى حفظ  النسخه الاحتياطية", Alert.AlertType.ERROR);
                });
            }
        });


    }

    public static void loadBackupData(String filePath) throws SQLException, IOException {
        String[] pathList = filePath.split(Pattern.quote("\\"));
        StringBuilder directorPath = new StringBuilder();
        for (int i = 0; i < pathList.length - 1; i++) {

            directorPath.append(pathList[i]);
            if (i != pathList.length - 2)
                directorPath.append("\\");

        }
        String fileName = pathList[pathList.length - 1];
        String sql = String.format("CREATE OR REPLACE DIRECTORY backup_dir AS '%s'", directorPath.toString());
        Statement s = DBconnection.getInstance().getConnection().createStatement();
        s.execute(sql);
        s.close();
        DBconnection.getInstance().getConnection().commit();
        String command = String.format("impdp fayedpharmacy/fayed203046 TABLE_EXISTS_ACTION=REPLACE schemas=fayedpharmacy directory=backup_dir dumpfile='%s'", fileName);

        LoadingWindow loadingWindow = new LoadingWindow("Loading database backup");
        loadingWindow.setOnTop();
        loadingWindow.showStage();

        loadingWindow.startProcess(() -> {
            ProcessBuilder process = new ProcessBuilder("cmd.exe", "/c", command);
            try {
                Process resultP = process.start();
                while (resultP.isAlive()) {
                    Thread.sleep(100);
                    if (resultP.isAlive() && loadingWindow.isProcessCanceled())
                        resultP.destroy();
                }

                Platform.runLater(loadingWindow::closeStage);
                int result = resultP.exitValue();
                if (!loadingWindow.isProcessCanceled())
                    new ProcessBuilder("cmd.exe", "/c", String.format("del %s\\import.log", directorPath)).start();

                if (result == 1)
                    throw new Exception();
                Platform.runLater(() -> {
                    new Alerts("تم تحميل النسخه الاحتياطية بنجاح", Alert.AlertType.INFORMATION);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    new Alerts("حدث خطئ فى تحميل النسخه الاحتياطية", Alert.AlertType.ERROR);
                });
            }
        });


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

    public class DataRecoverySettings {
        String backupPath;
        int years, months, days;

        public DataRecoverySettings(String backupPath, int years, int months, int days) {
            this.backupPath = backupPath;
            this.years = years;
            this.months = months;
            this.days = days;
        }

        public DataRecoverySettings() throws SystemError {
            loadFromConfigFile();
        }

        public String getBackupPath() {
            return backupPath;
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

        public void loadFromConfigFile() throws SystemError {

            backupPath = configurationFile.getValue("backup_path");
            String backupEvery = configurationFile.getValue("backup_interval");
            if (backupEvery == null) return;
            String[] backupInterval = backupEvery.split(",");
            years = Integer.parseInt(backupInterval[0]);
            months = Integer.parseInt(backupInterval[1]);
            days = Integer.parseInt(backupInterval[2]);
        }

        public void saveDataToConfigFile() throws SystemError {
            configurationFile.openSettingsFile();
            configurationFile.addValue("backup_path", backupPath);
            configurationFile.addValue("backup_interval", String.format("%d,%d,%d", years, months, days));
            configurationFile.saveData();
        }

    }

    public static class CreditExpirySettings {
        private int years, months, days;

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
    }
}
