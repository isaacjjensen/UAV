package edu.und.seau.presentation.views;

import android.content.SharedPreferences;

public interface ConnectionScreenView {

    void onConnected();
    void setUavName(String name);
    void setUavID(String id);
    SharedPreferences getSharedPreferences();

}
