package com.halaqat.attendance.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChange;
    private ProgressBar progressBar;
    private PreferenceManager prefManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        setupToolbar();
        initViews();
        setupListeners();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("تغيير كلمة المرور");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChange = findViewById(R.id.btn_change);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupListeners() {
        btnChange.setOnClickListener(v -> attemptChangePassword());
    }
    
    private void attemptChangePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        if (currentPassword.isEmpty()) {
            etCurrentPassword.setError("الرجاء إدخال كلمة المرور الحالية");
            return;
        }
        
        if (newPassword.isEmpty()) {
            etNewPassword.setError("الرجاء إدخال كلمة المرور الجديدة");
            return;
        }
        
        if (newPassword.length() < 6) {
            etNewPassword.setError("كلمة المرور يجب أن تكون 6 أحرف على الأقل");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("كلمات المرور غير متطابقة");
            return;
        }
        
        changePassword(currentPassword, newPassword);
    }
    
    private void changePassword(String currentPassword, String newPassword) {
        showLoading(true);
        
        Map<String, String> passwords = new HashMap<>();
        passwords.put("currentPassword", currentPassword);
        passwords.put("newPassword", newPassword);
        
        ApiClient.getApiService().changePassword(prefManager.getAuthToken(), passwords)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Object> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                Toast.makeText(ChangePasswordActivity.this, 
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, 
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, 
                                    "فشل تغيير كلمة المرور", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(ChangePasswordActivity.this, 
                                "خطأ في الاتصال: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnChange.setEnabled(!show);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}