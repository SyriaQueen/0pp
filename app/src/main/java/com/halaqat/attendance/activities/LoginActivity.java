package com.halaqat.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.models.LoginResponse;
import com.halaqat.attendance.models.User;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private PreferenceManager prefManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        initViews();
        setupListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
    }
    
    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty()) {
            etUsername.setError("الرجاء إدخال اسم المستخدم");
            return;
        }
        
        if (password.isEmpty()) {
            etPassword.setError("الرجاء إدخال كلمة المرور");
            return;
        }
        
        performLogin(username, password);
    }
    
    private void performLogin(String username, String password) {
        showLoading(true);
        
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        
        ApiClient.getApiService().login(credentials).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        
                        prefManager.saveToken(loginResponse.getToken());
                        prefManager.saveUser(loginResponse.getUser());
                        prefManager.setLoggedIn(true);
                        
                        navigateToDashboard(loginResponse.getUser().getRole());
                    } else {
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "فشل تسجيل الدخول", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this, "خطأ في الاتصال: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(this, "نوع مستخدم غير صالح", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }
}