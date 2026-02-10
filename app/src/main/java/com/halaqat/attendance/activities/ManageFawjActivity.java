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
import com.halaqat.attendance.adapters.FawjAdapter;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.models.Fawj;
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

public class ManageFawjActivity extends AppCompatActivity implements FawjAdapter.OnFawjActionListener {
    
    private static final String TAG = "ManageFawjActivity";
    
    private RecyclerView rvFawj;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FloatingActionButton fabAdd;
    private PreferenceManager prefManager;
    private FawjAdapter adapter;
    private List<Halaqa> halaqatList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_fawj);
        
        prefManager = AttendanceApp.getInstance().getPreferenceManager();
        
        setupToolbar();
        initViews();
        setupRecyclerView();
        setupListeners();
        loadHalaqat();
        loadFawj();
    }
    
    private void setupToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("إدارة الأفواج");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar", e);
        }
    }
    
    private void initViews() {
        rvFawj = findViewById(R.id.rv_fawj);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        fabAdd = findViewById(R.id.fab_add);
    }
    
    private void setupRecyclerView() {
        adapter = new FawjAdapter(this, new ArrayList<>(), this);
        rvFawj.setLayoutManager(new LinearLayoutManager(this));
        rvFawj.setAdapter(adapter);
    }
    
    private void setupListeners() {
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddFawjDialog());
        }
    }
    
    private void loadHalaqat() {
        String token = prefManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            return;
        }
        
        Log.d(TAG, "Loading halaqat for spinner...");
        
        ApiClient.getApiService().getHalaqat(token)
                .enqueue(new Callback<ApiResponse<List<Halaqa>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Halaqa>>> call, 
                                         Response<ApiResponse<List<Halaqa>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Halaqa>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                halaqatList = apiResponse.getData();
                                Log.d(TAG, "Loaded " + halaqatList.size() + " halaqat for spinner");
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<Halaqa>>> call, Throwable t) {
                        Log.e(TAG, "Failed to load halaqat", t);
                        showError("خطأ في تحميل الحلقات");
                    }
                });
    }
    
    private void loadFawj() {
        showLoading(true);
        
        String token = prefManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            showLoading(false);
            showError("خطأ في المصادقة");
            return;
        }
        
        Log.d(TAG, "Loading fawj with token...");
        
        try {
            ApiClient.getApiService().getAllFawj(token)
                    .enqueue(new Callback<ApiResponse<List<Fawj>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Fawj>>> call, 
                                             Response<ApiResponse<List<Fawj>>> response) {
                            showLoading(false);
                            
                            Log.d(TAG, "Response code: " + response.code());
                            
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<List<Fawj>> apiResponse = response.body();
                                
                                Log.d(TAG, "Response success: " + apiResponse.isSuccess());
                                Log.d(TAG, "Response message: " + apiResponse.getMessage());
                                
                                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                    List<Fawj> fawjList = apiResponse.getData();
                                    
                                    Log.d(TAG, "Fawj loaded: " + fawjList.size());
                                    
                                    if (fawjList.isEmpty()) {
                                        showNoData(true);
                                    } else {
                                        showNoData(false);
                                        adapter.updateData(fawjList);
                                    }
                                } else {
                                    String message = apiResponse.getMessage();
                                    showError(message != null ? message : "فشل تحميل الأفواج");
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
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<List<Fawj>>> call, Throwable t) {
                            showLoading(false);
                            Log.e(TAG, "Network error: " + t.getMessage(), t);
                            
                            String errorMessage = "خطأ في الاتصال بالخادم";
                            if (t.getMessage() != null) {
                                if (t.getMessage().contains("Unable to resolve host")) {
                                    errorMessage = "تعذر الاتصال بالخادم";
                                } else if (t.getMessage().contains("timeout")) {
                                    errorMessage = "انتهت مهلة الاتصال";
                                } else if (t.getMessage().contains("Connection refused")) {
                                    errorMessage = "الخادم غير متاح. تأكد من تشغيل Backend";
                                }
                            }
                            
                            showError(errorMessage + "\n" + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            showLoading(false);
            Log.e(TAG, "Exception loading fawj", e);
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
    
    private void showAddFawjDialog() {
        if (halaqatList.isEmpty()) {
            showError("لا توجد حلقات متاحة. يرجى إضافة حلقة أولاً");
            return;
        }
        
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_fawj, null);
            
            EditText etName = dialogView.findViewById(R.id.et_name);
            Spinner spinnerHalaqa = dialogView.findViewById(R.id.spinner_halaqa);
            
            // إعداد Spinner للحلقات
            List<String> halaqaNames = new ArrayList<>();
            halaqaNames.add("اختر الحلقة");
            for (Halaqa halaqa : halaqatList) {
                halaqaNames.add(halaqa.getName());
            }
            
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_spinner_item, halaqaNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerHalaqa.setAdapter(spinnerAdapter);
            
            new AlertDialog.Builder(this)
                    .setTitle("إضافة فوج جديد")
                    .setView(dialogView)
                    .setPositiveButton("إضافة", (dialog, which) -> {
                        String name = etName.getText().toString().trim();
                        int position = spinnerHalaqa.getSelectedItemPosition();
                        
                        if (name.isEmpty()) {
                            showError("الرجاء إدخال اسم الفوج");
                            return;
                        }
                        
                        if (position == 0) {
                            showError("الرجاء اختيار الحلقة");
                            return;
                        }
                        
                        int halaqaId = halaqatList.get(position - 1).getId();
                        createFawj(name, halaqaId);
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing dialog", e);
            showError("حدث خطأ في فتح النافذة");
        }
    }
    
    private void createFawj(String name, int halaqaId) {
        try {
            String token = prefManager.getAuthToken();
            if (token == null) {
                showError("خطأ في المصادقة");
                return;
            }
            
            // استخدام Map بدلاً من Object
            Map<String, Object> fawjMap = new HashMap<>();
            fawjMap.put("name", name);
            fawjMap.put("halaqa_id", halaqaId);
            
            Log.d(TAG, "Creating fawj: " + name + " for halaqa_id: " + halaqaId);
            
            ApiClient.getApiService().createFawj(token, fawjMap)
                    .enqueue(new Callback<ApiResponse<Fawj>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Fawj>> call, 
                                             Response<ApiResponse<Fawj>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Fawj> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    showSuccess("تم إضافة الفوج بنجاح");
                                    loadFawj();
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                Log.e(TAG, "Create fawj failed: " + response.code());
                                
                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.e(TAG, "Error body: " + errorBody);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error body", e);
                                }
                                
                                showError("فشل إضافة الفوج");
                            }
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<Fawj>> call, Throwable t) {
                            Log.e(TAG, "Create fawj error", t);
                            showError("خطأ في الاتصال: " + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error creating fawj", e);
            showError("حدث خطأ");
        }
    }
    
    @Override
    public void onEditFawj(Fawj fawj) {
        if (fawj == null) {
            showError("خطأ: بيانات غير صالحة");
            return;
        }
        showError("قريباً");
    }
    
    @Override
    public void onDeleteFawj(Fawj fawj) {
        if (fawj == null) {
            showError("خطأ: بيانات غير صالحة");
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل أنت متأكد من حذف " + fawj.getName() + "؟")
                .setPositiveButton("حذف", (dialog, which) -> deleteFawj(fawj.getId()))
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void deleteFawj(int fawjId) {
        String token = prefManager.getAuthToken();
        if (token == null) {
            showError("خطأ في المصادقة");
            return;
        }
        
        ApiClient.getApiService().deleteFawj(token, fawjId)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, 
                                         Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Object> apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                showSuccess("تم الحذف بنجاح");
                                loadFawj();
                            } else {
                                showError(apiResponse.getMessage());
                            }
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
        if (rvFawj != null) {
            rvFawj.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void showNoData(boolean show) {
        if (tvNoData != null) {
            tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvFawj != null) {
            rvFawj.setVisibility(show ? View.GONE : View.VISIBLE);
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
