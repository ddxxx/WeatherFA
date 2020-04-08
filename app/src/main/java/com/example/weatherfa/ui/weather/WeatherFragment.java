package com.example.weatherfa.ui.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherfa.MainActivity;
import com.example.weatherfa.R;
import com.example.weatherfa.adapter.WtForecastAdapter;
import com.example.weatherfa.adapter.WtLifeIndexAdapter;
import com.example.weatherfa.gson.FutureEDay;
import com.example.weatherfa.wtclass.WtDetail;
import com.example.weatherfa.adapter.WtDetailAdapter;
import com.example.weatherfa.gson.Weather;
import com.example.weatherfa.util.HttpUtil;
import com.example.weatherfa.util.Utility;
import com.example.weatherfa.wtclass.WtForecast;
import com.example.weatherfa.wtclass.WtLifeIndex;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherFragment extends Fragment{
    /*
    组件声明；详细天气信息
     */
    //now
    private ImageView nowIconIV;
    private TextView nowWmdTV,nowCityTV,nowTtTV;
    //详情
    private List<WtDetail> wtDetailList=new ArrayList<>();
    private RecyclerView detailRV;
    //未来一周
    private List<WtForecast> wtForecastList=new ArrayList<>();
    private RecyclerView forecastRV;
    //生活指数
    private List<WtLifeIndex> wtLifeIndexList=new ArrayList<>();
    private RecyclerView lifeIndexRV;

    private String cityName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WeatherViewModel weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        View root = inflater.inflate(R.layout.fragment_weather, container, false);
        //实例化
        nowIconIV= root.findViewById(R.id.wt_now_icon_iv);//-------------now
        nowWmdTV= root.findViewById(R.id.wt_now_wmd_tv);
        nowCityTV= root.findViewById(R.id.wt_now_city_tv);
        nowTtTV= root.findViewById(R.id.wt_now_tt_tv);//天气+温度
        detailRV= root.findViewById(R.id.detail_recycler_view);//-------detail
        forecastRV= root.findViewById(R.id.forecast_recycler_view);//------forecast
        lifeIndexRV= root.findViewById(R.id.life_index_recycler_view);//------lifeindex


        requestWeather("东平");

        return root;
    }
    /*
    根据城市名请求城市天气信息
     */
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
                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences
                                    (getActivity()).edit();
                            editor.apply();//提交
                            //更新ui
                            showWeatherInfo(weather);
                        }
                        else{
                            Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();

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
                    }
                });
            }
        });
    }

    //weather相关组件更新-----改为：初始化list数据
    private void showWeatherInfo(Weather weather){
        //------------now
        nowIconIV.setImageResource(R.drawable.sunny);
        nowWmdTV.setText(weather.result.realTime.week);//只显示周几
        String sCity=weather.result.distinct+"-"+weather.result.county;
        nowCityTV.setText(sCity);
        String sTt=weather.result.realTime.wtType+"  "+weather.result.realTime.wtTemp+"℃";
        nowTtTV.setText(sTt);
        //----------detail
        WtDetail aqi=new WtDetail(R.drawable.wt_aqi,"PM2.5",weather.result.realTime.wtAqi);
        wtDetailList.add(aqi);
        WtDetail humi=new WtDetail(R.drawable.wt_humi,"湿度",weather.result.realTime.wtHumi);
        wtDetailList.add(humi);
        WtDetail wind=new WtDetail(R.drawable.wt_wind,"风力",weather.result.realTime.wtWindp);
        wtDetailList.add(wind);
        WtDetail vis=new WtDetail(R.drawable.wt_vis,"能见度",weather.result.realTime.wtVisibility);
        wtDetailList.add(vis);
        WtDetail rain=new WtDetail(R.drawable.wt_rain,"降水量",weather.result.realTime.wtRainfall);
        wtDetailList.add(rain);
        detailRV.setNestedScrollingEnabled(false);//禁止滑动
        detailRV.setLayoutManager(new GridLayoutManager(getActivity(), 3));//GridLayout
        WtDetailAdapter adapter1=new WtDetailAdapter(wtDetailList);
        detailRV.setAdapter(adapter1);
        //------------forecast
        for(FutureEDay eDay:weather.result.futureDay){//(gson类名，变量，实体)
            WtForecast wf=new WtForecast(eDay.week,eDay.dateYmd.substring(5,10),R.drawable.ic_back_b,
                    eDay.wtType1,eDay.wtTemp1+"℃",eDay.wtTemp2+"℃");
            wtForecastList.add(wf);
        }
        forecastRV.setNestedScrollingEnabled(false);//禁止滑动
        forecastRV.setLayoutManager(new LinearLayoutManager(getActivity()));//LinearLayoutManager
        WtForecastAdapter adapter2=new WtForecastAdapter(wtForecastList);
        forecastRV.setAdapter(adapter2);
        //------------lifeindex 4个
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

    }
}
