package com.example.weatherfa.util;

import com.example.weatherfa.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Utility {
    //将返回的JSON数据解析成Status实体类
    public static Weather handleWeatherResponse(String response){
        try{
            return new Gson().fromJson(response, Weather.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
