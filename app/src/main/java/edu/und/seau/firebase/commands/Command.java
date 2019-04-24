package edu.und.seau.firebase.commands;

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
            if(documentSnapshot.contains(FirebaseConstants.KEY_COMMAND_NUMBER)){
                setCommandNumber(Objects.requireNonNull(documentSnapshot.getLong(FirebaseConstants.KEY_COMMAND_NUMBER)).intValue());
                dataCopy.remove(FirebaseConstants.KEY_COMMAND_NUMBER);
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
}
