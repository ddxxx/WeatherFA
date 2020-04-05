package com.example.weatherfa.gson;

import com.google.gson.annotations.SerializedName;

public class LifeIndex {
    public Detail uv;
    public Detail xt;
    @SerializedName("ct")
    public Detail cy;
    public Detail xc;
    public Detail kq;

    public static class Detail{
        public String LiNo;
        public String liNm;
        public String liAttr;
        public String liDese;
    }
}
