package database.entities;
import database.DBStatement;

public interface TablesOperations<Table> {
    public DBStatement<Table> addRow();
    public DBStatement <Table> DeleteRow() ;
    public DBStatement <Table> update() ;

}
