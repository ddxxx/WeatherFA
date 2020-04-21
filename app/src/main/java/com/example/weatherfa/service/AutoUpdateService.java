package com.example.weatherfa.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.weatherfa.gson.Weather;
import com.example.weatherfa.util.HttpUtil;
import com.example.weatherfa.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int updateFreq=sp.getInt("updateFreq",-1);
        if(updateFreq==-1){
            updateFreq=8;
            editor.putInt("updateFreq",8);
            editor.commit();
        }
        Log.e("AutoUpdateService",String.valueOf(updateFreq)+"小时后后台更新数据");
        int anHour=updateFreq*60*60*1000;//8小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

        return super.onStartCommand(intent, flags, startId);
    }
    //更新天气信息
    private void updateWeather() {
        sp=getSharedPreferences("weatherfa",MODE_PRIVATE);
        editor=sp.edit();
        String sCityname=sp.getString("cityname",null);
        if(sCityname!=null){
            //有缓存城市时更新天气数据
            //拼装接口地址
            String weatherUrl="http://api.k780.com/?app=weather.realtime&weaid=" +sCityname+
                    "&ag=today,futureDay,lifeIndex,futureHour&appkey=44303&" +
                    "sign=8825b94df83576ebde4c12e7a4a45be4&format=json";
            //发出请求
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String responseText= Objects.requireNonNull(response.body()).string();//得到服务器返回内容
                    final Weather weather = Utility.handleWeatherResponse(responseText);

                    if(weather !=null && "1".equals(weather.status)){
                        //请求成功，数据缓存到SharedPreferences中
                        editor.putString("weather_info",responseText);
                        //更新刷新时间
                        String sNowTime=nowTime();
                        editor.putString("nowTime",sNowTime);
                        editor.commit();//提交
                    }
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    //获取当前系统时间，用于提示更新时间
    private String nowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
