package com.example.weatherfa.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("WeatherFA\n天气预报及历史天气分析\n版本：v1.0");
    }

    public LiveData<String> getText() {
        return mText;
    }
}