package com.yaotu.proj.studydemo.util;


/**
 * Created by Administrator on 2018/4/28.
 */

public class HttpUrlAddress {

    public static String getHttpUrl(){
        String httpUrl = "";
        if (!SharedPreferencesManager.getHttpPreferencesUtils().getString(SharedPreferencesManager.sHttpurl).isEmpty()) {
            httpUrl = SharedPreferencesManager.getHttpPreferencesUtils().getString(SharedPreferencesManager.sHttpurl);
        }
        return httpUrl;
    }
}
