package com.yaotu.proj.studydemo.util;

import android.app.Application;

import com.yaotu.proj.studydemo.application.MyApplication;

/**
 * Created by Administrator on 2018/4/28.
 * 项目中专门用来管理本地用户数据 和公共的数据的 SharedPreferences
 */

public class SharedPreferencesManager {
    private static SharedPreferencesUtils   httpPreferencesUtils;
    public static final String sHttpurl = "httpurl";

    public static SharedPreferencesUtils getHttpPreferencesUtils(){
        if (httpPreferencesUtils == null) {
            httpPreferencesUtils = new SharedPreferencesUtils(MyApplication.getInstance(), sHttpurl);
        }
        return httpPreferencesUtils;
    }

}
