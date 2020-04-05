package com.example.weatherfa.gson;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("success")
    public String status;
    public Results result;
}
