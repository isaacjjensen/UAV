package edu.und.seau.presentation.views;

import android.content.SharedPreferences;
import android.os.Bundle;

public interface ConnectionScreenView {

    void onConnected(String uavID);
    void setUavName(String name);
    void setUavID(String id);
    SharedPreferences getSharedPreferences();
    Bundle GetExtraData();

}
