package edu.und.seau.presentation.views;

import android.content.SharedPreferences;
import android.os.Bundle;

public interface ControlScreenView {
    SharedPreferences getSharedPreferences();
    Bundle GetExtraData();

}
