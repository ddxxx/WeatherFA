package com.example.weatherfa;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
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
import com.example.weatherfa.adapter.WtCityAdapter;
import com.example.weatherfa.wtclass.WtCity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

public class CityManagement extends AppCompatActivity {
    /*
    组件声明
     */
    //loc
    private TextView locCityTV;
    private View locCityView;
    //recycleview
    private List<WtCity> wtCityList=new ArrayList<>();
    private RecyclerView cityRV;
    /*
    定位、搜索城市变量声明
     */
    //默认定位城市为北京（如果设为空，LocateState为failure时会闪退（库问题））
    private LocatedCity locatedCity=new LocatedCity("北京","北京","101010100");
    private int errodCode=-1;
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
        //实例化
        locCityTV=(TextView)findViewById(R.id.loc_city_name_tv);
        locCityView=(View)findViewById(R.id.loc_city_lo);
        cityRV=(RecyclerView)findViewById(R.id.city_recycler_view);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //定位
        Location();
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
                                String sCity=data.getName()+"-"+data.getProvince();

                                Toast.makeText(getApplicationContext(),
                                        String.format("点击的数据: %s",sCity),
                                        Toast.LENGTH_SHORT).show();
                                showCity(sCity);
                            }

                            @Override
                            public void onLocate() {
                                //自定义定位

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
                                }, 0);
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

    private void showCity(String sCity){
        //----------recyclerview
        Log.e("112233","选择城市："+sCity);
        WtCity wtCity=new WtCity(sCity);
        wtCityList.add(wtCity);
        cityRV.setNestedScrollingEnabled(false);
        cityRV.setLayoutManager(new LinearLayoutManager(this));
        WtCityAdapter adapter=new WtCityAdapter(wtCityList,this);//添加跳转响应，adapter需context参数
        cityRV.setAdapter(adapter);
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
                if(aMapLocation.getErrorCode()==0){
                    //去掉“市”
                    String s1=aMapLocation.getDistrict().substring(0,aMapLocation.getDistrict().length()-1);
                    String s2=aMapLocation.getProvince().substring(0,aMapLocation.getProvince().length()-1);
                    locatedCity=new LocatedCity(s1, s2,aMapLocation.getCityCode());
                    //loc跳转到mainactivity（但返回后回到上一个界面）
                    locCityView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(CityManagement.this,MainActivity.class);
                            intent.putExtra("cityName",s1);
                            startActivity(intent);
                            finish();
                        }
                    });
                    locCityTV.setText(s1+"-"+s2);
                    Log.e("112233",aMapLocation.getDistrict());
                }else{
                    //定位失败（错误码，错误信息）
                    Log.e("112233",
                            aMapLocation.getErrorCode()+aMapLocation.getErrorInfo());
                }
            }
        }
    }

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
