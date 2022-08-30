package database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.entities.TablesOperations;
import main.Main;

public abstract class DBStatement<Table> {

    public  enum Type{
        ADD,UPDATE,DELETE
    }
    private PreparedStatement preparedStatement;
    private Table statement_table;
    private String sql_statement;
    private DBStatement.Type statement_type;

    public DBStatement(String sql_statement,Table statement_table, DBStatement.Type statement_type) {
        this.statement_table = statement_table;
        this.sql_statement=sql_statement;
        this.statement_type=statement_type;

    }

    public Type getStatement_type() {
        return statement_type;
    }

    public void execute_statement()throws SQLException {
        this.preparedStatement= DBconnection.getInstance().getConnection().prepareStatement(sql_statement);
        this.statement_initialization();
        this.preparedStatement.execute();
        this.after_execution_action();
    }
    public abstract void statement_initialization() throws SQLException;
    public abstract void after_execution_action() throws SQLException;

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public Table getStatement_table() {
        return statement_table;
    }

    @Override
    public boolean equals(Object obj) {
        return this.statement_table.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.statement_table.hashCode();
    }
}
