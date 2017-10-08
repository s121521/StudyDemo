package com.yaotu.proj.studydemo.BLL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.yaotu.proj.studydemo.bean.tableBean.TkEnterpriseBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.DateDialog;
import com.yaotu.proj.studydemo.customclass.TempData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/28.
 */

public class TkEnterpriseOperate implements Operate<TkEnterpriseBean> {
    private Context context;
    private View view;
    //----------------------------------------------
    private final String TAG = this.getClass().getSimpleName();
    private MapValueType valueType = null;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locMag;
    private Location m_location;
    private LinearLayout mapLayout,LinearLayoutButtom;
    private EditText tk_ktzxzb_etxt;//中心坐标
    private TextView tk_ktxmmzb_etxt;//项目面坐标
    private EditText tk_scmj_etxt;//实测面积
    private EditText tk_zjyxqb_etxt;//证件有效期 开始
    private EditText tk_zjyxqe_etxt;//证件有效期 结束
    private EditText tk_kqsx_etxt;//矿权属性
    private EditText tk_bhqmc_etxt;//保护区名称
    private EditText tk_jb_etxt;//级别
    private EditText tk_qymc_etxt;//企业名称
    private EditText tk_fzjg_etxt;//发证机关
    private RadioGroup tk_sjqhGroup_radio;//始建前后按钮
    private RadioButton tk_sjqh_radio1, tk_sjqh_radio2;//始建前、始建后
    private String bhqsjqh;//存始建前后值
    private EditText tk_kqslqk_etxt;//历史沿革
    private EditText tk_ktfs_etxt;//勘探方式
    private EditText tk_hbpfwh_etxt;//环保批复文号
    private EditText tk_sfsbhq_etxt;//是否涉保护区
    private EditText tk_ktscqk_etxt;//生产情况
    private EditText tk_syq_etxt;//实验区
    private EditText tk_hcq_etxt;//缓冲区
    private EditText tk_hxq_etxt;//核心区
    private EditText tk_lsqk_etxt;//落实情况
    private TextView txt_showInfo;
    private ImageView tk_photo_img;
    private TextView tk_canleImage;
    private TextView tk_sfsbhq_dmz,tk_kqsx_dmz;
    private TextView tk_syq_txt,tk_hcq_txt,tk_hxq_txt;
    private String placeid = "";
    private ImageButton dot_btn, reset_btn,getvelue_btn;//地图button
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private List<Long> times = new ArrayList<Long>();//触摸事件标志

    public TkEnterpriseOperate(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_tkenterprise, null);
        mapLayout = (LinearLayout) view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        mapLayout.setVisibility(View.GONE);
        LinearLayoutButtom.setVisibility(View.GONE);
        mapView = (MapView) view.findViewById(R.id.report_mapview);
        baiduMap = mapView.getMap();

        dot_btn = (ImageButton) view.findViewById(R.id.dot_btn);//地图打点按钮
        imgBtn_moveCurrt = (ImageButton) view.findViewById(R.id.imgBtn_moveCurrt);
        reset_btn = (ImageButton) view.findViewById(R.id.reset_btn);//地图重置按钮
        getvelue_btn = (ImageButton) view.findViewById(R.id.getvelue_btn);//获取值

        tk_ktzxzb_etxt = (EditText) view.findViewById(R.id.tk_ktzxzb_etxt);//显示中心坐标
        tk_ktxmmzb_etxt = (TextView) view.findViewById(R.id.tk_ktxmmzb_etxt);//显示项目面坐标
        tk_scmj_etxt = (EditText) view.findViewById(R.id.tk_scmj_etxt);//实测面积
        tk_zjyxqe_etxt = (EditText) view.findViewById(R.id.tk_zjyxqe_etxt);//证件有效期 结束
        tk_zjyxqb_etxt = (EditText) view.findViewById(R.id.tk_zjyxqb_etxt);//证件有效期 开始
        tk_kqsx_etxt = (EditText) view.findViewById(R.id.tk_kqsx_etxt);//矿权属性
        txt_showInfo = (TextView) view.findViewById(R.id.report1_textview_area);
        tk_photo_img = (ImageView) view.findViewById(R.id.tkphoto_img);
        tk_canleImage = (TextView) view.findViewById(R.id.tkcanleImage);
        tk_bhqmc_etxt = (EditText) view.findViewById(R.id.tk_bhqmc_etxt);
        tk_jb_etxt = (EditText) view.findViewById(R.id.tk_jb_etxt);
        tk_qymc_etxt = (EditText) view.findViewById(R.id.tk_qymc_etxt);
        tk_fzjg_etxt = (EditText) view.findViewById(R.id.tk_fzjg_etxt);
        tk_sjqhGroup_radio = (RadioGroup) view.findViewById(R.id.tk_sjqhGroup_radio);
        tk_sjqh_radio1 = (RadioButton) view.findViewById(R.id.tk_sjqh_radio1);
        tk_sjqh_radio2 = (RadioButton) view.findViewById(R.id.tk_sjqh_radio2);
        tk_kqslqk_etxt = (EditText) view.findViewById(R.id.tk_kqslqk_etxt);
        tk_ktfs_etxt = (EditText) view.findViewById(R.id.tk_ktfs_etxt);
        tk_hbpfwh_etxt = (EditText) view.findViewById(R.id.tk_hbpfwh_etxt);
        tk_sfsbhq_etxt = (EditText) view.findViewById(R.id.tk_sfsbhq_etxt);
        tk_ktscqk_etxt = (EditText) view.findViewById(R.id.tk_ktscqk_etxt);
        tk_syq_etxt = (EditText) view.findViewById(R.id.tk_syq_etxt);
        tk_hcq_etxt = (EditText) view.findViewById(R.id.tk_hcq_etxt);
        tk_hxq_etxt = (EditText) view.findViewById(R.id.tk_hxq_etxt);
        tk_lsqk_etxt = (EditText) view.findViewById(R.id.tk_lsqk_etxt);

        tk_sfsbhq_dmz = (TextView) view.findViewById(R.id.tk_sfsbhq_dmz);
        tk_kqsx_dmz = (TextView) view.findViewById(R.id.tk_sfsbhq_dmz);
        tk_hxq_txt = (TextView)view.findViewById(R.id.tk_hxq_txt);
        tk_hcq_txt = (TextView) view.findViewById(R.id.tk_hcq_txt);
        tk_syq_txt = (TextView) view.findViewById(R.id.tk_syq_txt);
        initMethod();
    }

    private void initMethod() {

        //长按获取中心坐标
        /*实现原理：
            1.打开地图显示控件(mapLayout可见)
            2.在地图上获取当前经纬度坐标，显示在tk_ktzxzb_etxt上，地图显示控件隐藏
        * */
        tk_ktzxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                initMap();//地图初始化
                dot_btn.setVisibility(View.GONE);
                reset_btn.setVisibility(View.GONE);
                valueType = MapValueType.centerCoordinate;
                return false;
            }
        });

        //双击获取项目面坐标
        /*实现原理：
            1.打开地图显示控件(mapLayout可见)
            2.在地图上打点获取当前位置坐标并存入List中，List内容显示在tk_ktxmmzb_etxt上，地图显示控件隐藏
        * */
       /* tk_ktxmmzb_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tk_ktxmmzb_etxt.setFocusableInTouchMode(true);//获得焦点
                tk_ktxmmzb_etxt.requestFocus();//焦点放在调用这个方法的控件上
                times.add(SystemClock.uptimeMillis());
                if (times.size() == 2) {
                    //已经完成了一次双击，list可以清空了
                    if (times.get(times.size() - 1) - times.get(0) < 500) {
                        times.clear();
                        mapLayout.setVisibility(View.VISIBLE);
                        initMap();//地图初始化
                        valueType = MapValueType.multiPoint;
                    } else {
                        //这种情况下，第一次点击的时间已经没有用处了，第二次就是“第一次”
                        times.remove(0);
                    }
                }
            }
        });*/
        tk_ktxmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                initMap();//地图初始化
                valueType = MapValueType.multiPoint;
                return false;
            }
        });
        //选择日期对话框
        tk_zjyxqe_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dd = new DateDialog(context, tk_zjyxqe_etxt);
                dd.showDataDialog();
            }
        });
        tk_zjyxqb_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dd = new DateDialog(context, tk_zjyxqb_etxt);
                dd.showDataDialog();
            }
        });
        //自定义对话框，选择矿权属性
        tk_kqsx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tk_kqsx_etxt.setFocusableInTouchMode(true);//获得焦点
                //tk_kqsx_etxt.requestFocus();//焦点放在调用这个方法的控件上
                CustomDialog dialog = new CustomDialog(context,tk_kqsx_dmz,tk_kqsx_etxt, "矿权属性", "KQSX");
                dialog.showQksxDialog();
            }
        });
         /*
        * 打开相册或照相机
        * */
        tk_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPhotoDialog();
                showMessage("此处不能修该照片!");
            }
        });

        /*
           始建前后
        * */
        tk_sjqhGroup_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (tk_sjqh_radio1.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------始建前后---->" + tk_sjqh_radio1.getText().toString());
                    bhqsjqh = tk_sjqh_radio1.getText().toString().trim();
                } else if (tk_sjqh_radio2.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------始建前后---->" + tk_sjqh_radio2.getText().toString());
                    bhqsjqh = tk_sjqh_radio2.getText().toString().trim();
                }
            }
        });
       /*
         是否设保护区
        */
        tk_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(context,tk_sfsbhq_dmz,tk_sfsbhq_etxt, "是否涉保护区", "ISBHQ");
                dialog.showQksxDialog();
            }
        });

        /*点击地图取值事件*/
        getvelue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValueMethod();
            }
        });
        /*移动到当前位置
        * */
        imgBtn_moveCurrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCurrentPosition();
            }
        });
        /*
            地图打点
        * */
        dot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDotMethod();
            }
        });
        /*地图重置按钮
        * */
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMethod();
            }
        });
    }

    private void initMap() {
        // 隐藏logo
        View child = mapView.getChildAt(1);
        if (child != null) {
            child.setVisibility(View.GONE);
        }
        locMag = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locMag.getProviders(true);
        for (String provider : providers) {
            m_location = locMag.getLastKnownLocation(provider);
            if (null != m_location) {
                localization(m_location);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
            }
            locMag.requestLocationUpdates(provider, 1 * 1000, 0, m_locationListener);
        }
    }

    private LocationListener m_locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            m_location = location;
            Log.i(TAG, "onLocationChanged: ---------TkEnterpriseOperate:");
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
    private void localization(Location loc) {
        LatLng latLng = latLngConVert(new LatLng(loc.getLatitude(), loc.getLongitude()));
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
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
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(loc.getLatitude(), loc.getLongitude())), 16));

    }

    // 将GPS设备采集的原始GPS坐标转换成百度坐标
    private LatLng latLngConVert(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    public void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    tk_ktzxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
                    dot_btn.setVisibility(View.VISIBLE);
                    reset_btn.setVisibility(View.VISIBLE);
                    break;
                case multiPoint:
                    Log.i(TAG, "getValueMethod: --------------->" + TempData.pointList.toString());
                    StringBuilder sb = new StringBuilder();
                    int len = TempData.pointList.size();
                    for (int i = 0; i < len; i++) {
                        sb.append("[" + TempData.latLngList.get(i).longitude + "," + TempData.latLngList.get(i).latitude + "],");
                        if (i == len - 1) {
                            sb.append("[" + TempData.latLngList.get(i).longitude + "," + TempData.latLngList.get(i).latitude + "]");
                        }
                    }
                    if (len == 0) {
                        tk_ktxmmzb_etxt.setMovementMethod(ScrollingMovementMethod.getInstance());
                        tk_ktxmmzb_etxt.setText("未获得点...");
                    } else {
                        tk_ktxmmzb_etxt.setMovementMethod(ScrollingMovementMethod.getInstance());
                        tk_ktxmmzb_etxt.setText("[[" + sb.toString() + "]]");
                        tk_scmj_etxt.setText(String.format("%.2f", area));
                    }


                    break;
            }
            TempData.pointList.clear();
            TempData.latLngList.clear();
            baiduMap.clear();
            txt_showInfo.setText("");
            locMag.removeUpdates(m_locationListener);
        }
    }

    public void resetMethod() {//重置
        TempData.pointList.clear();
        baiduMap.clear();
        txt_showInfo.setText("");
    }

    private boolean isBaiduLatlng = true;

    public void getDotMethod() {//打点
        if (null != m_location) {
            LatLng latLng = latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude()));
            TempData.pointList.add(latLng);//存百度坐标点
            TempData.latLngList.add(new LatLng(m_location.getLatitude(), m_location.getLongitude()));//存设备经纬度坐标点
            Log.i(TAG, "getDotMethod: ----------------->" + TempData.pointList.toString());
            if (TempData.pointList.size() < 3) {
                drawDot(latLng);
                showMessage("还需 " + (3 - TempData.pointList.size()) + " 个点显示面");
            } else {
                drawPolyGonByGps(TempData.pointList, isBaiduLatlng);
            }
        } else {
            showMessage("获得经纬度失败...");
        }
    }

    public void moveCurrentPosition() {//移动到当前位置
        //showMessage("move current position");
        if (null != m_location) {
            localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
    }
    //===============================================================================================
    /**
     * 画Dot,Polyline,Polygon
     */
    private Overlay overlay_polygon, overlay_polyline;

    private void drawDot(LatLng latLng) {
       /* DotOptions dotOptions = new DotOptions()
                .center(latLng).color(R.color.blue)
                .visible(true).radius(8);*/
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        OverlayOptions markerOptions = new MarkerOptions()
                .position(latLng).icon(bitmapDescriptor).visible(true);
        baiduMap.addOverlay(markerOptions);

    }

    private void drawPolyLine(List<LatLng> points) {//至少两个点
        if (null != overlay_polyline) {
            overlay_polyline.remove();
        }
        OverlayOptions polylineOptions = new PolylineOptions()
                .points(points).color(0xAA00FF00).width(8);
        double distance = DistanceUtil.getDistance(points.get(points.size() - 1), points.get(points.size() - 2));
        txt_showInfo.setText("距离：" + String.format("%.2f", distance) + "米");
        overlay_polyline = baiduMap.addOverlay(polylineOptions);
    }
    //画多边形

    /**
     * 通过Gps坐标点画多边形，注意两点：1.传过来的坐标点是Gps点，在百度地图上显示需要进行坐标转换在显示
     * 2.利用arcgis api 计算多边形面积，直接使用传过来的点进行计算
     *
     * @param latLngs
     */
    private double area, permiter;

    private void drawPolyGonByGps(List<LatLng> latLngs, boolean isBaiduPoint) {//至少三个点
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
            for (int i = 0; i < latLngs.size(); i++) {
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
            for (int i = 0; i < latLngs.size(); i++) {
                if (i == 0) {
                    arcgis_Polygon.startPath((Point) GeometryEngine.project(new Point(latLngs.get(i).longitude, latLngs.get(i).latitude), SpatialReference.create(SpatialReference.WKID_WGS84), SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR)));
                } else {
                    arcgis_Polygon.lineTo((Point) GeometryEngine.project(new Point(latLngs.get(i).longitude, latLngs.get(i).latitude), SpatialReference.create(SpatialReference.WKID_WGS84), SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR)));
                }
            }
            area = Math.abs(arcgis_Polygon.calculateArea2D());
            permiter = arcgis_Polygon.calculateLength2D();
            //showMessage(String.format("%.2f",Math.abs(area))+"平方米");
            txt_showInfo.setText(String.format("%.2f", area) + " 平方米|" + String.format("%.2f", permiter) + " 米");
        }
    }

    //---------------------------------------------------------------------
    //================================初始化表单数据==========================================
    private String photoPath = "";
    @Override
    public TkEnterpriseBean getEntityBean(String bhqid,String bhqmc,String jsxmid) {
        TkEnterpriseBean tb = new TkEnterpriseBean();
        StringBuilder sb = new StringBuilder();
        if (!tk_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(tk_hxq_txt.getText().toString().trim() + ":" + tk_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!tk_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(tk_hcq_txt.getText().toString().trim() + ":" + tk_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!tk_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(tk_syq_txt.getText().toString().trim() + ":" + tk_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx = sb.toString();//与保护区位置关系
        tb.setJsxmid(jsxmid);
        tb.setJsxmjb(tk_jb_etxt.getText().toString().trim());
        tb.setJsxmmc(tk_qymc_etxt.getText().toString().trim());
        tb.setBhqid(bhqid);
        tb.setTrzj("");
        tb.setNcz("");
        tb.setFzjg(tk_fzjg_etxt.getText().toString().trim());
        tb.setZjyxqb(tk_zjyxqb_etxt.getText().toString().trim());
        tb.setZjyxqe(tk_zjyxqe_etxt.getText().toString().trim());
        tb.setBjbhqsj(bhqsjqh);
        tb.setKqslqk(tk_kqslqk_etxt.getText().toString().trim());
        tb.setKtfs(tk_ktfs_etxt.getText().toString().trim());
        tb.setKqsx(tk_kqsx_dmz.getText().toString().trim());//tk_kqsx_etxt.getText().toString().trim()
        tb.setHbpzwh(tk_hbpfwh_etxt.getText().toString().trim());
        tb.setIsbhq(tk_sfsbhq_dmz.getText().toString().trim());
        tb.setScqk(tk_ktscqk_etxt.getText().toString().trim());
        tb.setYbhqgx(ybhqwzgx);
        tb.setZgcs(tk_lsqk_etxt.getText().toString().trim());
        tb.setScqksm("");
        tb.setVisbhq(tk_sfsbhq_etxt.getText().toString().trim());
        //将中心坐标截取x,y
        String centerpoint = tk_ktzxzb_etxt.getText().toString().trim();
        String pointx = "";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getTkEnterpriseBean: ------centerpoint------->" + centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp + 1);
        }
        tb.setCenterpointx(pointx);
        tb.setCenterpointy(pointy);
        tb.setTjmj(tk_scmj_etxt.getText().toString().trim());
        tb.setHxmj(tk_hxq_etxt.getText().toString().trim());
        tb.setHcmj(tk_hcq_etxt.getText().toString().trim());
        tb.setSymj(tk_syq_etxt.getText().toString().trim());
        tb.setMjzb(tk_ktxmmzb_etxt.getText().toString().trim());

        tb.setVkqsx(tk_kqsx_etxt.getText().toString().trim());
       // tb.setUsername(TempData.username);
       // tb.setUsertel(TempData.usertel);
        tb.setPlaceid(placeid);
        tb.setPhotoPath(photoPath);
        tb.setBhqmc(bhqmc);
        return tb;
    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //========================================================================================
    @Override
    public View getView(TkEnterpriseBean bean) {
        if (bean != null) {
            tk_bhqmc_etxt.setText(bean.getBhqmc());
            tk_qymc_etxt.setText(bean.getJsxmmc());
            tk_jb_etxt.setText(bean.getJsxmjb());
            tk_fzjg_etxt.setText(bean.getFzjg());
            tk_zjyxqb_etxt.setText(bean.getZjyxqb());
            tk_zjyxqe_etxt.setText(bean.getZjyxqe());
            if (bean.getCenterpointx().equals("") && bean.getCenterpointy().equals("")) {
                tk_ktzxzb_etxt.setText("");
            } else {
                tk_ktzxzb_etxt.setText(bean.getCenterpointx() + "," + bean.getCenterpointy());
            }

            tk_ktxmmzb_etxt.setText(bean.getMjzb());
            tk_scmj_etxt.setText(bean.getTjmj());

            if (tk_sjqh_radio1.getText().toString().trim().equals(bean.getBjbhqsj())) {
                tk_sjqh_radio1.setChecked(true);
            } else if (tk_sjqh_radio2.getText().toString().trim().equals(bean.getBjbhqsj())) {
                tk_sjqh_radio2.setChecked(true);
            }
            tk_kqslqk_etxt.setText(bean.getKqslqk());
            tk_ktfs_etxt.setText(bean.getKtfs());
            tk_kqsx_etxt.setText(bean.getVkqsx());
            tk_hbpfwh_etxt.setText(bean.getHbpzwh());
            tk_sfsbhq_etxt.setText(bean.getVisbhq());
            tk_ktscqk_etxt.setText(bean.getScqk());
            tk_syq_etxt.setText(bean.getSymj());
            tk_hcq_etxt.setText(bean.getHcmj());
            tk_hxq_etxt.setText(bean.getHxmj());
            tk_lsqk_etxt.setText(bean.getZgcs());
            photoPath = bean.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(bean.getPhotoPath());
            tk_photo_img.setImageBitmap(bitmap);
            placeid = bean.getPlaceid();
        }

        return view;
    }


}
