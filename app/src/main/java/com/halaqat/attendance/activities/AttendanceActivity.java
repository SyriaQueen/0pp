package com.halaqat.attendance.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.adapters.AttendanceMarkAdapter;
import com.halaqat.attendance.models.*;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity {
    
    private Spinner spinnerFawj;
    private TextView tvSelectedDate;
    private Button btnSelectDate, btnSave;
    private RecyclerView rvStudents;
    private ProgressBar progressBar;
    private FloatingActionButton fabMarkAll;
    private PreferenceManager prefManager;
    private AttendanceMarkAdapter adapter;
    private List<Fawj> fawjList = new ArrayList<>();
    private String selectedDate;
    private int selectedFawjId = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadFawjList();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("تسجيل الحضور");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        spinnerFawj = findViewById(R.id.spinner_fawj);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSave = findViewById(R.id.btn_save);
        rvStudents = findViewById(R.id.rv_students);
        progressBar = findViewById(R.id.progress_bar);
        fabMarkAll = findViewById(R.id.fab_mark_all);
        
        tvSelectedDate.setText(selectedDate);
    }
    
    private void setupRecyclerView() {
        adapter = new AttendanceMarkAdapter(this, new ArrayList<>());
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);
    }
    
    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());
        
        spinnerFawj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedFawjId = fawjList.get(position - 1).getId();
                    loadStudents();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        btnSave.setOnClickListener(v -> saveAttendance());
        
        fabMarkAll.setOnClickListener(v -> adapter.markAllPresent());
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            tvSelectedDate.setText(selectedDate);
            if (selectedFawjId != -1) {
                loadStudents();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void loadFawjList() {
        User user = prefManager.getUser();
        if (user == null) return;
        
        Call<ApiResponse<List<Fawj>>> call;
        if ("admin".equals(user.getRole())) {
            call = ApiClient.getApiService().getAllFawj(prefManager.getAuthToken());
        } else {
            call = ApiClient.getApiService().getFawjByTeacher(prefManager.getAuthToken(), user.getId());
        }
        
        call.enqueue(new Callback<ApiResponse<List<Fawj>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Fawj>>> call, Response<ApiResponse<List<Fawj>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Fawj>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        fawjList = apiResponse.getData();
                        setupFawjSpinner();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Fawj>>> call, Throwable t) {
                Toast.makeText(AttendanceActivity.this, "خطأ في تحميل الأفواج", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupFawjSpinner() {
        List<String> fawjNames = new ArrayList<>();
        fawjNames.add("اختر الفوج");
        for (Fawj fawj : fawjList) {
            fawjNames.add(fawj.getName() + " - " + fawj.getHalaqaName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, fawjNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFawj.setAdapter(adapter);
    }
    
    private void loadStudents() {
        showLoading(true);
        
        ApiClient.getApiService().getStudentsByFawj(prefManager.getAuthToken(), selectedFawjId)
                .enqueue(new Callback<ApiResponse<List<Student>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Student>>> call, Response<ApiResponse<List<Student>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Student>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                adapter.updateData(apiResponse.getData());
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<Student>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(AttendanceActivity.this, "خطأ في تحميل الطلاب", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void saveAttendance() {
        List<Map<String, Object>> attendanceList = adapter.getAttendanceData(selectedFawjId, selectedDate);
        
        if (attendanceList.isEmpty()) {
            Toast.makeText(this, "لا توجد بيانات للحفظ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ApiClient.getApiService().markAttendance(prefManager.getAuthToken(), attendanceList)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AttendanceActivity.this, "تم حفظ الحضور بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(AttendanceActivity.this, "خطأ في حفظ الحضور", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}