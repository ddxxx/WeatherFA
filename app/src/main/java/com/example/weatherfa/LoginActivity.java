package com.example.weatherfa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherfa.constant.NetConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //用于控制GSON request的编码格式
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    // 声明UI对象
    Button bt_login = null;
    EditText et_account = null;
    EditText et_password = null;
    TextView tv_to_register = null;
    TextView tv_forget_password = null;
    TextView tv_service_agreement = null;

    // 声明SharedPreferences对象
    SharedPreferences sp;
    // 声明SharedPreferences编辑器对象
    SharedPreferences.Editor editor;
    // 声明token
    private String token;
    private String token_telphone;
    private String token_password;

    // Log打印的通用Tag
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();//初始化
        setOnClickListener();//各组件的点击事件
         /*
            设置当输入框焦点失去时提示错误信息
            第一个参数指明输入框对象
            第二个参数指明输入数据类型
            第三个参数指明输入不合法时提示信息
         */
        setOnFocusChangeErrMsg(et_account, "phone", "手机号格式不正确");
        setOnFocusChangeErrMsg(et_password, "password", "密码必须不少于6位");
    }

    private void initUI() {
        bt_login = findViewById(R.id.bt_login); // 登录按钮
        et_account = findViewById(R.id.et_account); // 输入账号
        et_password = findViewById(R.id.et_password); // 输入密码
        tv_to_register = findViewById(R.id.tv_to_register); // 注册
        tv_forget_password = findViewById(R.id.tv_forget_password); // 忘记密码
        tv_service_agreement = findViewById(R.id.tv_service_agreement); // 同意协议
    }
    private void setOnClickListener() {
        bt_login.setOnClickListener(this); // 登录按钮
        tv_to_register.setOnClickListener(this); // 注册文字
        tv_forget_password.setOnClickListener(this); // 忘记密码文字
        tv_service_agreement.setOnClickListener(this); // 同意协议文字
    }
    //点击事件的响应
    @Override
    public void onClick(View v) {
        // 从textview获取用户输入的账号和密码，进行验证
        String account = et_account.getText().toString();
        String password = et_password.getText().toString();

        switch (v.getId()) {
            case R.id.bt_login:// 登录按钮
                // 让密码输入框失去焦点,触发setOnFocusChangeErrMsg方法
                et_password.clearFocus();
                // 发送URL请求之前,先进行校验
                if (!(isTelphoneValid(account) && isPasswordValid(password))) {
                    Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                /*
                   因为验证是耗时操作，所以独立成方法，
                   在方法中开辟子线程，避免在当前UI线程进行耗时操作
                */
                asyncValidate(account, password);//验证输入的用户名和密码是否正确
                break;
            case R.id.tv_to_register:// 注册按钮
                /*
                把登录页面填写但未注册的手机号传递到注册页面
                 */
                Intent it_login_to_register = new Intent(this, RegisterActivity.class);
                it_login_to_register.putExtra("account", account);
                startActivity(it_login_to_register);
                Toast.makeText(this, "注册按了", Toast.LENGTH_SHORT).show();
                break;
            // 以下功能目前都没有实现
            case R.id.tv_forget_password:
                // 跳转到修改密码界面

                Toast.makeText(this, "忘记密码按了", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void setOnFocusChangeErrMsg(EditText editText,String inputType, String errMsg){
        editText.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        String inputStr = editText.getText().toString();
                        if (!hasFocus){
                            if(inputType == "phone"){
                                if (isTelphoneValid(inputStr)){
                                    editText.setError(null);
                                }else {
                                    editText.setError(errMsg);
                                }
                            }
                            if (inputType == "password"){
                                if (isPasswordValid(inputStr)){
                                    editText.setError(null);
                                }else {
                                    editText.setError(errMsg);
                                }
                            }
                        }
                    }
                }
        );
    }
    // 校验账号不能为空且必须是中国大陆手机号（宽松模式匹配）
    private boolean isTelphoneValid(String account) {
        if (account == null) {
            return false;
        }
        // 首位为1, 第二位为3-9, 剩下九位为 0-9, 共11位数字
        String pattern = "^[1]([3-9])[0-9]{9}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(account);
        return m.matches();
    }
    // 校验密码不少于6位
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    /*
      okhttp异步POST请求 要求API level 21+，请求的account只能是手机号
     */
    private void asyncValidate(final String account, final String password) {
        /*
         发送请求属于耗时操作，所以开辟子线程执行
         上面的参数都加上了final，否则无法传递到子线程中
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // okhttp异步POST请求； 总共5步
                // 1、初始化okhttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                // 2、构建请求体requestBody
                final String telphone = account;  // 为了让键和值名字相同，我把account改成了telphone，没其他意思
                StringBuffer sb=new StringBuffer();
                sb.append("telphone=").append(telphone)
                        .append("&password=").append(password);
                RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, sb.toString());
                // 3、发送请求，因为要传密码，所以用POST方式
                Request request = new Request.Builder()
                        .url(NetConstant.getLoginURL())
                        .post(requestBody)
                        .build();
                // 4、使用okhttpClient对象获取请求的回调方法，enqueue()方法代表异步执行
                okHttpClient.newCall(request).enqueue(new Callback() {
                    // 5、重写两个回调方法
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "请求URL失败： " + e.getMessage());
                        showToastInThread(LoginActivity.this, "请求URL失败, 请重试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 先判断一下服务器是否异常
                        String responseStr = response.toString();
                        if (responseStr.contains("200")) {
                             /*
                            注意这里，同一个方法内
                            response.body().string()只能调用一次，多次调用会报错
                             */
                            /* 使用Gson解析response的JSON数据的第一步 */
                            String responseBodyStr = response.body().string();
                            /* 使用Gson解析response的JSON数据的第二步 */
                       //     JsonObject responseBodyJSONObject = (JsonObject) new JsonParser().parse(responseBodyStr);
                            // 如果返回的status为success，则getStatus返回true，登录验证通过
                            Log.d(TAG, String.valueOf(responseBodyStr.equals("success")));

                            if (responseBodyStr.equals("success")) {Log.d(TAG,"tongguo");
                            /*
                             更新token，下次自动登录
                             真实的token值应该是一个加密字符串
                             我为了让token不为null，就随便传了一个字符串
                             这里的telphone和password每次都要重写的
                             否则无法实现修改密码
                            */
                                sp = getSharedPreferences("login_info", MODE_PRIVATE);
                                editor = sp.edit();
                                editor.putString("token", "token_value");
                                editor.putString("telphone", telphone);
                                editor.putString("password", password);
                                if (editor.commit()) {
                                    Intent it_login_to_main = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(it_login_to_main);
                                    // 登录成功后，登录界面就没必要占据资源了
                                    finish();
                                } else {
                                    showToastInThread(LoginActivity.this, "token保存失败，请重新登录");
                                }
                            } else {
                           //     getResponseErrMsg(LoginActivity.this, responseBodyJSONObject);
                                Log.d(TAG, "账号或密码验证失败");
                            }
                        } else {
                            Log.d(TAG, "服务器异常");
                            showToastInThread(LoginActivity.this, responseStr);
                        }
                    }
                });

            }
        }).start();
    }
    /*
      使用Gson解析response的JSON数据
      本来总共是有三步的，一、二步在方法调用之前执行了
    */
    private String getStatus(JsonObject responseBodyJSONObject) {
        /* 使用Gson解析response的JSON数据的第三步
           通过JSON对象获取对应的属性值 */
        String status = responseBodyJSONObject.get("status").getAsString();
        // 登录成功返回的json为{ "status":"success", "data":null }
        // 只获取status即可，data为null
        return status;
    }
    /*
      使用Gson解析response返回异常信息的JSON中的data对象
      这也属于第三步，一、二步在方法调用之前执行了
     */
    private void getResponseErrMsg(Context context, JsonObject responseBodyJSONObject) {
        JsonObject dataObject = responseBodyJSONObject.get("data").getAsJsonObject();
        String errorCode = dataObject.get("errorCode").getAsString();
        String errorMsg = dataObject.get("errorMsg").getAsString();
        Log.d(TAG, "errorCode: " + errorCode + " errorMsg: " + errorMsg);
        // 在子线程中显示Toast
        showToastInThread(context, errorMsg);
    }
    // 实现在子线程中显示Toast
    private void showToastInThread(Context context, String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
