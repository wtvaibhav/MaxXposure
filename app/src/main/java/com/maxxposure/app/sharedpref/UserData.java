package com.maxxposure.app.sharedpref;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.maxxposure.app.MaxXposureApplication;


public class UserData {

    private static UserData _instance = null;
    private static SharedPreferences _sharedPreferences = null;
    private static Editor _sharedPrefEditor = null;
    private final String SHARED_PREFERENCE_NAME = "user_data";


    private final String TOKEN = "TOKEN";
    private final String USER_ID = "USER_ID";
    private final String ADMIN_ID = "ADMIN_ID";
    private final String FULL_NAME = "FULL_NAME";
    private final String IMAGE_URL = "IMAGE_URL";
    private final String SAVE_IMAGE_DATA = "SAVE_IMAGE_DATA";


    private UserData() {

    }

    public static UserData getInstance() {
        if (_instance == null) {
            _instance = new UserData();
            _instance._initSharedPreferences();
        }
        return _instance;
    }

    /**
     * This method is used to initialized {@link SharedPreferences} and
     * {@link Editor}
     */
    public void _initSharedPreferences() {
        _sharedPreferences = _getSharedPref();
        _sharedPrefEditor = _getSharedPrefEditor();
    }

    /**
     * Method to get the SharedPreferences.
     *
     * @return the {@link SharedPreferences} object.
     */
    private SharedPreferences _getSharedPref() {
        if (_sharedPreferences == null) {
            _sharedPreferences = MaxXposureApplication.appContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
        return _sharedPreferences;
    }

    /**
     * Method to get the {@link Editor} for writing values to {@link SharedPreferences}.
     *
     * @return the {@link Editor} object.
     */
    private Editor _getSharedPrefEditor() {
        if (_sharedPrefEditor == null) {
            _sharedPrefEditor = _getSharedPref().edit();
        }
        return _sharedPrefEditor;
    }


    public void setTOKEN(String token) {
        _getSharedPrefEditor().putString(TOKEN, token).commit();
    }

    public String getTOKEN() {
        return _getSharedPref().getString(TOKEN, null);
    }

    public void setUSER_ID(String langType) {
        _getSharedPrefEditor().putString(USER_ID, langType).commit();
    }

    public String getUSER_ID() {
        return _getSharedPref().getString(USER_ID, null);
    }

    public void setADMIN_ID(String langType) {
        _getSharedPrefEditor().putString(ADMIN_ID, langType).commit();
    }

    public String getADMIN_ID() {
        return _getSharedPref().getString(ADMIN_ID, null);
    }

    public void setFullName(String langType) {
        _getSharedPrefEditor().putString(FULL_NAME, langType).commit();
    }

    public String getFullName() {
        return _getSharedPref().getString(FULL_NAME, null);
    }

    public void setImage(String langType) {
        _getSharedPrefEditor().putString(IMAGE_URL, langType).commit();
    }

    public String setImage() {
        return _getSharedPref().getString(IMAGE_URL, null);
    }

    public void saveImageData(boolean save) {
        _getSharedPrefEditor().putBoolean(SAVE_IMAGE_DATA, save).commit();
    }

    public boolean isSavedImageData() {
        return _getSharedPref().getBoolean(SAVE_IMAGE_DATA, false);
    }


    public void clearData() {
        _getSharedPrefEditor().clear();
        _getSharedPrefEditor().commit();
    }


}
