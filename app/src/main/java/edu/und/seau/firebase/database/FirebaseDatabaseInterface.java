package edu.und.seau.firebase.database;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import edu.und.seau.firebase.models.uav.UavDBModel;
import edu.und.seau.lib.UAV.objects.UAV;

public interface FirebaseDatabaseInterface {

    void DoesUAVExist(String uavID, Consumer<Boolean> OnResult);
    void CreateNewUAV(Consumer<String> OnResult);
    void DeleteRequest(String uavID, String userID, Consumer<Boolean> OnResult);
    ListenerRegistration ListenForRequests(String uavID, Consumer<List<DocumentSnapshot>> OnResult);
    void SendResponse(String uavID, String userID, Map<String, Object> ResponseData, Consumer<Boolean> OnResult);
}
