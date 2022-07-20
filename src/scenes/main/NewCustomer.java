package scenes.main;

public class NewCustomer extends WindowAbstract<scenes.controller.NewCustomer>{
    public NewCustomer() {
        load("NewCustomer",700,300);
        getController().ini(this);

    }

    @Override
    public void CloseAction() {

    }
}
