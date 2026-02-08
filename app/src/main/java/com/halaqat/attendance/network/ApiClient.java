package com.halaqat.attendance.network;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    // ═══════════════════════════════════════════════════════════════════
    // 🔧 قم بتغيير هذا السطر فقط حسب احتياجك:
    // ═══════════════════════════════════════════════════════════════════
    
    // ✅ الخيار 1: استخدام Domain Name (الأفضل للإنتاج)
   // private static final String BASE_URL = "https://example.com/api/";
    
    // ✅ الخيار 2: استخدام Domain بدون HTTPS
    // private static final String BASE_URL = "http://example.com/api/";
    
    // ✅ الخيار 3: استخدام Subdomain
    // private static final String BASE_URL = "https://api.example.com/";
    
    // ✅ الخيار 4: استخدام Port مخصص
    private static final String BASE_URL = "http://fi11.bot-hosting.net:21316/api/";
    
    // ✅ الخيار 5: للمحاكي (Development)
    // private static final String BASE_URL = "http://10.0.2.2:3000/api/";
    
    // ✅ الخيار 6: للجهاز الحقيقي (Development)
    // private static final String BASE_URL = "http://192.168.1.5:3000/api/";
    
    // ═══════════════════════════════════════════════════════════════════
    
    private static Retrofit retrofit;
    private static ApiService apiService;
    
    public static void init(Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiService = retrofit.create(ApiService.class);
    }
    
    public static ApiService getApiService() {
        return apiService;
    }
}
