package com.example.weatherfa.wtclass;

public class WtHistory {
    public WtHistory(int fulls, int icon, String type) {
        this.fulls = fulls;
        this.icon = icon;
        this.type = type;
    }

    private int fulls;
    private int icon;
    private String type;

    public int getFulls() {
        return fulls;
    }

    public int getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }
}
