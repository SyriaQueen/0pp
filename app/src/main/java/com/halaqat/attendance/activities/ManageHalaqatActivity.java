package com.halaqat.attendance.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
    
    private static final String TAG = "ManageHalaqatActivity";
    
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
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("إدارة الحلقات");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar", e);
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
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddHalaqaDialog());
        }
    }
    
    private void loadHalaqat() {
        showLoading(true);
        
        String token = prefManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            showLoading(false);
            showError("خطأ في المصادقة");
            return;
        }
        
        Log.d(TAG, "Loading halaqat...");
        
        try {
            ApiClient.getApiService().getHalaqat(token)
                    .enqueue(new Callback<ApiResponse<List<Halaqa>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Halaqa>>> call, 
                                             Response<ApiResponse<List<Halaqa>>> response) {
                            showLoading(false);
                            
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<List<Halaqa>> apiResponse = response.body();
                                
                                if (apiResponse.isSuccess()) {
                                    List<Halaqa> halaqat = apiResponse.getData();
                                    
                                    if (halaqat != null && !halaqat.isEmpty()) {
                                        Log.d(TAG, "Loaded " + halaqat.size() + " halaqat");
                                        showNoData(false);
                                        adapter.updateData(halaqat);
                                    } else {
                                        showNoData(true);
                                    }
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("خطأ في تحميل البيانات");
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<List<Halaqa>>> call, Throwable t) {
                            showLoading(false);
                            Log.e(TAG, "Error loading halaqat", t);
                            showError("خطأ في الاتصال: " + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            showLoading(false);
            Log.e(TAG, "Exception loading halaqat", e);
            showError("حدث خطأ: " + e.getMessage());
        }
    }
    
    private void showAddHalaqaDialog() {
        try {
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
                            showError("الرجاء إدخال اسم الحلقة");
                            return;
                        }
                        
                        createHalaqa(name, description);
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing dialog", e);
            showError("حدث خطأ");
        }
    }
    
    private void createHalaqa(String name, String description) {
        try {
            Halaqa halaqa = new Halaqa();
            halaqa.setName(name);
            halaqa.setDescription(description);
            
            String token = prefManager.getAuthToken();
            if (token == null) {
                showError("خطأ في المصادقة");
                return;
            }
            
            ApiClient.getApiService().createHalaqa(token, halaqa)
                    .enqueue(new Callback<ApiResponse<Halaqa>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Halaqa>> call, 
                                             Response<ApiResponse<Halaqa>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().isSuccess()) {
                                    showSuccess("تم إضافة الحلقة بنجاح");
                                    loadHalaqat();
                                } else {
                                    showError(response.body().getMessage());
                                }
                            } else {
                                showError("فشل إضافة الحلقة");
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<Halaqa>> call, Throwable t) {
                            showError("خطأ في الاتصال: " + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error creating halaqa", e);
            showError("حدث خطأ");
        }
    }
    
    @Override
    public void onEditHalaqa(Halaqa halaqa) {
        if (halaqa == null) {
            showError("خطأ: بيانات غير صالحة");
            return;
        }
        showError("قريباً");
    }
    
    @Override
    public void onDeleteHalaqa(Halaqa halaqa) {
        if (halaqa == null) {
            showError("خطأ: بيانات غير صالحة");
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف " + halaqa.getName() + "؟")
                .setPositiveButton("حذف", (dialog, which) -> deleteHalaqa(halaqa.getId()))
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void deleteHalaqa(int halaqaId) {
        String token = prefManager.getAuthToken();
        if (token == null) {
            showError("خطأ في المصادقة");
            return;
        }
        
        ApiClient.getApiService().deleteHalaqa(token, halaqaId)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, 
                                         Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showSuccess("تم الحذف بنجاح");
                            loadHalaqat();
                        } else {
                            showError("فشل الحذف");
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        showError("خطأ في الاتصال");
                    }
                });
    }
    
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvHalaqat != null) {
            rvHalaqat.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showNoData(boolean show) {
        if (tvNoData != null) {
            tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvHalaqat != null) {
            rvHalaqat.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showError(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            Log.e(TAG, message);
        }
    }
    
    private void showSuccess(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
