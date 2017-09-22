package com.kwikwi.android.getgitusers.api;

import com.kwikwi.android.getgitusers.model.ItemResponse;
import com.kwikwi.android.getgitusers.model.UserDisplayResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static android.R.attr.x;

/**
 * Created by KWIKWI on 9/16/2017.
 */

public interface Service {

    @GET("/search/users?q=language:java+location:lagos")
    Call<ItemResponse> getItems(@Query("page") int no);

    @GET
    Call<UserDisplayResponse>getUser(@Url String url);


}
