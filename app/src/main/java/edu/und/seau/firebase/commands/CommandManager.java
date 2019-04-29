package edu.und.seau.firebase.commands;

import android.location.LocationManager;

import com.google.firebase.Timestamp;
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
import edu.und.seau.firebase.commands.enumerations.MovementType;
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

    public void SendResponse(String uavID, String userID, HashMap<String, Object> ResponseData, Consumer<Boolean> OnResult){
        ResponseData.put(FirebaseConstants.KEY_TIME_STAMP, Timestamp.now());
        databaseInterface.SendResponse(uavID, userID, ResponseData,OnResult);
    }

    public void SendCommand0Response(String uavID, String userID, String id, String modelString, Integer status, Consumer<Boolean> OnResult){
        HashMap<String, Object> command0Data = GenerateCommandDataResponseBase(0);
        command0Data.put(FirebaseConstants.KEY_ID, id);
        command0Data.put(FirebaseConstants.KEY_MODEL_STRING, modelString);
        command0Data.put(FirebaseConstants.KEY_STATUS, status);
        SendResponse(uavID, userID, command0Data, OnResult);
    }

    public void SendCommand1Response(String uavID, String userID, ControlStatus controlStatus, Consumer<Boolean> OnResult){
        HashMap<String, Object> command1Data = GenerateCommandDataResponseBase(1);
        command1Data.put(FirebaseConstants.KEY_CONTROL_STATUS, controlStatus.getValue());
        SendResponse(uavID, userID, command1Data,OnResult);
    }

    public void SendCommand2Response(String uavID, String userID, ControlStatus controlStatus, Consumer<Boolean> OnResult){
        HashMap<String, Object> command2Data = GenerateCommandDataResponseBase(2);
        command2Data.put(FirebaseConstants.KEY_CONTROL_STATUS, controlStatus.getValue());
        SendResponse(uavID,userID, command2Data, OnResult);
    }

    public void SendCommand1000Response(String uavID, String userID, MovementType movementType, Consumer<Boolean> OnResult){
        HashMap<String, Object> command1000Data = GenerateCommandDataResponseBase(1000);
        command1000Data.put(FirebaseConstants.KEY_MOVEMENT_TYPE,movementType.getValue());
        SendResponse(uavID,userID,command1000Data,OnResult);
    }

    public void SendCommand1001Response(String uavID, String commandID, Double roll, Double pitch, Double yaw, Double lift, Consumer<Boolean> OnResult) {
        HashMap<String, Object> command1001Data = GenerateCommandDataResponseBase(1001);
        command1001Data.put(FirebaseConstants.KEY_ROLL,roll);
        command1001Data.put(FirebaseConstants.KEY_PITCH,pitch);
        command1001Data.put(FirebaseConstants.KEY_YAW,yaw);
        command1001Data.put(FirebaseConstants.KEY_LIFT,lift);
        SendResponse(uavID,commandID,command1001Data,OnResult);
    }
    public void SendCommand1002Response(String uavID, String userID, int timeout, Consumer<Boolean> OnResult) {
        HashMap<String, Object> command1002Data = GenerateCommandDataResponseBase(1002);
        command1002Data.put(FirebaseConstants.KEY_TIMEOUT,timeout);
        SendResponse(uavID,userID,command1002Data,OnResult);
    }

    private HashMap<String, Object> GenerateCommandDataResponseBase(int commandNumber){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(FirebaseConstants.KEY_COMMAND_NUMBER, commandNumber);
        return hashMap;
    }

    public void RemoveRequest(String uavID, String userID, Consumer<Boolean> OnResult){
        databaseInterface.DeleteRequest(uavID,userID,OnResult);
    }



}
