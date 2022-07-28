package scenes.main;

public class NewOrder extends WindowAbstract<scenes.controller.NewOrder>{
    public NewOrder() {
        load("NewOrder",880,700);
        getController().ini();
    }

    @Override
    public void closeAction() {

    }
}
