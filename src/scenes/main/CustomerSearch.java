package scenes.main;

public class CustomerSearch extends WindowAbstract<scenes.controller.CustomerSearch>{

    public CustomerSearch() {
        load("CustomerSearch",700,650);
        getController().init(this);
    }

    @Override
    public void CloseAction() {

    }
}
