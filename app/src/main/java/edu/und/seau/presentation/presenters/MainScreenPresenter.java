package edu.und.seau.presentation.presenters;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import edu.und.seau.common.SharedPreferenceKeys;
import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.lib.UAV.objects.UAV;
import edu.und.seau.presentation.views.MainView;

public class MainScreenPresenter {

    private FirebaseDatabaseInterface databaseInterface;
    private MainView view;
    private SharedPreferences mainActivitySharedPreferences;
    private String uavID;
    public UAV uav = null;


    @Inject
    public MainScreenPresenter(FirebaseDatabaseInterface firebaseDatabaseInterface)
    {
        databaseInterface = firebaseDatabaseInterface;
    }



    public void setView(MainView view){
        if(view != null){
            this.view = view;
            initializePreferences();
        }
    }

    private void initializePreferences(){
        if(view != null){
            mainActivitySharedPreferences = view.getSharedPreferences();
            LoadOrCreateUavID(mainActivitySharedPreferences);
        }
    }

    private void LoadOrCreateUavID(@NotNull SharedPreferences sharedPreferences) {
        String uavIDString = sharedPreferences.getString(SharedPreferenceKeys.KEY_UAV_ID,null);
        if(uavIDString == null){
            databaseInterface.CreateNewUAV(s -> {
                OnUAVCreated(sharedPreferences, s);
            });
        }
        else{
            databaseInterface.DoesUAVExist(uavIDString, aBoolean -> {
                CheckIfUAVIDExists(sharedPreferences, uavIDString, aBoolean);
            });
        }
    }

    private void CheckIfUAVIDExists(SharedPreferences sharedPreferences, String uavIDString, Boolean doesUAVIdExist) {
        if(doesUAVIdExist){
            OnUAVCreated(sharedPreferences, uavIDString);
        }
        else {
            databaseInterface.CreateNewUAV(s -> OnUAVCreated(sharedPreferences, s));
        }
    }

    private void OnUAVCreated(@NotNull SharedPreferences sharedPreferences, String uavID){
        if(view != null){
            sharedPreferences.edit().putString(SharedPreferenceKeys.KEY_UAV_ID,uavID).apply();
            view.setID(uavID);
        }
    }

    public void onConnectClicked()
    {
        if(view != null)
        {
            view.onConnected();
            //
        }
    }


}
