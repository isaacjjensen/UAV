package edu.und.seau.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import edu.und.seau.di.components.DaggerPresentationComponent;
import edu.und.seau.di.components.PresentationComponent;
import edu.und.seau.presentation.presenters.MainScreenPresenter;
import edu.und.seau.presentation.views.MainView;
import edu.und.seau.uav.R;
import edu.und.seau.uav.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MainView {


    private MainScreenPresenter presenter;
    private ActivityMainBinding binding;
    private static final String TAG = MainActivity.class.getSimpleName();


    private static final String[] LOCATION_PERMS={
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final int LOCATION_REQUEST=1338;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        PresentationComponent component = DaggerPresentationComponent.create();

        presenter = component.getMainPresenter();
        presenter.setView(this);

        binding.connectButton.setOnClickListener(v -> onConnectButtonClicked());

        if (!canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }


    private void onConnectButtonClicked()
    {
        presenter.onConnectClicked();
    }

    public void setID(String ID){
        if(binding != null){
            binding.idTextView.setText(ID);
        }
    }

    public void onConnected()
    {
        Intent connectScreenIntent = new Intent(this, connect_screen.class);
        connectScreenIntent.putExtra(connect_screen.KEY_PASSED_UAV_ID,binding.idTextView.getText());
        connectScreenIntent.putExtra(connect_screen.KEY_UAV_NAME, binding.selectedName.getText());
        startActivity(connectScreenIntent);
    }

    public SharedPreferences getSharedPreferences(){
        return getPreferences(MODE_PRIVATE);
    }

    public String getSelectedName(){
        return binding.selectedName.getText().toString();
    }
    private boolean canAccessLocation() {
        return((hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)) && (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)));
    }

    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }
}
