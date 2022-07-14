package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemUser implements TablesOperations<SystemUser>{
    int user_id;
    String username;
    String login_name;
    String login_password;
    int admin=0;

    public SystemUser(int user_id, String username, String login_name, String login_password, int admin) {
        this.user_id = user_id;
        this.username = username;
        this.login_name = login_name;
        this.login_password = login_password;
        this.admin = admin;
    }

    public SystemUser(String username, String login_name, String login_password, int admin) {
      this(0,username,login_name,login_password,admin);
    }
    public static SystemUser get_user(String login_name,String login_password)throws SQLException,DataNotFound
    {
        SystemUser systemUser=null;
        String sql_statement="SELECT *  FROM SYSTEM_USERS WHERE LOGIN_NAME=? and LOGIN_PASSWORD=? ";
        PreparedStatement p= Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setString(1,login_name);
        p.setString(2,login_password);
        ResultSet r=p.executeQuery();

        while (r.next())
            systemUser=fetch_resultSet(r);
        r.close();p.close();
        if (systemUser==null)
            throw new DataNotFound("Wrong username or password");
        return systemUser;
    }
    private static SystemUser fetch_resultSet(ResultSet r) throws SQLException
    {
        return new SystemUser(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getInt(5));
    }

    @Override
    public DBStatement<SystemUser> addRow() {
        return null;
    }

    @Override
    public DBStatement<SystemUser> DeleteRow() {
        return null;
    }

    @Override
    public DBStatement<SystemUser> update() {
        return null;
    }
}
