package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.AppSettings.CreditExpirySettings;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SystemConfiguration implements TablesOperations<SystemConfiguration> {
    public enum SystemAttribute {

        CURRENT_SALE("اعدادات  تحويل النقاط"),CREDIT_EXPIRY_SETTINGS("اعدادات مده انتهاء الفاتوره"),EXPIRED_ORDERS_LAST_CHECK("اعدادات حفظ الفواتير المنتهيه");
        String description;

        SystemAttribute(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private int attrib_id;
    private SystemAttribute attrib_name;
    private String attrib_value;
    public interface CustomGetAttribValue{
        public String getAttrib_value();
    }
    CustomGetAttribValue customGetAttribValue;

    public SystemConfiguration(int attrib_id, SystemAttribute attrib_name, String attrib_value) {
        this.attrib_id = attrib_id;
        this.attrib_name = attrib_name;
        this.attrib_value = attrib_value;
    }
    public SystemConfiguration(SystemAttribute attrib_name,String attrib_value)
    {
        this.attrib_name=attrib_name;
        this.attrib_value=attrib_value;
    }
    public SystemConfiguration(SystemAttribute attrib_name,CustomGetAttribValue customGetAttribValue)
    {
        this.attrib_name=attrib_name;
        this.customGetAttribValue=customGetAttribValue;
    }
    public static SystemConfiguration fromCreditExpirySettings(CreditExpirySettings creditExpirySettings)
    {
        SystemAttribute attribute=SystemAttribute.CREDIT_EXPIRY_SETTINGS;
        String attribute_value=String.format("%d,%d,%d",creditExpirySettings.getYears(),creditExpirySettings.getMonths(),creditExpirySettings.getDays());
        return new SystemConfiguration(attribute,attribute_value);
    }


    public int getAttrib_id() {
        return attrib_id;
    }

    public void setAttrib_id(int attrib_id) {
        this.attrib_id = attrib_id;
    }

    public SystemAttribute getAttrib_name() {
        return attrib_name;
    }

    public void setAttrib_name(SystemAttribute attrib_name) {
        this.attrib_name = attrib_name;
    }

    public String getAttrib_value() {
        return attrib_value;
    }

    public void setAttrib_value(String attrib_value) {
        this.attrib_value = attrib_value;
    }


    @Override
    public DBStatement<SystemConfiguration> addRow() {
        return null;
    }

    @Override
    public DBStatement<SystemConfiguration> DeleteRow() {
        return null;
    }

    @Override
    public DBStatement<SystemConfiguration> update() {
        String sql = "UPDATE SYSTEM_CONFIGURATION set attrib_value=? where ATTRIB_NAME=?";
        DBStatement<SystemConfiguration> dbStatement = new DBStatement<SystemConfiguration>(sql, this, DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                if(customGetAttribValue!=null)
                    this.getPreparedStatement().setString(1, customGetAttribValue.getAttrib_value());
                else
                    this.getPreparedStatement().setString(1, getAttrib_value());
                this.getPreparedStatement().setString(2, getAttrib_name().name());
            }
            @Override
            public void after_execution_action() throws SQLException {


            }
        };
        return dbStatement;
    }

    private static SystemConfiguration fetch_resultSet(ResultSet r) throws SQLException {
        return new SystemConfiguration(r.getInt(1), SystemAttribute.valueOf( r.getString(2)), r.getString(3));
    }
    public static void updateCurrentSale(SaleHistory saleHistory)throws SQLException
    {
        String sql_statement = "UPDATE  SYSTEM_CONFIGURATION ATTRIB_VALUE =? SET WHERE ATTRIB_NAME='CURRENT_SALE'";
        PreparedStatement p = Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setString(1,Integer.toString(saleHistory.getSale_id()));
         p.execute();
        p.close();


    }
    public static SystemConfiguration getSystemConfiguration(SystemAttribute systemAttribute)throws SQLException ,DataNotFound
    {

        String sql_statement = String.format("SELECT *  FROM SYSTEM_CONFIGURATION WHERE ATTRIB_NAME='%s'",systemAttribute.name());
        Statement s = Main.dBconnection.getConnection().createStatement();
        ResultSet r = s.executeQuery(sql_statement);
        SystemConfiguration sale_history_config = null;
        while (r.next())
            sale_history_config = fetch_resultSet(r);
        if(sale_history_config==null)
            throw new DataNotFound("خطء في ايجاد "+systemAttribute);
        return sale_history_config;
    }

    public static CreditExpirySettings get_CreditExpirySettings() throws SQLException {
        String sql_statement = "SELECT *  FROM SYSTEM_CONFIGURATION WHERE ATTRIB_NAME='CREDIT_EXPIRY_SETTINGS'";
        Statement p = Main.dBconnection.getConnection().createStatement();
        ResultSet r = p.executeQuery(sql_statement);
        CreditExpirySettings creditExpirySettings = null;
        while (r.next())
            creditExpirySettings = fetch_credit_resultSet(r);
        r.close();
        p.close();
        return creditExpirySettings;
    }

    private static CreditExpirySettings fetch_credit_resultSet(ResultSet r) throws SQLException {
        String s=r.getString(3);
        String data[] =s.split(",");
        int years = Integer.parseInt(data[0]);
        int months = Integer.parseInt(data[1]);
        int days = Integer.parseInt(data[2]);

        return new CreditExpirySettings(years, months, days);
    }
}
