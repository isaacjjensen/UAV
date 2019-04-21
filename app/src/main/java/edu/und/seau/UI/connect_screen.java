package edu.und.seau.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import edu.und.seau.di.components.DaggerPresentationComponent;
import edu.und.seau.di.components.PresentationComponent;
import edu.und.seau.presentation.presenters.ConnectionScreenPresenter;
import edu.und.seau.presentation.views.ConnectionScreenView;
import edu.und.seau.uav.R;
import edu.und.seau.uav.databinding.ConnectScreenBinding;

public class connect_screen extends AppCompatActivity implements ConnectionScreenView {
    public static final String KEY_PASSED_UAV_ID = "UAV_ID";
    public static final String KEY_UAV_NAME = "UAV_NAME";

    ConnectScreenBinding binding;
    PresentationComponent component;
    ConnectionScreenPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.connect_screen);
        component = DaggerPresentationComponent.create();
        presenter = component.getConnectionScreenPresenter();

        HandleExtraData();

        presenter.setView(this);
    }

    private void HandleExtraData(){
        Bundle extraData = getIntent().getExtras();
        if(extraData != null){
            if(extraData.containsKey(KEY_PASSED_UAV_ID)){
                String uavID = Objects.requireNonNull(extraData.getCharSequence(KEY_PASSED_UAV_ID)).toString();
                setUavID(uavID);
            }
            if(extraData.containsKey(KEY_UAV_NAME)){
                String uavName = Objects.requireNonNull(extraData.getCharSequence(KEY_UAV_NAME).toString());
                setUavName(uavName);
            }
        }
    }

    @Override
    public void onConnected() {
        startActivity(new Intent(this, control_screen.class));
    }

    public SharedPreferences getSharedPreferences(){
        return getPreferences(MODE_PRIVATE);
    }

    @Override
    public void setUavName(String name) {
        binding.nameInfo.setText(name);
    }

    @Override
    public void setUavID(String id) {
        binding.idValue.setText(id);
    }
}
