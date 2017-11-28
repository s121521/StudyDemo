package com.yaotu.proj.studydemo.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.gson.Gson;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassTkqy;
import com.yaotu.proj.studydemo.bean.tableBean.TKJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkEnterpriseBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkRecord;
import com.yaotu.proj.studydemo.common.LocalBaseInfo;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.DateDialog;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.HandleImage;
import com.yaotu.proj.studydemo.customclass.InsertLocalTableData;
import com.yaotu.proj.studydemo.customclass.PhotoImageSize;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Response;

/*
* 探矿企业信息
* */
public class TKEnterpriseActivity extends AppCompatActivity {
    private final int  UPLOADSUCCEED = 0X110;//上传成功
    private final int UPLOADFAIL = 0X111;//上传失败
    private final int UPLOADPICSUCCEED = 0X002;//照片上传成功
    private final int UPLOADPICFAIL = 0X003;//照片上传失败
    private String IPURL ="";//服务器IP地址
    private final String TAG = this.getClass().getSimpleName();
    private MapValueType valueType = null;
    private Context context = TKEnterpriseActivity.this;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locMag;
    private Location m_location;
    private LinearLayout mapLayout;
    private EditText tk_ktzxzb_etxt;//中心坐标
    private TextView tk_ktxmmzb_etxt;//项目面坐标
    private EditText tk_scmj_etxt;//实测面积
    private EditText tk_zjyxqb_etxt;//证件有效期 开始
    private EditText tk_zjyxqe_etxt;//证件有效期 结束
    private EditText tk_kqsx_etxt;//矿权属性
    private TextView tk_kqsx_dmz;
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
    private TextView tk_sfsbhq_dmz;//是否设保护区
    private EditText tk_ktscqk_etxt;//生产情况
    private EditText tk_syq_etxt;//实验区
    private EditText tk_hcq_etxt;//缓冲区
    private EditText tk_hxq_etxt;//核心区
    private EditText tk_lsqk_etxt;//落实情况
    private TextView tk_syq_txt,tk_hcq_txt,tk_hxq_txt;
    private TextView txt_showInfo;
    private ImageView tk_photo_img;
    private TextView tk_canleImage;
    private String bhqid, bhqmc, bhqjb,bhqjbdm;
    private Intent intent;
    private ImageButton dot_btn, reset_btn;//地图button
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    private ProgressDialog progressDialog;
    private String activityType;//用来编辑当前activity添加新数据或更新数据
    private NoPassTkqy noPassTkqy;
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String jcdId;//监测点ID
    private DBManager dbManager;
    private String jsxmID = "0";//添加为0；更行为原有id;
    private String dataUplocadSucceed;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case  UPLOADSUCCEED:
                    progressDialog.dismiss();
                    if (!"".equals(photoPath)) {//上传相片
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setMessage("数据上传成功！是否上传相片?");
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                uploadPic();

                            }
                        });
                        builder.show();
                    } else {
                        showMessage("上传成功");
                    }
                    break;
                case UPLOADFAIL:
                    progressDialog.dismiss();
                    showMessage("上传失败...");
                    break;
                case UPLOADPICSUCCEED:
                    showMessage("照片上传成功!");
                    break;
                case UPLOADPICFAIL:
                    showMessage("照片上传失败!");
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tkenterprise);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);

        initView();//初始化页面需要的基本view
        initMethod();//定义基本元素事件
        IPURL = getResources().getString(R.string.http_url);//服务器IP地址
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.report_mapview);
        baiduMap = mapView.getMap();

        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        tk_ktzxzb_etxt = (EditText) findViewById(R.id.tk_ktzxzb_etxt);//显示中心坐标
        tk_ktxmmzb_etxt = (TextView) findViewById(R.id.tk_ktxmmzb_etxt);//显示项目面坐标
        tk_scmj_etxt = (EditText) findViewById(R.id.tk_scmj_etxt);//实测面积
        tk_zjyxqe_etxt = (EditText) findViewById(R.id.tk_zjyxqe_etxt);//证件有效期 结束
        tk_zjyxqb_etxt = (EditText) findViewById(R.id.tk_zjyxqb_etxt);//证件有效期 开始
        tk_kqsx_etxt = (EditText) findViewById(R.id.tk_kqsx_etxt);//矿权属性
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        tk_photo_img = (ImageView) findViewById(R.id.tkphoto_img);
        tk_canleImage = (TextView) findViewById(R.id.tkcanleImage);
        tk_bhqmc_etxt = (EditText) findViewById(R.id.tk_bhqmc_etxt);
        tk_jb_etxt = (EditText) findViewById(R.id.tk_jb_etxt);

        tk_qymc_etxt = (EditText) findViewById(R.id.tk_qymc_etxt);
        tk_fzjg_etxt = (EditText) findViewById(R.id.tk_fzjg_etxt);
        tk_sjqhGroup_radio = (RadioGroup) findViewById(R.id.tk_sjqhGroup_radio);
        tk_sjqh_radio1 = (RadioButton) findViewById(R.id.tk_sjqh_radio1);
        tk_sjqh_radio2 = (RadioButton) findViewById(R.id.tk_sjqh_radio2);
        tk_kqslqk_etxt = (EditText) findViewById(R.id.tk_kqslqk_etxt);
        tk_ktfs_etxt = (EditText) findViewById(R.id.tk_ktfs_etxt);
        tk_hbpfwh_etxt = (EditText) findViewById(R.id.tk_hbpfwh_etxt);
        tk_sfsbhq_dmz = (TextView) findViewById(R.id.tk_sfsbhq_dmz);
        tk_kqsx_dmz = (TextView) findViewById(R.id.tk_sfsbhq_dmz);
        tk_sfsbhq_etxt = (EditText) findViewById(R.id.tk_sfsbhq_etxt);
        tk_ktscqk_etxt = (EditText) findViewById(R.id.tk_ktscqk_etxt);
        tk_syq_etxt = (EditText) findViewById(R.id.tk_syq_etxt);
        tk_hcq_etxt = (EditText) findViewById(R.id.tk_hcq_etxt);
        tk_hxq_etxt = (EditText) findViewById(R.id.tk_hxq_etxt);
        tk_lsqk_etxt = (EditText) findViewById(R.id.tk_lsqk_etxt);
        tk_hxq_txt = (TextView) findViewById(R.id.tk_hxq_txt);
        tk_hcq_txt = (TextView) findViewById(R.id.tk_hcq_txt);
        tk_syq_txt = (TextView) findViewById(R.id.tk_syq_txt);

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

        //获取项目面坐标
        /*实现原理：
            1.打开地图显示控件(mapLayout可见)
            2.在地图上打点获取当前位置坐标并存入List中，List内容显示在tk_ktxmmzb_etxt上，地图显示控件隐藏
        * */
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
                Log.i(TAG, "onClick: --------qksx_dmz---------->"+tk_kqsx_dmz.getText().toString());
            }
        });
         /*
        * 打开相册或照相机
        * */
        tk_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        tk_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tk_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
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
                CustomDialog dialog = new CustomDialog(context,tk_sfsbhq_dmz, tk_sfsbhq_etxt, "是否涉保护区", "ISBHQ");
                dialog.showQksxDialog();
                Log.i(TAG, "onClick: ------------sfsbhq_dmz------>"+tk_sfsbhq_dmz.getText().toString()+"==="+tk_sfsbhq_etxt.getText().toString().trim());
            }
        });

    }

    private void initMap() {
        // 隐藏logo
        View child = mapView.getChildAt(1);
        if (child != null) {
            child.setVisibility(View.GONE);
        }
        locMag = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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

    public void getValueMethod(View view) {//获取值
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
        //editText_area.setText(String.valueOf(area));
        // editText_perimeter.setText(String.valueOf(permiter));
    }

    public void resetMethod(View view) {//重置
        TempData.pointList.clear();
        baiduMap.clear();
        txt_showInfo.setText("");
    }

    private boolean isBaiduLatlng = true;

    public void getDotMethod(View view) {//打点
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

    public void moveCurrentPosition(View view) {//移动到当前位置
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
    /*
    打开拍照对话框
    * */
    private final int OPEN_RESULT = 1; // 相机
    private final int PICK_RESULT = 2;// 本地相册
    private AlertDialog.Builder dialog;

    public void showPhotoDialog() {
        dialog = new AlertDialog.Builder(context);
        final String[] items = new String[]{"拍照", "从相册选择"};
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Log.i("TAG", "onClick: 0" + items[which]);
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, OPEN_RESULT);
                        dialog.dismiss();
                        break;
                    case 1:
                        Log.i("TAG", "onClick: 1" + items[which]);
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_RESULT);
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    private String photoPath = "";


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_RESULT:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > 19) {
                        //4.4及以上系统使用这个方法处理图片
                        photoPath = HandleImage.handleImgeOnKitKat(context,data);
                    }else {
                        photoPath = HandleImage.handleImageBeforeKitKat(context,data);
                    }
                    Log.i("TAG", "onActivityResult: ====================>"+photoPath);
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(photoPath);
                    tk_photo_img.setImageBitmap(bitmap);
                    if(bitmap!=null){
                        Log.i("TAG", "onActivityResult:-------相册相片路径---------> " + photoPath + "---字节数---->" + bitmap.getByteCount() + "<========>" + bitmap.getRowBytes() * bitmap.getHeight());
                    }

                }
                break;
            case OPEN_RESULT:
                /**
                 * 通过这种方法取出的拍摄会默认压缩，因为如果相机的像素比较高拍摄出来的图会比较高清，
                 * 如果图太大会造成内存溢出（OOM），因此此种方法会默认给图片进行压缩
                 */
                String fileName = String.valueOf(System.currentTimeMillis());
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                File file = FileUtils.saveBitmap(bitmap, fileName);
                photoPath = file.getAbsolutePath();
                Log.i("TAG", "onActivityResult:-------相机相片路径---------> " + photoPath + "----->" + file.getPath());
                tk_photo_img.setImageBitmap(bitmap);
                //发送广播更新系统相册
                upDateDcim(file.getName());

                break;
            default:
                break;
        }
    }


    //============================提交/保存========================================================
    public void submitBtnMethod(View view) {
       // Log.i(TAG, "submitBtnMethod: -----------sfsbhq_dmz---->"+tk_sfsbhq_dmz.getText().toString()+"------");
        int netType = CheckNetwork.getNetWorkState(context);
        if (tk_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
            return;
        }
        if (netType == 0) {//none
            showMessage("请打开网络连接");
        } else if (netType == 1) {//wifi
            upload();
        } else if (netType == 2) {//mobile
            upload();
        }

    }

    public void saveBtnMethod(View view) {
        if (tk_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TkEnterpriseBean tkEnterpriseBean = getTkEnterpriseBean();
                if (IsExist(tkEnterpriseBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertTkqyInfo(tkEnterpriseBean,0+"");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
                    if (inResult) {
                        showMessage("保存成功!");
                    } else {
                        showMessage("保存失败...");
                    }
                }

            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
       // showMessage("save");
    }
//================================================================================================
    private boolean threadFlag;
    private void upload(){
        List<TkRecord> Record = new ArrayList<>();
        TkEnterpriseBean tb = getTkEnterpriseBean();
        TkRecord recordBean = new TkRecord();
        recordBean.setObjectid(jcdId);
        recordBean.setCenterpointx(jcdLongitude);
        recordBean.setCenterpointy(jcdlatitude);
        recordBean.setBean(tb);
        Record.add(recordBean);
        TKJsonBean tkJsonBean = new TKJsonBean();
        tkJsonBean.setKey("02");
        tkJsonBean.setYhdh(TempData.yhdh);
        if (activityType.equals("add")) {
            tkJsonBean.setIschecked("21");
        } else {
            tkJsonBean.setIschecked("22");
        }
        tkJsonBean.setRecord(Record);

        Gson gson = new Gson();
        final String JsonStr = gson.toJson(tkJsonBean);

        Log.i(TAG, "submitBtnMethod: ----------lllll---------->"+JsonStr);
        final AlertDialog.Builder alterDialog = new AlertDialog.Builder(context);
        alterDialog.setTitle("提示信息");
        alterDialog.setMessage("是否提交数据？");
        alterDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                threadFlag = true;
                //执行数据上传文件
                Log.i(TAG, "onClick: ------------探矿企业上传数据----------->"+JsonStr);
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("正在上传数据....");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        threadFlag = false;
                    }
                });
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    try {
                        Thread.sleep(2*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (threadFlag) {
                        //Response response = ParseIntentData.getDataPostByJson(IPURL + "/bhqService/BhqJsxm/SaveBhqJsxmInfo",JsonStr);
                        Response response = ParseIntentData.getDataPostByJson(IPURL + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo",JsonStr);
                        Message message = Message.obtain();
                        if (response != null && response.code() == 200) {
                            try {
                                String resultValue = response.body().string();
                                dataUplocadSucceed = resultValue;
                                message.what = UPLOADSUCCEED;
                                myHandler.sendMessage(message);
                                threadFlag = false;

                        } catch (IOException e) {
                            e.printStackTrace();
                            response.close();
                        }

                    } else {
                            message.what = UPLOADFAIL;
                            myHandler.sendMessage(message);
                            threadFlag = false;
                        }
                    }

                    }
                }).start();

                dialog.dismiss();
            }
        });
        alterDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alterDialog.create().show();
    }
    private void uploadPic(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                int bitMapSize = ParseIntentData.getBitmapSize(bitmap);
                Log.i(TAG, "run: -------bitmap大小----->" + bitMapSize);
                if (bitMapSize > 202800) {
                    File file = FileUtils.saveBitmap(PhotoImageSize.revitionImageSize(photoPath), String.valueOf(System.currentTimeMillis()));
                    photoPath = file.getAbsolutePath();
                    upDateDcim(file.getName());
                }
                String url = IPURL+"/BhqJsxm/Upload";
                Response response = ParseIntentData.upLoadImage(url, photoPath, dataUplocadSucceed);
                if (response != null && response.code() == 200) {
                    message.what = UPLOADPICSUCCEED;
                } else {
                    message.what = UPLOADPICFAIL;
                }
                myHandler.sendMessage(message);
            }
        }).start();
    }
    //============================================================================================
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        intent = getIntent();
        activityType = intent.getStringExtra("type");
        if (activityType.equals("add")) {
            bhqid = intent.getStringExtra("bhqid");
            bhqmc = intent.getStringExtra("bhqmc");
            bhqjb = intent.getStringExtra("bhqjb");
            bhqjbdm = intent.getStringExtra("bhqjbdm");
            jcdId = intent.getStringExtra("placeid");
            jcdLongitude = intent.getDoubleExtra("longitude", 0);
            jcdlatitude = intent.getDoubleExtra("latitude", 0);
            //------------------------------
            tk_bhqmc_etxt.setText(bhqmc);
            tk_jb_etxt.setText(bhqjb);
            tk_ktzxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
            jsxmID = "0";
        } else {
            noPassTkqy = (NoPassTkqy) intent.getSerializableExtra("bdData");
            bhqmc = intent.getStringExtra("bdBhqmc");
            jcdLongitude = intent.getDoubleExtra("bdjd",0);
            jcdlatitude = intent.getDoubleExtra("bdwd", 0);
            jcdId =intent.getStringExtra("bdobjid");
            Log.i(TAG, "onResume: ----------bhqmc:"+bhqmc);
            //--------------------------------------------
            setViewData();//获取更新数据
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    //================================初始化表单数据==========================================
    private TkEnterpriseBean getTkEnterpriseBean() {
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
        tb.setJsxmid(jsxmID);
        tb.setJsxmjb(bhqjbdm);//tk_jb_etxt.getText().toString().trim()
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
        tb.setUsername(TempData.yhdh);
        tb.setPlaceid(jcdId);
        tb.setPhotoPath(photoPath);
        tb.setBhqmc(bhqmc);
        return tb;
    }
    //============================将数据保存在本地sqlite TkqyInfo表中==================================

    private boolean insertTkqyInfo(TkEnterpriseBean bean,String upState){
        boolean result = false;
        if (bean != null) {
            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertTkqyInfo(bean, upState);
        }
       return result;
    }
    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(TkEnterpriseBean bean){
        QueryLocalTableData query = new QueryLocalTableData(context);
        //String sql ="select * from TkqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        //String[] strs = new String[]{bean.getBhqid(),bean.getJsxmmc(),bean.getUsername()};
        String sql ="select * from TkqyInfo where bhqid = ? and placeid= ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(),bean.getPlaceid(),bean.getUsername()};
        List<TkEnterpriseBean> list = query.queryTkqyInfos(sql,strs);
        if (list != null) {
            if (list.size() >0) {
                return true;//存在
            }
        }
        return false;//不存在
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //=============================================================================================

    private void setViewData(){
        if (noPassTkqy != null) {
            Log.i(TAG, "setViewData: -------------------------------"+bhqmc);
            jsxmID = noPassTkqy.getJSXMID();
            bhqid = noPassTkqy.getBHQID();
            tk_bhqmc_etxt.setText(bhqmc);
            CodeTypeBean bhqjbB = codeType("BHQJB", noPassTkqy.getJSXMJB()==null?"":noPassTkqy.getJSXMJB().toString().trim());
            Log.i(TAG, "setViewData: ===========bhqjb:"+noPassTkqy.getJSXMJB());
            if (bhqjbB != null) {
                tk_jb_etxt.setText(bhqjbB.getDMMC1());
                bhqjbdm = bhqjbB.getDMZ();
            }
            tk_qymc_etxt.setText(noPassTkqy.getJSXMMC());
            tk_fzjg_etxt.setText(noPassTkqy.getFZJG());
            if (noPassTkqy.getZJYXQB() != null) {
                tk_zjyxqb_etxt.setText(dateConversion(noPassTkqy.getZJYXQB()));
            }
            if (noPassTkqy.getZJYXQE() !=null) {
                tk_zjyxqe_etxt.setText(dateConversion(noPassTkqy.getZJYXQE()));
            }
            tk_ktzxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
            tk_ktxmmzb_etxt.setText(noPassTkqy.getMJZB());
            tk_scmj_etxt.setText(String.valueOf(noPassTkqy.getTJMJ()));
            if (tk_sjqh_radio1.getText().equals(noPassTkqy.getBJBHQSJ()==null?"":noPassTkqy.getBJBHQSJ().toString().trim())) {
                tk_sjqh_radio1.setChecked(true);
            } else if (tk_sjqh_radio2.getText().equals(noPassTkqy.getBJBHQSJ()==null?"":noPassTkqy.getBJBHQSJ().toString().trim())) {
                tk_sjqh_radio2.setChecked(true);
            }
            tk_kqslqk_etxt.setText(noPassTkqy.getKQSLQK());
            tk_ktfs_etxt.setText(noPassTkqy.getKTFS());
            CodeTypeBean kqsxB= codeType("KQSX",noPassTkqy.getKQSX()==null?"":noPassTkqy.getKQSX().toString().trim());
            if (kqsxB != null) {
                tk_kqsx_etxt.setText(kqsxB.getDMMC1());
                tk_kqsx_dmz.setText(kqsxB.getDMZ());
            }
            tk_hbpfwh_etxt.setText(noPassTkqy.getHBPZWH());
            CodeTypeBean isbhqB = codeType("ISBHQ", noPassTkqy.getISBHQ()==null?"":noPassTkqy.getISBHQ().toString().trim());
            if (isbhqB != null) {
                tk_sfsbhq_etxt.setText(isbhqB.getDMMC1());
                tk_sfsbhq_dmz.setText(isbhqB.getDMZ());
            }
            tk_ktscqk_etxt.setText(noPassTkqy.getSCQK());
            tk_syq_etxt.setText(String.valueOf(noPassTkqy.getSYMJ()));
            tk_hcq_etxt.setText(String.valueOf(noPassTkqy.getHCMJ()));
            tk_hxq_etxt.setText(String.valueOf(noPassTkqy.getHXMJ()));
            tk_lsqk_etxt.setText(noPassTkqy.getZGCS());

        }

    }
    private String dateConversion(String str){// /Date(1499097600000)/用Java怎么转换成yyyy-MM-dd的格式
        str=str.replace("/Date(","").replace(")/","");
        Date date = new Date(Long.parseLong(str));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Log.i(TAG, "dateConversion: ---------------"+str);
        return format.format(date);
    }
    private  CodeTypeBean codeType(String dmlb,String dmz){
        dbManager = new DBManager(context);
        String sql = "select * from codeType where dmlb = ? and dmz = ?";
        String[] bindArgs = new String[]{dmlb,dmz};
        List<CodeTypeBean> list = LocalBaseInfo.loadDataBySqlLite(sql,bindArgs,dbManager);
       return list.size() == 0 ? null : list.get(0);
    }
    //发送广播更新系统相册
    private void upDateDcim(String fileName) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(FileUtils.SDPATH + fileName + ".jpg"));
        Log.i(TAG, "upDateDcim: -------------filename:" + fileName);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
