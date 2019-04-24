package edu.und.seau.firebase.commands;

import android.location.LocationManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import edu.und.seau.common.FirebaseConstants;
import edu.und.seau.firebase.commands.enumerations.ControlStatus;
import edu.und.seau.firebase.database.FirebaseDatabaseInterface;

public class CommandManager {
    private FirebaseDatabaseInterface databaseInterface;

    @Inject
    public CommandManager(FirebaseDatabaseInterface firebaseDatabaseInterface){
        databaseInterface = firebaseDatabaseInterface;
    }

    public ListenerRegistration ListenForRequests(String uavID, Consumer<List<Command>> OnResult){
        return databaseInterface.ListenForRequests(uavID, documentSnapshots -> {
            ArrayList<Command> commandList = new ArrayList<>();
            if(documentSnapshots != null){
                for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                    commandList.add(new Command(documentSnapshot));
                }
            }
            OnResult.accept(commandList);
        });
    }

    public void SendResponse(String uavID, String userID, Map<String, Object> ResponseData, Consumer<Boolean> OnResult){
        databaseInterface.SendResponse(uavID, userID, ResponseData,OnResult);
    }

    public void SendCommand2Response(String uavID, String userID, ControlStatus controlStatus, Consumer<Boolean> OnResult){
        HashMap<String, Object> command2Data = new HashMap<>();
        command2Data.put(FirebaseConstants.KEY_CONTROL_STATUS, controlStatus.getValue());
        SendResponse(uavID,userID, command2Data, OnResult);
    }


}
