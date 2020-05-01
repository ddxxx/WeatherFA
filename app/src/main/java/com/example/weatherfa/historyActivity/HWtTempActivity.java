package com.example.weatherfa.historyActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.weatherfa.R;
import com.example.weatherfa.constant.NetConstant;
import com.fantasy.doubledatepicker.DoubleDateSelectDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomappbar.BottomAppBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Parameter;
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

public class HWtTempActivity extends AppCompatActivity {
    //准备存储从sp获得的城市名
    private String cityName;
    //日期选择相关变量
    private Button mShowDatePickBtn;    //日期选择按钮
    private DoubleDateSelectDialog mDoubleTimeSelectDialog;
    private String allowedSmallestTime, allowedBiggestTime, defaultChooseDate;  //允许的起、止日期，默认选中日期
    //柱状图相关变量
    private LineChart chart;
    private XAxis xAxis;
    private YAxis yAxis;
    //用于控制GSON request的编码格式
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去除顶部状态栏
        setContentView(R.layout.activity_h_wt_temp);
        //获取当前城市名称
        SharedPreferences sp = this.getSharedPreferences("weatherfa", MODE_PRIVATE);
        cityName = sp.getString("cityname", "东平");
        //标题栏相关设置
        setTitle(cityName + "-历史温度变化");//名称
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
        //折线图相关属性设置
        //chart.setOnChartValueSelectedListener(this);
        chart.setNoDataText("当前未查看任何历史数据");//设置空数据时的显示文本
        chart.setDrawGridBackground(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.getAxisLeft().setEnabled(true);
        chart.getAxisRight().setEnabled(true);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawGridLines(false);
        // enable touch gestures
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        //设置图表绘制可见标签数量的最大值
        //chart.setMaxVisibleValueCount(50);

        //x
        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
 //       xAxis.setLabelRotationAngle(60);//旋转角度以免重叠
        chart.getAxisLeft().setDrawGridLines(false);
//
//        xAxis = chart.getXAxis();
//        //xAxis.setTypeface(tfLight);
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextSize(11f);
//        xAxis.setTextColor(R.color.black33);
//        xAxis.setDrawGridLines(false);
//        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = chart.getAxisLeft();
//        //leftAxis.setTypeface(tfLight);
//        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
//        leftAxis.setAxisMaximum(50f);
//        leftAxis.setAxisMinimum(-30f);
        leftAxis.setDrawGridLines(true);
//        leftAxis.setGranularityEnabled(true);

        //动画相关设置 add a nice and smooth animation
        //chart.animateY(1500);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(tfLight);
        l.setTextSize(11f);
        l.setTextColor(R.color.colorRed);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);
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
                sb.append("flag=").append("1")//flag作为标志，区分请求何种数据
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
                                    ArrayList<Entry> entries = new ArrayList<>();
                                    ArrayList<Entry> entries1 = new ArrayList<>();
                                    ArrayList<Entry> entries2 = new ArrayList<>();
                                    //ArrayList<Entry> xVals =new ArrayList<>();
                                    ArrayList<String> xValues =new ArrayList<>();
                                    //String[] xValues = new String[20];
                                    for (int i = 0; i < ResultJSONArray.length(); i++) {
                                        JSONObject resultJSONObject = ResultJSONArray.getJSONObject(i);
                                        xValues.add(resultJSONObject.getString("xdata"));
                                        //xValues[i] = resultJSONObject.getString("xdata");
                                        //Entry(x轴index，y轴对应数值),要求是float，需自定义设配器
                                        String xdata=resultJSONObject.getString("xdata").replace("-","");
                                        Log.e("show xdata:",String.valueOf(i)+"  "+xdata);
                                        entries.add(new Entry(i, resultJSONObject.getInt("ydata")));

                                    }
                                    drawChart(entries,xValues);//绘制柱状图函数
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("Hweather", "服务器异常");
                            showToastInThread(HWtTempActivity.this, responseStr);
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
    public void drawChart(ArrayList<Entry> entries,ArrayList<String> xValues) {

        LineDataSet set1;
        set1 = new LineDataSet(entries, "最高温度");//初始化柱状图数据源（单柱状图，未设置显示）
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(R.color.colorRed);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        set1.setDrawValues(true);


        //set1.setValueTextSize(13f);
        //x轴数据设置
        //xAxis.setLabelCount(xValues.size(),true); //设置true会位置错乱
        xAxis.setLabelCount(xValues.size());
        xAxis.setGranularity(1);     //这个很重要
        xAxis.setDrawLabels(true);
        //xAxis.setTextSize(13f);
        IAxisValueFormatter iAxisValueFormatter = new XAxisValueFormatter(xValues);
        xAxis.setValueFormatter(iAxisValueFormatter);
        xAxis.setLabelRotationAngle(90);
        //xAxis.setXOffset();




//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
        LineData data = new LineData(set1);
        data.setValueFormatter(new LargeValueFormatter("℃"));
        chart.setData(data);
        //chart.setFitBars(true);
        chart.invalidate();
    }

    //自定义x轴的名称显示
    public static class XAxisValueFormatter implements IAxisValueFormatter {
        private ArrayList<String> xValues;
        public XAxisValueFormatter(ArrayList<String> xValues){
            this.xValues=xValues;
        }
//        private String[] xValues;

//        public XAxisValueFormatter(String[] xValues) {
//            this.xValues = xValues;
//        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Log.e("value=",String.valueOf(value));
            return xValues.get((int)value);
         //   return xValues[(int) value];
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