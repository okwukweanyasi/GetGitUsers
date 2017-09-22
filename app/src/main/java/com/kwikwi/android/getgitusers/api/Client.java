package com.kwikwi.android.getgitusers.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by KWIKWI on 9/16/2017.
 */

public class Client {

    public static final String BASE_URL = "https://api.github.com";
    public static Retrofit retrofit = null;
    public static Retrofit retrofit2 = null;

    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

//    public static Retrofit getClient2(){
//        if(retrofit2 == null){
//            retrofit2 = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        return retrofit2;
//    }


}
