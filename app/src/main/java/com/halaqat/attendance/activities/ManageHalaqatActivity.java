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
import com.halaqat.attendance.adapters.HalaqatAdapter;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.models.Halaqa;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageHalaqatActivity extends AppCompatActivity implements HalaqatAdapter.OnHalaqaActionListener {
    
    private RecyclerView rvHalaqat;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FloatingActionButton fabAdd;
    private PreferenceManager prefManager;
    private HalaqatAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_halaqat);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadHalaqat();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("إدارة الحلقات");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        rvHalaqat = findViewById(R.id.rv_halaqat);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        fabAdd = findViewById(R.id.fab_add);
    }
    
    private void setupRecyclerView() {
        adapter = new HalaqatAdapter(this, new ArrayList<>(), this);
        rvHalaqat.setLayoutManager(new LinearLayoutManager(this));
        rvHalaqat.setAdapter(adapter);
    }
    
    private void setupListeners() {
        fabAdd.setOnClickListener(v -> showAddHalaqaDialog());
    }
    
    private void loadHalaqat() {
        showLoading(true);
        
        ApiClient.getApiService().getHalaqat(prefManager.getAuthToken())
                .enqueue(new Callback<ApiResponse<List<Halaqa>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Halaqa>>> call, Response<ApiResponse<List<Halaqa>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Halaqa>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<Halaqa> halaqat = apiResponse.getData();
                                if (halaqat.isEmpty()) {
                                    showNoData(true);
                                } else {
                                    showNoData(false);
                                    adapter.updateData(halaqat);
                                }
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<Halaqa>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(ManageHalaqatActivity.this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showAddHalaqaDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_halaqa, null);
        
        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etDescription = dialogView.findViewById(R.id.et_description);
        
        new AlertDialog.Builder(this)
                .setTitle("إضافة حلقة جديدة")
                .setView(dialogView)
                .setPositiveButton("إضافة", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    
                    if (name.isEmpty()) {
                        Toast.makeText(this, "الرجاء إدخال اسم الحلقة", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    createHalaqa(name, description);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void createHalaqa(String name, String description) {
        Halaqa halaqa = new Halaqa();
        halaqa.setName(name);
        halaqa.setDescription(description);
        
        ApiClient.getApiService().createHalaqa(prefManager.getAuthToken(), halaqa)
                .enqueue(new Callback<ApiResponse<Halaqa>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Halaqa>> call, Response<ApiResponse<Halaqa>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ManageHalaqatActivity.this, "تم إضافة الحلقة بنجاح", Toast.LENGTH_SHORT).show();
                            loadHalaqat();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Halaqa>> call, Throwable t) {
                        Toast.makeText(ManageHalaqatActivity.this, "خطأ في إضافة الحلقة", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    @Override
    public void onEditHalaqa(Halaqa halaqa) {
        // Implement edit functionality
    }
    
    @Override
    public void onDeleteHalaqa(Halaqa halaqa) {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف " + halaqa.getName() + "؟")
                .setPositiveButton("حذف", (dialog, which) -> deleteHalaqa(halaqa.getId()))
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void deleteHalaqa(int halaqaId) {
        ApiClient.getApiService().deleteHalaqa(prefManager.getAuthToken(), halaqaId)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ManageHalaqatActivity.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                            loadHalaqat();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(ManageHalaqatActivity.this, "خطأ في الحذف", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvHalaqat.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showNoData(boolean show) {
        tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        rvHalaqat.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}