package com.example.weatherfa.constant;

public class NetConstant {

    private static String loginURL = "http://192.168.1.104:8080/weatherBack/LoginServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String registerURL = "http://192.168.1.104:8080/weatherBack/RegisterServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String createItemURL = "http://10.0.2.2:8090/item/create";
    private static String getItemListURL = "http://10.0.2.2:8090/item/list";
    private static String submitOrderURL = "http://10.0.2.2:8090/order/createorder";
    private static String changeNameURL="http://192.168.1.104:8080/weatherBack/ChangeNameServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static String changePwdURL="http://192.168.1.104:8080/weatherBack/ChangePwdServlet?useUnicode=true&characterEncoding=utf-8&useSSL=false";


    public static String getLoginURL() {
        return loginURL;
    }

    public static String getRegisterURL() {
        return registerURL;
    }

    public static String getCreateItemURL() {
        return createItemURL;
    }

    public static String getGetItemListURL() {
        return getItemListURL;
    }

    public static String getSubmitOrderURL() {
        return submitOrderURL;
    }

    public static String getChangeNameURL(){ return  changeNameURL;}

    public static String getChangePwdURL(){ return  changePwdURL;}


}
