package com.example.paul.phpretrofitupload.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by paul on 2018/12/30
 * last modified at 10:12.
 * Desc:
 */

public class RetrofitClient {
    private static Retrofit retrofitClient = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofitClient == null) {
            retrofitClient = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitClient;
    }
}
