package com.example.weatherfa.constant;

public class NetConstant {

    private static String loginURL = "http://192.168.1.108:8080/weatherBack/LoginServlet";
    private static String registerURL = "http://192.168.1.108:8080/weatherBack/RegisterServlet";
    private static String changeNameURL="http://192.168.1.108:8080/weatherBack/ChangeNameServlet";
    private static String changePwdURL="http://192.168.1.108:8080/weatherBack/ChangePwdServlet";
    private static String getHWeatherURL = "http://192.168.1.108:8080/weatherBack/getHistoryWeatherServlet";//历史统计

    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }

    public static String getGetHWeatherURL() {
        return getHWeatherURL;
    }

    public static String getChangeNameURL(){ return  changeNameURL;}

    public static String getChangePwdURL(){ return  changePwdURL;}


}
