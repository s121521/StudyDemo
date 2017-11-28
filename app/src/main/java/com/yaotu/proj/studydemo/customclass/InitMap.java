package com.yaotu.proj.studydemo.customclass;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.yaotu.proj.studydemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/21.
 * 初始化地图
 */

public class InitMap {
    private MapView mapView;
    private LocationManager locMag;
    private Context context;
    private Location m_location;
    private BaiduMap baiduMap;
    private TextView txt_showInfo;
    private final String TAG = this.getClass().getSimpleName();
    public InitMap(Context context,MapView mapView,BaiduMap baiduMap,TextView showView) {
        this.context = context;
        this.mapView = mapView;
        this.baiduMap = baiduMap;
        txt_showInfo = showView;
        mapMethod();
    }
    public void mapMethod(){
        // 隐藏logo
        View child = mapView.getChildAt(1);
        if (child != null){
            child.setVisibility(View.GONE);
        }
        locMag = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locMag.getProviders(true);
        for (String provider : providers) {
            m_location = locMag.getLastKnownLocation(provider);
            if (null != m_location) {
                localization(m_location);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(),m_location.getLongitude())),16));
            }
            locMag.requestLocationUpdates(provider,3 * 1000,0,m_locationListener);
        }
    }

    private LocationListener m_locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            m_location = location;
            Log.i(TAG, "InitMap---------onLocationChanged: ------>"+location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    //定位
    public void localization(Location loc){
        LatLng latLng = latLngConVert(new LatLng(loc.getLatitude(),loc.getLongitude()));
        // 开启定位图层
        baiduMap .setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(loc.getAccuracy())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .latitude(latLng.latitude)
                .longitude(latLng.longitude).build();
        // 设置定位数据
        baiduMap.setMyLocationData(locData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.marker_loc);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, mCurrentMarker);
        baiduMap.setMyLocationConfiguration(config);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(loc.getLatitude(),loc.getLongitude())),16));

    }
    // 将GPS设备采集的原始GPS坐标转换成百度坐标
    public LatLng latLngConVert(LatLng sourceLatLng){
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
    //===============================================================================================
    /**
     * 画Dot,Polyline,Polygon
     */
    private Overlay overlay_polygon,overlay_polyline;
    public void drawDot(LatLng latLng){
       /* DotOptions dotOptions = new DotOptions()
                .center(latLng).color(R.color.blue)
                .visible(true).radius(8);*/
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        OverlayOptions markerOptions = new MarkerOptions()
                .position(latLng).icon(bitmapDescriptor).visible(true);
        baiduMap.addOverlay(markerOptions);

    }
    public void drawDotBlue(LatLng latLng){
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_blue_mark);
        OverlayOptions markerOptions = new MarkerOptions()
                .position(latLng).icon(bitmapDescriptor).visible(true);
        baiduMap.addOverlay(markerOptions);

    }
    public void drawPolyLine(List<LatLng> points){//至少两个点
        if (null != overlay_polyline) {
            overlay_polyline.remove();
        }
        OverlayOptions polylineOptions = new PolylineOptions()
                .points(points).color(0xAA00FF00).width(8);
        double distance = DistanceUtil.getDistance(points.get(points.size()-1),points.get(points.size()-2));
        txt_showInfo.setText("距离："+String.format("%.2f",distance)+"米");
        overlay_polyline = baiduMap.addOverlay(polylineOptions);
    }
    //画多边形

    /**
     * 通过Gps坐标点画多边形，注意两点：1.传过来的坐标点是Gps点，在百度地图上显示需要进行坐标转换在显示
     *                                  2.利用arcgis api 计算多边形面积，直接使用传过来的点进行计算
     * @param latLngs
     */
    private double area,permiter;
    public void drawPolyGonByGps(List<LatLng> latLngs,boolean isBaiduPoint){//至少三个点
        OverlayOptions polygonOption = null;
        if (null != overlay_polygon) {
            overlay_polygon.remove();
        }
        if (isBaiduPoint) {//isBaiduPoint 判断latLngs数据是否百度坐标点
            polygonOption = new PolygonOptions()
                    .points(latLngs)
                    .stroke(new Stroke(5, 0xAA00FF00))
                    .fillColor(0xAAFFFF00);
        } else {//将经纬度坐标点转换为百度坐标点
            List<LatLng> BaiduPoints = new ArrayList<>();
            for (int i = 0; i < latLngs.size();i++) {
                BaiduPoints.add(latLngConVert(new LatLng(latLngs.get(i).latitude, latLngs.get(i).longitude)));
            }
            //构建用户绘制多边形的Option对象
            polygonOption = new PolygonOptions()
                    .points(BaiduPoints)
                    .stroke(new Stroke(5, 0xAA00FF00))
                    .fillColor(0xAAFFFF00);
        }
        overlay_polygon = baiduMap.addOverlay(polygonOption); //在地图上添加多边形Option，用于显示
        Polygon arcgis_Polygon = new Polygon();
        // 利用arcgis sdk 计算polygon面积
        if (latLngs.size() > 1) {
            for (int i = 0 ; i < latLngs.size() ; i++) {
                if (i == 0) {
                    arcgis_Polygon.startPath((Point) GeometryEngine.project(new Point(latLngs.get(i).longitude,latLngs.get(i).latitude), SpatialReference.create(SpatialReference.WKID_WGS84),SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR)));
                } else {
                    arcgis_Polygon.lineTo((Point) GeometryEngine.project(new Point(latLngs.get(i).longitude,latLngs.get(i).latitude), SpatialReference.create(SpatialReference.WKID_WGS84),SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR)));
                }
            }
            area =Math.abs(arcgis_Polygon.calculateArea2D());
            permiter = arcgis_Polygon.calculateLength2D();
            //showMessage(String.format("%.2f",Math.abs(area))+"平方米");
            txt_showInfo.setText(String.format("%.2f",area)+" 平方米|"+String.format("%.2f",permiter)+" 米");
        }
    }

    public double getArea(){
        return area;
    }
    public double getPermiter(){
        return permiter;
    }
    public void reset(){
        TempData.pointList.clear();
        TempData.latLngList.clear();
        baiduMap.clear();
        txt_showInfo.setText("");
        locMag.removeUpdates(m_locationListener);
    }
    public Location getM_location(){
        Log.i(TAG, "InitMap---------getM_location: ---------->"+m_location);
        return m_location;
    }
    public void stopLocationListener(){
        locMag.removeUpdates(m_locationListener);
    }
}
