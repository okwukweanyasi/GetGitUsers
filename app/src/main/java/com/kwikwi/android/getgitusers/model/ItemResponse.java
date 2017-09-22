package com.kwikwi.android.getgitusers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by KWIKWI on 9/16/2017.
 */

public class ItemResponse {

    @SerializedName("items")
    @Expose
    private List<Item> items;
     @SerializedName("total_count")
     @Expose
     private long total_count;
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    public long getTotal_count(){
        return total_count;
    }

    public void setTotal_count(long total_count) {
        this.total_count = total_count;
    }
}
