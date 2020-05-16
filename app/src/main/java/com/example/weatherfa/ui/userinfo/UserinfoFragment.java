package com.example.weatherfa.ui.userinfo;

import androidx.annotation.TransitionRes;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherfa.MainActivity;
import com.example.weatherfa.R;
import com.example.weatherfa.RegisterActivity;
import com.example.weatherfa.constant.NetConstant;
import com.example.weatherfa.ui.weather.WeatherViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class UserinfoFragment extends Fragment {
    //用于控制GSON request的编码格式
    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");


    private final String TAG = "UserInfoFragment";

    private TextView userInfoTV;
    private EditText editNameET=null,editOpwdET=null,editpwd1ET=null,editpwd2ET=null;
    private Button editFinish1BT=null,editFinish2BT=null;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String token_telphone;
    private String token_password;
    private String token_username;

    String name="";
    String opwd="",pwd1="",pwd2="";

    private UserinfoViewModel mViewModel;
    private View root;

    public static UserinfoFragment newInstance() {
        return new UserinfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(UserinfoViewModel.class);
        root = inflater.inflate(R.layout.fragment_userinfo, container, false);
        sp = Objects.requireNonNull(getActivity()).getSharedPreferences("login_info", Context.MODE_PRIVATE);
        token_telphone=sp.getString("telphone","");
        token_password=sp.getString("password","");
        token_username=sp.getString("name","");

        initUI();

        return root;
    }

    private void initUI(){
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        userInfoTV=root.findViewById(R.id.userinfo_tv);
        userInfoTV.setText("手机号："+token_telphone);

        editNameET=root.findViewById(R.id.edit_name_tv);
        editFinish1BT=root.findViewById(R.id.edit_finish1_bt);
        editOpwdET=root.findViewById(R.id.edit_pwd_origin_tv);
        editpwd1ET=root.findViewById(R.id.edit_pwd1_tv);
        editpwd2ET=root.findViewById(R.id.edit_pwd2_tv);
        editFinish2BT=root.findViewById(R.id.edit_finish2_bt);
        inputMethodManager.hideSoftInputFromWindow(editNameET.getWindowToken(), 0); //隐藏
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserinfoViewModel.class);
        // TODO: Use the ViewModel

        editFinish1BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                String name=editNameET.getText().toString();
                asyncChangeName(name);
            }
        });
        editFinish2BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                String opwd=editOpwdET.getText().toString();
                String pwd1=editpwd1ET.getText().toString();
                String pwd2=editpwd2ET.getText().toString();
                asyncChangePwd(opwd,pwd1,pwd2);
            }
        });
    }

    private void asyncChangePwd(final String opwd,
                                final String pwd1,
                                final String pwd2) {
        if(TextUtils.isEmpty(opwd) || TextUtils.isEmpty(pwd1)
                || TextUtils.isEmpty(pwd2)){
            Toast.makeText(getActivity(), "存在输入为空，修改密码失败",
                    Toast.LENGTH_SHORT).show();
        }/*else if(!(token_password.equals(opwd))){
            Toast.makeText(getActivity(), "原密码错误，修改密码失败",
                    Toast.LENGTH_SHORT).show();

        } */ else if(pwd1.equals(pwd2)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //1
                    OkHttpClient okHttpClient=new OkHttpClient();

                    RequestBody requestBody=new FormBody.Builder()
                            .add("telphone",token_telphone)
                            .add("opassword",opwd)
                            .add("password",pwd1)
                            .build();
                    Request request=new Request.Builder()
                            .url(NetConstant.getChangePwdURL())
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d(TAG,"onFailure:"+e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseStr=response.toString();
                            Log.e(TAG,responseStr);
                            if(responseStr.contains("200")){
                                String responseBodyStr=Objects.requireNonNull(response.body()).string();
                                Log.e(TAG,responseBodyStr);
                                if((responseBodyStr).equals("success")){
                                    editor=sp.edit();
                                    editor.putString("token","token_value");
                                    editor.putString("password",pwd1);
                                    if(editor.commit()){
                                        Looper.prepare();
                                        Toast.makeText(getActivity(),"修改密码成功",
                                                Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        Log.e(TAG,"修改成功");
                                        editor.putString("password",pwd1);
                                        editor.commit();

                                    }
                                }else{
                                    Log.e(TAG,"error");
                                }
                            }else{
                                Log.e(TAG,"服务器异常");
                                Looper.prepare();
                                Toast.makeText(getActivity(),"服务器异常",
                                        Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
            }).start();
        }else{
            Toast.makeText(getActivity(),"两次密码不一致，请重试",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //修改昵称
    private void asyncChangeName(final String name) {
        if(TextUtils.isEmpty(name)){
            Toast.makeText(getActivity(),"输入内容为空，未进行任何操作",
                    Toast.LENGTH_SHORT).show();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //1
                    OkHttpClient okHttpClient=new OkHttpClient();
                    //2
                    StringBuffer sb=new StringBuffer();
                    sb.append("telphone=").append(token_telphone)
                            .append("&name=").append(name);
                    Log.e("gai---",sb.toString());
                    RequestBody requestBody = RequestBody.create(FORM_CONTENT_TYPE, sb.toString());

                    Request request=new Request.Builder()
                            .url(NetConstant.getChangeNameURL())
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d(TAG,"on Failure:"+e.getMessage());
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            //下三行其实没用，服务器不返回
                            String responseStr=response.toString();
                            Log.e(TAG,responseStr);
                            if(responseStr.contains("200")){
                                String responseBodyStr= Objects.requireNonNull(response.body()).string();
                                Log.e(TAG,responseBodyStr);
                                if((responseBodyStr).equals("success")){
                                //    sp1=getSharedPreferences("login_info",MODE_PRIVATE);
                                    //如何存储在SharedPreference
                                    editor=sp.edit();
                                    editor.putString("name",name);
                                    editor.commit();
                                    Looper.prepare();
                                    Toast.makeText(getActivity(),"修改昵称成功",
                                            Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else{
                                    Log.e(TAG,"error");
                                }
                            }else{
                                Log.e(TAG,"服务器异常");
                                Looper.prepare();
                                Toast.makeText(getActivity(),responseStr,
                                        Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
            }).start();
        }
    }

}
