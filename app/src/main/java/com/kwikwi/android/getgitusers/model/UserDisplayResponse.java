package com.kwikwi.android.getgitusers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by KWIKWI on 9/20/2017.
 */

public class UserDisplayResponse {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("bio")
    @Expose
    private String bio;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio(){
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

}
