package com.pengxinyang.chessgamecilent.config;

public class APIConfig {
    private static final String api = "http://localhost:8081";
    public static String getApi(String url) {
        return api + url;
    }
}
