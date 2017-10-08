package com.yaotu.proj.studydemo.bean;

import android.graphics.Point;

/**
 * Created by Administrator on 2017/3/13.
 */

public class FlagBean {
    public static boolean ischoose = true;//用于判断是否第一次进行画矢量图
    public static boolean dcFlag = false;//判断是否通过点击屏幕点进行测量
    public static boolean scFlag = false;//判断是否根据GPS坐标进行测量
    public static boolean GPSFlag = false;//判断是否开启GPS航迹
    public static boolean StartGPS = true ;//
    public static final String SAMPLE_REPORT1 = "TYPE1";//样表一
    public static final String SAMPLE_REPORT2 = "TYPE2";//样表二
    public static final String SAMPLE_REPORT3 = "TYPE3";//样表三
    public static final String SAMPLE_REPORT4 = "TYPE4";//样表四
    public static boolean isHaveOverLay = false;//判断当前地图是否添加了覆盖物
}
