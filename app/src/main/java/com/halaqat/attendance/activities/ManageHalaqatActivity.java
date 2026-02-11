package com.halaqat.attendance.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        Log.d(TAG, "Loading halaqat with token...");
        
        try {
            ApiClient.getApiService().getAllHalaqat(token)
                    .enqueue(new Callback<ApiResponse<List<Halaqa>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Halaqa>>> call,
                                             Response<ApiResponse<List<Halaqa>>> response) {
                            
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                showLoading(false);
                                
                                Log.d(TAG, "Response code: " + response.code());
                                
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<List<Halaqa>> apiResponse = response.body();
                                    
                                    Log.d(TAG, "Response success: " + apiResponse.isSuccess());
                                    Log.d(TAG, "Response message: " + apiResponse.getMessage());
                                    
                                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                        List<Halaqa> halaqat = apiResponse.getData();
                                        
                                        Log.d(TAG, "Halaqat loaded: " + halaqat.size());
                                        
                                        if (halaqat.isEmpty()) {
                                            showNoData(true);
                                        } else {
                                            showNoData(false);
                                            if (adapter != null) {
                                                adapter.updateData(halaqat);
                                            }
                                        }
                                    } else {
                                        String message = apiResponse.getMessage();
                                        showError(message != null ? message : "فشل تحميل الحلقات");
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
                        public void onFailure(Call<ApiResponse<List<Halaqa>>> call, Throwable t) {
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
            Log.e(TAG, "Exception while loading halaqat", e);
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
    
    private void showAddHalaqaDialog() {
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_halaqa, null);
            
            EditText etName = dialogView.findViewById(R.id.et_name);
            EditText etLocation = dialogView.findViewById(R.id.et_location);
            
            new AlertDialog.Builder(this)
                    .setTitle("إضافة حلقة جديدة")
                    .setView(dialogView)
                    .setPositiveButton("إضافة", (dialog, which) -> {
                        String name = etName.getText().toString().trim();
                        String location = etLocation.getText().toString().trim();
                        
                        if (name.isEmpty()) {
                            showError("الرجاء إدخال اسم الحلقة");
                            return;
                        }
                        
                        addHalaqa(name, location);
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing add dialog", e);
            showError("خطأ في عرض النافذة: " + e.getMessage());
        }
    }
    
    private void addHalaqa(String name, String location) {
        String token = prefManager.getAuthToken();
        if (token == null) {
            showError("خطأ في المصادقة");
            return;
        }
        
        Map<String, Object> halaqaData = new HashMap<>();
        halaqaData.put("name", name);
        halaqaData.put("location", location);
        halaqaData.put("is_active", true);
        
        ApiClient.getApiService().createHalaqa(token, halaqaData)
                .enqueue(new Callback<ApiResponse<Halaqa>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Halaqa>> call,
                                         Response<ApiResponse<Halaqa>> response) {
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Halaqa> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    showSuccess("تمت الإضافة بنجاح");
                                    loadHalaqat();
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("فشلت الإضافة");
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Halaqa>> call, Throwable t) {
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
    public void onEditHalaqa(Halaqa halaqa) {
        showSuccess("تعديل الحلقة قيد التطوير");
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
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Object> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    showSuccess("تم الحذف بنجاح");
                                    loadHalaqat();
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
