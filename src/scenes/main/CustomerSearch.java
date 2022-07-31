package scenes.main;

import javafx.stage.Modality;

public class CustomerSearch extends WindowAbstract<scenes.controller.CustomerSearch>{

    public interface OnCustomerSelection{
        void customerSelectionAction();
    }
    private OnCustomerSelection onCustomerSelection;
    public CustomerSearch(boolean byNameOnly) {
        load("CustomerSearch",600,600);
        getController().init(this);
        if(byNameOnly)
        {
            getStage().initModality(Modality.APPLICATION_MODAL);
            getController().getGetCustomerBar().getController().searchByNameOnly();
        }
    }


    public OnCustomerSelection getOnCustomerSelection() {
        return onCustomerSelection;
    }

    public void setOnCustomerSelection(OnCustomerSelection onCustomerSelection) {
        this.onCustomerSelection = onCustomerSelection;
    }

}
