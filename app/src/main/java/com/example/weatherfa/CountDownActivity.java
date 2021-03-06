package com.example.weatherfa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.weatherfa.constant.ModelConstant;
import com.example.weatherfa.constant.NetConstant;
import com.example.weatherfa.util.ModelPreference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CountDownActivity extends AppCompatActivity {
    // 右上角的文字控件
    private AppCompatTextView countDownText;
    private CountDownTimer timer;

    final static String TAG = "CountDownActivity";
    private Boolean isLogin;

    //用于控制GSON request的编码格式
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenConfig();
        setContentView(R.layout.activity_count_down);
        //组件初始化
        countDownText = findViewById(R.id.tv_count_down);

        initCountDown();
    }

    private void initCountDown() {
        if (!isFinishing()) {
            timer = new CountDownTimer(1000 * 6, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
                    // 获取token
                    String token = sp.getString("token", null);
                    if (token != null) {
                        String token_telphone = sp.getString("telphone", "");
                        String token_password = sp.getString("password", "");
                        // 异步登录
                        asyncValidate(token_telphone, token_password);
                    }

                    countDownText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkToJump();
                        }
                    });
                    int time = (int) millisUntilFinished;
                    countDownText.setText(time / 1000 + " 跳过");
                }

                @Override
                public void onFinish() {
                    checkToJump();
                }
            }.start();
        }
    }
    // 首次进入引导页判断
    private void checkToJump() {
        boolean isFirstin = ModelPreference.getBoolean(CountDownActivity.this, ModelConstant.FIRST_IN, true);
        if (isFirstin) {//首次使用进入注册页
            //Intent it_to_guide = new Intent(CountDownActivity.this, RegisterActivity.class);
            Intent it_to_guide = new Intent(CountDownActivity.this, LoginActivity.class);
            startActivity(it_to_guide);
            // 关闭首次打开
            ModelPreference.putBoolean(CountDownActivity.this, ModelConstant.FIRST_IN, false);
        } else {
            if (isLogin!=null && isLogin){//自动验证登录成功
                startActivity(new Intent(CountDownActivity.this, MainActivity.class));
            } else {//未登录等情况
                startActivity(new Intent(CountDownActivity.this, LoginActivity.class));
            }
        }
        // 回收内存
        destoryTimer();
        finish();
    }
    public void destoryTimer() {
        // 避免内存泄漏
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void asyncValidate(final String account, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                final String telphone = account;  // 为了让键和值名字相同，我把account改成了telphone，没其他意思
                StringBuffer sb=new StringBuffer();
                sb.append("telphone=").append(telphone)
                        .append("&password=").append(password);
                RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, sb.toString());
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败bai： " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                            String responseBodyStr = response.body().string();
                   //         JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            if (responseBodyStr.equals("success")) {
                                isLogin = true;
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                        }
                    }
                });
            }
        }).start();
    }
    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status;
    }

    private void fullScreenConfig() {
        // 去除ActionBar(因使用的是NoActionBar的主题，故此句有无皆可)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 去除状态栏，如 电量、Wifi信号等
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
