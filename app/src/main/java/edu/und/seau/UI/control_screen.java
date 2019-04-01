package edu.und.seau.UI;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import edu.und.seau.uav.R;
import edu.und.seau.uav.databinding.ControlScreenBinding;

public class control_screen extends AppCompatActivity {
    private ControlScreenBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.control_screen_old);
    }
}
