package edu.und.seau.firebase.database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Key;
import java.util.HashMap;
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
    private static final String KEY_RESPONSES = FirebaseConstants.KEY_RESPONSES;

    private FirebaseFirestore database;

    @Inject
    public FirebaseDatabaseManager(FirebaseFirestore database){
        this.database = database;
    }

    public void DoesUAVExist(String uavID, Consumer<Boolean> OnResult){
        database.collection(KEY_UAV)
                .document(uavID)
                .get()
                .addOnCompleteListener(command -> {
                   if(Objects.requireNonNull(command.getResult()).exists()){
                       OnResult.accept(Boolean.TRUE);
                   }
                   else {
                       OnResult.accept(Boolean.FALSE);
                   }
                });
    }

    public void CreateNewUAV(Consumer<String> OnResult){
        database.
                collection(KEY_UAV)
                .document()
                .get()
                .addOnCompleteListener(command -> {
                   if(command.isSuccessful()){
                       String uavID = Objects.requireNonNull(command.getResult()).getId();
                       OnResult.accept(uavID);
                   }
                   else {
                       OnResult.accept(null);
                   }
                });
    }

}
