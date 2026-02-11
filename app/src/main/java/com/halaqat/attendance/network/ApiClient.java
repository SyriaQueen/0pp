package com.halaqat.attendance.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.halaqat.attendance.utils.BooleanDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    // ✅ قم بتغيير هذا الـ IP إلى IP الخاص بالـ Backend
    private static final String BASE_URL = "http://fi11.bot-hosting.net:21316/api/";
    
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // إعداد Logging Interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // إعداد OkHttp Client
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            // ✅ إعداد Gson مع Custom Deserializer للـ Boolean
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, new BooleanDeserializer())
                    .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                    .setLenient()
                    .create();
            
            // إعداد Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getClient().create(ApiService.class);
        }
        return apiService;
    }
    
    // إعادة تعيين الـ Retrofit instance (مفيد عند تغيير الـ BASE_URL)
    public static void resetClient() {
        retrofit = null;
        apiService = null;
    }
}
