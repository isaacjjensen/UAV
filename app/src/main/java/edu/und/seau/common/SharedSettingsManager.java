package edu.und.seau.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedSettingsManager {

    private static SharedPreferences preferences;

    public static final String KEY_UAVID = "UAVID";

    private static Context context;


    public static void setContext(Context inContext)
    {
        context = inContext;
    }

    public static void StoreSetting(String key, String value)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key,value).apply();
    }

    public static String GetSetting(String key){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,null);
    }

}
