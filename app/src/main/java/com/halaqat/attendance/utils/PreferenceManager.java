package com.halaqat.attendance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.halaqat.attendance.models.User;

public class PreferenceManager {
    private static final String PREF_NAME = "HalaqatAttendancePrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private SharedPreferences preferences;
    private Gson gson;
    
    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public void saveToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }
    
    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        preferences.edit().putString(KEY_USER, userJson).apply();
    }
    
    public User getUser() {
        String userJson = preferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    
    public void setLoggedIn(boolean isLoggedIn) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }
    
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void clearSession() {
        preferences.edit().clear().apply();
    }
    
    public String getAuthToken() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
}