package com.example.weatherfa.ui.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherfa.R;
import com.example.weatherfa.adapter.WtForecastAdapter;
import com.example.weatherfa.adapter.WtLifeIndexAdapter;
import com.example.weatherfa.gson.FutureEDay;
import com.example.weatherfa.historyActivity.HWtStatisticsActivity;
import com.example.weatherfa.service.AutoUpdateService;
import com.example.weatherfa.wtclass.WtDetail;
import com.example.weatherfa.adapter.WtDetailAdapter;
import com.example.weatherfa.gson.Weather;
import com.example.weatherfa.util.HttpUtil;
import com.example.weatherfa.util.Utility;
import com.example.weatherfa.wtclass.WtForecast;
import com.example.weatherfa.wtclass.WtLifeIndex;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherFragment extends Fragment{
    public SwipeRefreshLayout swipeRefreshLO;  //下拉更新
    private SharedPreferences sp;     //存储
    private SharedPreferences.Editor editor;
    private String cityName;
    //组件声明；详细天气信息
    private ImageView nowIconIV;    //now
    private TextView updateTimeTV,nowWmdTV,nowCityTV,nowTtTV;
    private List<WtDetail> wtDetailList=new ArrayList<>();    //详情
    private RecyclerView detailRV;
    private List<WtForecast> wtForecastList=new ArrayList<>();//未来一周
    private RecyclerView forecastRV;
    private List<WtLifeIndex> wtLifeIndexList=new ArrayList<>(); //生活指数
    private RecyclerView lifeIndexRV;
    private ImageView hIM0,hIM1;//历史天气2
    private Button hFullsBT0,hFullsBT1;
    private LinearLayout WtLO;//整体布局

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        //实例化
        swipeRefreshLO= root.findViewById(R.id.swipe_refresh); //------更新
        swipeRefreshLO.setColorSchemeResources(R.color.colorPrimary);
        nowIconIV= root.findViewById(R.id.wt_now_icon_iv);//-------------now
        updateTimeTV=root.findViewById(R.id.wt_now_updatetime_tv);
        nowWmdTV= root.findViewById(R.id.wt_now_wmd_tv);
        nowCityTV= root.findViewById(R.id.wt_now_city_tv);
        nowTtTV= root.findViewById(R.id.wt_now_tt_tv);//天气+温度
        detailRV= root.findViewById(R.id.detail_recycler_view);//-------detail
        forecastRV= root.findViewById(R.id.forecast_recycler_view);//------forecast
        lifeIndexRV= root.findViewById(R.id.life_index_recycler_view);//------lifeindex
        hIM0=root.findViewById(R.id.h_item_icon_iv0);//========history
        hIM1=root.findViewById(R.id.h_item_icon_iv1);
        hFullsBT0=root.findViewById(R.id.h_item_fulls_bt0);
        hFullsBT1=root.findViewById(R.id.h_item_fulls_bt1);

        WtLO=root.findViewById(R.id.wt_layout);

        sp= this.getActivity().getSharedPreferences("weatherfa",this.getActivity().MODE_PRIVATE);
        editor=sp.edit();
        cityName=sp.getString("cityname",null);//获取当前城市名称
        String locCityName=sp.getString("locCityName",null);//获取
        String weatherString=sp.getString("weather_info",null);//获取天气信息缓存
        String sNowTime=sp.getString("nowTime",null);

        WtLO.setVisibility(View.INVISIBLE);

        if(weatherString!=null){//有缓存时直接解析天气
            updateTimeTV.setText("更新时间:"+sNowTime);
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{//无缓存时去服务器查询天气
            requestWeather(cityName);
        }
        //历史天气分析界面
        hIM0.setImageResource(R.drawable.h_line);
        hIM1.setImageResource(R.drawable.h_histogram);
        setOnClickListener();
        //下拉刷新响应
        swipeRefreshLO.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(cityName);
            }
        });
        return root;
    }
    //历史天气按钮的响应
    private void setOnClickListener(){
        hFullsBT0.setOnClickListener(this::onClick);
        hFullsBT1.setOnClickListener(this::onClick);
    }
    private void onClick(View v) {
        switch (v.getId()){
            case R.id.h_item_fulls_bt0:
                Toast.makeText(getActivity(),"第一个按钮",Toast.LENGTH_SHORT).show();
                break;
            case R.id.h_item_fulls_bt1://跳转，历史天气统计
                Intent intent = new Intent(getActivity(), HWtStatisticsActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(),"第二个按钮",Toast.LENGTH_SHORT).show();
        }
    }

    //根据城市名请求城市天气信息
    private void requestWeather(final String cityName){
        //拼装接口地址
        String weatherUrl="http://api.k780.com/?app=weather.realtime&weaid=" +cityName+
                "&ag=today,futureDay,lifeIndex,futureHour&appkey=44303&" +
                "sign=8825b94df83576ebde4c12e7a4a45be4&format=json";
        //发出请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText= Objects.requireNonNull(response.body()).string();//得到服务器返回内容
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //因为要进行UI操作，必须将线程转换至主线程
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather !=null && "1".equals(weather.status)){
                            //请求成功，数据缓存到SharedPreferences中
                            editor.putString("weather_info",responseText);
                            //更新刷新时间
                            String sNowTime=nowTime();
                            updateTimeTV.setText("更新时间"+sNowTime);
                            editor.putString("nowTime",sNowTime);
                            editor.commit();//提交

                            showWeatherInfo(weather);
                            Toast.makeText(getActivity(),"获取天气信息成功",Toast.LENGTH_SHORT).show();
                            swipeRefreshLO.setRefreshing(false);
                        }
                        else{
                            Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                            swipeRefreshLO.setRefreshing(false);
                        }
                    }
                });
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("112233",e.toString());
                e.printStackTrace();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"请求失败",Toast.LENGTH_SHORT).show();
                        swipeRefreshLO.setRefreshing(false);
                    }
                });
            }
        });
    }
    //获取当前系统时间，用于提示更新时间
    private String nowTime(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    //drawable资源：将名称转换成id
    private int imageId(String s){
        return this.getResources().getIdentifier(s,"drawable",
                Objects.requireNonNull(getContext()).getOpPackageName());
    }
    //weather相关组件更新
    private void showWeatherInfo(Weather weather){
        String sIcon="d"+weather.result.realTime.wtIcon;
        nowIconIV.setImageResource(imageId(sIcon));
        nowWmdTV.setText(weather.result.realTime.week);//只显示周几
        String sCity=weather.result.county;

        nowCityTV.setText(cityName);
        String sTt=weather.result.realTime.wtType+"  "+weather.result.realTime.wtTemp+"℃";
        nowTtTV.setText(sTt);
        //----------detail
        wtDetailList.clear();
        WtDetail aqi=new WtDetail(R.drawable.wt_aqi,"PM2.5",weather.result.realTime.wtAqi);
        wtDetailList.add(aqi);
        WtDetail humi=new WtDetail(R.drawable.wt_humi,"湿度",weather.result.realTime.wtHumi);
        wtDetailList.add(humi);
        WtDetail windtype=new WtDetail(R.drawable.wt_windtype,"风向",weather.result.realTime.wtWindType);
        wtDetailList.add(windtype);
        WtDetail vis=new WtDetail(R.drawable.wt_vis,"能见度",weather.result.realTime.wtVisibility);
        wtDetailList.add(vis);
        WtDetail rain=new WtDetail(R.drawable.wt_rain,"降水量",weather.result.realTime.wtRainfall);
        wtDetailList.add(rain);
        WtDetail windp=new WtDetail(R.drawable.wt_windp,"风力",weather.result.realTime.wtWinp);
        wtDetailList.add(windp);
        detailRV.setNestedScrollingEnabled(false);//禁止滑动
        detailRV.setLayoutManager(new GridLayoutManager(getActivity(), 3));//GridLayout
        WtDetailAdapter adapter1=new WtDetailAdapter(wtDetailList);
        detailRV.setAdapter(adapter1);
        //------------forecast
        wtForecastList.clear();
        int drawableId;
        for(FutureEDay eDay:weather.result.futureDay){//(gson类名，变量，实体)
            String sIcon1="d"+eDay.wtIcon1;
            WtForecast wf=new WtForecast(eDay.week,eDay.dateYmd.substring(5,10),imageId(sIcon1),
                    eDay.wtType1,eDay.wtTemp1+"℃",eDay.wtTemp2+"℃");
            wtForecastList.add(wf);
        }
        forecastRV.setNestedScrollingEnabled(false);//禁止滑动
        forecastRV.setLayoutManager(new LinearLayoutManager(getActivity()));//LinearLayoutManager
        WtForecastAdapter adapter2=new WtForecastAdapter(wtForecastList);
        forecastRV.setAdapter(adapter2);
        //------------lifeindex 4个
        wtLifeIndexList.clear();
        WtLifeIndex uv=new WtLifeIndex(R.drawable.life_index_uv,
                weather.result.today.lifeIndex.uv.liAttr,weather.result.today.lifeIndex.uv.liNm);
        wtLifeIndexList.add(uv);
        WtLifeIndex xt=new WtLifeIndex(R.drawable.life_index_xt,
                weather.result.today.lifeIndex.xt.liAttr,weather.result.today.lifeIndex.xt.liNm);
        wtLifeIndexList.add(xt);
        WtLifeIndex cy=new WtLifeIndex(R.drawable.life_index_cy,
                weather.result.today.lifeIndex.cy.liAttr,weather.result.today.lifeIndex.cy.liNm);
        wtLifeIndexList.add(cy);
        WtLifeIndex xc=new WtLifeIndex(R.drawable.life_index_xc,
                weather.result.today.lifeIndex.xc.liAttr,weather.result.today.lifeIndex.xc.liNm);
        wtLifeIndexList.add(xc);
        lifeIndexRV.setNestedScrollingEnabled(false);//禁止滑动
        lifeIndexRV.setLayoutManager(new GridLayoutManager(getActivity(), 4));//GridLayout
        WtLifeIndexAdapter adapter3=new WtLifeIndexAdapter(wtLifeIndexList);
        lifeIndexRV.setAdapter(adapter3);

        //后台自动更新
        WtLO.setVisibility(View.VISIBLE);
        Intent intent=new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);
    }
}
