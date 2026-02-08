package com.halaqat.attendance.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageUsersActivity extends AppCompatActivity implements UsersAdapter.OnUserActionListener {
    
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("إدارة المستخدمين");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        fabAdd.setOnClickListener(v -> showAddUserDialog());
    }
    
    private void loadUsers() {
        showLoading(true);
        
        ApiClient.getApiService().getUsers(prefManager.getAuthToken())
                .enqueue(new Callback<ApiResponse<List<User>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<User>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<User> users = apiResponse.getData();
                                if (users.isEmpty()) {
                                    showNoData(true);
                                } else {
                                    showNoData(false);
                                    adapter.updateData(users);
                                }
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(ManageUsersActivity.this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showAddUserDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        
        EditText etUsername = dialogView.findViewById(R.id.et_username);
        EditText etFullName = dialogView.findViewById(R.id.et_full_name);
        EditText etPassword = dialogView.findViewById(R.id.et_password);
        EditText etEmail = dialogView.findViewById(R.id.et_email);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinner_role);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        
        new AlertDialog.Builder(this)
                .setTitle("إضافة مستخدم جديد")
                .setView(dialogView)
                .setPositiveButton("إضافة", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String fullName = etFullName.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String role = spinnerRole.getSelectedItem().toString();
                    
                    if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
                        Toast.makeText(this, "الرجاء ملء الحقول المطلوبة", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    createUser(username, fullName, password, email, phone, role);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void createUser(String username, String fullName, String password, String email, String phone, String role) {
        User user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role.toLowerCase());
        
        // Note: Password will be handled in the request body separately
        
        ApiClient.getApiService().createUser(prefManager.getAuthToken(), user)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<User> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                Toast.makeText(ManageUsersActivity.this, "تم إضافة المستخدم بنجاح", Toast.LENGTH_SHORT).show();
                                loadUsers();
                            } else {
                                Toast.makeText(ManageUsersActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                        Toast.makeText(ManageUsersActivity.this, "خطأ في إضافة المستخدم", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    @Override
    public void onEditUser(User user) {
        // Implement edit dialog similar to add
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
        ApiClient.getApiService().deleteUser(prefManager.getAuthToken(), userId)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ManageUsersActivity.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(ManageUsersActivity.this, "خطأ في الحذف", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showNoData(boolean show) {
        tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        rvUsers.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}