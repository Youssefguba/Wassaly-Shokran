package com.phoenix.otlobbety.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getInstance() {
        if (instance == null)
            instance = new Retrofit.Builder()
                    .baseUrl("https://orderapp-ff86d.firebaseio.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return instance;
    }
}
