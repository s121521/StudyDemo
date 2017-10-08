package com.yaotu.proj.studydemo.util;

import android.os.Environment;
import android.util.Log;

import com.esri.android.map.FeatureLayer;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.tasks.query.QueryParameters;
import com.yaotu.proj.studydemo.bean.PlaceBean;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Administrator on 2017/3/10.
 * 1.如果获得该要素中的geoMetry，需要注意投影问题
 * 2. 注意文件路径
 */

public class MyGeoDataBase {
    private static final String SDCARD_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    private static final String GEODATABASE_PATH = "/point_data.geodatabase";
    private List<PlaceBean> list;
    private Geodatabase  geodatabase= null;
    private final String TAG = this.getClass().getSimpleName();
    public MyGeoDataBase() {

    }
    //================================================================================
    public List<PlaceBean> getListEntity() {
        list = new ArrayList<PlaceBean>();
        try {
            geodatabase = new Geodatabase(SDCARD_ROOT_PATH + GEODATABASE_PATH);
            GeodatabaseFeatureTable table = geodatabase.getGeodatabaseFeatureTableByLayerId(0);
            FeatureLayer featureLayer = new FeatureLayer(table);
            CallbackListener<FeatureResult> callListener = new CallbackListener<FeatureResult>() {
                @Override
                public void onCallback(FeatureResult objects) {

                }

                @Override
                public void onError(Throwable throwable) {

                }
            };
            Future<FeatureResult> future = featureLayer.selectFeatures(new QueryParameters(), FeatureLayer.SelectionMode.NEW, callListener);
            FeatureResult featureResult = future.get();
            Log.i(TAG, "run: ---------"+featureResult);
            if (featureResult != null) {
                for (Object element : featureResult) {
                    if (element instanceof Feature) {
                        Feature feature = (Feature) element;
                        String name = feature.getAttributes().get("NAME").toString();
                        String type = feature.getAttributes().get("tab_type").toString();
                        PlaceBean placeBean = new PlaceBean();
                        placeBean.setP_name(name);
                        placeBean.setP_type(type);
                        placeBean.setP_state("0000");
                        list.add(placeBean);
                        Log.i(TAG, "run: ===============>"+ name);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            if (null != geodatabase) {
                geodatabase.dispose();
            }
        }
        Log.i(TAG, "getAllAddressName: "+list.size());
        return list;
    }

    //=============================================================================================
    public Feature queryGeodatabase(String args) {
        Feature feature=null;
        try {
            geodatabase = new Geodatabase(SDCARD_ROOT_PATH + GEODATABASE_PATH);
            GeodatabaseFeatureTable table = geodatabase.getGeodatabaseFeatureTableByLayerId(0);
            FeatureLayer layer = new FeatureLayer(table);
            QueryParameters parameters = new QueryParameters();
            parameters.setReturnGeometry(true);
            parameters.setWhere(args);
            CallbackListener callbackListener = new CallbackListener() {
                @Override
                public void onCallback(Object o) {

                }

                @Override
                public void onError(Throwable throwable) {

                }
            };
            Future<FeatureResult> future = layer.selectFeatures(parameters, FeatureLayer.SelectionMode.NEW, callbackListener);
            FeatureResult featureResult = future.get();
            if (featureResult != null) {
                for (Object element : featureResult) {
                    if (element instanceof  Feature) {
                        feature = (Feature) element;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return feature;
    }
}
