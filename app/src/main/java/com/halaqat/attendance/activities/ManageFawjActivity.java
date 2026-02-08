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
import com.halaqat.attendance.adapters.FawjAdapter;
import com.halaqat.attendance.models.ApiResponse;
import com.halaqat.attendance.models.Fawj;
import com.halaqat.attendance.models.Halaqa;
import com.halaqat.attendance.network.ApiClient;
import com.halaqat.attendance.utils.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageFawjActivity extends AppCompatActivity implements FawjAdapter.OnFawjActionListener {
    
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("إدارة الأفواج");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        fabAdd.setOnClickListener(v -> showAddFawjDialog());
    }
    
    private void loadHalaqat() {
        ApiClient.getApiService().getHalaqat(prefManager.getAuthToken())
                .enqueue(new Callback<ApiResponse<List<Halaqa>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Halaqa>>> call, Response<ApiResponse<List<Halaqa>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Halaqa>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                halaqatList = apiResponse.getData();
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<Halaqa>>> call, Throwable t) {
                        Toast.makeText(ManageFawjActivity.this, "خطأ في تحميل الحلقات", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void loadFawj() {
        showLoading(true);
        
        ApiClient.getApiService().getAllFawj(prefManager.getAuthToken())
                .enqueue(new Callback<ApiResponse<List<Fawj>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Fawj>>> call, Response<ApiResponse<List<Fawj>>> response) {
                        showLoading(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<Fawj>> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                List<Fawj> fawjList = apiResponse.getData();
                                if (fawjList.isEmpty()) {
                                    showNoData(true);
                                } else {
                                    showNoData(false);
                                    adapter.updateData(fawjList);
                                }
                            }
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<List<Fawj>>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(ManageFawjActivity.this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showAddFawjDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_fawj, null);
        
        EditText etName = dialogView.findViewById(R.id.et_name);
        Spinner spinnerHalaqa = dialogView.findViewById(R.id.spinner_halaqa);
        
        List<String> halaqaNames = new ArrayList<>();
        halaqaNames.add("اختر الحلقة");
        for (Halaqa halaqa : halaqatList) {
            halaqaNames.add(halaqa.getName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, halaqaNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHalaqa.setAdapter(adapter);
        
        new AlertDialog.Builder(this)
                .setTitle("إضافة فوج جديد")
                .setView(dialogView)
                .setPositiveButton("إضافة", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    int position = spinnerHalaqa.getSelectedItemPosition();
                    
                    if (name.isEmpty()) {
                        Toast.makeText(this, "الرجاء إدخال اسم الفوج", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (position == 0) {
                        Toast.makeText(this, "الرجاء اختيار الحلقة", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    int halaqaId = halaqatList.get(position - 1).getId();
                    createFawj(name, halaqaId);
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }
    
    private void createFawj(String name, int halaqaId) {
        Fawj fawj = new Fawj();
        fawj.setName(name);
        fawj.setHalaqaId(halaqaId);
        
        ApiClient.getApiService().createFawj(prefManager.getAuthToken(), fawj)
                .enqueue(new Callback<ApiResponse<Fawj>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Fawj>> call, Response<ApiResponse<Fawj>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ManageFawjActivity.this, "تم إضافة الفوج بنجاح", Toast.LENGTH_SHORT).show();
                            loadFawj();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Fawj>> call, Throwable t) {
                        Toast.makeText(ManageFawjActivity.this, "خطأ في إضافة الفوج", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    @Override
    public void onEditFawj(Fawj fawj) {
        // Implement edit functionality
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
        ApiClient.getApiService().deleteFawj(prefManager.getAuthToken(), fawjId)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ManageFawjActivity.this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                            loadFawj();
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(ManageFawjActivity.this, "خطأ في الحذف", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvFawj.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showNoData(boolean show) {
        tvNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        rvFawj.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}