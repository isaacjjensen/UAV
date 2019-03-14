package edu.und.seau.lib.UAV.logic;

import edu.und.seau.lib.UAV.objects.ControlServo;

public class ControlServoLogic {

    public static void CalculatePIDYaw(ControlServo Servo, float YawError, int polarity) {

        float PIDValue;
        float yawError = 0.0f;
        int yawCurrenti;

        Servo.setYaw_Error_Index(Servo.getYaw_Current_i(),YawError);

        for (float currentYawError:Servo.getYaw_Error())
        {
            yawError += Math.round(currentYawError);
        }
        Servo.setYaw_Accumulator(yawError);

        yawCurrenti = Servo.getYaw_Current_i();
        Servo.setYaw_Delta(Servo.getYaw_Error_Index(yawCurrenti) - Servo.getYaw_Error_Index(yawCurrenti-1));

        // PIDValue has a -1 polarity for Servo2 and Servo4
        // polarity is probably a bad variable name, but I couldn't think of anything else
        PIDValue = (float) (polarity * ((Servo.getYaw_Error_Index(yawCurrenti) * Servo.getYaw_PTerm()) + (Servo.getYaw_ITerm() * Servo.getYaw_Accumulator()) + (Servo.getYaw_DTerm() * Servo.getYaw_Delta())));

        Servo.setYaw_PMW_Output(PIDValue);

        Servo.setYaw_Current_i(yawCurrenti + 1);
    }

    public static void CalculatePIDPitch(ControlServo Servo, float PitchError, int polarity) {

        float PIDValue;
        float pitchError = 0.0f;
        int pitchCurrenti;

        Servo.setPitch_Error_Index(Servo.getPitch_Current_i(),PitchError);

        for (float currentPitchError:Servo.getPitch_Error()){

            pitchError += Math.round(currentPitchError);
        }
        Servo.setPitch_Accumulator(pitchError);

        pitchCurrenti = Servo.getPitch_Current_i();
        Servo.setPitch_Delta(Servo.getPitch_Error_Index(pitchCurrenti) - Servo.getPitch_Error_Index(pitchCurrenti-1));

        // Has a -1 polarity for Servo3 and Servo4
        PIDValue = (float) (polarity * ((Servo.getPitch_Error_Index(pitchCurrenti) * Servo.getPitch_PTerm()) + (Servo.getPitch_ITerm()* Servo.getPitch_Accumulator()) + (Servo.getPitch_DTerm() * Servo.getPitch_Delta())));

        Servo.setPitch_PMW_Output(PIDValue);

        Servo.setPitch_Current_i(pitchCurrenti + 1);
    }
}
