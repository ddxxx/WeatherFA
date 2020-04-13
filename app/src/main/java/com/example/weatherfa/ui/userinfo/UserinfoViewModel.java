package com.example.weatherfa.ui.userinfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserinfoViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<String> mText;

    public UserinfoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is userinfo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
