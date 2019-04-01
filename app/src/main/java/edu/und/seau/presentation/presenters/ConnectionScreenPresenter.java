package edu.und.seau.presentation.presenters;

import java.util.function.Consumer;

import javax.inject.Inject;

import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;
import edu.und.seau.presentation.views.ConnectionScreenView;

public class ConnectionScreenPresenter {
    FirebaseDatabaseInterface databaseInterface;
    ConnectionScreenView view;

    UAV uav = null;

    @Inject
    public ConnectionScreenPresenter(FirebaseDatabaseInterface databaseInterface)
    {
        databaseInterface.getUAVDBInstance(this::onUavLoaded);
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
        this.view = view;
    }

}
