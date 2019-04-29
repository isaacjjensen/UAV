package edu.und.seau.presentation.views;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

public interface ConnectionScreenView {

    void onConnected(String uavID, String userID);
    void setUavName(String name);
    void setUavID(String id);
    void setQRCode(Bitmap QRCode);
    SharedPreferences getSharedPreferences();
    Bundle GetExtraData();

}
