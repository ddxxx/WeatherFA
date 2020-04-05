package com.example.weatherfa.wtclass;

public class WtDetail {
    private int icon;
    private String key;
    private String value;
    public WtDetail(int icon,String key,String value){
        this.icon=icon;
        this.key=key;
        this.value=value;
    }
    public int getIcon() {
        return icon;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
