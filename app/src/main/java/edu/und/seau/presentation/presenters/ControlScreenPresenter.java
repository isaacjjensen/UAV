package edu.und.seau.presentation.presenters;

import android.os.Bundle;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import edu.und.seau.common.FirebaseConstants;
import edu.und.seau.firebase.commands.Command;
import edu.und.seau.firebase.commands.CommandManager;
import edu.und.seau.firebase.commands.enumerations.ControlStatus;
import edu.und.seau.firebase.commands.enumerations.MovementType;
import edu.und.seau.firebase.database.FirebaseDatabaseInterface;
import edu.und.seau.presentation.views.ControlScreenView;

import static edu.und.seau.common.FirebaseConstants.MaxPublicCommandNumber;
import static edu.und.seau.ui.connect_screen.KEY_PASSED_UAV_ID;

public class ControlScreenPresenter {

    private Bundle extraData;
    private ControlScreenView view;
    private String uavID = null;
    private String userID = null;
    private FirebaseDatabaseInterface firebaseDatabaseInterface;
    private CommandManager commandManager;
    private List<Command> commandList;
    private ListenerRegistration listenerRegistration = null;
    private static Boolean uavIsRunning = false;
    private static Date lastMessage;
    private Thread executionThread;

    private Double roll = 0.0;
    private Double pitch = 0.0;
    private Double yaw = 0.0;
    private Double lift = 0.0;

    public ControlScreenPresenter(FirebaseDatabaseInterface firebaseDatabaseInterface, CommandManager commandManager){
        this.firebaseDatabaseInterface = firebaseDatabaseInterface;
        this.commandManager = commandManager;
    }

    public void setView(ControlScreenView view){
        this.view = view;
        if(view != null){
            handleExtraData(view.GetExtraData());
            getUavThread().start();
        }
    }

    private void handleExtraData(Bundle extraData) {
        if(view != null){
            if(extraData != null){
                this.extraData = extraData;
                if(extraData.containsKey(KEY_PASSED_UAV_ID)){
                    uavID = Objects.requireNonNull(extraData.getCharSequence(KEY_PASSED_UAV_ID)).toString();
                }
                if(extraData.containsKey(FirebaseConstants.KEY_USERNAME)){
                    userID = Objects.requireNonNull(extraData.getCharSequence(FirebaseConstants.KEY_USERNAME)).toString();
                }
            }
        }
    }

    private void SetupRequestListener(){
        if(commandManager != null && uavID != null ){
            listenerRegistration = commandManager.ListenForRequests(uavID, this::UpdateCommandList);
        }
    }

    private void UpdateCommandList(List<Command> commands){
        commands.sort((o1, o2) -> o2.getTimeStamp().compareTo(o1.getTimeStamp()));
        this.commandList = commands;
    }

    public Thread getUavThread(){
        return new Thread(){
            @Override
            public void run() {
                uavIsRunning = true;
                lastMessage = new Date();
                SetupRequestListener();
                while(uavIsRunning){

                    Command command = getNextCommand();
                    ExecuteCommand(command);
                }
                listenerRegistration.remove();
            }
        };
    }

    private Command getNextCommand(){
        Command commandToReturn = null;
        if(commandList != null){
            if(!commandList.isEmpty()){
                commandToReturn = commandList.get(0);
                for (Command command :
                        commandList) {
                    if(command.getCommandID() == uavID){
                        commandToReturn = command;
                        break;
                    }
                }
            }
        }
        if(commandToReturn != null){
            commandList.remove(commandToReturn);
        }
        return commandToReturn;
    }

    private void Exit(){

    }

    private void ExecuteCommand(Command commandToExecute){
        if(commandManager != null && commandToExecute != null){
            if(commandToExecute.getCommandNumber() > MaxPublicCommandNumber){
                if(!commandToExecute.getCommandID().equals(uavID)){
                    commandManager.RemoveRequest(uavID,commandToExecute.getCommandID(),null);
                    return;
                }
            }

            boolean isCurrentUser = Objects.equals(commandToExecute.getCommandID(),userID);
            ControlStatus controlStatus;

            switch (commandToExecute.getCommandNumber()){
                case 0 :
                    commandManager.SendCommand0Response(uavID,commandToExecute.getCommandID(),uavID,"MODEL0",0,null);
                    break;
                case 1 :
                    commandManager.SendCommand1Response(uavID,commandToExecute.getCommandID(), ControlStatus.ALREADY_CONTROLLED,null);
                    break;
                case 2 :
                    controlStatus = ControlStatus.ALREADY_CONTROLLED;
                    if(isCurrentUser){
                        controlStatus = ControlStatus.CONTROL_REQUEST_SUCCESS;
                    }
                    commandManager.SendCommand2Response(uavID,commandToExecute.getCommandID(),controlStatus,null);
                    break;
                case 3 :
                    controlStatus = ControlStatus.INVALID;
                    if(Objects.equals(commandToExecute.getCommandID(), userID)){
                        controlStatus = ControlStatus.CONTROL_REQUEST_SUCCESS;
                    }
                    commandManager.SendCommand2Response(uavID,commandToExecute.getCommandID(), controlStatus,null);
                    if(isCurrentUser){
                        Exit();
                    }
                    break;
                case 1000 :
                    commandManager.SendCommand1000Response(uavID,commandToExecute.getCommandID(), MovementType.DISCRETE,null);
                    break;
                case 1001 :
                    commandManager.SendCommand1001Response(uavID,commandToExecute.getCommandID(),roll,pitch,yaw,lift,null);
                    break;
                case 1002 :
                    commandManager.SendCommand1002Response(uavID,commandToExecute.getCommandID(),60000,null);
                    break;
                case  10000 :
                    break;
                case 10001 :
                    break;
                case 10002 :
                    break;
                case 100000 :
                    break;
                default:
                    break;

            }
        }

    }

}
