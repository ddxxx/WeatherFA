package com.example.weatherfa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherfa.ui.weather.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    public int aaa=1;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);//不可删改
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        /*
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_header_refresh) {
                    Toast.makeText(MainActivity.this,"anle",Toast.LENGTH_SHORT).show();
                    sp.getString("name", name);
                    sp.getString("telphone", telphone);
                    nameTV.setText(name);
                    telphoneTV.setText(telphone);
                }
                return true;
            }
        });
        */
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //绑定当前的ActionBar，除此之外NavigationUI还能绑定Toolbar和CollapsingToolbarLayout
        //绑定后，系统会默认处理ActionBar左上角区域，为你添加返回按钮，将所切换到的Fragment在导航图里的name属性中的内容显示到Title
        //.setDrawerLayout(drawerLayout)后才会出现菜单按钮
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mAppBarConfiguration=new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawer).build();
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_weather,R.id.nav_userinfo,R.id.nav_setting,
//                R.id.nav_feedback, R.id.nav_about)
//                .setDrawerLayout(drawer)
//                .build();

         NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
         NavigationUI.setupWithNavController(navigationView, navController);

    }
    @Override
    public void setRequestedOrientation(int requestedOrientation){
        return;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//响应menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){//响应menu的item
        switch (item.getItemId()) {
            case R.id.city_settings:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, CityManagement.class);
                startActivity(intent);
                //finish();
                return true;
            default://确保onSupportNavigateUp被调用
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
