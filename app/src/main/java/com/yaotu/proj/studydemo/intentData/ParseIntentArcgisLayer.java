package com.yaotu.proj.studydemo.intentData;

import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.FeatureSet;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;

import java.util.Iterator;


/**
 * Created by Administrator on 2017/7/14.
 */

public class ParseIntentArcgisLayer {

    /*
     1、QueryTask：是一个进行空间和属性查询的功能类，它可以在某个地图服务的某个子图层内进行查询，
    顺便提一下的是，QueryTask进行查询的地图服务并不必须加载到Map中进行显示。
    QueryTask的执行需要两个先决条件：一个是需要查询的图层URL、一个是进行查询的过滤条件。

    */

    public static FeatureSet QueryTask(String url, String whereArgs)  {
        //新建一个QueryTask
        QueryTask queryTask = new QueryTask(url);
        // Query对象
        Query query = new Query();

        //传入空间几何范围，可以不设置
        //合法的geometry类型是Extent, Point, Multipoint, Polyline, Polygon

        //是否返回查询结果的空间几何信息
        query.setReturnGeometry(true);
        query.setOutFields(new String[] { "*" }); //返回字段
        query.setOutSpatialReference(SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));//设置查询输出的坐标系;
        //查询的where条件，可以是任何合法的SQL语句，可以不设置
        query.setWhere(whereArgs);

        //同步查询，不需要绑定事件，直接返回查询结果
        FeatureSet featureSet = null;
        try {
            featureSet = queryTask.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return featureSet;
    }
}
