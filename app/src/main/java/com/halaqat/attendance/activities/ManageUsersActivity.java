package com.halaqat.attendance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.adapters.UsersAdapter;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.models.User;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity implements UsersAdapter.OnUserActionListener {
    
    private static final String TAG = "ManageUsersActivity";
    
    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FloatingActionButton fabAdd;
    private PreferenceManager prefManager;
    private UsersAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadUsers();
    }
    
    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("إدارة المستخدمين");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar", e);
        }
    }
    
    private void initViews() {
        rvUsers = findViewById(R.id.rv_users);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        fabAdd = findViewById(R.id.fab_add);
    }
    
    private void setupRecyclerView() {
        adapter = new UsersAdapter(this, new ArrayList<>(), this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);
    }
    
    private void setupListeners() {
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddUserDialog());
        }
    }
    
    private void loadUsers() {
        if (!isActivityValid()) {
            return;
        }
        
        showLoading(true);
        
        String token = prefManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            showLoading(false);
            showError("خطأ في المصادقة. الرجاء تسجيل الدخول مرة أخرى");
            return;
        }
        
        Log.d(TAG, "Loading users with token...");
        
        try {
            ApiClient.getApiService().getUsers(token)
                    .enqueue(new Callback<ApiResponse<List<User>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<User>>> call, 
                                             Response<ApiResponse<List<User>>> response) {
                            
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                showLoading(false);
                                
                                Log.d(TAG, "Response code: " + response.code());
                                
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<List<User>> apiResponse = response.body();
                                    
                                    Log.d(TAG, "Response success: " + apiResponse.isSuccess());
                                    Log.d(TAG, "Response message: " + apiResponse.getMessage());
                                    
                                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                        List<User> users = apiResponse.getData();
                                        
                                        Log.d(TAG, "Users loaded: " + users.size());
                                        
                                        if (users.isEmpty()) {
                                            showNoData(true);
                                        } else {
                                            showNoData(false);
                                            if (adapter != null) {
                                                adapter.updateData(users);
                                            }
                                        }
                                    } else {
                                        String message = apiResponse.getMessage();
                                        showError(message != null ? message : "فشل تحميل المستخدمين");
                                    }
                                } else {
                                    Log.e(TAG, "Response not successful: " + response.code());
                                    
                                    try {
                                        if (response.errorBody() != null) {
                                            String errorBody = response.errorBody().string();
                                            Log.e(TAG, "Error body: " + errorBody);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error reading error body", e);
                                    }
                                    
                                    handleErrorResponse(response.code());
                                }
                            });
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                showLoading(false);
                                Log.e(TAG, "Network error: " + t.getMessage(), t);
                                
                                String errorMessage = "خطأ في الاتصال بالخادم";
                                if (t.getMessage() != null) {
                                    if (t.getMessage().contains("Unable to resolve host")) {
                                        errorMessage = "تعذر الاتصال بالخادم. تحقق من الإنترنت";
                                    } else if (t.getMessage().contains("timeout")) {
                                        errorMessage = "انتهت مهلة الاتصال";
                                    } else if (t.getMessage().contains("Connection refused")) {
                                        errorMessage = "الخادم غير متاح. تأكد من تشغيل Backend";
                                    }
                                }
                                
                                showError(errorMessage + "\n" + t.getMessage());
                            });
                        }
                    });
        } catch (Exception e) {
            showLoading(false);
            Log.e(TAG, "Exception while loading users", e);
            showError("حدث خطأ: " + e.getMessage());
        }
    }
    
    private void handleErrorResponse(int code) {
        String message;
        switch (code) {
            case 401:
                message = "غير مصرح. الرجاء تسجيل الدخول مرة أخرى";
                break;
            case 403:
                message = "ليس لديك صلاحية للوصول";
                break;
            case 404:
                message = "الخدمة غير متوفرة";
                break;
            case 500:
                message = "خطأ في الخادم";
                break;
            default:
                message = "خطأ غير متوقع (كود: " + code + ")";
        }
        showError(message);
    }
    
    private void showAddUserDialog() {
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
            
            EditText etUsername = dialogView.findViewById(R.id.et_username);
            EditText etFullName = dialogView.findViewById(R.id.et_full_name);
            EditText etPassword = dialogView.findViewById(R.id.et_password);
            EditText etEmail = dialogView.findViewById(R.id.et_email);
            EditText etPhone = dialogView.findViewById(R.id.et_phone);
            Spinner spinnerRole = dialogView.findViewById(R.id.spinner_role);
            
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.user_roles, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRole.setAdapter(spinnerAdapter);
            
            new AlertDialog.Builder(this)
                    .setTitle("إضافة مستخدم جديد")
                    .setView(dialogView)
                    .setPositiveButton("إضافة", (dialog, which) -> {
                        String username = etUsername.getText().toString().trim();
                        String fullName = etFullName.getText().toString().trim();
                        String password = etPassword.getText().toString().trim();
                        String email = etEmail.getText().toString().trim();
                        String phone = etPhone.getText().toString().trim();
                        String role = spinnerRole.getSelectedItem().toString().toLowerCase();
                        
                        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                            showError("الرجاء ملء الحقول المطلوبة");
                            return;
                        }
                        
                        addUser(username, fullName, password, email, phone, role);
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing add dialog", e);
            showError("خطأ في عرض النافذة: " + e.getMessage());
        }
    }
    
    private void addUser(String username, String fullName, String password, 
                        String email, String phone, String role) {
        String token = prefManager.getAuthToken();
        if (token == null) {
            showError("خطأ في المصادقة");
            return;
        }
        
        // ✅ تم تغيير Map<String, Object> إلى Map<String, String>
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("full_name", fullName);
        userData.put("password", password);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("role", role);
        userData.put("is_active", "true");
        
        ApiClient.getApiService().createUser(token, userData)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, 
                                         Response<ApiResponse<User>> response) {
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<User> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    showSuccess("تمت الإضافة بنجاح");
                                    loadUsers();
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("فشلت الإضافة");
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            showError("خطأ في الاتصال: " + t.getMessage());
                        });
                    }
                });
    }
    
    @Override
    public void onEditUser(User user) {
        // Implementation similar to add but with update
        showSuccess("تعديل المستخدم قيد التطوير");
    }
    
    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف " + user.getFullName() + "؟")
                .setPositiveButton("حذف", (dialog, which) -> deleteUser(user.getId()))
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void deleteUser(int userId) {
        String token = prefManager.getAuthToken();
        if (token == null) {
            showError("خطأ في المصادقة");
            return;
        }
        
        ApiClient.getApiService().deleteUser(token, userId)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, 
                                         Response<ApiResponse<Object>> response) {
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Object> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    showSuccess("تم الحذف بنجاح");
                                    loadUsers();
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("فشل الحذف");
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            showError("خطأ في الاتصال: " + t.getMessage());
                        });
                    }
                });
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvUsers != null) {
            rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showNoData(boolean show) {
        if (tvNoData != null) {
            tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvUsers != null) {
            rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showError(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error: " + message);
        }
    }
    
    private void showSuccess(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean isActivityValid() {
        return !isFinishing() && !isDestroyed();
    }
    
    private void safeRunOnUiThread(Runnable action) {
        if (isActivityValid()) {
            try {
                runOnUiThread(action);
            } catch (Exception e) {
                Log.e(TAG, "Error running on UI thread", e);
            }
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
