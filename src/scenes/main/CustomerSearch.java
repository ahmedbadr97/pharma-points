package scenes.main;

public class CustomerSearch extends WindowAbstract<scenes.controller.CustomerSearch>{

    public interface OnCustomerSelection{
        void customerSelectionAction();
    }
    private OnCustomerSelection onCustomerSelection;
    public CustomerSearch() {
        load("CustomerSearch",700,650);
        getController().init(this);
    }

    public OnCustomerSelection getOnCustomerSelection() {
        return onCustomerSelection;
    }

    public void setOnCustomerSelection(OnCustomerSelection onCustomerSelection) {
        this.onCustomerSelection = onCustomerSelection;
    }

    @Override
    public void closeAction() {

    }
}
