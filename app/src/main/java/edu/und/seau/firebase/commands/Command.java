package edu.und.seau.firebase.commands;

import com.google.firebase.Timestamp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.und.seau.common.FirebaseConstants;
import edu.und.seau.firebase.database.FirebaseDatabaseManager;

public class Command {
    private Integer commandNumber;
    private String commandID;
    private Timestamp timeStamp;
    private Map<String, Object> commandData;

    public Command(){
        setCommandNumber(-1);
        setCommandID(null);
        setCommandData(null);
    }

    public Command(@NotNull DocumentSnapshot documentSnapshot){
        setCommandID(documentSnapshot.getId());
        if(documentSnapshot.getData() != null){
            HashMap<String, Object> dataCopy = new HashMap<>(documentSnapshot.getData());
            if(dataCopy.containsKey(FirebaseConstants.KEY_COMMAND_NUMBER)){
                setCommandNumber(Objects.requireNonNull(documentSnapshot.getLong(FirebaseConstants.KEY_COMMAND_NUMBER)).intValue());
                dataCopy.remove(FirebaseConstants.KEY_COMMAND_NUMBER);
            }
            if(dataCopy.containsKey(FirebaseConstants.KEY_TIME_STAMP)){
                setTimeStamp(Objects.requireNonNull(documentSnapshot.getTimestamp(FirebaseConstants.KEY_TIME_STAMP)));
                dataCopy.remove(FirebaseConstants.KEY_TIME_STAMP);
            }
            setCommandData(dataCopy);
        }
    }

    public int getCommandNumber(){
        return commandNumber;
    }

    public String getCommandID(){
        return commandID;
    }

    public Timestamp getTimeStamp() { return timeStamp; }

    public Map<String, Object> getCommandData(){
        return commandData;
    }

    public void setCommandNumber(int commandNumber){
        this.commandNumber = commandNumber;
    }

    public  void setCommandID(String commandID){
        this.commandID = commandID;
    }

    public void setCommandData(Map<String, Object> commandData){
        this.commandData = commandData;
    }

    public void setTimeStamp(Timestamp timeStamp){ this.timeStamp = timeStamp; }
}
