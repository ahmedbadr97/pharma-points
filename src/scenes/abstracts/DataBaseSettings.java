package scenes.abstracts;


import scenes.main.WindowAbstract;

import java.util.ArrayList;

public class DataBaseSettings extends WindowAbstract<scenes.controller.DataBaseSettings> {
    public interface OnConnectAction{
        public void action();
    }
    ArrayList<OnConnectAction> onConnectActions;
    public DataBaseSettings()
    {
        onConnectActions=new ArrayList<>();
        loadFxmlOnly("DataBaseSettings",460,250);
        getController().ini(this);
    }
    public void addOnConnectionAction(OnConnectAction action)
    {
        onConnectActions.add(action);
    }
    private void executeOnConnectActions()
    {
        for (OnConnectAction a:onConnectActions) {
            a.action();
        }

    }

}
