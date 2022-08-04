package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SystemUser implements TablesOperations<SystemUser>{
    private int user_id;
    private String username;
    private String login_name;
    private String login_password;
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
    public static ArrayList<SystemUser> getSystemUsers()throws SQLException,DataNotFound
    {
        ArrayList<SystemUser> systemUsers=new ArrayList<>();
        SystemUser systemUser=null;
        String sql_statement="SELECT *  FROM SYSTEM_USERS ";
        Statement s= Main.dBconnection.getConnection().createStatement();

        ResultSet r=s.executeQuery(sql_statement);

        while (r.next())
        {
            systemUser=fetch_resultSet(r);
            systemUsers.add(systemUser);
        }

        r.close();s.close();
        if (systemUsers.isEmpty())
            throw new DataNotFound("no system users found");
        return systemUsers;
    }
    private static SystemUser fetch_resultSet(ResultSet r) throws SQLException
    {
        return new SystemUser(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getInt(5));
    }

    public int getUser_id() {
        return user_id;
    }

    public String getLogin_name() {
        return login_name;
    }

    public String getLogin_password() {
        return login_password;
    }

    public int getAdmin() {
        return admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public void setLogin_password(String login_password) {
        this.login_password = login_password;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    @Override
        public DBStatement<SystemUser> addRow() {
            String sql="INSERT INTO SYSTEM_USERS  values (?,?,?,?,?)";
            DBStatement<SystemUser> dbStatement=new DBStatement<SystemUser>(sql,this,DBStatement.Type.ADD) {
                @Override
                public void statement_initialization() throws SQLException {
                    Statement s= Main.dBconnection.getConnection().createStatement();
                    ResultSet r=s.executeQuery(" SELECT SYSTEM_USERS_SEQ.NEXTVAL from DUAL");
                    while (r.next())
                        this.getStatement_table().setUser_id(r.getInt(1));
                    r.close();s.close();
                    this.getPreparedStatement().setFloat(1,getStatement_table().getUser_id());
                    this.getPreparedStatement().setString(2,getStatement_table().getUsername());
                    this.getPreparedStatement().setString(3,getStatement_table().getLogin_name());
                    this.getPreparedStatement().setString(4,getStatement_table().getLogin_password());
                    this.getPreparedStatement().setInt(5,getStatement_table().getAdmin());
                }

                @Override
                public void after_execution_action() throws SQLException {


                }
            };
            return dbStatement;
        }

    @Override
    public DBStatement<SystemUser> DeleteRow() {
        String sql="DELETE FROM SYSTEM_USERS where USER_ID=? ";
        DBStatement<SystemUser> dbStatement=new DBStatement<SystemUser>(sql,this,DBStatement.Type.DELETE) {
            @Override
            public void statement_initialization() throws SQLException {

                this.getPreparedStatement().setFloat(1,getStatement_table().getUser_id());
            }

            @Override
            public void after_execution_action() throws SQLException {


            }
        };
        return dbStatement;
    }

    @Override
    public DBStatement<SystemUser> update() {
        String sql="UPDATE SYSTEM_USERS SET USERNAME=? , LOGIN_NAME=? , LOGIN_PASSWORD=? ,ADMIN=?  where USER_ID=? ";
        DBStatement<SystemUser> dbStatement=new DBStatement<SystemUser>(sql,this,DBStatement.Type.UPDATE) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setString(1,getStatement_table().getUsername());
                this.getPreparedStatement().setString(2,getStatement_table().getLogin_name());
                this.getPreparedStatement().setString(3,getStatement_table().getLogin_password());
                this.getPreparedStatement().setInt(4,getStatement_table().getAdmin());
                this.getPreparedStatement().setFloat(5,getStatement_table().getUser_id());
            }

            @Override
            public void after_execution_action() throws SQLException {


            }
        };
        return dbStatement;
    }
}
