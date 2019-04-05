package edu.und.seau.common;

import android.content.SharedPreferences;

public class SharedResourceManager {
    public static final String KEY_UAV_NAME = "UAV_NAME";
    public static final String KEY_UAV_ID = "UAV_ID";

    private static SharedPreferences _preferences;

    public static void setSharedPreferences(SharedPreferences preferences){
        _preferences = preferences;
    }

    public static SharedPreferences getPreferences(){
        return _preferences;
    }

}
