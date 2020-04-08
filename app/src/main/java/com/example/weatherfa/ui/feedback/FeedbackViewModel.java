package com.example.weatherfa.ui.feedback;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FeedbackViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FeedbackViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("请发送邮件至：\n1426138878@qq.com");
    }

    public LiveData<String> getText() {
        return mText;
    }
}