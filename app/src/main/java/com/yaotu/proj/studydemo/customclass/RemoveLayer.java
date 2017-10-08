package com.yaotu.proj.studydemo.customclass;

import android.content.Context;

import com.esri.android.map.GraphicsLayer;
import com.yaotu.proj.studydemo.bean.FlagBean;

/**实现清除地图上添加的图层，并初始化
 * Created by Administrator on 2017/3/13.
 */

public class RemoveLayer {

    public static void remove(GraphicsLayer[] layers) {
     int len = layers.length;
        for (int i=0;i<len;i++) {
            layers[i].removeAll();
            System.out.println("clean graphiclayer");
        }
        FlagBean.ischoose = true;
        FlagBean.dcFlag = false;
        FlagBean.GPSFlag = false;//关闭GPS航迹
        FlagBean.StartGPS = true;

    }
}
