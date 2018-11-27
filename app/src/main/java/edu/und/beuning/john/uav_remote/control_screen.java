package edu.und.beuning.john.uav_remote;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class control_screen  extends AppCompatActivity implements SensorEventListener {


    //Flight Variables
    private String control_command, UAV_Pilot_name;
    private int cmd;

    // TextView Variables
    private TextView Roll;
    private TextView Pitch;
    private TextView Yaw;
    private TextView YawOrientation;
    private TextView PitchOrientation;
    private TextView MotorControl;
    private TextView Accum1;
    private TextView Accum2;
    private TextView Accum3;
    private TextView Accum4;
    private TextView chat_conversation;
    private TextView USBData;

    //PID Variables
    private TextView Servo1;
    private TextView Servo2;
    private TextView Servo3;
    private TextView Servo4;
    public double CalcTimer;
    boolean ServoRight;
    boolean ServoLeft;
    boolean ServoFront;
    boolean ServoBack;
    boolean is_uav_taking_off = false;
    public byte[] Servo_Arr = {0, 0, 0, 0}; // { Front Left, Front Right, Back Left, Back Right }
    public byte[] Servo_Arr2 = {0, 0, 0, 0}; // { Front Left, Front Right, Back Left, Back Right }
    public float[] DesiredOrientationPitch_Array = {0, 0, 0, 0};  // { Front Left, Front Right, Back Left, Back Right }
    public float[] DesiredOrientationYaw_Array = {0, 0, 0, 0}; // { Front Left, Front Right, Back Left, Back Right }

    public float Servo1_Pitch_PMW_Output = 0;
    public float Servo1_Yaw_PMW_Output = 0;
    public float Servo1_PWM_Output = 0;

    public float Servo2_Pitch_PMW_Output = 0;
    public float Servo2_Yaw_PMW_Output = 0;
    public float Servo2_PWM_Output = 0;

    public float Servo3_Pitch_PMW_Output = 0;
    public float Servo3_Yaw_PMW_Output = 0;
    public float Servo3_PWM_Output = 0;

    public float Servo4_Pitch_PMW_Output = 0;
    public float Servo4_Yaw_PMW_Output = 0;
    public float Servo4_PWM_Output = 0;

    //PID Servo 1 Pitch Constants
    public double Servo1_Pitch_PTerm = 0;
    public double Servo1_Pitch_ITerm = 0;
    public double Servo1_Pitch_DTerm = 0;
    public float Servo1_Pitch_Accumulator;
    public float[] Servo1_Pitch_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo1_Pitch_Current_i = 0;
    public float Servo1_Pitch_Delta = 0;

    //PID Servo 1 Yaw Constants
    public double Servo1_Yaw_PTerm = 0;
    public double Servo1_Yaw_ITerm = 0;
    public double Servo1_Yaw_DTerm = 0;
    public float Servo1_Yaw_Accumulator;
    public float[] Servo1_Yaw_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo1_Yaw_Current_i = 0;
    public float Servo1_Yaw_Delta = 0;

    //PID Servo 2 Pitch Constants
    public double Servo2_Pitch_PTerm = 0;
    public double Servo2_Pitch_ITerm = 0;
    public double Servo2_Pitch_DTerm = 1/2;
    public float Servo2_Pitch_Accumulator;
    public float[] Servo2_Pitch_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo2_Pitch_Current_i = 0;
    public float Servo2_Pitch_Delta = 0;

    //PID Servo 2 Yaw Constants
    public double Servo2_Yaw_PTerm = 0;
    public double Servo2_Yaw_ITerm = 0;
    public double Servo2_Yaw_DTerm = 1/2;
    public float Servo2_Yaw_Accumulator;
    public float[] Servo2_Yaw_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo2_Yaw_Current_i = 0;
    public float Servo2_Yaw_Delta = 0;

    //PID Servo 3 Pitch Constants
    public double Servo3_Pitch_PTerm = 0;
    public double Servo3_Pitch_ITerm = 0;
    public double Servo3_Pitch_DTerm = 0;
    public float Servo3_Pitch_Accumulator;
    public float[] Servo3_Pitch_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo3_Pitch_Current_i = 0;
    public float Servo3_Pitch_Delta = 0;

    //PID Servo 3 Yaw Constants
    public double Servo3_Yaw_PTerm = 0;
    public double Servo3_Yaw_ITerm = 0;
    public double Servo3_Yaw_DTerm = 0;
    public float Servo3_Yaw_Accumulator;
    public float[] Servo3_Yaw_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo3_Yaw_Current_i = 0;
    public float Servo3_Yaw_Delta = 0;

    //PID Servo 4 Pitch Constants
    public double Servo4_Pitch_PTerm = 0;
    public double Servo4_Pitch_ITerm = 0;
    public double Servo4_Pitch_DTerm = 0;
    public float Servo4_Pitch_Accumulator;
    public float[] Servo4_Pitch_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo4_Pitch_Current_i = 0;
    public float Servo4_Pitch_Delta = 0;

    //PID Servo 4 Yaw Constants
    public double Servo4_Yaw_PTerm = 0;
    public double Servo4_Yaw_ITerm = 0;
    public double Servo4_Yaw_DTerm = 0;
    public float Servo4_Yaw_Accumulator;
    public float[] Servo4_Yaw_Error = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int Servo4_Yaw_Current_i = 0;
    public float Servo4_Yaw_Delta = 0;



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
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_screen);

        //Find TextView variables for GYRO
        Roll = (TextView) findViewById(R.id.Roll);
        Pitch = (TextView) findViewById(R.id.Pitch);
        Yaw = (TextView) findViewById(R.id.Yaw);
        YawOrientation = (TextView) findViewById(R.id.YawOrientation);
        PitchOrientation = (TextView) findViewById(R.id.PitchOrientation);
        MotorControl = (TextView) findViewById(R.id.MotorControl);
        Servo1 = (TextView) findViewById(R.id.Servo1);
        Servo2 = (TextView) findViewById(R.id.Servo2);
        Servo3 = (TextView) findViewById(R.id.Servo3);
        Servo4 = (TextView) findViewById(R.id.Servo4);
        Accum1 = (TextView) findViewById(R.id.Accum1);
        Accum2 = (TextView) findViewById(R.id.Accum2);
        Accum3 = (TextView) findViewById(R.id.Accum3);
        Accum4 = (TextView) findViewById(R.id.Accum4);
        //Connect to Sensor
        SensorData = (SensorManager) getSystemService(SENSOR_SERVICE);
        user_name = getIntent().getExtras().get("user_name").toString();
        UAV_name = getIntent().getExtras().get("UAV_name").toString();
        setTitle(" UAV Control: " + user_name);

                                                        Servo1_Pitch_PTerm = Servo2_Pitch_PTerm = Servo3_Pitch_PTerm = Servo4_Pitch_PTerm = .075;
                                                        Servo1_Pitch_ITerm = Servo2_Pitch_ITerm = Servo3_Pitch_ITerm = Servo4_Pitch_ITerm = .0001;
                                                        Servo1_Pitch_DTerm = Servo2_Pitch_DTerm = Servo3_Pitch_DTerm = Servo4_Pitch_DTerm = .95;
                                                        Servo1_Yaw_PTerm = Servo2_Yaw_PTerm = Servo3_Yaw_PTerm = Servo4_Yaw_PTerm = .075;
                                                        Servo1_Yaw_ITerm = Servo2_Yaw_ITerm = Servo3_Yaw_ITerm = Servo4_Yaw_ITerm = .0001;
                                                        Servo1_Yaw_DTerm = Servo2_Yaw_DTerm = Servo3_Yaw_DTerm = Servo4_Yaw_DTerm = .95;


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


        USBStatus = (TextView) findViewById(R.id.usb_status);

        //Added by John for defining Text Views so I can write to them later
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        stopButton = (Button) findViewById(R.id.buttonStop);
        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);


        // Initialize Text Vars
        chat_conversation = (TextView) findViewById(R.id.textView);
        USBData = (TextView) findViewById(R.id.usb_data);
        USBStatus = (TextView) findViewById(R.id.usb_status);


        // GPS Initilization
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() { // This is where the values for time, alt, long, and lat are defined.
            @Override
            public void onLocationChanged(Location location) {
                time = location.getTime();
                altitude = location.getAltitude();
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Date date = new Date(time);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener); // (type, refresh time (ms), distance needed for change (m), pointer)


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

        while (i.hasNext()) {
            control_command = (String) ((DataSnapshot) i.next()).getValue();
            UAV_Pilot_name = (String) ((DataSnapshot) i.next()).getValue();

        }

        chat_conversation.setText("");
        chat_conversation.append(UAV_Pilot_name + " : " + control_command);

        if (UAV_Pilot_name.equals("HOST")) {

            cmd = check_for_err(control_command);
            Map<String, Object> map = new HashMap<String, Object>();
            //temp_key = root.push().getKey();
            DatabaseReference message_root = root.child(temp_key);

            byte j;
            Log.d("D","CMD = "+cmd);
            switch (cmd) {

                case 79:
                    Log.d("R:", "Center");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Center");
                    break;

                case 80:
                    Log.d("R:", "Land");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    // USB Code
                    //wait_in_milli(1000);


                    while(Servo_Arr[0] != 0 && Servo_Arr[1] != 0 && Servo_Arr[2] != 0 && Servo_Arr[2] !=0) {

                        for (j = 0; j < 4; j++) {

                            Servo_Arr[j] -= 1; // decrease all servo speeds
                            Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10

                        }

                        if (usb_is_connected) serialPort.write(Servo_Arr2);
                        USBData.setText("Serial Data Sent : Landing");

                    }

                    break;

                case 81:
                    Log.d("R:", "E-Stop");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);

                    for(int k=0;k<4;k++){
                        Servo_Arr2[k] = -50;
                    }

                    Log.d("Serial Data", Arrays.toString(Servo_Arr2));

                    if (usb_is_connected) {
                        serialPort.write(Servo_Arr2);
                        USBData.setText("Serial Data Sent : E-Stop");
                    }

                    serialPort.close();
                    System.exit(0);
                    break;

                case 97:
                    Log.d("R:", "Up 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);

                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] += 10; // increase all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Up");
                    break;

                case 82:
                    Log.d("R:", "Up 2.5%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] += 2.5; // increase all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Up");
                    break;

                case 83:
                    Log.d("R:", "Down 2.5%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);

                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] -= 2.5; // increase all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Up");
                    break;

                case 98:
                    Log.d("R:", "Up 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);

                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] += 20; // increase all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Up");
                    break;

                case 99:
                    Log.d("R:", "Up 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);

                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] += 30; // increase all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Up");
                    break;

                case 100:
                    Log.d("R:", "Up 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);

                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] += 35; // increase all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Up");
                    break;

                case 101:
                    Log.d("R:", "Down 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] -= 10; // decrease all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Down");
                    break;

                case 102:
                    Log.d("R:", "Down 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] -= 20; // decrease all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Down");
                    break;

                case 103:
                    Log.d("R:", "Down 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] -= 30; // decrease all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Down");
                    break;

                case 104:
                    Log.d("R:", "Down 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    // USB Code
                    for (j = 0; j < 4; j++) {
                        Servo_Arr[j] -= 40; // decrease all servo speeds
                        Servo_Arr[j] = chk_min_max_speed(Servo_Arr[j]); // Max of 90, Min of 10
                    }
                    if (usb_is_connected) serialPort.write(Servo_Arr2);
                    USBData.setText("Serial Data Sent : Down");
                    break;

                case 113:
                    Log.d("R:", "Left 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -5/2;
                    DesiredOrientationPitch_Array[1]= 5/2;
                    DesiredOrientationPitch_Array[2]= -5/2;
                    DesiredOrientationPitch_Array[3]= 5/2;
                    MotorControl.setText("Set Point = Left");
                    break;

                case 114:
                    Log.d("R:", "Left 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -5;
                    DesiredOrientationPitch_Array[1]= 5;
                    DesiredOrientationPitch_Array[2]= -5;
                    DesiredOrientationPitch_Array[3]= 5;
                    MotorControl.setText("Set Point = Left");
                    break;

                case 115:
                    Log.d("R:", "Left 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -15/2;
                    DesiredOrientationPitch_Array[1]= 15/2;
                    DesiredOrientationPitch_Array[2]= -15/2;
                    DesiredOrientationPitch_Array[3]= 15/2;
                    MotorControl.setText("Set Point = Left");
                    break;

                case 116:
                    Log.d("R:", "Left 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -10;
                    DesiredOrientationPitch_Array[1]= 10;
                    DesiredOrientationPitch_Array[2]= -10;
                    DesiredOrientationPitch_Array[3]= 10;
                    MotorControl.setText("Set Point = Left");
                    break;

                case 117:
                    Log.d("R:", "Right 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 5/2;
                    DesiredOrientationPitch_Array[1]= -5/2;
                    DesiredOrientationPitch_Array[2]= 5/2;
                    DesiredOrientationPitch_Array[3]= -5/2;
                    MotorControl.setText("Set Point = Right");
                    break;

                case 118:
                    Log.d("R:", "Right 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 5;
                    DesiredOrientationPitch_Array[1]= -5;
                    DesiredOrientationPitch_Array[2]= 5;
                    DesiredOrientationPitch_Array[3]= -5;
                    MotorControl.setText("Set Point = Right");
                    break;

                case 119:
                    Log.d("R:", "Right 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 15/2;
                    DesiredOrientationPitch_Array[1]= -15/2;
                    DesiredOrientationPitch_Array[2]= 15/2;
                    DesiredOrientationPitch_Array[3]= -15/2;
                    MotorControl.setText("Set Point = Right");
                    break;

                case 120:
                    Log.d("R:", "Right 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 10;
                    DesiredOrientationPitch_Array[1]= -10;
                    DesiredOrientationPitch_Array[2]= 10;
                    DesiredOrientationPitch_Array[3]= -10;
                    MotorControl.setText("Set Point = Right");
                    break;

                case 105:
                    Log.d("R:", "Forward 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -5/2;
                    DesiredOrientationPitch_Array[1]= -5/2;
                    DesiredOrientationPitch_Array[2]= 5/2;
                    DesiredOrientationPitch_Array[3]= 5/2;
                    MotorControl.setText("Set Point = Forward");
                    break;

                case 106:
                    Log.d("R:", "Forward 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -5;
                    DesiredOrientationPitch_Array[1]= -5;
                    DesiredOrientationPitch_Array[2]= 5;
                    DesiredOrientationPitch_Array[3]= 5;
                    MotorControl.setText("Set Point = Forward");
                    break;

                case 107:
                    Log.d("R:", "Forward 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -15/2;
                    DesiredOrientationPitch_Array[1]= -15/2;
                    DesiredOrientationPitch_Array[2]= 15/2;
                    DesiredOrientationPitch_Array[3]= 15/2;
                    MotorControl.setText("Set Point = Forward");
                    break;

                case 108:
                    Log.d("R:", "Forward 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -10;
                    DesiredOrientationPitch_Array[1]= -10;
                    DesiredOrientationPitch_Array[2]= 10;
                    DesiredOrientationPitch_Array[3]= 10;
                    MotorControl.setText("Set Point = Forward");
                    break;

                case 109:
                    Log.d("R:", "Backward 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 5/2;
                    DesiredOrientationPitch_Array[1]= 5/2;
                    DesiredOrientationPitch_Array[2]= -5/2;
                    DesiredOrientationPitch_Array[3]= -5/2;
                    MotorControl.setText("Set Point = Backward");
                    break;

                case 15:
                    Log.d("R:", "Backward 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 5;
                    DesiredOrientationPitch_Array[1]= 5;
                    DesiredOrientationPitch_Array[2]= -5;
                    DesiredOrientationPitch_Array[3]= -5;
                    MotorControl.setText("Set Point = Backward");
                    break;

                case 111:
                    Log.d("R:", "Backward 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 15/2;
                    DesiredOrientationPitch_Array[1]= 15/2;
                    DesiredOrientationPitch_Array[2]= -15/2;
                    DesiredOrientationPitch_Array[3]= -15/2;
                    MotorControl.setText("Set Point = Backward");
                    break;

                case 112:
                    Log.d("R:", "Backward 100 %");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 10;
                    DesiredOrientationPitch_Array[1]= 10;
                    DesiredOrientationPitch_Array[2]= -10;
                    DesiredOrientationPitch_Array[3]= -10;
                    MotorControl.setText("Set Point = Backward");
                    break;

                case 121:
                    Log.d("R:", "Forward Left 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -5/2;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= 5/2;
                    DesiredOrientationYaw_Array[0]= -5/2;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= 5/2;
                    MotorControl.setText("Set Point = Forward Left");
                    break;

                case 122:
                    Log.d("R:", "Forward Left 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -5;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= 5;
                    DesiredOrientationYaw_Array[0]= -5;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= 5;
                    MotorControl.setText("Set Point = Forward Left");
                    break;

                case 123:
                    Log.d("R:", "Forward Left 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -15/2;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= 15/2;
                    DesiredOrientationYaw_Array[0]= -15/2;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= 15/2;
                    MotorControl.setText("Set Point = Forward Left");
                    break;

                case 124:
                    Log.d("R:", "Forward Left 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= -10;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= 10;
                    DesiredOrientationYaw_Array[0]= -10;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= 10;
                    MotorControl.setText("Set Point = Forward Left");
                    break;

                case 67:
                    Log.d("R:", "Forward Right");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= -5/2;
                    DesiredOrientationPitch_Array[2]= 5/2;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= -5/2;
                    DesiredOrientationYaw_Array[2]= 5/2;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Forward Right");
                    break;

                case 68:
                    Log.d("R:", "Forward Right 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= -5;
                    DesiredOrientationPitch_Array[2]= 5;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= -5;
                    DesiredOrientationYaw_Array[2]= 5;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Forward Right");
                    break;

                case 69:
                    Log.d("R:", "Forward Right 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= -15/2;
                    DesiredOrientationPitch_Array[2]= 15/2;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= -15/2;
                    DesiredOrientationYaw_Array[2]= 15/2;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Forward Right");
                    break;

                case 70:
                    Log.d("R:", "Forward Right 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= -10;
                    DesiredOrientationPitch_Array[2]= 10;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= -10;
                    DesiredOrientationYaw_Array[2]= 10;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Forward Right");
                    break;

                case 71:
                    Log.d("R:", "Backward Left 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= 5/2;
                    DesiredOrientationPitch_Array[2]= -5/2;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= 5/2;
                    DesiredOrientationYaw_Array[2]= -5/2;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Backward Left");
                    break;

                case 72:
                    Log.d("R:", "Backward Left 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= 5;
                    DesiredOrientationPitch_Array[2]= -5;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= 5;
                    DesiredOrientationYaw_Array[2]= -5;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Backward Left");
                    break;


                case 73:
                    Log.d("R:", "Backward Left 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= 15/2;
                    DesiredOrientationPitch_Array[2]= -15/2;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= 15/2;
                    DesiredOrientationYaw_Array[2]= -15/2;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Backward Left");
                    break;


                case 74:
                    Log.d("R:", "Backward Left 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 0;
                    DesiredOrientationPitch_Array[1]= 10;
                    DesiredOrientationPitch_Array[2]= -10;
                    DesiredOrientationPitch_Array[3]= 0;
                    DesiredOrientationYaw_Array[0]= 0;
                    DesiredOrientationYaw_Array[1]= 10;
                    DesiredOrientationYaw_Array[2]= -10;
                    DesiredOrientationYaw_Array[3]= 0;
                    MotorControl.setText("Set Point = Backward Left");
                    break;


                case 75:
                    Log.d("R:", "Backward Right 25%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 5/2;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= -5/2;
                    DesiredOrientationYaw_Array[0]= 5/2;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= -5/2;
                    MotorControl.setText("Set Point = Backward Right");
                    break;

                case 76:
                    Log.d("R:", "Backward Right 50%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 5;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= -5;
                    DesiredOrientationYaw_Array[0]= 5;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= -5;
                    MotorControl.setText("Set Point = Backward Right");
                    break;

                case 77:
                    Log.d("R:", "Backward Right 75%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 15/2;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= -15/2;
                    DesiredOrientationYaw_Array[0]= 15/2;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= -15/2;
                    MotorControl.setText("Set Point = Backward Right");
                    break;

                case 78:
                    Log.d("R:", "Backward Right 100%");
                    // Firebase Code
                    map.put("name", user_name);
                    map.put("msg", time + " " + latitude + " " + longitude + " " + altitude);
                    message_root.updateChildren(map);
                    DesiredOrientationPitch_Array[0]= 10;
                    DesiredOrientationPitch_Array[1]= 0;
                    DesiredOrientationPitch_Array[2]= 0;
                    DesiredOrientationPitch_Array[3]= -10;
                    DesiredOrientationYaw_Array[0]= 10;
                    DesiredOrientationYaw_Array[1]= 0;
                    DesiredOrientationYaw_Array[2]= 0;
                    DesiredOrientationYaw_Array[3]= -10;
                    MotorControl.setText("Set Point = Backward Right");
                    break;

                default:
                    Log.d("R:", "Didn't Match Case");
            }

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
            //Log.d("R:", "Command returned = " + rc);
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

        //else it will output the Roll, Pitch and Yawn data
        Roll.setText("Roll :" + Float.toString(Math.round(event.values[0])));
        Pitch.setText("Pitch :" + Float.toString(Math.round(event.values[1])));
        Yaw.setText("Yaw :" + Float.toString(Math.round(event.values[2])));

        //Log.d("Number", String.valueOf(event.values[2]));
        //Log.d("Math", String.valueOf(.5*event.values[2]));

        CalculatePIDPitchServo1(event.values[1]);
        CalculatePIDYawServo1(event.values[2]);
        CalculatePIDPitchServo2(event.values[1]);
        CalculatePIDYawServo2(event.values[2]);
        CalculatePIDPitchServo3(event.values[1]);
        CalculatePIDYawServo3(event.values[2]);
        CalculatePIDPitchServo4(event.values[1]);
        CalculatePIDYawServo4(event.values[2]);

        //Log.d("Event Value 0", " "+ event.values[0]);
       // Log.d("Event Value 1", " "+ event.values[1]);
       // Log.d("Event Value 2", " "+ event.values[2]);



        Servo1_PWM_Output = Servo1_Pitch_PMW_Output + Servo1_Yaw_PMW_Output+Servo_Arr[0];
        Servo2_PWM_Output = Servo2_Pitch_PMW_Output + Servo2_Yaw_PMW_Output+Servo_Arr[1];
        Servo3_PWM_Output = Servo3_Pitch_PMW_Output + Servo3_Yaw_PMW_Output+Servo_Arr[2];
        Servo4_PWM_Output = Servo4_Pitch_PMW_Output + Servo4_Yaw_PMW_Output+Servo_Arr[3];

        //Log.d("Servo1_Pitch_PMW_Output",Float.toString(Servo1_Pitch_PMW_Output));
        //Log.d("Servo1_Yaw_PMW_Output",Float.toString(Servo1_Yaw_PMW_Output));
        //Log.d("Servo_Arr",Arrays.toString(Servo_Arr));
        //Log.d("Servo1_PWM_Output",Float.toString(Servo1_PWM_Output));


        Servo_Arr2[0]=(byte) Servo1_PWM_Output;
        Servo_Arr2[1]=(byte) Servo2_PWM_Output;
        Servo_Arr2[2]=(byte) Servo3_PWM_Output;
        Servo_Arr2[3]=(byte) Servo4_PWM_Output;

        appendLog(Float.toString(System.currentTimeMillis())+"\t"+Float.toString(event.values[1])+"\t"+Float.toString(event.values[2])+"\t"+Arrays.toString(Servo_Arr2));
        //Log.d("Serial Data", Arrays.toString(Servo_Arr2));

        for (byte j = 0; j < 4; j++) {
            Servo_Arr2[j] = chk_min_max_speed(Servo_Arr2[j]);
        } // Check for min and max servo speed
        Servo1.setText("Servo1%:" + Servo_Arr2[0]);
        Servo2.setText("Servo2%:" + Servo_Arr2[1]);
        Servo3.setText("Servo3%:" + Servo_Arr2[2]);
        Servo4.setText("Servo4%:" + Servo_Arr2[3]);
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
        if (speed > 90) speed = 90;
        if (speed < 0) speed = 0;
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

    // PID Servo 1 function
    public void CalculatePIDPitchServo1(float PitchErrorServo1) {

        byte i;
        float PIDValue;

        Servo1_Pitch_Error[Servo1_Pitch_Current_i]=PitchErrorServo1;

        //Log.d("Servo1PiErrorCurrenti]",Float.toString(PitchErrorServo1));

        for (i=0;i<10;i++){
            Servo1_Pitch_Accumulator += Math.round(Servo1_Pitch_Error[i]);
        }

        //for (float j : Servo1_Pitch_Error) {Servo1_Pitch_Accumulator += Math.round(j);}


        if(Servo1_Pitch_Current_i != 0) {
            Servo1_Pitch_Delta = Servo1_Pitch_Error[Servo1_Pitch_Current_i] - Servo1_Pitch_Error[Servo1_Pitch_Current_i - 1];
        }
        else{
            Servo1_Pitch_Delta = Servo1_Pitch_Error[Servo1_Pitch_Current_i] - Servo1_Pitch_Error[9];
        }

        PIDValue = (float) ((Servo1_Pitch_Error[Servo1_Pitch_Current_i] * Servo1_Pitch_PTerm) + (Servo1_Pitch_ITerm * Servo1_Pitch_Accumulator) + (Servo1_Pitch_DTerm * Servo1_Pitch_Delta));
        //Log.d("PitchPIDValue",Float.toString(PIDValue));

        Servo1_Pitch_PMW_Output=PIDValue;
        if(Servo1_Pitch_PMW_Output > 50) Servo1_Pitch_PMW_Output = 50;
        if(Servo1_Pitch_PMW_Output < -50) Servo1_Pitch_PMW_Output = -50;

        Servo1_Pitch_Current_i++;
        if(Servo1_Pitch_Current_i > 9) Servo1_Pitch_Current_i=0;

        /*
        for(i=0;i<10;i++) {
            Servo1_Pitch_Error[i + 1] = Servo1_Pitch_Error[i];
            Servo1_Pitch_Error[0] = -1*(DesiredOrientationPitch_Array[0] - PitchErrorServo1);}

        for (float j : Servo1_Pitch_Error) {
            Servo1_Pitch_Accumulator += Math.round(j);}

        PIDValue = (Servo1_Pitch_Error[0] * Servo1_Pitch_PTerm) + ((Servo1_Pitch_ITerm * Servo1_Pitch_Accumulator)) + (Servo1_Pitch_DTerm * ((Servo1_Pitch_Error[0] - Servo1_Pitch_Error[9])));

        Servo1_Pitch_PMW_Output=PIDValue;
        if(Servo1_Pitch_PMW_Output > 50) Servo1_Pitch_PMW_Output = 50;
        if(Servo1_Pitch_PMW_Output < -50) Servo1_Pitch_PMW_Output = -50;

        Servo1_Pitch_Accumulator=0;
        */

    }

    public void CalculatePIDYawServo1(float YawErrorServo1) {

        byte i;
        float PIDValue;

        Servo1_Yaw_Error[Servo1_Yaw_Current_i]=YawErrorServo1;
        //Log.d("Servo1YaErrorCurrenti]",Float.toString(YawErrorServo1));


        for (i=0;i<10;i++){
            Servo1_Yaw_Accumulator += Math.round(Servo1_Yaw_Error[i]);
        }

        if(Servo1_Yaw_Current_i != 0) {
            Servo1_Yaw_Delta = Servo1_Yaw_Error[Servo1_Yaw_Current_i] - Servo1_Yaw_Error[Servo1_Yaw_Current_i - 1];
        }
        else{
            Servo1_Yaw_Delta = Servo1_Yaw_Error[Servo1_Yaw_Current_i] - Servo1_Yaw_Error[9];
        }

        Accum1.setText("Accum1:" + Float.toString(Servo1_Yaw_Accumulator+Servo1_Pitch_Accumulator));
        PIDValue = (float) ((Servo1_Yaw_Error[Servo1_Yaw_Current_i] * Servo1_Yaw_PTerm) + (Servo1_Yaw_ITerm * Servo1_Yaw_Accumulator) + (Servo1_Yaw_DTerm * Servo1_Yaw_Delta));
        //Log.d("YawPIDValue",Float.toString(PIDValue));

        Servo1_Yaw_PMW_Output=PIDValue;
        if(Servo1_Yaw_PMW_Output > 50) Servo1_Yaw_PMW_Output = 50;
        if(Servo1_Yaw_PMW_Output < -50) Servo1_Yaw_PMW_Output = -50;

        Servo1_Yaw_Current_i++;
        if(Servo1_Yaw_Current_i > 9)Servo1_Yaw_Current_i=0;
    }

    // PID Servo 2 function
    public void CalculatePIDPitchServo2(float PitchErrorServo2) {

        byte i;
        float PIDValue;

        Servo2_Pitch_Error[Servo2_Pitch_Current_i]=PitchErrorServo2;

        for (i=0;i<10;i++){
            Servo2_Pitch_Accumulator += Math.round(Servo2_Pitch_Error[i]);
        }

        if(Servo2_Pitch_Current_i != 0) {
            Servo2_Pitch_Delta = Servo2_Pitch_Error[Servo2_Pitch_Current_i] - Servo2_Pitch_Error[Servo2_Pitch_Current_i - 1];
        }
        else{
            Servo2_Pitch_Delta = Servo2_Pitch_Error[Servo2_Pitch_Current_i] - Servo2_Pitch_Error[9];
        }

        PIDValue = (float) ((Servo2_Pitch_Error[Servo2_Pitch_Current_i] * Servo2_Pitch_PTerm) + (Servo2_Pitch_ITerm * Servo2_Pitch_Accumulator) + (Servo2_Pitch_DTerm * Servo2_Pitch_Delta));

        Servo2_Pitch_PMW_Output=PIDValue;
        if(Servo2_Pitch_PMW_Output > 50) Servo2_Pitch_PMW_Output = 50;
        if(Servo2_Pitch_PMW_Output < -50) Servo2_Pitch_PMW_Output = -50;

        Servo2_Pitch_Current_i++;
        if(Servo2_Pitch_Current_i > 9)Servo2_Pitch_Current_i=0;
    }

    public void CalculatePIDYawServo2(float YawErrorServo2) {

        byte i;
        float PIDValue;

        Servo2_Yaw_Error[Servo2_Yaw_Current_i]=YawErrorServo2;

        for (i=0;i<10;i++){
            Servo2_Yaw_Accumulator += Math.round(Servo2_Yaw_Error[i]);
        }

        if(Servo2_Yaw_Current_i != 0) {
            Servo2_Yaw_Delta = Servo2_Yaw_Error[Servo2_Yaw_Current_i] - Servo2_Yaw_Error[Servo2_Yaw_Current_i - 1];
        }
        else{
            Servo2_Yaw_Delta = Servo2_Yaw_Error[Servo2_Yaw_Current_i] - Servo2_Yaw_Error[9];
        }

        Accum2.setText("Accum2:" + Float.toString(Servo2_Yaw_Accumulator+Servo2_Pitch_Accumulator));

        PIDValue = (float) (-1*((Servo2_Yaw_Error[Servo2_Yaw_Current_i] * Servo2_Yaw_PTerm) + (Servo2_Yaw_ITerm * Servo2_Yaw_Accumulator) + (Servo2_Yaw_DTerm * Servo2_Yaw_Delta)));

        Servo2_Yaw_PMW_Output=PIDValue;
        if(Servo2_Yaw_PMW_Output > 50) Servo2_Yaw_PMW_Output = 50;
        if(Servo2_Yaw_PMW_Output < -50) Servo2_Yaw_PMW_Output = -50;

        Servo2_Yaw_Current_i++;
        if(Servo2_Yaw_Current_i > 9)Servo2_Yaw_Current_i=0;
    }

    // PID Servo 3 function
    public void CalculatePIDPitchServo3(float PitchErrorServo3) {

        byte i;
        float PIDValue;

        Servo3_Pitch_Error[Servo3_Pitch_Current_i]=PitchErrorServo3;

        for (i=0;i<10;i++){
            Servo3_Pitch_Accumulator += Math.round(Servo3_Pitch_Error[i]);
        }

        if(Servo3_Pitch_Current_i != 0) {
            Servo3_Pitch_Delta = Servo3_Pitch_Error[Servo3_Pitch_Current_i] - Servo3_Pitch_Error[Servo3_Pitch_Current_i - 1];
        }
        else{
            Servo3_Pitch_Delta = Servo3_Pitch_Error[Servo3_Pitch_Current_i] - Servo3_Pitch_Error[9];
        }

        PIDValue = (float) (-1*((Servo3_Pitch_Error[Servo3_Pitch_Current_i] * Servo3_Pitch_PTerm) + (Servo3_Pitch_ITerm * Servo3_Pitch_Accumulator) + (Servo3_Pitch_DTerm * Servo3_Pitch_Delta)));

        Servo3_Pitch_PMW_Output=PIDValue;
        if(Servo3_Pitch_PMW_Output > 50) Servo3_Pitch_PMW_Output = 50;
        if(Servo3_Pitch_PMW_Output < -50) Servo3_Pitch_PMW_Output = -50;

        Servo3_Pitch_Current_i++;
        if(Servo3_Pitch_Current_i > 9)Servo3_Pitch_Current_i=0;
    }

    public void CalculatePIDYawServo3(float YawErrorServo3) {

        byte i;
        float PIDValue;

        Servo3_Yaw_Error[Servo3_Yaw_Current_i]=YawErrorServo3;


        for (i=0;i<10;i++){
            Servo3_Yaw_Accumulator += Math.round(Servo3_Yaw_Error[i]);
        }

        if(Servo3_Yaw_Current_i != 0) {
            Servo3_Yaw_Delta = Servo3_Yaw_Error[Servo3_Yaw_Current_i] - Servo3_Yaw_Error[Servo3_Yaw_Current_i - 1];
        }
        else{
            Servo3_Yaw_Delta = Servo3_Yaw_Error[Servo3_Yaw_Current_i] - Servo3_Yaw_Error[9];
        }

        Accum3.setText("Accum3:" + Float.toString(Servo3_Yaw_Accumulator+Servo3_Pitch_Accumulator));

        PIDValue = (float) ((Servo3_Yaw_Error[Servo3_Yaw_Current_i] * Servo3_Yaw_PTerm) + (Servo3_Yaw_ITerm * Servo3_Yaw_Accumulator) + (Servo3_Yaw_DTerm * Servo3_Yaw_Delta));

        Servo3_Yaw_PMW_Output=PIDValue;
        if(Servo3_Yaw_PMW_Output > 50) Servo3_Yaw_PMW_Output = 50;
        if(Servo3_Yaw_PMW_Output < -50) Servo3_Yaw_PMW_Output = -50;

        Servo3_Yaw_Current_i++;
        if(Servo3_Yaw_Current_i > 9)Servo3_Yaw_Current_i=0;
    }

    // PID Servo 4 function
    public void CalculatePIDPitchServo4(float PitchErrorServo4) {

        byte i;
        float PIDValue;

        Servo4_Pitch_Error[Servo4_Pitch_Current_i]=PitchErrorServo4;

        for (i=0;i<10;i++){
            Servo4_Pitch_Accumulator += Math.round(Servo4_Pitch_Error[i]);
        }

        if(Servo4_Pitch_Current_i != 0) {
            Servo4_Pitch_Delta = Servo4_Pitch_Error[Servo4_Pitch_Current_i] - Servo4_Pitch_Error[Servo4_Pitch_Current_i - 1];
        }
        else{
            Servo4_Pitch_Delta = Servo4_Pitch_Error[Servo4_Pitch_Current_i] - Servo4_Pitch_Error[9];
        }

        PIDValue = (float) (-1*((Servo4_Pitch_Error[Servo4_Pitch_Current_i] * Servo4_Pitch_PTerm) + (Servo4_Pitch_ITerm * Servo4_Pitch_Accumulator) + (Servo4_Pitch_DTerm * Servo4_Pitch_Delta)));

        Servo4_Pitch_PMW_Output=PIDValue;
        if(Servo4_Pitch_PMW_Output > 50) Servo4_Pitch_PMW_Output = 50;
        if(Servo4_Pitch_PMW_Output < -50) Servo4_Pitch_PMW_Output = -50;

        Servo4_Pitch_Current_i++;
        if(Servo4_Pitch_Current_i > 9)Servo4_Pitch_Current_i=0;
    }

    public void CalculatePIDYawServo4(float YawErrorServo4) {

        byte i;
        float PIDValue;

        Servo4_Yaw_Error[Servo4_Yaw_Current_i]=YawErrorServo4;

        for (i=0;i<10;i++){
            Servo4_Yaw_Accumulator += Math.round(Servo4_Yaw_Error[i]);
        }

        if(Servo4_Yaw_Current_i != 0) {
            Servo4_Yaw_Delta = Servo4_Yaw_Error[Servo4_Yaw_Current_i] - Servo4_Yaw_Error[Servo4_Yaw_Current_i - 1];
        }
        else{
            Servo4_Yaw_Delta = Servo4_Yaw_Error[Servo4_Yaw_Current_i] - Servo4_Yaw_Error[9];
        }

        Accum4.setText("Accum4:" + Float.toString(Servo4_Yaw_Accumulator+Servo4_Pitch_Accumulator));

        PIDValue = (float) (-1*((Servo4_Yaw_Error[Servo4_Yaw_Current_i] * Servo4_Yaw_PTerm) + (Servo4_Yaw_ITerm * Servo4_Yaw_Accumulator) + (Servo4_Yaw_DTerm * Servo4_Yaw_Delta)));

        Servo4_Yaw_PMW_Output=PIDValue;
        if(Servo4_Yaw_PMW_Output > 50) Servo4_Yaw_PMW_Output = 50;
        if(Servo4_Yaw_PMW_Output < -50) Servo4_Yaw_PMW_Output = -50;

        Servo4_Yaw_Current_i++;
        if(Servo4_Yaw_Current_i > 9)Servo4_Yaw_Current_i=0;
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