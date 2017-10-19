package com.yaotu.proj.studydemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.ProtocolType;
import com.baidu.trace.model.PushMessage;

/**
 * Created by Administrator on 2017/10/6.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";

    // 轨迹服务
    protected static Trace mTrace = null;

    // 鹰眼服务ID，开发者创建的鹰眼服务对应的服务ID
    public static final long serviceId = 140116;

    // 轨迹服务类型
    //0 : 不建立socket长连接，
    //1 : 建立socket长连接但不上传位置数据，
    //2 : 建立socket长连接并上传位置数据）
    private int traceType = 2;
     // 轨迹服务客户端
    public static LBSTraceClient mTraceClient = null;

    // Entity监听器
    public static OnEntityListener entityListener = null;

    // 采集周期（单位 : 秒）
    private int gatherInterval = 10;

    // 设置打包周期(单位 : 秒)
    private int packInterval = 20;

    protected static boolean isTraceStart = false;

    // 手机IMEI号设置为唯一轨迹标记号,只要该值唯一,就可以作为轨迹的标识号,使用相同的标识将导致轨迹混乱
    private String imei;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            imei = intent.getStringExtra("imei");
        }
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    //被销毁时反注册广播接收器
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTrace();
    }

    /**
     * 初始化
     */
    private void init() {
        // 初始化轨迹服务客户端
        mTraceClient = new LBSTraceClient(getApplicationContext());

        // 设置定位模式
        mTraceClient.setLocationMode(LocationMode.High_Accuracy);

        // 初始化轨迹服务
        mTrace  = new Trace(serviceId, imei, false);

        // 设置定位和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);

        // 设置http请求协议类型0:http,1:https
        mTraceClient.setProtocolType(ProtocolType.HTTP);
        //设置监听器
        mTraceClient.setOnTraceListener(mTraceListener);
        // 启动轨迹上传
        startTrace();
    }
    // 开启轨迹服务
    private void startTrace() {
        // 通过轨迹服务客户端client开启轨迹服务
        mTraceClient.startTrace(mTrace, null);
        mTraceClient.startGather(null);
    }

    // 停止轨迹服务
    public static void stopTrace() {
        // 通过轨迹服务客户端client停止轨迹服务
        Log.i(TAG, "stopTrace(), isTraceStart : " + isTraceStart);

        if(isTraceStart){
            mTraceClient.stopTrace(mTrace, null);
        }
    }

    // 初始化轨迹服务监听器
    private OnTraceListener mTraceListener = new OnTraceListener() {
        @Override
        public void onBindServiceCallback(int i, String s) {
            Log.i(TAG, "onBindServiceCallback: "+i+"-------"+s);
        }
        // 开启服务回调
        @Override
        public void onStartTraceCallback(int i, String s) {
            Log.i(TAG, "onStartTraceCallback: "+i+"--------"+s);
        }
        // 停止服务回调
        @Override
        public void onStopTraceCallback(int i, String s) {
            Log.i(TAG, "onStopTraceCallback: "+i+"--------"+s);
        }
        // 开启采集回调
        @Override
        public void onStartGatherCallback(int i, String s) {
            Log.i(TAG, "onStartGatherCallback: "+i+"--------"+s);
        }
        // 停止采集回调
        @Override
        public void onStopGatherCallback(int i, String s) {
            Log.i(TAG, "onStopGatherCallback: "+i+"--------"+s);
        }
        // 推送回调
        @Override
        public void onPushCallback(byte b, PushMessage pushMessage) {
            Log.i(TAG, "onPushCallback: "+b+"--------"+pushMessage);
        }
    };
}
