package com.halaqat.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.utils.PreferenceManager;
import com.halaqat.attendance.models.User;

public class SplashActivity extends AppCompatActivity {
    
    private static final int SPLASH_DURATION = 2000;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        new Handler().postDelayed(() -> {
            PreferenceManager prefManager = AttendanceApp.getInstance().getPreferenceManager();
            
            if (prefManager.isLoggedIn()) {
                User user = prefManager.getUser();
                if (user != null) {
                    navigateToDashboard(user.getRole());
                } else {
                    navigateToLogin();
                }
            } else {
                navigateToLogin();
            }
            finish();
        }, SPLASH_DURATION);
    }
    
    private void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "admin":
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "teacher":
                intent = new Intent(this, TeacherDashboardActivity.class);
                break;
            case "parent":
                intent = new Intent(this, ParentDashboardActivity.class);
                break;
            default:
                intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
    }
    
    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}