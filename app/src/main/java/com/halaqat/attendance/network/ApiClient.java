package com.halaqat.attendance.network;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.halaqat.attendance.utils.BooleanDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    private static final String TAG = "ApiClient";
    
    // ⚡ غير هذا السطر حسب حالتك - ضع IP الخاص بالـ Backend
    private static final String BASE_URL = "http://172.18.137.4:3000/api/";
    
    private static Retrofit retrofit;
    private static ApiService apiService;
    
    /**
     * تهيئة ApiClient - يجب استدعاؤها في Application.onCreate()
     */
    public static void init(Context context) {
        try {
            // إعداد Logging Interceptor
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
            
            // ✅ إعداد Gson مع BooleanDeserializer لحل مشكلة 0/1 vs true/false
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, new BooleanDeserializer())
                    .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                    .setLenient()
                    .serializeNulls()
                    .create();
            
            // إعداد Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            
            // إنشاء ApiService
            apiService = retrofit.create(ApiService.class);
            
            Log.d(TAG, "✅ ApiClient initialized successfully");
            Log.d(TAG, "📡 BASE_URL: " + BASE_URL);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing ApiClient", e);
        }
    }
    
    /**
     * الحصول على ApiService instance
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            Log.e(TAG, "⚠️ ApiService is null! Call ApiClient.init() first");
            throw new IllegalStateException("ApiClient must be initialized before use. Call ApiClient.init() in Application.onCreate()");
        }
        return apiService;
    }
    
    /**
     * الحصول على BASE_URL
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }
    
    /**
     * إعادة تعيين ApiClient (مفيد عند تغيير الـ URL)
     */
    public static void resetClient() {
        retrofit = null;
        apiService = null;
    }
    
    /**
     * التحقق من أن ApiClient تم تهيئته
     */
    public static boolean isInitialized() {
        return apiService != null;
    }
}
