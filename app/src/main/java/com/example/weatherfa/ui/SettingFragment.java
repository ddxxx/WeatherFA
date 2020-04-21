package com.example.weatherfa.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherfa.R;



public class SettingFragment extends Fragment {
    private SharedPreferences sp;     //存储
    private SharedPreferences.Editor editor;
    private int updateFreq;
    //组件声明
    private EditText setET;
    private Button setConfirmBT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        //实例化
        setET = root.findViewById(R.id.set_et);
        setConfirmBT = root.findViewById(R.id.set_confirm_bt);
        //获取之前的频率并显示
        sp = this.getActivity().getSharedPreferences("weatherfa", this.getActivity().MODE_PRIVATE);
        editor = sp.edit();
        updateFreq = sp.getInt("updateFreq", -1);
//        Log.e("112233", String.valueOf(updateFreq));
//        setET.setText(updateFreq);
//        setET.setHint(up dateFreq);
        //点击软键盘外部，收起软键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(setET.getWindowToken(), 0); //隐藏
        //按钮响应
        setConfirmBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setFreq = Integer.parseInt(setET.getEditableText().toString().trim());
                if (setFreq == updateFreq) {
                    Toast.makeText(getActivity(), "未修改", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putInt("updateFreq", setFreq);
                    editor.apply();
                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(SettingFragment.this)
                            .navigate(R.id.action_SettingFragment_to_WeatherFragment);

                }
            }
        });

        return root;
    }
}
