package edu.und.seau.presentation.presenters;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import edu.und.seau.common.SharedPreferenceKeys;
import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;
import edu.und.seau.presentation.views.ConnectionScreenView;

public class ConnectionScreenPresenter {
    private FirebaseDatabaseInterface databaseInterface;
    private ConnectionScreenView view;
    private SharedPreferences connectionScreenPreferences;

    UAV uav = null;

    @Inject
    public ConnectionScreenPresenter(FirebaseDatabaseInterface databaseInterface)
    {
        this.databaseInterface = databaseInterface;
    }



    private void onUavLoaded(UavDBModel model)
    {
        if((model != null) && (view != null))
        {
            view.setUavID(model.getId());
            view.setUavName(model.getName());
        }
    }

    public void setView(ConnectionScreenView view)
    {
        if(view != null) {
            this.view = view;
            connectionScreenPreferences = view.getSharedPreferences();
        }
    }


}
