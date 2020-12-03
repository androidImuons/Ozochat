package com.ozonetech.ozochat.network.webservices;

import android.util.Log;

import com.ozonetech.ozochat.utils.MyPreferenceManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {


//    Rahul Sir here are the new baseurls :
//
//    Replica :
//            -------------------
//    https://ozochatapireplica.ozonetech.biz
//    https://ozochatsocketreplica.ozonetech.biz
//
//    Live :
//            -------------------
//    https://ozochatapilive.ozonetech.biz
//    https://ozochatsocketlive.ozonetech.biz


    public static String API_BASE_URL = "https://ozochatapireplica.ozonetech.biz/";// replica
   // public static String API_BASE_URL = "https://ozochatapilive.ozonetech.biz/";// live

    public static String SOCKETURL = "https://ozochatsocketreplica.ozonetech.biz/";// replica socket
   // public static String SOCKETURL="https://ozochatsocketlive.ozonetech.biz/";// Live socket

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).
                    writeTimeout(60, TimeUnit.SECONDS);
    private static AppServices apiService;

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        API_BASE_URL = newApiBaseUrl;
        builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
    }

    private static Retrofit.Builder builder =
            new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {


        if (authToken != null) {
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + authToken)
                            // .header("Content-Type","application/x-www-form-urlencoded")
                            .header("Content-Type","application/json")
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();

                    return chain.proceed(request);
                }
            });
        }
        Log.d("toke","Bearer "+authToken);
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
    private static final long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis(60);

    public static AppServices getApiService() {
        if(apiService == null) {
            httpClient.connectTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            httpClient.writeTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            httpClient.readTimeout(HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            httpClient.retryOnConnectionFailure(true);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
            Retrofit retrofit = builder.client(httpClient.build()).build();

            apiService = retrofit.create(AppServices.class);
            return apiService;
        } else {
            return apiService;
        }
    }

}

