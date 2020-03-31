package com.example.weatherfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.List;

public class CityManagement extends AppCompatActivity {
    private TextView pickedCityTV;

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
                                //开始定位，模拟定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.from(CityManagement.this).locateComplete(
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
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