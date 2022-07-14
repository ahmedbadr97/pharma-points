package database.entities;

import database.DBStatement;
import main.Main;
import utils.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Order implements TablesOperations<Order>{
    private int order_id,cus_id;
    private DateTime order_time;
    private int order_type;
    private float total_points,total_money;
    private String notes;
    private Customer customer;
    private ArrayList<OrderTransaction> orderTransactions;

    private Order(int order_id, int cus_id, DateTime order_time, int order_type, float total_points, float total_money, String notes) {
        this.order_id = order_id;
        this.cus_id = cus_id;
        this.order_time = order_time;
        this.order_type = order_type;
        this.total_points = total_points;
        this.total_money = total_money;
        this.notes = notes;
    }
    public Order(Customer customer,int order_type,float total_points,float total_money,String notes){
        this(0,customer.getId(),null,order_type,0,0,notes);

    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getCus_id() {
        return cus_id;
    }

    public void setCus_id(int cus_id) {
        this.cus_id = cus_id;
    }

    public DateTime getOrder_time() {
        return order_time;
    }

    public void setOrder_time(DateTime order_time) {
        this.order_time = order_time;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public float getTotal_points() {
        return total_points;
    }

    public void setTotal_money(float total_money) {
        this.total_money = total_money;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public DBStatement<Order> addRow() {
        return null;

    }

    @Override
    public DBStatement<Order> DeleteRow() {
        return null;
    }

    @Override
    public DBStatement<Order> update() {
        return null;
    }
}
