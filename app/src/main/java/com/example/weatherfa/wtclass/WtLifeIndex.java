package com.example.weatherfa.wtclass;

public class WtLifeIndex {
    private int icon;
    private String value;
    private String type;

    public WtLifeIndex(int icon, String value, String type) {
        this.icon = icon;
        this.value = value;
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
