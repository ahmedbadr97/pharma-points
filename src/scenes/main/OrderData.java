package scenes.main;

import database.entities.Order;

public class OrderData extends WindowAbstract<scenes.controller.OrderData>{
    private final Order order;
    public OrderData(Order order) {
        this.order=order;
        load("OrderData",880,725);
        getController().ini(this);
    }
    public Order getOrder() {
        return order;
    }
}
