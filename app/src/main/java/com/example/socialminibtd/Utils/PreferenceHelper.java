package com.example.socialminibtd.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {

    private SharedPreferences helper;
    private Context PH_context;
    private final String LANGUAGE ="language";

    private final String DEVICE_TOKEN = "device_token";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";

    public PreferenceHelper(Context ph_context) {
        helper = ph_context.getSharedPreferences("socialBTD",Context.MODE_PRIVATE);
        this.PH_context = ph_context;
    }

    public void putLanguage(String put_language){

        SharedPreferences.Editor editor = helper.edit();
        editor.putString(LANGUAGE,put_language);
        editor.apply();


    }
    public String getLanguage(){

        return helper.getString(LANGUAGE,"");

    }


    public void putDeviceToken(String deviceToken) {
        SharedPreferences.Editor edit = helper.edit();
        edit.putString(DEVICE_TOKEN, deviceToken);
        edit.apply();
    }

    public String getDeviceToken() {
        return helper.getString(DEVICE_TOKEN, null);

    }

    public void putAppVersion(int version) {
        SharedPreferences.Editor edit = helper.edit();
        edit.putInt(PROPERTY_APP_VERSION, version);
        edit.apply();
    }

    public int getAppVersion() {
        return helper.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    }



    public void putRegisterationID(String RegID) {
        SharedPreferences.Editor edit = helper.edit();
        edit.putString(PROPERTY_REG_ID, RegID);
        edit.apply();
    }

    public String getRegistrationID() {
        return helper.getString(PROPERTY_REG_ID, "");
    }
}