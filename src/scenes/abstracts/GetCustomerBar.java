package scenes.abstracts;

import scenes.main.WindowAbstract;

public class GetCustomerBar extends WindowAbstract<scenes.controller.GetCustomerBar> {
    public static interface OnSearchAction{
        void searchAction();
    }
    private OnSearchAction searchWithNameAction;
    private OnSearchAction searchWithIdAction;

    public GetCustomerBar() {
        loadFxmlOnly("GetCustomerBar",460,45);
        getController().ini(this);
        searchWithNameAction =null;
    }

    public void setSearchWithIdAction(OnSearchAction searchWithIdAction) {
        this.searchWithIdAction = searchWithIdAction;
    }

    public OnSearchAction getSearchWithIdAction() {
        return searchWithIdAction;
    }

    public OnSearchAction getSearchWithNameAction() {
        return searchWithNameAction;
    }

    public void setSearchWithNameAction(OnSearchAction searchWithNameAction) {
        this.searchWithNameAction = searchWithNameAction;
    }



}
