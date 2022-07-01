package database.entities;
import database.DBStatement;
import exceptions.ConnectionError;
import exceptions.DataNotFound;

import java.sql.SQLException;

public interface TablesOperations<Table> {
    public DBStatement<Table> addRow();
    public DBStatement <Table> DeleteRow() ;
    public DBStatement <Table> update() ;

}
