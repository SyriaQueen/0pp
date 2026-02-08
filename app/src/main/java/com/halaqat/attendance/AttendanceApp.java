package com.halaqat.attendance;

import android.app.Application;
import com.halaqat.attendance.utils.PreferenceManager;
import com.halaqat.attendance.network.ApiClient;

public class AttendanceApp extends Application {
    
    private static AttendanceApp instance;
    private PreferenceManager preferenceManager;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferenceManager = new PreferenceManager(this);
        ApiClient.init(this);
    }
    
    public static AttendanceApp getInstance() {
        return instance;
    }
    
    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}