package com.example.weatherfa.gson;

import com.google.gson.annotations.SerializedName;

public class FutureEDay {
    public String dateYmd;
    public String week;
    @SerializedName("wtNm1")
    public String wtType1;
    @SerializedName("wtNm2")
    public String wtType2;
    public String wtIcon1;
    public String wtIcon2;
    public String wtTemp1;
    public String wtTemp2;
    /*
    未来一周的各种信息如需更多，再次添加：
    如风力风向，生活指数，日出日落时间
     */

}
