package com.example.weatherfa.constant;

public class NetConstant {

    private static String loginURL = "http://192.168.1.109:8080/weatherBack/LoginServlet";
    private static String registerURL = "http://192.168.1.109:8080/weatherBack/RegisterServlet";
    private static String createItemURL = "http://10.0.2.2:8090/item/create";
    private static String getItemListURL = "http://10.0.2.2:8090/item/list";
    private static String submitOrderURL = "http://10.0.2.2:8090/order/createorder";


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
}
