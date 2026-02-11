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
import com.halaqat.attendance.adapters.FawjAdapter;
import com.halaqat.attendance.models.*;
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
        if (!isActivityValid()) {
            return;
        }
        
        String token = prefManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            showError("خطأ في المصادقة. الرجاء تسجيل الدخول مرة أخرى");
            return;
        }
        
        Log.d(TAG, "Loading halaqat for spinner...");
        
        try {
            // ✅ استخدام getAllHalaqat
            ApiClient.getApiService().getAllHalaqat(token)
                    .enqueue(new Callback<ApiResponse<List<Halaqa>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Halaqa>>> call,
                                             Response<ApiResponse<List<Halaqa>>> response) {
                            
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                Log.d(TAG, "Halaqat response code: " + response.code());
                                
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<List<Halaqa>> apiResponse = response.body();
                                    
                                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                        halaqatList.clear();
                                        halaqatList.addAll(apiResponse.getData());
                                        
                                        Log.d(TAG, "Halaqat loaded for spinner: " + halaqatList.size());
                                        
                                        // الآن حمّل الأفواج
                                        loadFawj();
                                    } else {
                                        String message = apiResponse.getMessage();
                                        showError(message != null ? message : "فشل تحميل الحلقات");
                                        // حاول تحميل الأفواج حتى لو فشل تحميل الحلقات
                                        loadFawj();
                                    }
                                } else {
                                    Log.e(TAG, "Halaqat response not successful: " + response.code());
                                    showError("فشل تحميل الحلقات");
                                    // حاول تحميل الأفواج حتى لو فشل تحميل الحلقات
                                    loadFawj();
                                }
                            });
                        }
                        
                        @Override
                        public void onFailure(Call<ApiResponse<List<Halaqa>>> call, Throwable t) {
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                Log.e(TAG, "Halaqat loading error: " + t.getMessage(), t);
                                showError("خطأ في تحميل الحلقات: " + t.getMessage());
                                // حاول تحميل الأفواج حتى لو فشل تحميل الحلقات
                                loadFawj();
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while loading halaqat", e);
            showError("حدث خطأ: " + e.getMessage());
            // حاول تحميل الأفواج حتى لو فشل تحميل الحلقات
            loadFawj();
        }
    }
    
    private void loadFawj() {
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
        
        Log.d(TAG, "Loading fawj with token...");
        
        try {
            ApiClient.getApiService().getAllFawj(token)
                    .enqueue(new Callback<ApiResponse<List<Fawj>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<Fawj>>> call,
                                             Response<ApiResponse<List<Fawj>>> response) {
                            
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                showLoading(false);
                                
                                Log.d(TAG, "Fawj response code: " + response.code());
                                
                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<List<Fawj>> apiResponse = response.body();
                                    
                                    Log.d(TAG, "Fawj response success: " + apiResponse.isSuccess());
                                    Log.d(TAG, "Fawj response message: " + apiResponse.getMessage());
                                    
                                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                        List<Fawj> fawjList = apiResponse.getData();
                                        
                                        Log.d(TAG, "Fawj loaded: " + fawjList.size());
                                        
                                        if (fawjList.isEmpty()) {
                                            showNoData(true);
                                        } else {
                                            showNoData(false);
                                            if (adapter != null) {
                                                adapter.updateData(fawjList);
                                            }
                                        }
                                    } else {
                                        String message = apiResponse.getMessage();
                                        showError(message != null ? message : "فشل تحميل الأفواج");
                                    }
                                } else {
                                    Log.e(TAG, "Fawj response not successful: " + response.code());
                                    
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
                        public void onFailure(Call<ApiResponse<List<Fawj>>> call, Throwable t) {
                            if (!isActivityValid()) {
                                return;
                            }
                            
                            safeRunOnUiThread(() -> {
                                showLoading(false);
                                Log.e(TAG, "Fawj network error: " + t.getMessage(), t);
                                
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
            Log.e(TAG, "Exception while loading fawj", e);
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
            showError("الرجاء إضافة حلقات أولاً");
            return;
        }
        
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_fawj, null);
            
            EditText etName = dialogView.findViewById(R.id.et_name);
            Spinner spinnerHalaqa = dialogView.findViewById(R.id.spinner_halaqa);
            
            List<String> halaqaNames = new ArrayList<>();
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
                        int halaqaPosition = spinnerHalaqa.getSelectedItemPosition();
                        
                        if (name.isEmpty()) {
                            showError("الرجاء إدخال اسم الفوج");
                            return;
                        }
                        
                        if (halaqaPosition >= 0 && halaqaPosition < halaqatList.size()) {
                            int halaqaId = halaqatList.get(halaqaPosition).getId();
                            addFawj(name, halaqaId);
                        } else {
                            showError("الرجاء اختيار حلقة صحيحة");
                        }
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing add dialog", e);
            showError("خطأ في عرض النافذة: " + e.getMessage());
        }
    }
    
    private void addFawj(String name, int halaqaId) {
        String token = prefManager.getAuthToken();
        if (token == null) {
            showError("خطأ في المصادقة");
            return;
        }
        
        // ✅ Map<String, Object> - هذا صحيح لأن fawj يحتوي على integer (halaqa_id)
        Map<String, Object> fawjData = new HashMap<>();
        fawjData.put("name", name);
        fawjData.put("halaqa_id", halaqaId);
        fawjData.put("is_active", true);
        
        ApiClient.getApiService().createFawj(token, fawjData)
                .enqueue(new Callback<ApiResponse<Fawj>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Fawj>> call,
                                         Response<ApiResponse<Fawj>> response) {
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<Fawj> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    showSuccess("تمت الإضافة بنجاح");
                                    loadFawj();
                                } else {
                                    showError(apiResponse.getMessage());
                                }
                            } else {
                                showError("فشلت الإضافة");
                            }
                        });
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Fawj>> call, Throwable t) {
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
    public void onEditFawj(Fawj fawj) {
        showSuccess("تعديل الفوج قيد التطوير");
    }
    
    @Override
    public void onDeleteFawj(Fawj fawj) {
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
                        if (!isActivityValid()) {
                            return;
                        }
                        
                        safeRunOnUiThread(() -> {
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
