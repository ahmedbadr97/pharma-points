package database;

import database.entities.TablesOperations;
import main.Main;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class DBOperations {
    private LinkedHashMap<TablesOperations,DBStatement>  operations_queue;


    public static <T> DBStatement<T> get_DBStatement(TablesOperations<T> table,DBStatement.Type statement_type){
        switch (statement_type){
            case ADD:
                return table.addRow();
            case UPDATE:
                return table.update();
            default://Delete
                return table.DeleteRow();
        }
    }

    public DBOperations() {
        this.operations_queue = new LinkedHashMap<>();
    }
    public <T> void add(TablesOperations<T> table,DBStatement.Type statement_type){
        if (statement_type == DBStatement.Type.DELETE)
        {
            if (this.operations_queue.containsKey(table))
            {

                if (operations_queue.get(table).getStatement_type()== DBStatement.Type.ADD)
                    operations_queue.remove(table);
                else
                    operations_queue.put(table,get_DBStatement(table,statement_type));
            }
            else
                operations_queue.put(table,get_DBStatement(table,statement_type));
        }
        else
            operations_queue.put(table,get_DBStatement(table,statement_type));
    }
    public <T> boolean remove(TablesOperations<T> table,DBStatement.Type statement_type){
        return operations_queue.remove(table)!=null;
    }
    public void execute()throws SQLException{
        try {
            for(DBStatement statement:this.operations_queue.values()){
                statement.execute_statement();
            }
            Main.dBconnection.getConnection().commit();
            operations_queue.clear();

        }
        catch (SQLException sqlException)
        {
            Main.dBconnection.getConnection().rollback();
            throw sqlException;
        }
    }
    public void clear()
    {
        operations_queue.clear();
    }

}
