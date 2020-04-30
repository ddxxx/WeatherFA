package com.example.weatherfa.constant;

public class NetConstant {

    private static String loginURL = "http://192.168.1.104:8080/weatherBack/LoginServlet" +
            "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String registerURL = "http://192.168.1.104:8080/weatherBack/RegisterServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String changeNameURL="http://192.168.1.104:8080/weatherBack/ChangeNameServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String changePwdURL="http://192.168.1.104:8080/weatherBack/ChangePwdServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    //历史统计
    private static String getHWeatherURL = "http://192.168.1.105:8080/weatherBack/getHistoryWeatherServlet";

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
