package com.example.weatherfa.gson;

import com.google.gson.annotations.SerializedName;

public class RealTime {

    public String week;//周几
    @SerializedName("wtNm")
    public String wtType;//天气
    public String wtIcon;
    public String wtTemp;
    public String wtHumi;
    @SerializedName("wtWindNm")
    public String wtWindType;//风向
    public String wtWindp;//风力
    public String wtWinds;//风速

    public String wtAqi;//pm2.5
    public String wtVisibility;
    public String wtRainfall;//降雨量
}
