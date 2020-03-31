package com.example.weatherfa;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;




public class AddCity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private TextView curCityTV;
    private CheckBox hotCityCB,animCB,enableCB;
    private Button themeBtn;

    private static final String KEY="current_theme";

    private List<HotCity> hotCities;
    private int anim;
    private int theme;
    private boolean enable;

    /*之前编写的高德定位
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            theme = savedInstanceState.getInt(KEY);
            setTheme(theme > 0 ? theme : R.style.DefaultCityPickerTheme);
        }
        setContentView(R.layout.activity_add_city);

        curCityTV = findViewById(R.id.cur_city_text);
        hotCityCB = findViewById(R.id.hot_city_cb);
        animCB = findViewById(R.id.anim_cb);
        enableCB = findViewById(R.id.enable_anim_cb);
        themeBtn = findViewById(R.id.style_button);

        if (theme == R.style.DefaultCityPickerTheme) {
            themeBtn.setText("默认主题");
        } else if (theme == R.style.CustomTheme) {
            themeBtn.setText("自定义主题");
        }

        hotCityCB.setOnCheckedChangeListener(this);
        animCB.setOnCheckedChangeListener(this);
        enableCB.setOnCheckedChangeListener(this);

        themeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//切换主题
                if (themeBtn.getText().toString().startsWith("自定义")) {
                    themeBtn.setText("默认主题");
                    theme = R.style.DefaultCityPickerTheme;
                } else if (themeBtn.getText().toString().startsWith("默认")) {
                    themeBtn.setText("自定义主题");
                    theme = R.style.CustomTheme;
                }
                recreate();
            }
        });

        findViewById(R.id.pick_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPicker.from(AddCity.this).enableAnimation(enable)
                        .setAnimationStyle(anim)
                        .setLocatedCity(null)
                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                curCityTV.setText(String.format("当前城市： %s, %s",
                                        data.getName(), data.getCode()));
                                Toast.makeText(getApplicationContext(),
                                        String.format("点击的数据: %s, %s",
                                                data.getName(), data.getCode()),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，模拟定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.from(AddCity.this).locateComplete(
                                                new LocatedCity("深圳", "广东",
                                                        "101280601"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(getApplicationContext(),
                                        "取消选择", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
        switch (buttonView.getId()){
            case R.id.hot_city_cb:
                if(isChecked){
                    hotCities=new ArrayList<>();
                    hotCities.add(new HotCity("北京","北京","101010100"));
                    hotCities.add(new HotCity("上海","上海","101020100"));
                    hotCities.add(new HotCity("广州","广东","101280101"));
                    hotCities.add(new HotCity("深圳","广东","101280601"));
                    hotCities.add(new HotCity("杭州","浙江","101210101"));
                }else{
                    hotCities=null;
                }
                break;
            case R.id.anim_cb:
                anim=isChecked ? R.style.CustomAnim:R.style.DefaultCityPickerAnimation;
                break;
            case R.id.enable_anim_cb:
                enable=isChecked;
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY,theme);
    }
/*之前编写的高德定位
        Location();
        //添加返回按钮

       //actionbar添加返回按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){//添加menu响应


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){//menu的item响应
        switch (item.getItemId()){
            case android.R.id.home://返回键
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */



    /*之前写的定位，定位结果添加在按钮上
    private void Location(){//定位
        //初始化定位
        mLocationClient=new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption=new AMapLocationClientOption();
        //设置定位模式为高精度模式
        mLocationOption.setLocationMode(
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次结果
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    private class MyAMapLocationListener implements AMapLocationListener{//重写定位监听类
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation!=null){
                if(aMapLocation.getErrorCode()==0){
                    //获得定位信息后将城市编码存储，将城市显示在按钮上
                    locCityButton.setText(aMapLocation.getCity());
                    Log.e("123",aMapLocation.getCity());
                }else{
                    //定位失败（错误码，错误信息）
                    Log.e("123",
                            aMapLocation.getErrorCode()+aMapLocation.getErrorInfo());
                }
            }
        }
    }

 */

}
