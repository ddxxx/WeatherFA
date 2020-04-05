package com.example.weatherfa.gson;

import com.google.gson.annotations.SerializedName;

class FutureEHour {
    private String dateYmdh;//主要是使用时间
    @SerializedName("wtNm")
    private String wtType;
    private String wtIcon;
    private String wtTemp;
}
