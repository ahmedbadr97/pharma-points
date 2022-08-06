package scenes.main;

public class TransactionsLog extends WindowAbstract<scenes.controller.TransactionsLog>{
    public TransactionsLog() {
        load("TransactionsLog",780,720);
        getController().ini(this);
    }
}
