package com.halaqat.attendance.network;

import android.content.Context;
import android.util.Log;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    private static final String TAG = "ApiClient";
    
    // ⚡ غير هذا السطر حسب حالتك:
    private static final String BASE_URL = "http://fi11.bot-hosting.net:21316/api/";
    
    private static Retrofit retrofit;
    private static ApiService apiService;
    
    public static void init(Context context) {
        try {
            // إعداد Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, "OkHttp: " + message);
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // إعداد OkHttp Client
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
            
            // ✅ الحل الجذري: Gson مخصص يدعم snake_case والتواريخ
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .create();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            
            apiService = retrofit.create(ApiService.class);
            
            Log.d(TAG, "✅ ApiClient initialized successfully");
            Log.d(TAG, "📡 BASE_URL: " + BASE_URL);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing ApiClient", e);
        }
    }
    
    public static ApiService getApiService() {
        if (apiService == null) {
            Log.e(TAG, "⚠️ ApiService is null! Call ApiClient.init() first");
        }
        return apiService;
    }
    
    public static String getBaseUrl() {
        return BASE_URL;
    }
}
