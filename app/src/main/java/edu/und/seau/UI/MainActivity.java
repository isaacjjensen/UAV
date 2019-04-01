package edu.und.seau.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import edu.und.seau.common.SharedSettingsManager;
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
        SharedSettingsManager.setContext(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        PresentationComponent component = DaggerPresentationComponent.create();
        presenter = component.getMainPresenter();
        presenter.setView(this);

        binding.connectButton.setOnClickListener(v -> {
            onConnectButtonClicked();
        });

        if (!canAccessLocation()) {
            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);
        }
    }

    private void onConnectButtonClicked()
    {
        presenter.onConnectClicked();
    }

    public void onConnected()
    {
        startActivity(new Intent(this, connect_screen.class));
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
