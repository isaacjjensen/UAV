package edu.und.seau.lib.UAV.objects;

public class ControlServo {

    //PID Servo Pitch Constants

    private double Pitch_PTerm;
    public double getPitch_PTerm() {
        return Pitch_PTerm;
    }

    public void setPitch_PTerm(double pitch_PTerm) {
        Pitch_PTerm = pitch_PTerm;
    }

    private double Pitch_ITerm;

    public double getPitch_ITerm() {
        return Pitch_ITerm;
    }

    public void setPitch_ITerm(double pitch_ITerm) {
        Pitch_ITerm = pitch_ITerm;
    }

    private double Pitch_DTerm;

    public double getPitch_DTerm() {
        return Pitch_DTerm;
    }

    public void setPitch_DTerm(double pitch_DTerm) {
        Pitch_DTerm = pitch_DTerm;
    }

    private float Pitch_Accumulator;

    public float getPitch_Accumulator() {
        return Pitch_Accumulator;
    }

    public void setPitch_Accumulator(float pitch_Accumulator) {
        Pitch_Accumulator = pitch_Accumulator;
    }

    private float[] Pitch_Error;

    public float[] getPitch_Error() {
        return Pitch_Error;
    }

    public float getPitch_Error_Index(int index)
    {
        index = index % Pitch_Error.length;
        if(index < 0)
        {
            index += Pitch_Error.length;
        }
        return Pitch_Error[index];
    }

    public void setPitch_Error(float[] pitch_Error) {
        Pitch_Error = pitch_Error;
    }

    public void setPitch_Error_Index(int index, float pitch_Error)
    {
        index %= Pitch_Error.length;
        if(index < 0)
        {
            index += Pitch_Error.length;
        }
        Pitch_Error[index] = pitch_Error;
    }

    private int Pitch_Current_i;

    public int getPitch_Current_i() {
        return Pitch_Current_i;
    }

    public void setPitch_Current_i(int pitch_Current_i) {
        pitch_Current_i %= Pitch_Error.length;
        if(pitch_Current_i < 0)
        {
            pitch_Current_i += Pitch_Error.length;
        }
        Pitch_Current_i = pitch_Current_i;
    }

    private float Pitch_Delta;

    public float getPitch_Delta() {
        return Pitch_Delta;
    }

    public void setPitch_Delta(float pitch_Delta) {
        Pitch_Delta = pitch_Delta;
    }

    //PID Servo Yaw Constants
    private double Yaw_PTerm;

    public double getYaw_PTerm() {
        return Yaw_PTerm;
    }

    public void setYaw_PTerm(double yaw_PTerm) {
        Yaw_PTerm = yaw_PTerm;
    }

    private double Yaw_ITerm;

    public double getYaw_ITerm() {
        return Yaw_ITerm;
    }

    public void setYaw_ITerm(double yaw_ITerm) {
        Yaw_ITerm = yaw_ITerm;
    }

    private double Yaw_DTerm;

    public double getYaw_DTerm() {
        return Yaw_DTerm;
    }

    public void setYaw_DTerm(double yaw_DTerm) {
        Yaw_DTerm = yaw_DTerm;
    }

    private float Yaw_Accumulator;

    public float getYaw_Accumulator() {
        return Yaw_Accumulator;
    }

    public void setYaw_Accumulator(float yaw_Accumulator) {
        Yaw_Accumulator = yaw_Accumulator;
    }

    private float[] Yaw_Error;

    public float[] getYaw_Error() {
        return Yaw_Error;
    }

    public float getYaw_Error_Index(int index)
    {
        index %= Yaw_Error.length;
        if(index < 0)
        {
            index += Yaw_Error.length;
        }
        return Yaw_Error[index];
    }

    public void setYaw_Error(float[] yaw_Error) {
        Yaw_Error = yaw_Error;
    }

    public void setYaw_Error_Index(int index, float yaw_Error)
    {
        index %= Yaw_Error.length;
        if(index < 0)
        {
            index += Yaw_Error.length;
        }
        Yaw_Error[index] = yaw_Error;
    }

    private int Yaw_Current_i;

    public int getYaw_Current_i() {
        return Yaw_Current_i;
    }

    public void setYaw_Current_i(int yaw_Current_i) {
        yaw_Current_i %= Yaw_Error.length;
        if(yaw_Current_i < 0)
        {
            yaw_Current_i += Yaw_Error.length;
        }
        Yaw_Current_i = yaw_Current_i;
    }

    private float Yaw_Delta;

    public float getYaw_Delta() {
        return Yaw_Delta;
    }

    public void setYaw_Delta(float yaw_Delta) {
        Yaw_Delta = yaw_Delta;
    }

    private float Pitch_PMW_Output;

    public float getPitch_PMW_Output() {
        return Pitch_PMW_Output;
    }

    public void setPitch_PMW_Output(float pitch_PMW_Output) {
        pitch_PMW_Output = Math.min(pitch_PMW_Output,50);
        pitch_PMW_Output = Math.max(pitch_PMW_Output,-50);
        Pitch_PMW_Output = pitch_PMW_Output;
    }

    private float Yaw_PMW_Output;

    public float getYaw_PMW_Output() {
        return Yaw_PMW_Output;
    }

    public void setYaw_PMW_Output(float yaw_PMW_Output) {
        yaw_PMW_Output = Math.min(yaw_PMW_Output,50);
        yaw_PMW_Output = Math.max(yaw_PMW_Output,-50);
        Yaw_PMW_Output = yaw_PMW_Output;
    }

    private float PWM_Output;

    public float getPWM_Output() {
        return PWM_Output;
    }

    public void setPWM_Output(float PWM_Output) {
        this.PWM_Output = PWM_Output;
    }

    public ControlServo()
    {

        Pitch_PTerm = 0;
        Pitch_ITerm = 0;
        Pitch_DTerm = 0;
        Pitch_Accumulator = 0.0f;
        Pitch_Error = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Pitch_Current_i = 0;
        Pitch_Delta = 0;
        Yaw_PTerm = 0;
        Yaw_ITerm = 0;
        Yaw_DTerm = 0;
        Yaw_Accumulator = 0.0f;
        Yaw_Error = new float[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Yaw_Current_i = 0;
        Yaw_Delta = 0;

        Pitch_PMW_Output = 0;
        Yaw_PMW_Output = 0;
        PWM_Output = 0;
    }
}
