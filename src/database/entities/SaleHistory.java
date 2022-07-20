package database.entities;

import database.DBStatement;
import exceptions.DataNotFound;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleHistory implements TablesOperations<SaleHistory>{
    int sale_id;
    float money_to_credit;
    DateTime creation_time;

    public SaleHistory(int sale_id, float money_to_credit) {
       this(sale_id,money_to_credit,null);
    }
    public SaleHistory(int sale_id, float money_to_credit,DateTime creation_time) {
        this.sale_id = sale_id;
        this.money_to_credit = money_to_credit;
        this.creation_time = creation_time;
    }



    public int getSale_id() {
        return sale_id;
    }

    public float getMoney_to_credit() {
        return money_to_credit;
    }

    private static SaleHistory fetch_resultSet(ResultSet r) throws SQLException
    {
        String dateTimestamp=r.getString(3);
        DateTime creationDate=null;
        if (dateTimestamp!=null)
            creationDate=DateTime.from_timeStamp(dateTimestamp);
        return new SaleHistory(r.getInt(1),r.getFloat(2),creationDate);
    }
    @Override
    public DBStatement<SaleHistory> addRow() {
        String sql="INSERT INTO SALE_HISTORY (MONEY_TO_CREDIT) values (?)";
        DBStatement<SaleHistory> dbStatement=new DBStatement<SaleHistory>(sql,this,DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                this.getPreparedStatement().setFloat(1,getStatement_table().getMoney_to_credit());
            }

            @Override
            public void after_execution_action() throws SQLException {


            }
        };
        return dbStatement;
    }
    public static SaleHistory getSaleBy_id(int id)throws SQLException,DataNotFound
    {
        SaleHistory saleHistory=null;
        String sql_statement="SELECT *  FROM SALE_HISTORY WHERE SALE_ID= ?";
        PreparedStatement p= Main.dBconnection.getConnection().prepareStatement(sql_statement);
        p.setInt(1,id);
        ResultSet r=p.executeQuery();
        while (r.next())
            saleHistory=fetch_resultSet(r);
        if (saleHistory==null)
            throw new DataNotFound("no saleHistory found with this id "+ id);
        return saleHistory;
    }

    @Override
    public DBStatement<SaleHistory> DeleteRow() {
        return null;
    }

    @Override
    public DBStatement<SaleHistory> update() {
        return null;
    }
}
