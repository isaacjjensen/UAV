package edu.und.seau.presentation.presenters;

import android.content.SharedPreferences;

import javax.inject.Inject;

import edu.und.seau.common.SharedResourceManager;
import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.lib.UAV.objects.UAV;
import edu.und.seau.presentation.views.MainView;

public class MainScreenPresenter {

    private FirebaseDatabaseInterface databaseInterface;
    private MainView view;

    public UAV uav = null;

    @Inject
    public MainScreenPresenter(FirebaseDatabaseInterface firebaseDatabaseInterface)
    {
        databaseInterface = firebaseDatabaseInterface;
        initializePreferences();

    }

    private void initializePreferences(){
        SharedPreferences preferences = SharedResourceManager.getPreferences();
        if(preferences != null)
        {
            String uavName = preferences.getString(SharedResourceManager.KEY_UAV_ID,"");
            String uavId = preferences.getString(SharedResourceManager.KEY_UAV_NAME,null);
            if(uavId == null)
            {
                uav = new UAV();
                uav.generateNewID();
                uav.setName(uavName);
            }
        }

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
