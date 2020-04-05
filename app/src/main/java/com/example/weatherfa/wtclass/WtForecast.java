package com.example.weatherfa.wtclass;

public class WtForecast {
    private String week;
    private String date;
    private int icon;
    private String type;
    private String maxTemp;
    private String minTemp;

    public WtForecast(String week, String date, int icon, String type, String maxTemp, String minTemp) {
        this.week = week;
        this.date = date;
        this.icon = icon;
        this.type = type;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    public String getWeek() {
        return week;
    }

    public String getDate() {
        return date;
    }

    public int getIcon() {
        return icon;
    }

    public String getType() {
        return type;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }
}
