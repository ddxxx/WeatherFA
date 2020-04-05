package com.example.weatherfa.gson;

import com.google.gson.annotations.SerializedName;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.Future;

public class Results {
    @SerializedName("area_1")
    public String province;
    @SerializedName("area_2")
    public String county;
    @SerializedName("area_3")
    public String distinct;

    public RealTime realTime;
    public Today today;
    public List<FutureEDay> futureDay;
    public List<FutureEHour> futureHour;


}
