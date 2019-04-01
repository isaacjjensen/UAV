package edu.und.seau.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import edu.und.seau.di.components.DaggerPresentationComponent;
import edu.und.seau.di.components.PresentationComponent;
import edu.und.seau.presentation.presenters.ConnectionScreenPresenter;
import edu.und.seau.presentation.views.ConnectionScreenView;
import edu.und.seau.uav.R;
import edu.und.seau.uav.databinding.ConnectScreenBinding;

public class connect_screen extends AppCompatActivity implements ConnectionScreenView {
    ConnectScreenBinding binding;
    PresentationComponent component;
    ConnectionScreenPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.connect_screen);
        component = DaggerPresentationComponent.create();
        presenter = component.getConnectionScreenPresenter();
        presenter.setView(this);
    }

    @Override
    public void onConnected() {
        startActivity(new Intent(this, control_screen.class));
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
