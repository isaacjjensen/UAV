package edu.und.seau.presentation.presenters;

import javax.inject.Inject;

import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.lib.UAV.objects.UAV;
import edu.und.seau.presentation.views.MainView;

public class MainScreenPresenter {

    FirebaseDatabaseInterface databaseInterface;
    MainView view;

    public UAV uav = null;

    @Inject
    public MainScreenPresenter(FirebaseDatabaseInterface firebaseDatabaseInterface)
    {
        databaseInterface = firebaseDatabaseInterface;
    }

    public void setView(MainView view){
        this.view = view;
    }

    public void onConnectClicked()
    {
        if(view != null)
        {
            databaseInterface.initializeUAVDBInstance(this::onUAVConfigured);

        }
    }

    private void onUAVConfigured(UAV uav)
    {
        if(uav != null)
        {
            uav.setName(view.getSelectedName());
            databaseInterface.updateUAVName(uav,aBoolean -> {
                if(aBoolean)
                {
                    this.uav = uav;
                    view.onConnected();
                }
            });
        }
    }

}
