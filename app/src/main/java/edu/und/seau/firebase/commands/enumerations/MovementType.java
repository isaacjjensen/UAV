package edu.und.seau.firebase.commands.enumerations;

public enum MovementType {
    INVALID(-1),
    DISCRETE(0),
    GPS(1);

    private int value;

    MovementType(int value){
        this.value = value;
    }

    public static MovementType getMovementType(Integer movementTypeValue){
        MovementType type = INVALID;
        switch (movementTypeValue){
            case 0:
                type = DISCRETE;
                break;
            case 1:
                type = GPS;
                break;
            default:
                break;
        }
        return type;
    }

    public int getValue() {
        return value;
    }

    public static MovementType getMovementType(Object movementTypeValue){
        MovementType type = MovementType.INVALID;
        if(movementTypeValue instanceof Integer){
            type = getMovementType((Integer)movementTypeValue);
        }
        return type;
    }
}
