package com.halaqat.attendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.adapters.StudentAttendanceAdapter;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.models.Student;
import com.halaqat.attendance.models.User;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParentDashboardActivity extends AppCompatActivity {
    
    private TextView tvWelcome, tvNoStudents;
    private RecyclerView rvStudents;
    private ProgressBar progressBar;
    private CardView cardChangePassword;
    private PreferenceManager prefManager;
    private StudentAttendanceAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        setupListeners();
        displayUserInfo();
        loadStudents();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("لوحة تحكم ولي الأمر");
        }
    }
    
    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvNoStudents = findViewById(R.id.tv_no_students);
        rvStudents = findViewById(R.id.rv_students);
        progressBar = findViewById(R.id.progress_bar);
        cardChangePassword = findViewById(R.id.card_change_password);
    }
    
    private void setupRecyclerView() {
        adapter = new StudentAttendanceAdapter(this, new ArrayList<>());
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);
    }
    
    private void setupListeners() {
        cardChangePassword.setOnClickListener(v -> 
            startActivity(new Intent(this, ChangePasswordActivity.class)));
    }
    
    private void displayUserInfo() {
        User user = prefManager.getUser();
        if (user != null) {
            tvWelcome.setText("مرحباً، " + user.getFullName());
        }
    }
    
    private void loadStudents() {
        showLoading(true);
        
        User user = prefManager.getUser();
        if (user == null) return;
        
        ApiClient.getApiService()
                .getStudentsByParent(prefManager.getAuthToken(), user.getId())
                .enqueue(new Callback<ApiResponse<List<Student>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Student>>> call, 
                                         Response<ApiResponse<List<Student>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Student>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<Student> students = apiResponse.getData();
                                if (students.isEmpty()) {
                                    showNoStudents(true);
                                } else {
                                    showNoStudents(false);
                                    adapter.updateData(students);
                                }
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<Student>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(ParentDashboardActivity.this, 
                                     "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvStudents.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showNoStudents(boolean show) {
        tvNoStudents.setVisibility(show ? View.VISIBLE : View.GONE);
        rvStudents.setVisibility(show ? View.GONE : View.VISIBLE);
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