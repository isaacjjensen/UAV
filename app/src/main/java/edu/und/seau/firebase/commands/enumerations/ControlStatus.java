package edu.und.seau.firebase.commands.enumerations;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum ControlStatus {
    NOT_CONTROLLED(0),
    ALREADY_CONTROLLED(1),
    CONTROL_REQUEST_SUCCESS(2),
    INVALID(-1);

    private int value;

    ControlStatus(int value){
        this.value = value;
    }

    public static ControlStatus getControlStatus(Integer controlStatusValue){
        ControlStatus status = INVALID;
        switch (controlStatusValue){
            case 0:
                status = NOT_CONTROLLED;
                break;
            case 1:
                status = ALREADY_CONTROLLED;
                break;
            case 2:
                status = CONTROL_REQUEST_SUCCESS;
                break;
            default:
                break;
        }
        return status;
    }

    public static ControlStatus getControlStatus(Object controlStatusValue){
        ControlStatus status = ControlStatus.INVALID;
        if(controlStatusValue instanceof Integer){
            status = getControlStatus((Integer)controlStatusValue);
        }
        return status;
    }

    public int getValue() {
        return value;
    }

    public static ControlStatus MapToControlStatus(@NotNull String mapKey, @NotNull Map<String, Object> controlStatusMap){
        return getControlStatus(controlStatusMap.getOrDefault(mapKey,-1));
    }
}