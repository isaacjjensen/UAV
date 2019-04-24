package edu.und.seau.firebase.database;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;
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
    private static final String KEY_CONTROL_STATUS = FirebaseConstants.KEY_CONTROL_STATUS;

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
                    if(OnResult != null){
                        OnResult.accept(Objects.requireNonNull(command.getResult()).exists());
                    }
                });
    }

    public void CreateNewUAV(Consumer<String> OnResult){
        database.collection(KEY_UAV)
                .document()
                .get()
                .addOnCompleteListener(command -> {
                    String uavID = null;
                   if(command.isSuccessful()){
                       uavID = Objects.requireNonNull(command.getResult()).getId();
                       database.collection(KEY_UAV)
                               .document(uavID)
                               .set(new HashMap<String, Object>());

                   }
                   if(OnResult != null){
                       OnResult.accept(uavID);
                   }
                });
    }

    public void DeleteRequest(String uavID, String userID, Consumer<Boolean> OnResult){
        database.collection(KEY_UAV)
                .document(uavID)
                .collection(KEY_REQUESTS)
                .document(userID)
                .delete().addOnCompleteListener(command -> {
                    if(OnResult != null){
                        OnResult.accept(command.isSuccessful());
                    }
        });
    }

    public ListenerRegistration ListenForRequests(String uavID, Consumer<List<DocumentSnapshot>> OnResult){
        return database.collection(KEY_UAV)
                .document(uavID)
                .collection(KEY_REQUESTS)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if(OnResult != null){
                        if(queryDocumentSnapshots != null){
                            OnResult.accept(queryDocumentSnapshots.getDocuments());
                        }
                        else{
                            OnResult.accept(null);
                        }
                    }
                });
    }

    public void SendResponse(String uavID, String userID, Map<String, Object> ResponseData, Consumer<Boolean> OnResult){
        database.collection(KEY_UAV)
                .document(uavID)
                .collection(KEY_RESPONSES)
                .document(userID)
                .set(ResponseData)
                .addOnCompleteListener(command -> {
                    if(OnResult != null){
                        OnResult.accept(command.isSuccessful());
                    }
                });
    }


}
