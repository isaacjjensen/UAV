package edu.und.seau.UI;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import edu.und.seau.lib.UAV.logic.ControlServoLogic;
import edu.und.seau.lib.UAV.objects.ControlServo;
import edu.und.seau.uav.R;


public class control_screen extends AppCompatActivity implements SensorEventListener {

    //Flight Variables
    private Object control_command;
    private String UAV_Pilot_name;
    private Location currentLocation;

    private final String TAG = "UAV Control Screen";

    // TextView Variables
    private TextView Roll;
    private TextView Pitch;
    private TextView Yaw;
    private TextView MotorControl;
    private TextView Accum1;
    private TextView Accum2;
    private TextView Accum3;
    private TextView Accum4;
    private TextView chat_conversation;
    private TextView USBData;

    //PID Variables
    private TextView TextView_Servo1;
    private TextView TextView_Servo2;
    private TextView TextView_Servo3;
    private TextView TextView_Servo4;
    public double CalcTimer;
    boolean ServoRight;
    boolean ServoLeft;
    boolean ServoFront;
    boolean ServoBack;
    boolean is_uav_taking_off = false;
    public float[] Servo_Arr = {0, 0, 0, 0}; // { Front Left, Front Right, Back Left, Back Right }
    public byte[] Servo_Arr2 = {0, 0, 0, 0}; // { Front Left, Front Right, Back Left, Back Right }
//    public float[] DesiredOrientationPitch_Array = {0, 0, 0, 0};  // { Front Left, Front Right, Back Left, Back Right }
//    public float[] DesiredOrientationYaw_Array = {0, 0, 0, 0}; // { Front Left, Front Right, Back Left, Back Right }
    public float desiredPitch = 0.0f;
    public float desiredYaw = 0.0f;
    public float desiredRoll = 0.0f;

    private float maxPitch = 25.0f;
    private float maxYaw = 25.0f;
    // This decides how quickly the UAV changes thrust (also depends on pollTime in UAV_Controller)
    private float zMultiplier = 10.0f;

    public ControlServo Servo1;
    public ControlServo Servo2;
    public ControlServo Servo3;
    public ControlServo Servo4;

    private boolean areServosInitialized = false;
    private void InitializeServoSettings() {
        if(areServosInitialized)
        {
            return;
        }

        //InitializeServoSettings Servo 1
        Servo1 = new ControlServo();
        Servo1.setPitch_PTerm(.075);
        Servo1.setPitch_ITerm(.0001);
        Servo1.setPitch_DTerm((.95));
        Servo1.setYaw_PTerm(.075);
        Servo1.setYaw_ITerm(.0001);
        Servo1.setYaw_DTerm(.95);

        //InitializeServoSettings Servo 2
        Servo2 = new ControlServo();
        Servo2.setPitch_PTerm(.075);
        Servo2.setPitch_ITerm(.0001);
        Servo2.setPitch_DTerm((.95));
        Servo2.setYaw_PTerm(.075);
        Servo2.setYaw_ITerm(.0001);
        Servo2.setYaw_DTerm(.95);

        //InitializeServoSettings Servo 3
        Servo3 = new ControlServo();
        Servo3.setPitch_PTerm(.075);
        Servo3.setPitch_ITerm(.0001);
        Servo3.setPitch_DTerm((.95));
        Servo3.setYaw_PTerm(.075);
        Servo3.setYaw_ITerm(.0001);
        Servo3.setYaw_DTerm(.95);

        //InitializeServoSettings Servo 4
        Servo4 = new ControlServo();
        Servo4.setPitch_PTerm(.075);
        Servo4.setPitch_ITerm(.0001);
        Servo4.setPitch_DTerm((.95));
        Servo4.setYaw_PTerm(.075);
        Servo4.setYaw_ITerm(.0001);
        Servo4.setYaw_DTerm(.95);

        areServosInitialized = true;
    }

    //Sensor Data
    private SensorManager SensorData;

    //USB Variables
    Button startButton;
    Button stopButton;
    public boolean usb_is_connected = false;
    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialDevice serialPort;
    public String CommandSending = "";
    public String DataString = "";

    //Display Vars
    private TextView USBStatus;

    //USB Permissions library
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";

    //GPS Variables
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static long time = 0;
    public static double latitude = 0;
    public static double longitude = 0;
    public static double altitude = 0;

    //Misc Variables
    private String user_name, UAV_name;
    private DatabaseReference root;
    private String temp_key = "DATA";


    //Defining a Callback which triggers whenever data is read.
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            data = new String(arg0, StandardCharsets.UTF_8);
            data.concat("/n");
        }
    };

    //Broadcast Receiver to automatically start and stop the Serial connection.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            USBStatus.setText("Serial Connection Opened!\n");
                            usb_is_connected = true;
                            Log.d("SERIAL", "Serial connection is open");
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                Log.d("SERIAL", "onClickStart button pressed");
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                Log.d("SERIAL", "onClickStop button pressed");
                onClickStop(stopButton);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        InitializeServoSettings();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_screen);

        //Find TextView variables for GYRO
        Roll = findViewById(R.id.Roll);
        Pitch = findViewById(R.id.Pitch);
        Yaw = findViewById(R.id.Yaw);
        TextView yawOrientation = findViewById(R.id.YawOrientation);
        TextView pitchOrientation = findViewById(R.id.PitchOrientation);
        MotorControl = findViewById(R.id.MotorControl);
        TextView_Servo1 = findViewById(R.id.Servo1);
        TextView_Servo2 = findViewById(R.id.Servo2);
        TextView_Servo3 = findViewById(R.id.Servo3);
        TextView_Servo4 = findViewById(R.id.Servo4);
        Accum1 = findViewById(R.id.Accum1);
        Accum2 = findViewById(R.id.Accum2);
        Accum3 = findViewById(R.id.Accum3);
        Accum4 = findViewById(R.id.Accum4);

        //Connect to Sensor
        SensorData = (SensorManager) getSystemService(SENSOR_SERVICE);
        user_name = getIntent().getExtras().get("user_name").toString();
        UAV_name = getIntent().getExtras().get("UAV_name").toString();
        setTitle(" UAV Control: " + user_name);

        appendLog("Time\tYaw\tPitch\tOutput Array");

        root = FirebaseDatabase.getInstance().getReference().child(UAV_name);

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
                Log.d("Child Event Listener", "Child Added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
                Log.d("Child Event Listener", "Child Changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        USBStatus = findViewById(R.id.usb_status);

        //Added by John for defining Text Views so I can write to them later
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        startButton = findViewById(R.id.buttonStart);
        stopButton = findViewById(R.id.buttonStop);
        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        // InitializeServoSettings Text Vars
        chat_conversation = findViewById(R.id.textView);
        USBData = findViewById(R.id.usb_data);
        USBStatus = findViewById(R.id.usb_status);


        // GPS Initialization
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() { // This is where the values for time, alt, long, and lat are defined.
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
//                time = location.getTime();
//                altitude = location.getAltitude();
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//                Date date = new Date(time);
                //Log.d("R2H", "Updating GPS Coordinates");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { // Do nothing in this case
            }

            @Override
            public void onProviderEnabled(String provider) { // Do nothing in this case
            }

            @Override
            public void onProviderDisabled(String provider) { // Do nothing in this case
            }
        };// End GPS Listener

        // Function called ever 5 seconds to find new coordinates
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener); // (type, refresh time (ms), distance needed for change (m), pointer)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener); // (type, refresh time (ms), distance needed for change (m), pointer)


    }

    protected void onStart() {
        super.onStart();

        SensorData.registerListener(this, SensorData.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);

    }

    protected void onStop() {
        SensorData.unregisterListener(this);
        super.onStop();
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        //Do nothing
    }

    //Added by John for button boolean functions
    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        stopButton.setEnabled(bool);
    }

    // Sends directional commands out USB when received from host
    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        // This is very ineffective in getting what we need
        // TODO: Change this to be less GARBAGE
        while (i.hasNext()) {
            control_command = ((DataSnapshot) i.next()).getValue();
            UAV_Pilot_name = (String) ((DataSnapshot) i.next()).getValue();
        }

        Map cmdMap;

        if (control_command instanceof Map){
            cmdMap = (Map) control_command;
        } else {
            // Return if it is not a proper control_command
            return;
        }

        if (UAV_Pilot_name.equals("HOST")) {

        float cmdX = ((Number)cmdMap.get("x")).floatValue();
        float cmdY = ((Number)cmdMap.get("y")).floatValue();
        float cmdZ = ((Number)cmdMap.get("z")).floatValue();

        chat_conversation.setText("");
        chat_conversation.append(UAV_Pilot_name + " : " +
                String.format("%.2f", cmdX) + ", " +
                String.format("%.2f", cmdY) + ", " +
                String.format("%.2f", cmdZ));

            // TODO: store both location and UAV servo/orientation information
//            Map<String, Object> map = new HashMap<String, Object>();
//            temp_key = root.push().getKey();
//            DatabaseReference message_root = root.child(temp_key);
//
//            // Sending UAV's currentlocation to FireBase
//            map.put("name", user_name);
//            map.put("location", currentLocation);
//            message_root.updateChildren(map);

            // Adjustments for flight
            desiredPitch = maxPitch * cmdY;
            desiredYaw = maxYaw * cmdX;

            for (int j = 0; j < 4; j++) {
                Servo_Arr[j] += zMultiplier * cmdZ; // increase all servo speeds
                Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
            }
            Log.d("Test", "Servo_Arr[0]: " + Servo_Arr[0]);
        }
    }

    //Added by John for action when Start button is used, verifies USB connection is valid
    public void onClickStart(View view) {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    //Added by John for action when Stop button is used, closes USB connection and crashed program on last run
    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        USBStatus.setText("\nSerial Connection Closed! \n");
        usb_is_connected = false;
    }

    // Checks for errors in control command sent from host
    // This is weird
    public static int check_for_err(String cmd) {

        String[] str_arr = cmd.split("(?!^)");
        int rc = 0;
        if (cmd.length() != 2) {
            rc = 0;
            //Log.d("R:", "Command String Length != 2");
            return rc;
        }
        if ((str_arr[0]).equals(str_arr[1])) {
            byte[] return_arr = str_arr[0].getBytes();
            rc = return_arr[0];
            Log.d("Check Error", "Command returned = " + rc);
            return rc;
        } else {
            //Log.d("R:", "Command had length of 2 but strings not equal");
            return rc;
        }

    }

    long tsLastEvent = 0;
    long thresholdEvent = 10000000;
    // Gyro Sensor and Leveling code
    public void onSensorChanged(SensorEvent event) {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        if (event.timestamp > (tsLastEvent + thresholdEvent)) {
            tsLastEvent = event.timestamp;
            //Log.d("R:","Threshold Met");
        }
        else{return;}

        //else it will output the Roll, Pitch and Yaw data
        Roll.setText("Roll :" + Float.toString(Math.round(event.values[0])));
        Pitch.setText("Pitch :" + Float.toString(Math.round(event.values[1])));
        Yaw.setText("Yaw :" + Float.toString(Math.round(event.values[2])));

        // Calculating changes need to PIDs to balance out errors in both Pitch and Yaw
        // Slightly different for each servo do to workings of UAV
        CalculatePIDPitchServo1(event.values[1] - desiredPitch);
        CalculatePIDYawServo1(event.values[2] - desiredYaw);
        CalculatePIDPitchServo2(event.values[1] - desiredPitch);
        CalculatePIDYawServo2(event.values[2] - desiredYaw);
        CalculatePIDPitchServo3(event.values[1] - desiredPitch);
        CalculatePIDYawServo3(event.values[2] - desiredYaw);
        CalculatePIDPitchServo4(event.values[1] - desiredPitch);
        CalculatePIDYawServo4(event.values[2] - desiredYaw);

        Servo1.setPWM_Output(Servo1.getPitch_PMW_Output() + Servo1.getYaw_PMW_Output() + Servo_Arr[0]);
        Servo2.setPWM_Output(Servo2.getPitch_PMW_Output() + Servo2.getYaw_PMW_Output() + Servo_Arr[1]);
        Servo3.setPWM_Output(Servo3.getPitch_PMW_Output() + Servo3.getYaw_PMW_Output() + Servo_Arr[2]);
        Servo4.setPWM_Output(Servo4.getPitch_PMW_Output() + Servo4.getYaw_PMW_Output() + Servo_Arr[3]);

//        Log.d("ServoArray",Arrays.toString(Servo_Arr));
//        Log.d("Servo1", "Pitch PMW Output: \t" + Float.toString(Servo1.getPitch_PMW_Output()));
//        Log.d("Servo1", "Yaw PMW Output: \t\t" + Float.toString(Servo1.getYaw_PMW_Output()));
//        Log.d("Servo1", "PWM Output: \t\t\t" + Float.toString(Servo1.getPWM_Output()));
//        Log.d("Servo2", "Pitch PMW Output: \t" + Float.toString(Servo2.getPitch_PMW_Output()));
//        Log.d("Servo2", "Yaw PMW Output: \t\t" + Float.toString(Servo2.getYaw_PMW_Output()));
//        Log.d("Servo2", "PWM Output: \t\t\t" + Float.toString(Servo2.getPWM_Output()));
//        Log.d("Servo3", "Pitch PMW Output: \t" + Float.toString(Servo3.getPitch_PMW_Output()));
//        Log.d("Servo3", "Yaw PMW Output: \t\t" + Float.toString(Servo3.getYaw_PMW_Output()));
//        Log.d("Servo3", "PWM Output: \t\t\t" + Float.toString(Servo3.getPWM_Output()));
//        Log.d("Servo4", "Pitch PMW Output: \t" + Float.toString(Servo4.getPitch_PMW_Output()));
//        Log.d("Servo4", "Yaw PMW Output: \t\t" + Float.toString(Servo4.getYaw_PMW_Output()));
//        Log.d("Servo4", "PWM Output: \t\t\t" + Float.toString(Servo4.getPWM_Output()));

        Servo_Arr2[0]=(byte) Servo1.getPWM_Output();
        Servo_Arr2[1]=(byte) Servo2.getPWM_Output();
        Servo_Arr2[2]=(byte) Servo3.getPWM_Output();
        Servo_Arr2[3]=(byte) Servo4.getPWM_Output();

        appendLog(Float.toString(System.currentTimeMillis())+"\t"+Float.toString(event.values[1])+"\t"+Float.toString(event.values[2])+"\t"+Arrays.toString(Servo_Arr2));
        //Log.d("Serial Data", Arrays.toString(Servo_Arr2) + "________________________________");

        for (byte j = 0; j < 4; j++) {
            Servo_Arr2[j] = chk_min_max_speed(Servo_Arr2[j]);
        } // Check for min and max servo speed
        TextView_Servo1.setText("Servo1: %" + Servo_Arr2[0]);
        TextView_Servo2.setText("Servo2: %" + Servo_Arr2[1]);
        TextView_Servo3.setText("Servo3: %" + Servo_Arr2[2]);
        TextView_Servo4.setText("Servo4: %" + Servo_Arr2[3]);
        if (usb_is_connected) serialPort.write(Servo_Arr2);
        //USBData.setText("Serial Data Sent From Gyro");



    /* WAIT FUNCTION
        Log.d("R:","Start Wait");
        long a = System.currentTimeMillis();
        long b = System.currentTimeMillis();
        while((b-a) <= 50){
            b = System.currentTimeMillis();
        }
        Log.d("R:","End Wait");
*/
    }

    // Verifies servo speed doesn't ecliplse 90, go below 10
    public byte chk_min_max_speed(byte speed) {
        if (speed > 100) speed = 100;
        if (speed < 0) speed = 0;
        //Log.d("R:", "Checked Min Max Speed = "+speed);
        return speed;
    }

    // Verifies servo speed doesn't ecliplse 90, go below 10
    public float chk_min_max_speed(float speed) {
        if (speed > 100.0f) speed = 100.0f;
        if (speed < 0.0f) speed = 0.0f;
        //Log.d("R:", "Checked Min Max Speed = "+speed);
        return speed;
    }

    /* Wait function
    public void wait_in_milli(long Wait_Time) {
        Log.d("Time Class ", "Wait_Time Started ");
        TimerQueue=true;

        {
            final Handler Timer = new Handler();
            Timer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    TimerQueue=false;
                    Log.d("Time Class ", "Wait_Time in ms has seconds Passed ");

                }
            }, Wait_Time);
        }
    }*/


    // ***NOTE: polarity is -1 for Pitch Servo 3 and Servo 4, for Yaw Servo 2 and Servo 4
    // PID Servo 1 function
    public void CalculatePIDPitchServo1(float PitchErrorServo1) {
        ControlServoLogic.CalculatePIDPitch(Servo1, PitchErrorServo1, 1);
    }

    public void CalculatePIDYawServo1(float YawErrorServo1) {
        ControlServoLogic.CalculatePIDYaw(Servo1,YawErrorServo1,1);
        Accum1.setText("Accum1:" + Float.toString(Servo1.getYaw_Accumulator()+Servo1.getPitch_Accumulator()));
    }

    // PID Servo 2 function
    public void CalculatePIDPitchServo2(float PitchErrorServo2) {
        ControlServoLogic.CalculatePIDPitch(Servo2, PitchErrorServo2, 1);
    }

    public void CalculatePIDYawServo2(float YawErrorServo2) {
        ControlServoLogic.CalculatePIDYaw(Servo2,YawErrorServo2,-1);
        Accum2.setText("Accum2:" + Float.toString(Servo2.getYaw_Accumulator()+Servo2.getPitch_Accumulator()));
    }

    // PID Servo 3 function
    public void CalculatePIDPitchServo3(float PitchErrorServo3) {
        ControlServoLogic.CalculatePIDPitch(Servo3, PitchErrorServo3, -1);
    }

    public void CalculatePIDYawServo3(float YawErrorServo3) {
        ControlServoLogic.CalculatePIDYaw(Servo3,YawErrorServo3,1);
        Accum3.setText("Accum3:" + Float.toString(Servo3.getYaw_Accumulator()+Servo3.getPitch_Accumulator()));
    }

    // PID Servo 4 function
    public void CalculatePIDPitchServo4(float PitchErrorServo4) {
        ControlServoLogic.CalculatePIDPitch(Servo4,PitchErrorServo4, -1);
    }

    public void CalculatePIDYawServo4(float YawErrorServo4) {
        ControlServoLogic.CalculatePIDYaw(Servo4,YawErrorServo4,-1);
        Accum4.setText("Accum4:" + Float.toString(Servo4.getYaw_Accumulator()+Servo4.getPitch_Accumulator()));
    }

public void appendLog(String text)
    {
        File logFile = new File("sdcard/log.txt");
        //File logFile = new File("storage/extSdCard/Log/log.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}