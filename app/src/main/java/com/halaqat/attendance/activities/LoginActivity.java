package com.halaqat.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
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
    
    private static final String TAG = "LoginActivity";
    
    private TextInputEditText etUsername, etPassword;
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
        
        Log.d(TAG, "LoginActivity created");
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
            etUsername.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            etPassword.setError("الرجاء إدخال كلمة المرور");
            etPassword.requestFocus();
            return;
        }
        
        performLogin(username, password);
    }
    
    private void performLogin(String username, String password) {
        showLoading(true);
        
        // إنشاء الـ Request Body
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);
        
        Log.d(TAG, "Attempting login for user: " + username);
        Log.d(TAG, "API Base URL: " + ApiClient.getBaseUrl());
        
        try {
            ApiClient.getApiService().login(credentials)
                    .enqueue(new Callback<ApiResponse<LoginResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<LoginResponse>> call, 
                                     Response<ApiResponse<LoginResponse>> response) {
                    showLoading(false);
                    
                    Log.d(TAG, "Response code: " + response.code());
                    
                    if (response.isSuccessful()) {
                        ApiResponse<LoginResponse> apiResponse = response.body();
                        
                        if (apiResponse != null) {
                            Log.d(TAG, "Response success: " + apiResponse.isSuccess());
                            Log.d(TAG, "Response message: " + apiResponse.getMessage());
                            
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                LoginResponse loginResponse = apiResponse.getData();
                                
                                // التحقق من البيانات
                                if (loginResponse.getToken() != null && loginResponse.getUser() != null) {
                                    Log.d(TAG, "Login successful!");
                                    Log.d(TAG, "Token: " + loginResponse.getToken().substring(0, 20) + "...");
                                    Log.d(TAG, "User role: " + loginResponse.getUser().getRole());
                                    
                                    // حفظ البيانات
                                    prefManager.saveToken(loginResponse.getToken());
                                    prefManager.saveUser(loginResponse.getUser());
                                    prefManager.setLoggedIn(true);
                                    
                                    Toast.makeText(LoginActivity.this, 
                                            "مرحباً " + loginResponse.getUser().getFullName(), 
                                            Toast.LENGTH_SHORT).show();
                                    
                                    // الانتقال للصفحة المناسبة
                                    navigateToDashboard(loginResponse.getUser().getRole());
                                } else {
                                    Log.e(TAG, "Token or User is null");
                                    showError("خطأ في بيانات الاستجابة");
                                }
                            } else {
                                String message = apiResponse.getMessage();
                                Log.e(TAG, "Login failed: " + message);
                                showError(message != null && !message.isEmpty() 
                                        ? message 
                                        : "فشل تسجيل الدخول");
                            }
                        } else {
                            Log.e(TAG, "Response body is null");
                            showError("خطأ في الاستجابة من الخادم");
                        }
                    } else {
                        Log.e(TAG, "Response not successful: " + response.code());
                        
                        try {
                            String errorBody = response.errorBody() != null 
                                    ? response.errorBody().string() 
                                    : "Unknown error";
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        
                        handleErrorResponse(response.code());
                    }
                }
                
                @Override
                public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                    showLoading(false);
                    Log.e(TAG, "Network error: " + t.getMessage(), t);
                    
                    String errorMessage = "خطأ في الاتصال بالخادم";
                    
                    if (t.getMessage() != null) {
                        if (t.getMessage().contains("Unable to resolve host")) {
                            errorMessage = "تعذر الاتصال بالخادم. تحقق من الاتصال بالإنترنت";
                        } else if (t.getMessage().contains("timeout")) {
                            errorMessage = "انتهت مهلة الاتصال. حاول مرة أخرى";
                        } else if (t.getMessage().contains("Connection refused")) {
                            errorMessage = "الخادم غير متاح. تأكد من تشغيل Backend";
                        }
                    }
                    
                    showError(errorMessage + "\n" + t.getMessage());
                }
            });
        } catch (Exception e) {
            showLoading(false);
            Log.e(TAG, "Exception during login", e);
            showError("حدث خطأ: " + e.getMessage());
        }
    }
    
    private void handleErrorResponse(int code) {
        String message;
        switch (code) {
            case 400:
                message = "بيانات غير صحيحة";
                break;
            case 401:
                message = "اسم المستخدم أو كلمة المرور غير صحيحة";
                break;
            case 404:
                message = "الخادم غير متاح";
                break;
            case 500:
                message = "خطأ في الخادم";
                break;
            default:
                message = "خطأ غير متوقع (كود: " + code + ")";
        }
        showError(message);
    }
    
    private void navigateToDashboard(String role) {
        Intent intent;
        
        if (role == null || role.isEmpty()) {
            showError("نوع مستخدم غير صالح");
            return;
        }
        
        switch (role.toLowerCase()) {
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
                showError("نوع مستخدم غير معروف: " + role);
                return;
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!show);
        }
    }
    
    private void showError(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}
