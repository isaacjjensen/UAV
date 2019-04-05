package edu.und.seau.presentation.views;

import android.content.SharedPreferences;

public interface MainView {
    void onConnected();
    String getSelectedName();
    SharedPreferences getSharedPreferences();
}
