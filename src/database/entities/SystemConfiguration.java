package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.AppSettings;
import main.AppSettings.CreditExpirySettings;
import main.Main;
import utils.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SystemConfiguration implements TablesOperations<SystemConfiguration> {

    private int attrib_id;
    private String attrib_name;
    private String attrib_value;

    public SystemConfiguration(int attrib_id, String attrib_name, String attrib_value) {
        this.attrib_id = attrib_id;
        this.attrib_name = attrib_name;
        this.attrib_value = attrib_value;
    }

    public int getAttrib_id() {
        return attrib_id;
    }

    public void setAttrib_id(int attrib_id) {
        this.attrib_id = attrib_id;
    }

    public String getAttrib_name() {
        return attrib_name;
    }

    public void setAttrib_name(String attrib_name) {
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
        String sql = "UPDATE SYSTEM_CONFIGURATION set attrib_value=? where attrib_id=?";
        DBStatement<SystemConfiguration> dbStatement = new DBStatement<SystemConfiguration>(sql, this, DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setString(1, getAttrib_value());
                this.getPreparedStatement().setInt(2, getAttrib_id());
            }

            @Override
            public void after_execution_action() throws SQLException {

            }
        };
        return dbStatement;
    }

    private static SystemConfiguration fetch_resultSet(ResultSet r) throws SQLException {
        return new SystemConfiguration(r.getInt(1), r.getString(2), r.getString(3));
    }

    public static SaleHistory getCurrentSale() throws SQLException, DataNotFound {
        String sql_statement = "SELECT *  FROM SYSTEM_CONFIGURATION WHERE ATTRIB_VALUE=CURRENT_SALE";
        Statement s = Main.dBconnection.getConnection().createStatement();
        ResultSet r = s.executeQuery(sql_statement);
        SaleHistory current_sale_history = null;
        SystemConfiguration sale_history_config = null;
        while (r.next())
            sale_history_config = fetch_resultSet(r);
        if (sale_history_config != null)
            current_sale_history = SaleHistory.getSaleBy_id(Integer.parseInt(sale_history_config.getAttrib_value()));
        return current_sale_history;
    }

    public static CreditExpirySettings get_CreditExpirySettings() throws SQLException {
        String sql_statement = "SELECT *  FROM SYSTEM_CONFIGURATION WHERE ATTRIB_NAME=CREDIT_EXPIRY_SETTINGS";
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
        String data[] = r.getString(1).split(",");
        int years = Integer.parseInt(data[0]);
        int months = Integer.parseInt(data[1]);
        int days = Integer.parseInt(data[2]);

        return new CreditExpirySettings(years, months, days);
    }
}
