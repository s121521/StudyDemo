package com.yaotu.proj.studydemo.customclass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/6/19.
 */

public class CheckNetwork {
    //判断是否联网(方法一)
    public  static boolean isNetWorkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);//管理网络连接的操作
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();//获取代表联网状态的NetWorkInfo对象
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return  true;
                }
            }
        }
        return false;
    }
    //方法二
    private static final  int NETWORK_NONE=0;
    private static final int NETWORK_WIFI=1;// wifi
    private static final int NETWORK_MOBILE=2;//mobile
    public static int getNetWorkState(Context context) {//得到网络的连接状态
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //WIFI
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_WIFI;
                }
                //NETWORK
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return NETWORK_MOBILE;
                }
            }
        }
        return NETWORK_NONE;
    }
}
