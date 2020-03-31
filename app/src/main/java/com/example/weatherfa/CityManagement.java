package com.example.weatherfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

public class CityManagement extends AppCompatActivity {
    private TextView pickedCityTV;
    //默认定位城市为北京（如果设为空，LocateState为failure时会闪退（库问题））
    private LocatedCity locatedCity=new LocatedCity("北京","北京","101010100");
    private int errodCode;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new MyAMapLocationListener();
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DefaultCityPickerTheme);
        setContentView(R.layout.activity_city_management);

        pickedCityTV=findViewById(R.id.picked_city_tv);
        //添加返回按钮
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //添加fab：添加城市
        FloatingActionButton fab = findViewById(R.id.city_management_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPicker.from(CityManagement.this)//.enableAnimation(enable)     6.
                        // .setAnimationStyle(anim)
                        .setLocatedCity(null)
                        //  .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                pickedCityTV.setText(String.format("当前城市： %s, %s",
                                        data.getName(), data.getCode()));
                                Toast.makeText(getApplicationContext(),
                                        String.format("点击的数据: %s, %s",
                                                data.getName(), data.getCode()),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLocate() {
                                //自定义定位
                                Location();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(errodCode==0)
                                            CityPicker.from(CityManagement.this).locateComplete(
                                                locatedCity, LocateState.SUCCESS);
                                        else
                                            CityPicker.from(CityManagement.this).locateComplete(
                                                    locatedCity, LocateState.FAILURE);
                                    }
                                }, 700);
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

    private void Location(){//定位
        Log.e("112233","定位函数");
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
        mLocationOption.setOnceLocationLatest(false);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    private class MyAMapLocationListener implements AMapLocationListener{//重写定位监听类
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation!=null){
                errodCode=aMapLocation.getErrorCode();
              //  errodCode=aMapLocation.getErrorCode();
                if(aMapLocation.getErrorCode()==0){
                    //去掉“市”
                    String s1=aMapLocation.getCity().substring(0,aMapLocation.getCity().length()-1);
                    String s2=aMapLocation.getProvince().substring(0,aMapLocation.getProvince().length()-1);
                    locatedCity=new LocatedCity(s1, s2,aMapLocation.getCityCode());
                    Log.e("112233",aMapLocation.getCity());
                }else{
                    //定位失败（错误码，错误信息）
                    Log.e("112233",
                            aMapLocation.getErrorCode()+aMapLocation.getErrorInfo());
                }
            }
        }
    }/*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

   */

    @Override
    public boolean onCreateOptionsMenu(Menu menu){//添加menu响应
        getMenuInflater().inflate(R.menu.city_management_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){//menu的item响应
        switch (item.getItemId()){
            case android.R.id.home://返回键
                this.finish();
                return true;
            case R.id.city_management_del:
                Toast.makeText(CityManagement.this,"delete city clicked.",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.city_management_edit:
                Toast.makeText(CityManagement.this,"edit city clicked.",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
