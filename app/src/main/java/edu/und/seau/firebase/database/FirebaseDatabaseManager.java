package edu.und.seau.firebase.database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Key;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.inject.Inject;

import edu.und.seau.common.FirebaseConstants;
import edu.und.seau.firebase.modelmapper.UavDBMapper;
import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;


public class FirebaseDatabaseManager implements FirebaseDatabaseInterface {
    private static final String KEY_USER = FirebaseConstants.KEY_USER;
    private static final String KEY_ID = FirebaseConstants.KEY_ID;
    private static final String KEY_UAV  = FirebaseConstants.KEY_UAV;
    private static final String KEY_SERVERSETTINGS = FirebaseConstants.KEY_SERVERSETTINGS;
    private static final String KEY_NAME = FirebaseConstants.KEY_NAME;
    private static final String KEY_REQUESTS = FirebaseConstants.KEY_REQUESTS;

    private FirebaseFirestore database;

    @Inject
    public FirebaseDatabaseManager(FirebaseFirestore database){
        this.database = database;
    }


    public void isIdAvailable(String key, Consumer<Boolean> isAvailable){
        DocumentReference document = database
                .collection(KEY_UAV)
                .document(key);

        document.get().addOnCompleteListener(task -> {
            isAvailable.accept(task.getResult() == null);
        } );
    }
    
    public void initializeUAVDBInstance(String key,Consumer<UAV> onResult) {
        if(key != null)
        {
            database.collection(KEY_UAV).document(key).get().addOnCompleteListener(task ->{
                if(task.isSuccessful()){

                }
            });
        }
        else
        {
            onResult.accept(null);
        }
    }

    @Override
    public void initializeUAVDBInstance(Consumer<UAV> onResult) {

    }

    public void updateUAVName(UAV uav, Consumer<Boolean> onResult)
    {
        database.collection(KEY_UAV).get().addOnCompleteListener(task -> {
            Boolean isSuccessful = false;
            if(task.isSuccessful())
            {
                DocumentSnapshot snapshot = GetSnapshotFromID(Objects.requireNonNull(task.getResult()).getDocuments(),uav.getId());
                if(snapshot != null)
                {
                    database.collection(KEY_UAV).document(snapshot.getId()).set(UavDBMapper.getUavDBModel(uav));
                }
                else {
                    database.collection(KEY_UAV).document(uav.getId()).set(UavDBMapper.getUavDBModel(uav));
                }
                isSuccessful = true;
            }
            onResult.accept(isSuccessful);
        });
    }

    public void getUAVDBInstance(Consumer<UavDBModel> onResult)
    {
        //String UavID = SharedSettingsManager.GetSetting(SharedSettingsManager.KEY_UAVID);
        String UavID = "";
        if(UavID != null)
        {
            database.collection(KEY_UAV).document(UavID).get().addOnCompleteListener(command -> {
                if(command.isSuccessful())
                {
                    DocumentSnapshot snapshot = command.getResult();
                    String name = (String)snapshot.get(KEY_NAME);
                    UavDBModel model = new UavDBModel(UavID,name);
                    onResult.accept(model);
                }
                else
                {
                    onResult.accept(null);
                }
            });
        }
        else
        {
            onResult.accept(null);
        }
    }

    private DocumentSnapshot GetSnapshotFromID(List<DocumentSnapshot> snapshots, String id)
    {
        for(DocumentSnapshot object : snapshots)
        {
            if(id.equals(object.getId()))
            {
                return object;
            }
        }
        return null;
    }

    //TODO Fix New Request System
    /*
    public void notifyNewRequest(Consumer<String> onResult){
        String UavID = SharedSettingsManager.GetSetting(SharedSettingsManager.KEY_UAVID);
        database.collection(KEY_UAV).document(UavID).collection(KEY_REQUESTS).addSnapshotListener(command -> {
            onResult.accept("New Request");
        });
    }*/


    private Boolean ContainsID(List<DocumentSnapshot> snapshots, String id)
    {
        return GetSnapshotFromID(snapshots,id) != null;
    }

}
