package edu.und.seau.ui;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import edu.und.seau.presentation.views.ControlScreenView;
import edu.und.seau.uav.R;
import edu.und.seau.uav.databinding.ControlScreenBinding;

public class control_screen extends AppCompatActivity implements ControlScreenView {
    private ControlScreenBinding binding;


    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.control_screen);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);

    }

    public SharedPreferences getSharedPreferences(){
        return getPreferences(MODE_PRIVATE);
    }

    @Override
    public Bundle GetExtraData(){
        return getIntent().getExtras();
    }

    private void startLocationUpdates() {

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult == null){
                    return;
                }
                Location lastLocation = locationResult.getLastLocation();
            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        //fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
}
