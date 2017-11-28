package com.yaotu.proj.studydemo.customclass;

import com.baidu.mapapi.model.LatLng;
import com.esri.android.map.GraphicsLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/29.
 */

public class TempData {
    public static List<LatLng> pointList = new ArrayList<LatLng>();//存百度地图坐标
    public static List<LatLng> latLngList = new ArrayList<>();//存设备获取的经纬度坐标

    public static GraphicsLayer temp_graphicslayer=null;
    public static GraphicsLayer arcgis_Dcgraphicslayer= null;
    public static String placeid="";//用来存放监测点的ID值
    public static double longitude = 0;//用来存放监测点经度
    public static double latitude = 0;//用来存放监测点纬度

    public static String yhdh = "";//表示用户ID
    public static String yhmc = "";//用户名称
    public static String usertel ="";
    public static File file = null;
}
