package com.example.weatherfa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weatherfa.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener  {
    // Log打印的通用Tag
    private final String TAG = "RegisterActivity";

    Button bt_submit_register = null;
    EditText et_telphone = null;
    EditText et_username = null;
    EditText et_password1 = null;
    EditText et_password2 = null;

    String account = "";
    String password = "";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();//组件初始化
        setOnClickListener();//点击事件响应

    }

    private void initUI() { // 初始化
        bt_submit_register = findViewById(R.id.bt_submit_register);
        et_telphone = findViewById(R.id.et_telphone);
        et_username = findViewById(R.id.et_username);
        et_password1 = findViewById(R.id.et_password);
        et_password2 = findViewById(R.id.et_password2);
    }
    private void setOnClickListener() {//点击事件响应
        bt_submit_register.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String telphone = et_telphone.getText().toString();
        String username = et_username.getText().toString();
        String password1 = et_password1.getText().toString();
        String password2 = et_password2.getText().toString();

        switch (v.getId()){
            case R.id.bt_submit_register:
                asyncRegister(telphone,username,password1, password2);
                // 点击提交注册按钮响应事件
                // 尽管后端进行了判空，但Android端依然需要判空
                break;
        }
    }

    // okhttp异步请求进行注册
    // 参数统一传递字符串
    // 传递到后端再进行类型转换以适配数据库
    private void asyncRegister(final String telphone,
                               final String username,
                               final String password1,
                               final String password2) {
        if (TextUtils.isEmpty(telphone) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
            Toast.makeText(RegisterActivity.this, "存在输入为空，注册失败",
                    Toast.LENGTH_SHORT).show();
        } else if (password1.equals(password2)) {//信息填写完整且两次密码正确，进行注册操作
            // 发送请求属于耗时操作，开辟子线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // okhttp的使用，POST，异步； 总共5步
                    // 1、初始化okhttpClient对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // 2、构建请求体
                    // 注意这里的name 要和后端接收的参数名一一对应，否则无法传递过去
                    RequestBody requestBody = new FormBody.Builder()
                            .add("telphone", telphone)
                            .add("name", username)
                            .add("password", password1)
                            .build();
                    // 3、发送请求，特别强调这里是POST方式
                    Request request = new Request.Builder()
                            .url(NetConstant.getRegisterURL())
                            .post(requestBody)
                            .build();
                    // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        // 5、重写两个回调方法
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            // 先判断一下服务器是否异常
                            String responseStr = response.toString();
                            Log.e(TAG,responseStr);
                            if (responseStr.contains("200")) {
                                // response.body().string()只能调用一次，多次调用会报错
                                String responseBodyStr = Objects.requireNonNull(response.body()).string();
                            //==    JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                                // 如果返回的status为success，代表验证通过
                           //==     if (getResponseStatus(responseBodyJSONObject).equals("success")) {
                                Log.e(TAG,responseBodyStr);
                                if ((responseBodyStr).equals("success")) {

                                        // 注册成功，记录token
                                    sp = getSharedPreferences("login_info", MODE_PRIVATE);
                                    editor = sp.edit();
                                    editor.putString("token", "token_value");//三个信息都存储，用于drawer显示
                                    editor.putString("name",username);
                                    editor.putString("telphone", telphone);
                                    editor.putString("password", password1); // 注意这里是password1
                                    editor.commit();

                                    if (editor.commit()) {
                                        Intent it_register_to_main = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(it_register_to_main);
                                        // 注册成功后，注册界面就没必要占据资源了
                                        finish();
                                    }
                                } else {
                                    Log.e(TAG,"error");
                            //===        getResponseErrMsg(RegisterActivity.this, responseBodyStr);
                                }
                            } else {
                                Log.e(TAG, "服务器异常");
                                showToastInThread(RegisterActivity.this, responseStr);
                            }
                        }
                    });

                }
            }).start();
        } else {
            Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
        }
    }
    // 使用Gson解析response的JSON数据中的status，返回布尔值
    private String getResponseStatus(JsonObject responseBodyJSONObject) {
        // Gson解析JSON，总共3步
        // 1、获取response对象的字符串序列化
        // String responseData = response.body().string();
        // 2、通过JSON解析器JsonParser()把字符串解析为JSON对象，
        //
        // *****前两步抽写方法外面了*****
        //
        // JsonObject jsonObject = (JsonObject) new JsonParser().parse(responseBodyStr);
        // 3、通过JSON对象获取对应的属性值
        String status = responseBodyJSONObject.get("status").getAsString();
        return status;
    }
    // 获取验证码响应data
    // 使用Gson解析response返回异常信息的JSON中的data对象
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }
    /* 实现在子线程中显示Toast */
    private void showToastInThread(Context context, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

}
