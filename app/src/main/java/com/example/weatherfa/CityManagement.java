package com.example.weatherfa;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    //sharedpreferences
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    //loc
    private TextView locCityTV;
    private View locCityView;
    //recycleview
    private List<WtCity> wtCityList=new ArrayList<>();
    private RecyclerView cityRV;
    private WtCityAdapter adapter;
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
    //===========
    private Button delCityBT;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DefaultCityPickerTheme);
        setContentView(R.layout.activity_city_management);
        //实例化
        locCityTV=(TextView)findViewById(R.id.loc_city_name_tv);
        locCityView=(View)findViewById(R.id.loc_city_lo);
        cityRV=(RecyclerView)findViewById(R.id.city_recycler_view);
        //============
        delCityBT=(Button)findViewById(R.id.city_item_del_bt);
        //sharedpreferences
        sp=getSharedPreferences("city_list",MODE_PRIVATE);
        editor=sp.edit();
        adapter = new WtCityAdapter(wtCityList, this);//添加跳转响应，adapter需context参数


        cityRV.setNestedScrollingEnabled(false);
        cityRV.setLayoutManager(new LinearLayoutManager(this));
        cityRV.setAdapter(adapter);
        adapter.setOnItemClickListener(MyItemClickListener);


        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        showCity();
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
                                String sCity=data.getName();
                                Toast.makeText(getApplicationContext(),
                                        String.format("点击的数据: %s",sCity),
                                        Toast.LENGTH_SHORT).show();
                                addCity(sCity);
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
    //item及其内部控件的事件监听
    private WtCityAdapter.OnItemClickListener MyItemClickListener=
            new WtCityAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, WtCityAdapter.ViewName viewName, int position) {
                    switch (v.getId()){
                        case R.id.city_item_del_bt:
                            adapter.delDate(position);
                            saveToSP(wtCityList);
                            break;
                        default:
                            String sCityName=wtCityList.get(position).getName();
                            editor.putString("cityname",sCityName);
                            editor.commit();
                            //跳转
                            Intent intent=new Intent(CityManagement.this, MainActivity.class);
                            intent.putExtra("fragment_id",0);
                            startActivity(intent);
                            break;
                    }
                }
                @Override
                public void onItemLongClick(View v) {

                }
            };

    //选择的城市保存到sp中
    private void addCity(String sCity){
        WtCity wtCity=new WtCity(sCity);
        int i;
        for(i=0;i<wtCityList.size();i++){
            if(wtCityList.get(i).getName().equals(wtCity.getName())){
                adapter.delDate(i);
                saveToSP(wtCityList);
                break;
            }
        }
        adapter.addData(wtCity);
        //存储
    }

    public void saveToSP(List<WtCity> wtCityList1){
        editor.putInt("city_nums",wtCityList1.size());
        for(int i=0;i<wtCityList1.size();i++){
            editor.putString("item_"+i,wtCityList1.get(i).getName());
        }
        editor.commit();
    }

    private void showCity(){
        cityRV.removeAllViews();
        int cityNums=sp.getInt("city_nums",-1);
        if(cityNums!=-1) {
           for (int i = 0; i < cityNums; i++) {
               String sCityName = sp.getString("item_" + i, null);
               WtCity wtCity = new WtCity(sCityName);
               wtCityList.add(wtCity);
           }
       }
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
