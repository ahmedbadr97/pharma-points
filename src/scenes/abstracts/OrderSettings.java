package scenes.abstracts;


import scenes.main.WindowAbstract;

public class OrderSettings extends WindowAbstract<scenes.controller.OrderSettings> {
    public OrderSettings() {
        loadFxmlOnly("OrderSettings",530,400);
        getController().ini();


    }
}
