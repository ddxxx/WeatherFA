/*
历史天气分析之某一城市某一时间段内历史温度变化趋势的折线图：

从sp中读取天气页当前选择的城市，
点击按钮选择起始和终止日期，点击确定后，将（城市，起始日期，终止日期）作为url参数参入，
响应为（ydata,ydata1,ydata2,xdata）->(最高温，最低温，温差，日期)，日期做x轴，绘制三条折线
 */

package com.example.weatherfa.historyActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xiasuhuei321.loadingdialog.manager.StyleManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

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

public class HWtWindActivity extends AppCompatActivity {
    private StyleManager s = new StyleManager();
    private LoadingDialog ld;
    private String flag="3";//3:风向，4:风力
    //准备存储从sp获得的城市名
    private String cityName;
    //日期选择相关变量
    private Button mShowDatePickBtn;    //日期选择按钮
    private DoubleDateSelectDialog mDoubleTimeSelectDialog;
    private String allowedSmallestTime, allowedBiggestTime, defaultChooseDate;  //允许的起、止日期，默认选中日期
    //饼状图相关变量
    private PieChart chart;
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
        setContentView(R.layout.activity_h_wt_wind);
        //获取当前城市名称
        SharedPreferences sp = this.getSharedPreferences("weatherfa", MODE_PRIVATE);
        cityName = sp.getString("cityname", "东平");
        //标题栏相关设置
        setTitle(cityName + "-历史风向/风力统计");//名称
        ActionBar actionBar = getSupportActionBar();//添加返回键
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initUI();//(函数)
    }
    @Override
    public void setRequestedOrientation(int requestedOrientation){
        return;
    }

    private void initUI() {
        s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);
        LoadingDialog.initStyle(s);
        ld=new LoadingDialog(HWtWindActivity.this);
        //组件初始化
        mShowDatePickBtn = findViewById(R.id.query_bt);
        chart = findViewById(R.id.chart1);
        //设置“选择日期”的起、止、默认选中日期，格式：yyyy-MM-dd
        allowedSmallestTime = "2014-01-01";
        allowedBiggestTime = nowTime();
        defaultChooseDate = nowTime();
        //选择日期按钮的响应事件
        mShowDatePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomTimePicker();//（函数）显示选择日期的dialog
            }
        });
        //饼状图相关属性设置
        chart.setNoDataText("选择日期后查看历史风向统计");//设置空数据时的显示文本
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setTransparentCircleRadius(48f);// 半透明圈
        chart.setDrawCenterText(true);//饼状图中间可以添加文字
        chart.setHoleColor(Color.WHITE);
        chart.setCenterText("当前查看：历史风向统计");
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setUsePercentValues(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
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
                sb.append("flag=").append(flag)//flag作为标志，区分请求何种数据
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
                        //Log.e("Hweather", responseStr);
                        if (responseStr.contains("200")) {
                            // response.body().string()只能调用一次，多次调用会报错
                            String responseBodyStr = Objects.requireNonNull(response.body()).string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseBodyStr);//获得JSONBObject对象
                                int success = jsonObject.getInt("success");
                                if (success == 1) {
                                    //处理result
                                    JSONArray ResultJSONArray = jsonObject.getJSONArray("result");
                                    ArrayList<PieEntry> entries=new ArrayList<>();//总数
                                    ArrayList<String> xValues=new ArrayList<>();//类别

                                    for (int i = 0; i < ResultJSONArray.length(); i++) {
                                        JSONObject resultJSONObject = ResultJSONArray.getJSONObject(i);
                                        xValues.add(resultJSONObject.getString("xdata"));
                                        //Entry(x轴index，y轴对应数值),要求是float，需自定义设配器
                                        entries.add(new PieEntry(resultJSONObject.getInt("ydata"),
                                                resultJSONObject.getString("xdata")));
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ld.close();
                                            drawChart(entries,xValues);//绘制柱状图函数
                                        }
                                    });
                                }else{
                                    //请求失败，所请求的城市历史天气数据未入库
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ld.close();
                                            //页面返回时，弹出提示框，包括确认、取消按钮，提示文字
                                            AlertDialog.Builder builder = new AlertDialog.Builder(HWtWindActivity.this);
                                            builder.setTitle("提示");
                                            builder.setMessage("未查询到当前城市历史天气数据");
                                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //Log.e("Hweather", "服务器异常");
                            showToastInThread(HWtWindActivity.this, responseStr);
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
                    ld.setLoadingText("加载中...")//设置loading时显示的文字
                            .show();
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

    //获取当前系统时间的前一天，用于日期选择dialog的默认选中日期
    private String nowTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis()-24*60*60*1000);
        return simpleDateFormat.format(date);
    }

    //绘制折线图（最高温，最低温，温差，日期）
    public void drawChart(ArrayList<PieEntry> entries,ArrayList<String> xValues) {

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    //自定义x轴的名称显示
    public static class XAxisValueFormatter implements IAxisValueFormatter {
        private ArrayList<String> xValues;
        public XAxisValueFormatter(ArrayList<String> xValues){
            this.xValues=xValues;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return xValues.get((int)value);
        }
    }

    //添加menu响应
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wind_menu, menu);
        return true;
    }

    //menu的item响应
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.change_type:
                if(flag.equals("3")){
                    flag="4";
                    item.setTitle(R.string.change_type2);
                    chart.setNoDataText("选择日期后查看历史风力统计");//设置空数据时的显示文本
                    chart.setCenterText("当前查看：历史风力统计");
                    chart.clear();
                }else{
                    flag="3";
                    item.setTitle(R.string.change_type1);
                    chart.setNoDataText("选择日期后查看历史风向统计");//设置空数据时的显示文本
                    chart.setCenterText("当前查看：历史风向统计");
                    chart.clear();
                }
                return true;
        }
        if (item.getItemId() == android.R.id.home) {//返回键
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
