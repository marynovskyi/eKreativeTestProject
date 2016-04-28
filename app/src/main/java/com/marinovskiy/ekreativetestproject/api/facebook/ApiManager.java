package com.marinovskiy.ekreativetestproject.api.facebook;

import com.marinovskiy.ekreativetestproject.screens.utils.Prefs;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static ApiService sInstance;

    public static synchronized ApiService getInstance() {
        if (sInstance == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    HttpUrl httpUrl = chain.request().url()
                            .newBuilder()
                            .addQueryParameter(ApiConstants.ACCESS_TOKEN, Prefs.getAccessToken())
                            .addQueryParameter("fields", "name, email, cover, picture")
                            .build();

                    Request request = chain.request()
                            .newBuilder()
                            .url(httpUrl)
                            .build();
                    return chain.proceed(request);
                }
            };

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(interceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL + ApiConstants.API_VERSION)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            sInstance = retrofit.create(ApiService.class);
        }
        return sInstance;
    }
}