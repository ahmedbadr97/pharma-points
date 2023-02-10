package database.entities;

import database.DBStatement;
import database.DBconnection;
import exceptions.DataNotFound;
import main.Main;
import utils.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SaleHistory implements TablesOperations<SaleHistory>{
    int sale_id;
    float money_to_credit;
    DateTime creation_time;

    public SaleHistory( float money_to_credit) {
       this(0,money_to_credit,new DateTime());
    }
    public SaleHistory(int sale_id, float money_to_credit,DateTime creation_time) {
        this.sale_id = sale_id;
        this.money_to_credit = money_to_credit;
        this.creation_time = creation_time;
    }




    public float getCreditToMoney() {
        return 1.0f/money_to_credit;
    }
    public int getSale_id() {
        return sale_id;
    }

    public DateTime getCreation_time() {
        return creation_time;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
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
        String sql="INSERT INTO SALE_HISTORY (SALE_ID,MONEY_TO_CREDIT) values (?,?)";
        DBStatement<SaleHistory> dbStatement=new DBStatement<SaleHistory>(sql,this,DBStatement.Type.ADD) {
            @Override
            public void statement_initialization() throws SQLException {
                Statement s= DBconnection.getInstance().getConnection().createStatement();
                ResultSet r=s.executeQuery(" SELECT SALE_HISTORY_SEQ.NEXTVAL from DUAL");
                while (r.next())
                    this.getStatement_table().setSale_id(r.getInt(1));
                r.close();s.close();
                this.getPreparedStatement().setFloat(1,getStatement_table().getSale_id());
                this.getPreparedStatement().setFloat(2,getStatement_table().getMoney_to_credit());
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
        PreparedStatement p= DBconnection.getInstance().getConnection().prepareStatement(sql_statement);
        p.setInt(1,id);
        ResultSet r=p.executeQuery();

        while (r.next())
            saleHistory=fetch_resultSet(r);
        if (saleHistory==null)
            throw new DataNotFound("no saleHistory found with this id "+ id);
        p.close();r.close();
        return saleHistory;
    }
    public static ArrayList<SaleHistory> getSaleHistory() throws SQLException , DataNotFound{
        ArrayList<SaleHistory>saleHistoryList=new ArrayList<>();

        SaleHistory saleHistory=null;
        String sql_statement="SELECT *  FROM SALE_HISTORY ";
        Statement s= DBconnection.getInstance().getConnection().createStatement();
        ResultSet r=s.executeQuery(sql_statement);
        while (r.next())
        {
            saleHistory=fetch_resultSet(r);
            saleHistoryList.add(saleHistory);
        }
        s.close();r.close();
        if (saleHistoryList.isEmpty())
            throw new DataNotFound("no saleHistory ");
        return saleHistoryList;
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
