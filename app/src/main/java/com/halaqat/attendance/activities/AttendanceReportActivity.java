package com.halaqat.attendance.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.halaqat.attendance.AttendanceApp;
import com.halaqat.attendance.R;
import com.halaqat.attendance.adapters.AttendanceReportAdapter;
import com.halaqat.attendance.models.*;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceReportActivity extends AppCompatActivity {
    
    private Spinner spinnerFawj;
    private TextView tvStartDate, tvEndDate;
    private Button btnStartDate, btnEndDate, btnSearch;
    private RecyclerView rvReport;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private PreferenceManager prefManager;
    private AttendanceReportAdapter adapter;
    private List<Fawj> fawjList = new ArrayList<>();
    private String startDate, endDate;
    private Integer selectedFawjId = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());
        endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadFawjList();
        
        // Check if opened from parent dashboard
        if (getIntent().hasExtra("student_id")) {
            int studentId = getIntent().getIntExtra("student_id", -1);
            String studentName = getIntent().getStringExtra("student_name");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("سجل حضور " + studentName);
            }
            loadStudentAttendance(studentId);
        }
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("تقرير الحضور");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        spinnerFawj = findViewById(R.id.spinner_fawj);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        btnStartDate = findViewById(R.id.btn_start_date);
        btnEndDate = findViewById(R.id.btn_end_date);
        btnSearch = findViewById(R.id.btn_search);
        rvReport = findViewById(R.id.rv_report);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        
        tvStartDate.setText(startDate);
        tvEndDate.setText(endDate);
    }
    
    private void setupRecyclerView() {
        adapter = new AttendanceReportAdapter(this, new ArrayList<>());
        rvReport.setLayoutManager(new LinearLayoutManager(this));
        rvReport.setAdapter(adapter);
    }
    
    private void setupListeners() {
        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));
        btnSearch.setOnClickListener(v -> searchReport());
        
        spinnerFawj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedFawjId = null;
                } else {
                    selectedFawjId = fawjList.get(position - 1).getId();
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
            if (isStartDate) {
                startDate = date;
                tvStartDate.setText(date);
            } else {
                endDate = date;
                tvEndDate.setText(date);
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
                Toast.makeText(AttendanceReportActivity.this, "خطأ في تحميل الأفواج", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupFawjSpinner() {
        List<String> fawjNames = new ArrayList<>();
        fawjNames.add("جميع الأفواج");
        for (Fawj fawj : fawjList) {
            fawjNames.add(fawj.getName() + " - " + fawj.getHalaqaName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, fawjNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFawj.setAdapter(adapter);
    }
    
    private void searchReport() {
        showLoading(true);
        
        ApiClient.getApiService().getAttendanceReport(
                prefManager.getAuthToken(), 
                selectedFawjId, 
                startDate, 
                endDate
        ).enqueue(new Callback<ApiResponse<List<Attendance>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Attendance>>> call, Response<ApiResponse<List<Attendance>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Attendance>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Attendance> attendanceList = apiResponse.getData();
                        if (attendanceList.isEmpty()) {
                            showNoData(true);
                        } else {
                            showNoData(false);
                            adapter.updateData(attendanceList);
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Attendance>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AttendanceReportActivity.this, "خطأ في تحميل التقرير", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadStudentAttendance(int studentId) {
        showLoading(true);
        
        ApiClient.getApiService().getAttendanceByStudent(
                prefManager.getAuthToken(), 
                studentId, 
                startDate, 
                endDate
        ).enqueue(new Callback<ApiResponse<List<Attendance>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Attendance>>> call, Response<ApiResponse<List<Attendance>>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Attendance>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Attendance> attendanceList = apiResponse.getData();
                        if (attendanceList.isEmpty()) {
                            showNoData(true);
                        } else {
                            showNoData(false);
                            adapter.updateData(attendanceList);
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Attendance>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AttendanceReportActivity.this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvReport.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showNoData(boolean show) {
        tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        rvReport.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}