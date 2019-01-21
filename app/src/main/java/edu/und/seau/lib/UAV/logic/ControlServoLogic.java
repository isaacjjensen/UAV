package edu.und.seau.lib.UAV.logic;

import edu.und.seau.lib.UAV.objects.ControlServo;

public class ControlServoLogic {

    public static void CalculatePIDYaw(ControlServo Servo, float YawError) {
        byte i;
        float PIDValue;
        float yawError = 0.0f;
        int yawCurrenti;

        Servo.setYaw_ErrorIndex(Servo.getYaw_Current_i(),YawError);

        for (float currentError:Servo.getYaw_Error())
        {
            yawError += Math.round(currentError);
        }
        Servo.setYaw_Accumulator(yawError);

        yawCurrenti = Servo.getYaw_Current_i();
        Servo.setYaw_Delta(Servo.getYaw_Error_Index(yawCurrenti)-Servo.getYaw_Error_Index(yawCurrenti-1));

        PIDValue = (float) ((Servo.getYaw_Error_Index(yawCurrenti) * Servo.getYaw_PTerm()) + (Servo.getYaw_ITerm() * Servo.getYaw_Accumulator()) + (Servo.getYaw_DTerm() * Servo.getYaw_Delta()));

        Servo.setYaw_PMW_Output(PIDValue);

        Servo.setYaw_Current_i(yawCurrenti + 1);
    }

    public static void CalculatePIDPitch(ControlServo Servo, float PitchError) {

        byte i;
        float PIDValue;
        float pitchError = 0.0f;

        Servo.setPitch_Error_Index(Servo.getPitch_Current_i(),PitchError);

        for (float currentPitchError:Servo.getPitch_Error()){

            pitchError += Math.round(currentPitchError);
        }
        Servo.setPitch_Accumulator(pitchError);

        pitchError = Servo.getPitch_ErrorIndex(Servo.getPitch_Current_i()) - Servo.getPitch_ErrorIndex(Servo.getPitch_Current_i()-1);
        Servo.setPitch_Delta(pitchError);


        PIDValue = (float) ((Servo.getPitch_ErrorIndex(Servo.getPitch_Current_i()) * Servo.getPitch_PTerm()) + (Servo.getPitch_ITerm()* Servo.getPitch_Accumulator()) + (Servo.getPitch_DTerm() * Servo.getPitch_Delta()));
        Servo.setPitch_PMW_Output(PIDValue);

        Servo.setPitch_Current_i(Servo.getPitch_Current_i() + 1);
    }
}
