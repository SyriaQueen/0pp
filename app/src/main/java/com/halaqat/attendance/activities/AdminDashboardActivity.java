package com.halaqat.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.models.User;
import com.halaqat.attendance.utils.PreferenceManager;

public class AdminDashboardActivity extends AppCompatActivity {
    
    private TextView tvWelcome;
    private CardView cardManageUsers, cardManageHalaqat, cardManageFawj, 
                     cardAttendance, cardReports, cardChangePassword;
    private PreferenceManager prefManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        setupToolbar();
        initViews();
        setupListeners();
        displayUserInfo();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("لوحة تحكم المدير");
        }
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        cardManageUsers = findViewById(R.id.card_manage_users);
        cardManageHalaqat = findViewById(R.id.card_manage_halaqat);
        cardManageFawj = findViewById(R.id.card_manage_fawj);
        cardAttendance = findViewById(R.id.card_attendance);
        cardReports = findViewById(R.id.card_reports);
        cardChangePassword = findViewById(R.id.card_change_password);
    }
    
    private void setupListeners() {
        cardManageUsers.setOnClickListener(v -> 
            startActivity(new Intent(this, ManageUsersActivity.class)));
        
        cardManageHalaqat.setOnClickListener(v -> 
            startActivity(new Intent(this, ManageHalaqatActivity.class)));
        
        cardManageFawj.setOnClickListener(v -> 
            startActivity(new Intent(this, ManageFawjActivity.class)));
        
        cardAttendance.setOnClickListener(v -> 
            startActivity(new Intent(this, AttendanceActivity.class)));
        
        cardReports.setOnClickListener(v -> 
            startActivity(new Intent(this, AttendanceReportActivity.class)));
        
        cardChangePassword.setOnClickListener(v -> 
            startActivity(new Intent(this, ChangePasswordActivity.class)));
    }
    
    private void displayUserInfo() {
        User user = prefManager.getUser();
        if (user != null) {
            tvWelcome.setText("مرحباً، " + user.getFullName());
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        prefManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}