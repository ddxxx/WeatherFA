/*
历史天气分析之某一城市某一时间段内天气类型统计的柱状图：

从sp中读取天气页当前选择的城市，
点击按钮选择起始和终止日期，点击确定后，将（城市，起始日期，终止日期）作为url参数参入，
响应为（weathertype，total），weathertype作为x轴名称，total作为柱状图数值
 */
package com.example.weatherfa.historyActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.weatherfa.MainActivity;
import com.example.weatherfa.R;
import com.example.weatherfa.constant.NetConstant;
import com.fantasy.doubledatepicker.DoubleDateSelectDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HWtStatisticsActivity extends AppCompatActivity {
    //准备存储从sp获得的城市名
    private String cityName;
    //日期选择相关变量
    private Button mShowDatePickBtn;    //日期选择按钮
    private DoubleDateSelectDialog mDoubleTimeSelectDialog;
    private String allowedSmallestTime, allowedBiggestTime, defaultChooseDate;  //允许的起、止日期，默认选中日期
    //柱状图相关变量
    private BarChart chart;
    private XAxis xAxis;
    //用于控制GSON request的编码格式
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去除顶部状态栏
        setContentView(R.layout.activity_h_wt_statistics);
        //获取当前城市名称
        SharedPreferences sp = this.getSharedPreferences("weatherfa", MODE_PRIVATE);
        cityName = sp.getString("cityname", "东平");
        //标题栏相关设置
        setTitle(cityName + "-历史天气统计");//名称
        ActionBar actionBar = getSupportActionBar();//添加返回键
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initUI();//(函数)
    }

    private void initUI() {
        //组件初始化
        mShowDatePickBtn = findViewById(R.id.query_bt);
        chart = findViewById(R.id.chart1);
        //设置“选择日期”的起、止、默认选中日期，格式：yyyy-MM-dd
        allowedSmallestTime = "2018-01-01";
        allowedBiggestTime = nowTime();
        defaultChooseDate = nowTime();
        //选择日期按钮的响应事件
        mShowDatePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomTimePicker();//（函数）显示选择日期的dialog
            }
        });
        //柱状图相关属性设置
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(60);// 柱条超过60时不会再编注数值
        chart.setPinchZoom(false);// false只能x或y方向放大
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        //x轴名称相关属性设置
        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(60);//旋转角度以免重叠
        chart.getAxisLeft().setDrawGridLines(false);
        //动画相关设置 add a nice and smooth animation
        //chart.animateY(1500);
        chart.getLegend().setEnabled(false);
    }

    /*
     okhttp异步请求进行注册
     参数统一传递字符串
     传递到后端再进行类型转换以适配数据库
     */
    private void asyncGetHWeather(final String cityName,
                                  final String date1,
                                  final String date2) {//城市名，起、止日期
        // 发送请求属于耗时操作，开辟子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                // okhttp的使用，POST，异步； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体
                StringBuffer sb = new StringBuffer();
                sb.append("flag=").append("2")//flag作为标志，区分请求何种数据
                        .append("&cityName=").append(cityName)
                        .append("&date1=").append(date1)
                        .append("&date2=").append(date2);   //设置表单参数
                Log.i("Hwt", "" + sb.toString());
                RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, sb.toString());
                // 3、发送请求，特别强调这里是POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getGetHWeatherURL())
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("Hweather", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        // 先判断一下服务器是否异常
                        String responseStr = response.toString();
                        Log.e("Hweather", responseStr);
                        if (responseStr.contains("200")) {
                            // response.body().string()只能调用一次，多次调用会报错
                            String responseBodyStr = Objects.requireNonNull(response.body()).string();
                            Log.e("fanhui",responseBodyStr);
                            try {
                                JSONObject jsonObject = new JSONObject(responseBodyStr);//获得JSONBObject对象
                                int success = jsonObject.getInt("success");
                                if (success == 200) {
                                    /*
                                    将response的result中的weathertype放入String数组（用于x轴名称显示），
                                    total作为柱状图数值
                                     */
                                    JSONArray ResultJSONArray = jsonObject.getJSONArray("result");
                                    ArrayList<BarEntry> yVals = new ArrayList<>();
                                    ArrayList<String> xValues=new ArrayList<>();
                                    //String[] xValues = new String[20];
                                    for (int i = 0; i < ResultJSONArray.length(); i++) {
                                        JSONObject resultJSONObject = ResultJSONArray.getJSONObject(i);
                                        //xValues[i] = resultJSONObject.getString("xdata");
                                        xValues.add(resultJSONObject.getString("xdata"));
                                        //BarEntry(x轴index，y轴对应数值)
                                        yVals.add(new BarEntry(i, resultJSONObject.getInt("ydata")));
                                    }
                                    drawChart(yVals, xValues);//绘制柱状图函数
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("Hweather", "服务器异常");
                            showToastInThread(HWtStatisticsActivity.this, responseStr);
                        }
                    }
                });
            }
        }).start();
    }

    //实现在子线程中显示Toast
    private void showToastInThread(Context context, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    //显示选择起止日期对话框
    public void showCustomTimePicker() {
        if (mDoubleTimeSelectDialog == null) {
            mDoubleTimeSelectDialog = new DoubleDateSelectDialog(this,
                    allowedSmallestTime, allowedBiggestTime, defaultChooseDate);
            //确定选择事件响应
            mDoubleTimeSelectDialog.setOnDateSelectFinished(new DoubleDateSelectDialog.OnDateSelectFinished() {
                @Override
                public void onSelectFinished(String startTime, String endTime) {
                    //按钮显示已选中日期
                    mShowDatePickBtn.setText(startTime.replace("-", ".")
                            + "至" + endTime.replace("-", "."));
                    //（函数）获取历史天气相关数据
                    asyncGetHWeather(cityName, startTime, endTime);
                }
            });
            //取消选择事件响应
            mDoubleTimeSelectDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
        }
        if (!mDoubleTimeSelectDialog.isShowing()) {
            mDoubleTimeSelectDialog.show();
        }
    }

    //获取当前系统时间，用于提示更新时间
    private String nowTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis()-24*60*60*1000);
        return simpleDateFormat.format(date);
    }

    //绘制柱状图（total-数值，weathertype-名称）
    public void drawChart(ArrayList<BarEntry> yVals, ArrayList<String> xValues) {

        BarDataSet set1;
        set1 = new BarDataSet(yVals, "weather type");//初始化柱状图数据源（单柱状图，未设置显示）
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);//颜色设置
        set1.setDrawValues(true);//在顶部显示柱状条数值
        set1.setValueTextSize(13f);
        //x轴数据设置
        //xAxis.setLabelCount(yVals.size(),true); 设置true会位置错乱
        xAxis.setLabelCount(yVals.size());
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(13f);
        IAxisValueFormatter iAxisValueFormatter = new XAxisValueFormatter(xValues);
        xAxis.setValueFormatter(iAxisValueFormatter);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        chart.setData(data);
        chart.setFitBars(true);
        chart.invalidate();
    }

    //自定义x轴的名称显示
    public static class XAxisValueFormatter implements IAxisValueFormatter {
        private ArrayList<String> xValues;

        public XAxisValueFormatter(ArrayList<String> xValues) {
            this.xValues = xValues;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return xValues.get((int) value);
        }
    }

    //添加menu响应
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_button, menu);
        return true;
    }

    //menu的item响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//返回键
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}