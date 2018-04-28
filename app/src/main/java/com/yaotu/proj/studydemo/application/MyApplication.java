package com.yaotu.proj.studydemo.application;

import android.app.Application;
import com.yaotu.proj.studydemo.util.VersionUtils;


/**
 * Created by Administrator on 2017/10/10.
 */

public class MyApplication extends Application {
    private static MyApplication instance = null;
    /**
     * 双数为默认取线上,单数则取本地测试服务器
     */
    private boolean isReleaseVersion;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //根据版本号取余数
        isReleaseVersion = (VersionUtils.getVersionCode(this) % 2 == 0);
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public boolean isReleaseVersion() {
        return isReleaseVersion;
    }
}
