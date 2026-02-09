package com.halaqat.attendance.network;

import android.content.Context;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    private static final String TAG = "ApiClient";
    
    // ⚡ غير هذا السطر حسب حالتك:
    private static final String BASE_URL = "http://fi11.bot-hosting.net:21316/api/";
    // أو: "https://example.com/api/"
    
    private static Retrofit retrofit;
    private static ApiService apiService;
    
    public static void init(Context context) {
        try {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            apiService = retrofit.create(ApiService.class);
            
            Log.d(TAG, "ApiClient initialized successfully with BASE_URL: " + BASE_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing ApiClient: " + e.getMessage(), e);
        }
    }
    
    public static ApiService getApiService() {
        if (apiService == null) {
            Log.e(TAG, "ApiService is null! Make sure to call ApiClient.init() first");
        }
        return apiService;
    }
    
    public static String getBaseUrl() {
        return BASE_URL;
    }
}
