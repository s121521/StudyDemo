package com.yaotu.proj.studydemo.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
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
import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.BhqHbhcBean;
import com.yaotu.proj.studydemo.bean.FlagBean;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.CustomWinMenu;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentArcgisLayer;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.MyGeoDataBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private final int REQUEST_CODE_DATAACTIVITY = 0x000001;
    private final int INFO_SHOW_REQUEST_CODE = 0x000002;
    private String username,placeid;
    private Intent intent;
    private Point wgspoint;
    private Point mapPoint;
    private LocationManager locMag;
    private TextView txt_pType, txt_pName;
    private Location loc;
    private GraphicsLayer locGraphics;
    private Point startPoint = null, currtPoint = null, prePoint = null;
    private Polyline polyline = null;
    private Polygon polygon = null;
    private LocationDisplayManager locdisMag;
    private Geometry.Type drawType;
    private Context context = MainActivity.this;
    private final String TAG = getClass().getName();
    private GraphicsLayer featurGraphicsLayer;//用于显示查询出要显示要素的图层
    private GraphicsLayer symbolGraphicsLayer;//用于显示测量图的线和面的图层
    private GraphicsLayer GpsLayer;//用于显示GPS航迹图层
    private Point query_point = null;//用于表示从离线geodatabase中查出的点
    private String menuFlag;//用于菜单创建子菜单时的标记
    private ArcGISLocalTiledLayer localTiledImgLayer=null,localTiledOtherLayer;
    private com.baidu.mapapi.map.MapView mapView_baidu;
    private BaiduMap baiduMap;
    private Location m_location;
    private Overlay overlay_polygon, overlay_polyline;
    private boolean isBaiduPoint = true;
    private TextView txt_showInfo;
    private String bhqid = "";
    private String bhqmc = "";
    private String bhqjb = "";
    private FrameLayout frameLayout = null;
    private int mapType = 1;//地图类型：1---->百度地图(百度API);2---->自定义地图(arcgis API)；3---->离线地图(arcgis 加载离线地图)
    private String http_gis = "";
    private ImageButton arcgis_zoom_in, arcgis_zoom_out;
    private ImageButton btnsavelocalvalue, btnsetlocalvalue,localLayerImgBtn;
    private int NetCode = 0;
    //------------------------------------------------------
    private DrawerLayout mDrawerLayout = null;
    private DBManager dbManager;
    private Cursor bhqCursor;
    private  File localImgFolder;//离线文件夹(存放影像图或矢量图)
    private File localLayerFolder;//离线文件夹(存放离线图层)
    private File localFileFolder;//保存文件
    private String localImgName;//离线影像或矢量图名称
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.my_framentLayout);
        txt_pType = (TextView) findViewById(R.id.txt_pType);
        txt_pName = (TextView) findViewById(R.id.txt_pName);
        txt_showInfo = (TextView) findViewById(R.id.txt_showInfo);
        http_gis = getResources().getString(R.string.http_gis);
        arcgis_zoom_in = (ImageButton) findViewById(R.id.arcgis_map_zoom_in);
        arcgis_zoom_out = (ImageButton) findViewById(R.id.arcgis_map_zoom_out);
        //------------------

        btnsavelocalvalue = (ImageButton) findViewById(R.id.BtnSaveLocalGetvalue);
        btnsetlocalvalue = (ImageButton) findViewById(R.id.BtnSetLocalGetvalue);
        localLayerImgBtn = (ImageButton) findViewById(R.id.localLayerImgBtn);
        //---------------
        intent = getIntent();
        username = intent.getStringExtra("username");
        NetCode = CheckNetwork.getNetWorkState(context);
        TempData.username = username;
        Log.i(TAG, "onCreate:---------------用户登录ID:"+TempData.username);
        dbManager = new DBManager(context);
        //---------------------------
        localImgFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/arcgisImg");
        localLayerFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/arcgisLayer");
        localFileFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/arcgisFile");
        if (!localImgFolder.exists()) {
            localImgFolder.mkdirs();
        }
        if (!localLayerFolder.exists()) {
            localLayerFolder.mkdirs();
        }
        if (!localFileFolder.exists()) {
            localFileFolder.mkdirs();
        }
        Log.i(TAG, "onCreate: "+localImgFolder.getAbsolutePath()+"==========="+localLayerFolder.getAbsolutePath());
        //----------------------------------------------
        initCustomMenu();//创建底部菜单
        if (mapType == 1) {
            LinearLayout bd_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.baidu_mapview, null);
            initBaiduView(bd_layout);
            frameLayout.removeAllViews();
            frameLayout.addView(bd_layout);
            mapView = new MapView(context);
            arcgis_zoom_in.setVisibility(View.INVISIBLE);//GONE:不可见且不占用空间;INVISIBLE:不可见但是占用空间;VISIBLE:可见
            arcgis_zoom_out.setVisibility(View.INVISIBLE);
            localLayerImgBtn.setVisibility(View.INVISIBLE);
            btnsetlocalvalue.setVisibility(View.GONE);
            btnsavelocalvalue.setVisibility(View.GONE);

        }
        List<String> providers = locMag.getProviders(true);
        for (String provider : providers) {
            m_location = locMag.getLastKnownLocation(provider);
            if (null != m_location) {
                localization(m_location);
            }
            locMag.requestLocationUpdates(provider, 1 * 1000, 0, m_locationListener);
        }


    }

    private void initBaiduView(View view) {
        mapView_baidu = (com.baidu.mapapi.map.MapView) view.findViewById(R.id.baidumapview);
        baiduMap = mapView_baidu.getMap();
        mapView_baidu.showScaleControl(false);//设置是否显示比例尺控件
        mapView_baidu.showZoomControls(true);//设置是否显示缩放控件
        mapView_baidu.getChildAt(2).setPadding(0, 0, 20, 200);//这是控制缩放控件的位置
        locMag = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        //showMessage("获取当前使用的坐标类型===>"+SDKInitializer.getCoordType());
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {//利用小工具测量距离和面积
            @Override
            public void onMapClick(LatLng latLng) {
                baiduMap.hideInfoWindow();//隐藏已显示的弹窗并且标志状态为""
                TempData.placeid = "";
                if (FlagBean.dcFlag) {
                    if (drawType.equals(Geometry.Type.POLYLINE)) {
                        TempData.pointList.add(latLng);
                        if (TempData.pointList.size() < 2) {
                            drawDot(latLng);
                        } else {
                            drawDot(latLng);
                            drawPolyLine(TempData.pointList);
                        }
                    } else if (drawType.equals(Geometry.Type.POLYGON)) {
                        TempData.pointList.add(latLng);
                        if (TempData.pointList.size() < 3) {
                            drawDot(latLng);
                            showMessage("还需 " + (3 - TempData.pointList.size()) + " 个点显示面");
                        } else {
                            drawDot(latLng);
                            drawPolyGonByGps(TempData.pointList, isBaiduPoint);
                        }
                    }

                }
                baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // 获得marker中的数据
                        BhqHbhcBean info = (BhqHbhcBean) marker.getExtraInfo().get("info");
                        //生成一个View用户在地图中显示InfoWindow
                        LinearLayout markerLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.baidu_infowindow, null);
                        ViewHolder viewHolder = null;
                        if (markerLayout.getTag() == null) {
                            viewHolder = new ViewHolder();
                            viewHolder.info_window_id_etxt = (EditText) markerLayout.findViewById(R.id.info_window_etxt_id);
                            viewHolder.info_window_jd_etxt = (EditText) markerLayout.findViewById(R.id.info_window_jd_etxt);
                            viewHolder.info_window_wd_etxt = (EditText) markerLayout.findViewById(R.id.info_window_wd_etxt);
                            markerLayout.setTag(viewHolder);
                        }
                        viewHolder = (ViewHolder) markerLayout.getTag();
                        viewHolder.info_window_id_etxt.setText(String.valueOf(info.getOBJECTID()));
                        viewHolder.info_window_jd_etxt.setText(String.valueOf(info.getJD()));
                        viewHolder.info_window_wd_etxt.setText(String.valueOf(info.getWD()));
                        TempData.placeid = String.valueOf(info.getOBJECTID());//记录监测点ID
                        Log.i(TAG, "onMarkerClick: (((((((((("+TempData.placeid+"-------->"+TempData.username);
                        TempData.longitude = info.getJD();//记录监测点经度
                        TempData.latitude = info.getWD();//记录监测点纬度
                        //初始化infowindow
                        InfoWindow infoWindow = new InfoWindow(markerLayout, marker.getPosition(), -80);
                        baiduMap.showInfoWindow(infoWindow);
                        //移动到当前点
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
                        return false;
                    }
                });

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    /**
     * 复用弹出面板markerLayout的控件
     */
    private class ViewHolder {
        EditText info_window_id_etxt;
        EditText info_window_jd_etxt;
        EditText info_window_wd_etxt;
    }

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
        // 当不需要定位图层时关闭定位图层
        // baiduMap.setMyLocationEnabled(false);

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

    //================================自定义菜单实现方法===========================================================================
    private LinearLayout buttom_custom_menu;

    private void initCustomMenu() {
        buttom_custom_menu = (LinearLayout) findViewById(R.id.buttom_custom_menu);
        String report1_name = this.getString(R.string.submenu_report1_name);
        String report2_name = this.getString(R.string.submenu_report2_name);
        String report3_name = this.getString(R.string.submenu_report3_name);
        String report4_name = this.getString(R.string.submenu_report4_name);
        String report5_name = this.getString(R.string.submenu_report5_name);
        String report6_name = this.getString(R.string.submenu_report6_name);
        String report7_name = this.getString(R.string.submenu_report7_name);
        //加载自定义菜单
        //String jsonStr = "{\"custommenu\":[{\"title\":\"" + context.getString(R.string.menu_report) + "\",\"sub\":[{\"title\":\"" + report1_name + "\"},{\"title\":\"" + report2_name + "\"},{\"title\":\"" + report3_name + "\"},{\"title\":\"" + report4_name + "\"},{\"title\":\"" + report5_name + "\"},{\"title\":\"" + report6_name + "\"},{\"title\":\"" + report7_name + "\"}]},{\"title\":\"" + context.getString(R.string.menu_function) + "\",\"sub\":[{\"title\":\"" + context.getString(R.string.submenu_gps) + "\"},{\"title\":\"" + context.getString(R.string.submenu_way_analyze) + "\"}]},{\"title\":\"" + context.getString(R.string.menu_clean) + "\",\"sub\":[{\"title\":\"" + context.getString(R.string.submenu_layer) + "\"}]},{\"title\":\"" + context.getString(R.string.menu_look) + "\",\"sub\":[]}]}";
        String jsonStr = "{\"custommenu\":[{\"title\":\"" + context.getString(R.string.menu_report) + "\",\"sub\":[{\"title\":\"" + report1_name + "\"},{\"title\":\"" + report2_name + "\"},{\"title\":\"" + report3_name + "\"},{\"title\":\"" + report4_name + "\"},{\"title\":\"" + report5_name + "\"},{\"title\":\"" + report6_name + "\"},{\"title\":\"" + report7_name + "\"}]},{\"title\":\"" + context.getString(R.string.menu_function) + "\",\"sub\":[{\"title\":\"" + context.getString(R.string.submenu_gps) + "\"},{\"title\":\"" + context.getString(R.string.submenu_way_analyze) + "\"}]},{\"title\":\"" + context.getString(R.string.menu_clean) + "\",\"sub\":[{\"title\":\"" + context.getString(R.string.submenu_layer) + "\"}]},{\"title\":\"" + context.getString(R.string.menu_look) + "\",\"sub\":[{\"title\":\""+context.getString(R.string.submenu_backdata)+"\"},{\"title\":\""+context.getString(R.string.submenu_complete)+"\"}]}]}";
        try {
            creatCustomMenu(new JSONObject(jsonStr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void creatCustomMenu(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("custommenu");
            buttom_custom_menu.removeAllViews();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                LinearLayout menuItemLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_main_custommenu_item, null);
                menuItemLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                TextView textView = (TextView) menuItemLayout.findViewById(R.id.txtview_menu);
                textView.setText(obj.getString("title").toString());
                if (obj.getJSONArray("sub").length() > 0) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_black, 0);
                }
                menuItemLayout.setOnClickListener(new myCustomMenuOnClickListener(obj));
                buttom_custom_menu.addView(menuItemLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class myCustomMenuOnClickListener implements View.OnClickListener {
        private JSONObject jsonObject;

        public myCustomMenuOnClickListener(JSONObject object) {
            this.jsonObject = object;
        }

        @Override
        public void onClick(View v) {
            try {
                if (jsonObject.getJSONArray("sub").length() == 0) {//主菜单点击事件

                    String menuValue = jsonObject.getString("title");
                    if (menuValue.equals(context.getString(R.string.menu_look))) {
                        showMessage("查看已经完成的外业点");
                        Intent intent = new Intent(context, ShowCompleteActivity.class);
                        intent.putExtra("p_name", txt_pName.getText().toString().trim());
                        startActivity(intent);
                    } else if (menuValue.equals("")) {//可添加
                    }
                } else {//创建子菜单
                    CustomWinMenu customWinMenu = null;
                    menuFlag = jsonObject.getString("title");//当menuFlag = "样例表" 时可以动态创建所需子菜单项
                    customWinMenu = new CustomWinMenu(MainActivity.this, jsonObject.getJSONArray("sub"), v.getWidth() + 40, 0, baiduMap, mapView, txt_pType, txt_pName, txt_showInfo, menuFlag, bhqid, bhqmc, bhqjb,btnsavelocalvalue,btnsetlocalvalue);
                    customWinMenu.showAtLocation(v);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 自定义菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu1 = menu.addSubMenu("普通测量");
        subMenu1.add(1, 1, 1, "测距离");
        subMenu1.add(1, 2, 2, "测面积");
        subMenu1.clearHeader();
        SubMenu subMenu2 = menu.addSubMenu("GPS测量");
        subMenu2.add(2, 7, 1, "GPS测距离");
        subMenu2.add(2, 8, 2, "GPS测面积");
        subMenu2.clearHeader();
        //-----------------------------------------
        SubMenu subMenu3 = menu.addSubMenu("地图选择");
        subMenu3.add(3, 3, 1, "百度地图");
        subMenu3.add(3, 4, 2, "在线地图");
        subMenu3.add(3, 5, 3, "离线地图");
        subMenu3.clearHeader();
        menu.add(4, 6, 1, "保护区选择");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Log.i("TAG", "测距离: ");
                FlagBean.dcFlag = true;
                drawType = Geometry.Type.POLYLINE;
                txt_showInfo.setText("");
                if(mapType != 1){
                    isChoose = true;
                    symbol = new SimpleLineSymbol(Color.YELLOW, 6, SimpleLineSymbol.STYLE.SOLID);
                    if (TempData.arcgis_Dcgraphicslayer != null) {
                        mapView.removeLayer(TempData.arcgis_Dcgraphicslayer);
                    }
                    TempData.arcgis_Dcgraphicslayer = new GraphicsLayer();
                    mapView.addLayer(TempData.arcgis_Dcgraphicslayer);
                }
                break;
            case 2:
                Log.i("TAG", "测面积: ");
                FlagBean.dcFlag = true;
                drawType = Geometry.Type.POLYGON;
                txt_showInfo.setText("");
                if (mapType != 1) {
                    isChoose = true;
                    fillSymbol = new SimpleFillSymbol(Color.YELLOW, SimpleFillSymbol.STYLE.SOLID);
                    fillSymbol.setAlpha(30);
                    if (TempData.arcgis_Dcgraphicslayer != null) {
                        mapView.removeLayer(TempData.arcgis_Dcgraphicslayer);
                    }
                    TempData.arcgis_Dcgraphicslayer = new GraphicsLayer();
                    mapView.addLayer(TempData.arcgis_Dcgraphicslayer);
                }
                break;
            case 3://百度地图
                txt_showInfo.setText("");
                frameLayout.removeAllViews();
                LinearLayout baidu_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.baidu_mapview, null);
                frameLayout.addView(baidu_layout);
                mapType = 1;
                initBaiduView(baidu_layout);
                arcgis_zoom_in.setVisibility(View.INVISIBLE);
                arcgis_zoom_out.setVisibility(View.INVISIBLE);
                btnsetlocalvalue.setVisibility(View.GONE);
                btnsavelocalvalue.setVisibility(View.GONE);
                if (null != m_location && locdisMag != null) {
                    localization(m_location);
                    baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
                    locdisMag.stop();//定位停止
                }
                if (!txt_pName.getText().toString().trim().equals("")){//(!bhqid.equals("")) {
                    //通过保护区ID查询出所有监测点(List<BhqHbhcBean>)
                    findBhqhcdByBhqid(bhqid);
                }
                break;
            case 4://在线地图
                txt_showInfo.setText("");
                frameLayout.removeAllViews();
                LinearLayout arcgis_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.arcgis_mapview, null);
                frameLayout.addView(arcgis_layout);
                mapType = 2;
                initArcgisView(arcgis_layout);
                arcgis_zoom_in.setVisibility(View.VISIBLE);
                arcgis_zoom_out.setVisibility(View.VISIBLE);
                localLayerImgBtn.setVisibility(View.GONE);
                btnsetlocalvalue.setVisibility(View.GONE);
                btnsavelocalvalue.setVisibility(View.GONE);
                TempData.temp_graphicslayer = null;

                break;
            case 5://离线地图
                //1.打开对话框，选择离线影像或矢量数据
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("离线数据(影像或矢量.tpk)");
                final String[] localImgs = localImgFolder.list();
                builder.setItems(localImgs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       localImgName =  localImgs[which];
                        txt_showInfo.setText("");
                        frameLayout.removeAllViews();
                        LinearLayout localLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.arcgis_mapview, null);
                        frameLayout.addView(localLayout);
                        mapType = 3;
                        initLocalArcgisView(localLayout);
                        arcgis_zoom_in.setVisibility(View.VISIBLE);
                        arcgis_zoom_out.setVisibility(View.VISIBLE);
                        localLayerImgBtn.setVisibility(View.VISIBLE);
                        btnsetlocalvalue.setVisibility(View.GONE);
                        btnsavelocalvalue.setVisibility(View.GONE);
                        TempData.temp_graphicslayer = null;
                    }
                });
                builder.show();

                break;
            case 6:
                Log.i("TAG", "保护区信息加载: ");
                Intent intent = new Intent(MainActivity.this, BhqDataActivity.class);
                //startActivity(intent);//启动其他Activity
                startActivityForResult(intent, REQUEST_CODE_DATAACTIVITY);
                // Intent intent = new Intent(MainActivity.this, DataMapActivity.class);
                // startActivityForResult(intent,REQUEST_CODE_DATAACTIVITY);   //启动其他Activity并返回结果
                break;
            case 7://GPS测距离
                btnsetlocalvalue.setVisibility(View.VISIBLE);
                btnsavelocalvalue.setVisibility(View.VISIBLE);
                TempData.pointList.clear();
                FlagBean.scFlag = true;
                drawType= Geometry.Type.POLYLINE;
                if(mapType != 1){
                    isChoose = true;
                    symbol = new SimpleLineSymbol(Color.YELLOW, 20, SimpleLineSymbol.STYLE.SOLID);
                    if (TempData.arcgis_Dcgraphicslayer != null) {
                        mapView.removeLayer(TempData.arcgis_Dcgraphicslayer);
                    }
                    TempData.arcgis_Dcgraphicslayer = new GraphicsLayer();
                    mapView.addLayer(TempData.arcgis_Dcgraphicslayer);
                    Log.i(TAG, "onOptionsItemSelected: ------layerNum:"+mapView.getLayers().length);
                }
                showMessage("利用GPS点测距离");
                break;
            case 8://GPS测面积
                btnsetlocalvalue.setVisibility(View.VISIBLE);
                btnsavelocalvalue.setVisibility(View.VISIBLE);
                TempData.pointList.clear();
                FlagBean.scFlag = true;
                drawType= Geometry.Type.POLYGON;
                if(mapType != 1){
                    isChoose = true;
                    fillSymbol = new SimpleFillSymbol(Color.YELLOW, SimpleFillSymbol.STYLE.SOLID);
                    fillSymbol.setAlpha(30);
                    if (TempData.arcgis_Dcgraphicslayer != null) {
                        mapView.removeLayer(TempData.arcgis_Dcgraphicslayer);
                    }
                    TempData.arcgis_Dcgraphicslayer = new GraphicsLayer();
                    mapView.addLayer(TempData.arcgis_Dcgraphicslayer);
                    Log.i(TAG, "onOptionsItemSelected: ------layerNum:"+mapView.getLayers().length);
                }
                showMessage("利用GPS点测面积");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //============================利用百度API画多边形==========================================
    private Marker marker = null;

    private void drawDotByParams(List<BhqHbhcBean> infos) {
        baiduMap.clear();
        LatLng latLng = null;
        OverlayOptions markerOptions = null;
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        for (BhqHbhcBean item : infos) {
            latLng = latLngConVert(new LatLng(item.getWD(), item.getJD()));
            markerOptions = new MarkerOptions()
                    .position(latLng).icon(bitmapDescriptor).visible(true).zIndex(10).alpha(0.8f);
            marker = (Marker) baiduMap.addOverlay(markerOptions);
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", item);
            marker.setExtraInfo(bundle);
        }
        // 将地图移到到最后一个经纬度位置
        //Log.i(TAG, "将地图移到到最后一个经纬度位置: "+latLng.longitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng); //.newLatLng(latLng);
        Log.i(TAG, "将地图移到ddd: "+u);
        baiduMap.setMapStatus(u);
    }

    /**
     * 画Dot,Polyline,Polygon
     */
    private void drawDot(LatLng latLng) {
        /*DotOptions dotOptions = new DotOptions()
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
            double area = Math.abs(arcgis_Polygon.calculateArea2D());
            //showMessage(String.format("%.2f",Math.abs(area))+"平方米");
            txt_showInfo.setText("面积： " + String.format("%.2f", Math.abs(area)) + " 平方米");
        }
    }

    /**
     * 定位当前位置
     *
     * @param view
     */
    public void locCurrent(View view) {
        if (m_location != null) {
            if (mapType == 1) {
                //localization(m_location);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
            } else {
                mapView.centerAt(m_location.getLatitude(), m_location.getLongitude(), true);
            }
        } else {
            showMessage("暂时无法定位，请确保可以使用定位权限");
        }
    }

    /**
     * 重写方法
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView_baidu.onPause();
        mapView.pause();
        locMag.removeUpdates(m_locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView_baidu.onResume();
        mapView.unpause();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView_baidu.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("TAG", "onBackPressed: -------------------------");
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
            } else super.onBackPressed();
        }
    }

    /**
     * 提示信息
     *
     * @param msg
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 重写该方法，该方法以回调的方式来获取指定Activity返回的结果
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUEST_CODE_DATAACTIVITY:
                if (resultCode == 1) {
                    Bundle data = intent.getExtras();
                    String pName = data.getString("dataName");
                    String tabType = data.getString("tabType");
                    // queryDataAndRender(" name = '"+pName+"'");
                    String p_log = data.getString("p_log");
                    String p_lat = data.getString("p_lat");
                    queryDataAndRender(new LatLng(Double.valueOf(p_lat), Double.valueOf(p_log)));
                    showMessage(pName);
                    //txt_pType.setText(tabType);
                    txt_pType.setText("1111111");//默认7张表都显示
                    txt_pName.setText(pName);
                }
                ;
                if (resultCode == 2) {
                    txt_pType.setText("1111111");//默认7张表都显示
                    FlagBean.isHaveOverLay = true;
                    Bundle data = intent.getExtras();
                    bhqid = data.getString("bhqid");
                    bhqmc = data.getString("bhqmc");
                    bhqjb = data.getString("bhqjb");
                    txt_pName.setText(bhqmc);
                   /* if (mapType == 1) {//当前由百度地图显示
                        //通过保护区ID查询出所有监测点(List<BhqHbhcBean>)
                         findBhqhcdByBhqid(bhqid);

                    } else{
                        //arcgis api调用查询服务查一个点图层
                        if (NetCode == 0) {
                            showMessage("无法连接网络,监测点无法加载...");
                        } else if (NetCode == 1) {
                            findLayerByBhqid(bhqid);
                        } else if (NetCode == 2) {
                            findLayerByBhqid(bhqid);
                        }
                    }*/
                    findBhqhcdByBhqid(bhqid);
                }
                break;
            case INFO_SHOW_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    callout.hide();
                }
                break;
        }

    }

    //------------baidu------------通过保护区ID查询该保护区下所有监测点(监测点中包含每一个点的经纬度)
    private boolean threadFlag;
    private List<BhqHbhcBean> hbhc_list = null;
    private void findBhqhcdByBhqid(final String bhqid) {
        //-------------------------------------------------------
        hbhc_list = loadDataBySqlLite("select * from bhqpointInfo where bhqid = ?",new String[]{bhqid});
        int num = 0;
        if(hbhc_list != null){
            num = hbhc_list.size();
        }
        if(num >0){
            if (mapType != 1) {
                List<Graphic> list = new ArrayList<>();
                for (int i = 0; i < hbhc_list.size(); i++) {
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("OBJECTID", hbhc_list.get(i).getOBJECTID());
                    attributes.put("JD", hbhc_list.get(i).getJD());
                    attributes.put("WD", hbhc_list.get(i).getWD());
                    Point _wgspoint = new Point(hbhc_list.get(i).getJD(), hbhc_list.get(i).getWD());
                    //Log.i(TAG, "findBhqhcdByBhqid: ??---------"+mapView+"????????"+mapView.getSpatialReference());
                    Point _spoint = (Point) GeometryEngine.project(_wgspoint, SpatialReference.create(SpatialReference.WKID_WGS84), mapView.getSpatialReference());
                   list.add(new Graphic(_spoint, null,attributes));
                }

                arcgis_graphics = list.toArray(new Graphic[]{});
                Message message = Message.obtain();
                message.what = DRAW_ARCGIS_MAP_GRAYPHIC;
                my_handler.sendMessage(message);
                mapView.centerAt((Point) arcgis_graphics[arcgis_graphics.length-1].getGeometry(),true);
                Log.i(TAG, "findBhqhcdByBhqid: --------arcgis_graphicsNum--->"+arcgis_graphics.length+"-----"+list.size());
            } else {
                drawDotByParams(hbhc_list);//在百度地图上画出所有点信息
            }
        }else {
            int netType = CheckNetwork.getNetWorkState(context);
            if (netType == 0) {//none
                showMessage("请打开网络连接");
                txt_pName.setText("");
                return;
            }
            //---------------------------------------------------------
            threadFlag = true;
            final FormBody.Builder builder = new FormBody.Builder();
            builder.add("bhqid", bhqid);
            Log.i(TAG, "findBhqhcdByBhqid: ---------bhqid-------->" + bhqid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String http = getResources().getString(R.string.http_url);
                    Message message = Message.obtain();
                    while (threadFlag) {
                        Response response = ParseIntentData.getDataPostByString(http + "/bhqService/BhqJsxm/GetBhqPointInfo", builder);
                        if (response != null && response.code() == 200) {
                            try {
                                String value = response.body().string();
                                Log.i(TAG, "run: -------------------value---->" + value + "--------bhqid--->" + bhqid);
                                Type type = new TypeToken<List<BhqHbhcBean>>() {
                                }.getType();
                                Gson gson = new Gson();
                                hbhc_list = gson.fromJson(value, type);//得到数据
                                //boolean  result = dbManager.deleteTableData("bhqpointInfo","bhqid = ? ",new String[]{bhqid});//防止重复插入
                                insertDataBySqlLite(hbhc_list);//插入本地数据库
                                if (mapType != 1) {
                                    message.what = DRAW_ARCGIS_MAP_GRAYPHIC;
                                } else {
                                    message.what = DRAW_BAIDU_MAP_POINT;
                                }
                                my_handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                threadFlag = false;
                            }
                        } else {
                            Log.i(TAG, "run: ------else----");
                            message.what = DRAW_MAP_POINT_FAIL;
                            my_handler.sendMessage(message);
                            threadFlag = false;
                        }
                    }

                }
            }).start();
        }
    }

    //-----------------------------请求一个自定义服务，通过保护区ID查询一个图层----------------------------------------------------
    private boolean arcgisThreadFlag;
    private Graphic[] arcgis_graphics;
    private void findLayerByBhqid(String bhqid) {
        arcgisThreadFlag = true;
        final String url = getResources().getString(R.string.http_gis);
        final String where = "bhqid = '" + bhqid + "'";
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                while (arcgisThreadFlag) {
                    FeatureSet featureSet = ParseIntentArcgisLayer.QueryTask(url + "/arcgis/rest/services/jsxm/MapServer/0", where);
                    if (featureSet != null) {
                        Log.i(TAG, "run: ----------arcgis layer------>" + featureSet.getGraphics().length);
                        message.what = DRAW_ARCGIS_MAP_GRAYPHIC;
                        arcgis_graphics = featureSet.getGraphics();
                        //获得属性值
                        /*int len = arcgis_graphics.length;
                        hbhc_list = new ArrayList<BhqHbhcBean>();
                        for (int i = 0 ; i < len ; i++) {
                            BhqHbhcBean bean = new BhqHbhcBean();
                            bean.setBHQGNQ(featureSet.getGraphics()[i].getAttributeValue("OBJECTID").toString());
                            bean.setBHQID(featureSet.getGraphics()[i].getAttributeValue("BHQID").toString());
                            bean.setBHQK(featureSet.getGraphics()[i].getAttributeValue("BHQK").toString());
                            bean.setBHQLX(featureSet.getGraphics()[i].getAttributeValue("BHQLX").toString());
                            bean.setBHQMC(featureSet.getGraphics()[i].getAttributeValue("BHQMC").toString());
                            bean.setJD(Double.valueOf(featureSet.getGraphics()[i].getAttributeValue("JD").toString()));
                            bean.setWD(Double.valueOf(featureSet.getGraphics()[i].getAttributeValue("WD").toString()));
                            Log.i(TAG, "run: ---------------获得到了值----------》"+bean.getBHQGNQ()+"-----"+bean.getJD()+"-------"+bean.getWD());
                            hbhc_list.add(bean);
                        }*/
                        my_handler.sendMessage(message);
                        arcgisThreadFlag = false;
                    } else {
                        arcgisThreadFlag = false;
                    }

                }

            }
        }).start();

    }
    //------------------------------------------------------------------------



    //================弹出callout方法===================================
    private Callout callout = null;
    private void show_call_out(float screenX, float screenY) {
        int[] graphicIDs = null;
        if (TempData.temp_graphicslayer !=null) {
            graphicIDs = graphicsLayer.getGraphicIDs(screenX, screenY, 20);
        }
        callout = mapView.getCallout();
        callout.setStyle(R.xml.calloutstyle);
        //callout.setOffset(0,-15);
        if (null != graphicIDs && graphicIDs.length > 0) {
            graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("JD");
            Log.i(TAG, "show_call_out: -----------------当前经度-------->" + graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("JD"));
            double x = Double.valueOf(graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("JD").toString());
            double y = Double.valueOf(graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("WD").toString());
            LinearLayout markerLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.baidu_infowindow, null);
            ViewHolder viewHolder = null;
            if (markerLayout.getTag() == null) {
                viewHolder = new ViewHolder();
                viewHolder.info_window_id_etxt = (EditText) markerLayout.findViewById(R.id.info_window_etxt_id);
                viewHolder.info_window_jd_etxt = (EditText) markerLayout.findViewById(R.id.info_window_jd_etxt);
                viewHolder.info_window_wd_etxt = (EditText) markerLayout.findViewById(R.id.info_window_wd_etxt);
                markerLayout.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) markerLayout.getTag();
            viewHolder.info_window_id_etxt.setText(graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("OBJECTID").toString());
            viewHolder.info_window_jd_etxt.setText(graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("JD").toString());
            viewHolder.info_window_wd_etxt.setText(graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("WD").toString());
            TempData.placeid = viewHolder.info_window_id_etxt.getText().toString().trim();//记录监测点ID
            TempData.longitude = Double.valueOf(viewHolder.info_window_jd_etxt.getText().toString().trim());//记录监测点经度
            TempData.latitude = Double.valueOf(viewHolder.info_window_wd_etxt.getText().toString().trim());//记录监测点纬度
            Point point = (Point) GeometryEngine.project(new Point(x, y), SpatialReference.create(SpatialReference.WKID_WGS84), SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR));
            callout.show(point, markerLayout);
        } else {
            callout.hide();
            TempData.placeid = "";
        }
    }

    private View.OnClickListener myTestClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showMessage("你好啊");
            Intent intent = new Intent(MainActivity.this, ShowInfoActivity.class);
            startActivityForResult(intent, INFO_SHOW_REQUEST_CODE);
        }
    };

    /**
     * 路径分析
     */
    public void routeAnalyse(View view) {

    }

    /**
     * 点击两次返回键退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showMessage("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //=======================离线数据查询并渲染=================================================
    private Feature feature = null;//用于显示查询出的要素信息

    private void queryDataAndRender(String paramater) {
        Log.i(TAG, "queryDataAndRender: ==============>" + paramater);
        // featurGraphicsLayer.removeAll();

        MyGeoDataBase myGeoDataBase = new MyGeoDataBase();
        feature = myGeoDataBase.queryGeodatabase(paramater);
        Geometry geometry = feature.getGeometry();
        /*if (geometry.getType().equals(Geometry.Type.POINT)) {
            query_point= (Point) GeometryEngine.project( geometry, SpatialReference.create(SpatialReference.WKID_WGS84), mapView.getSpatialReference());
            Graphic gp = new Graphic(query_point, new SimpleMarkerSymbol(Color.RED, 12, SimpleMarkerSymbol.STYLE.CIRCLE));
            featurGraphicsLayer.addGraphic(gp);
            mapView.centerAt(query_point,true);
        }*/
        if (geometry.getType().equals(Geometry.Type.POINT)) {
            //query_point= (Point) GeometryEngine.project( geometry, SpatialReference.create(SpatialReference.WKID_WGS84), mapView.getSpatialReference());
            Point _point = (Point) geometry;
            showMessage(_point.getY() + "");
            LatLng _latLng = latLngConVert(new LatLng(_point.getY(), _point.getX()));
            baiduMap.clear();
            /*DotOptions dotOptions = new DotOptions()
                    .center(_latLng).color(R.color.red)
                    .visible(true).radius(12);*/
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_blue_mark);
            OverlayOptions markerOptions = new MarkerOptions()
                    .position(_latLng).icon(bitmapDescriptor).visible(true);
            baiduMap.addOverlay(markerOptions);
            FlagBean.isHaveOverLay = true;
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(_latLng, 8));
        }
        Log.i(TAG, "queryDataAndRender: ------------------------>" + geometry.getType());
    }

    private void queryDataAndRender(LatLng latLng) {

        LatLng _latLng = latLngConVert(latLng);
        baiduMap.clear();
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_blue_mark);
        OverlayOptions markerOptions = new MarkerOptions()
                .position(_latLng).icon(bitmapDescriptor).visible(true);
        baiduMap.addOverlay(markerOptions);
        FlagBean.isHaveOverLay = true;
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(_latLng, 15));

    }

    //====================显示GPS航迹===========================================================
    private Polyline poly;
    private Point startPt = null;

    private void drawGpsGraphic(Point point) {//此point已经过坐标转换
        //当前定位点
        //Graphic graphicPoint = new Graphic(point,new SimpleMarkerSymbol(Color.CYAN,4, SimpleMarkerSymbol.STYLE.CIRCLE));
        //locGraphics.addGraphic(graphicPoint);
        //划线
        if (FlagBean.StartGPS) {
            startPt = null;
        }
        if (startPt == null) {
            poly = new Polyline();
            startPt = point;
            poly.startPath(startPt.getX(), startPt.getY());
            Graphic graphicPoint = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                //graphicPoint = new Graphic(point, new PictureMarkerSymbol(getDrawable(R.drawable.marker_loc)));
                graphicPoint = new Graphic(point, new SimpleMarkerSymbol(Color.BLUE,12,SimpleMarkerSymbol.STYLE.CIRCLE));
            }
            locGraphics.addGraphic(graphicPoint);
        } else {
            poly.lineTo(point.getX(), point.getY());
            GpsLayer.addGraphic(new Graphic(poly, new SimpleLineSymbol(Color.RED, 2)));
        }
        FlagBean.StartGPS = false;
    }

    //===========================arcgis mapview=========================================================
    private ArcGISDynamicMapServiceLayer dynamicLayer = null;
    private GraphicsLayer graphicsLayer = null;
    private final int ARCGIS_MAP_SUCCEED = 0X000;//arcgis 加载动态图层
    private final int DRAW_BAIDU_MAP_POINT = 0X001;//百度地图加载保护区点信息
    private final int DRAW_ARCGIS_MAP_GRAYPHIC = 0X002;//arcgis地图加载保护区点信息
    private final int DRAW_MAP_POINT_FAIL = 0x003;//连接服务器失败
    private Handler my_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ARCGIS_MAP_SUCCEED:
                     mapView.addLayer(dynamicLayer,1);
                    break;
                case DRAW_BAIDU_MAP_POINT:
                    if (hbhc_list.size() == 0) {
                        showMessage("该保护区下没有监测点!");
                    } else {
                        drawDotByParams(hbhc_list);//在地图上画出所有点信息
                    }
                    Log.i(TAG, "handleMessage: -------------hbhc_list-------------->" + hbhc_list.size());
                    break;
                case DRAW_ARCGIS_MAP_GRAYPHIC:
                    if (arcgis_graphics.length == 0) {
                        showMessage("该保护区下没有监测点!");
                    } else {
                        if (TempData.temp_graphicslayer!=null) {
                            if (mapView.getLayers().length > 2) {
                                mapView.removeLayer(TempData.temp_graphicslayer);
                                TempData.temp_graphicslayer = null;
                            }
                        }
                        graphicsLayer = new GraphicsLayer();
                        PictureMarkerSymbol marksymbol = new PictureMarkerSymbol(context, getResources().getDrawable(R.drawable.icon_openmap_blue_mark));
                        graphicsLayer.setRenderer(new SimpleRenderer(marksymbol));
                        graphicsLayer.addGraphics(arcgis_graphics);

                        mapView.addLayer(graphicsLayer);
                        TempData.temp_graphicslayer = graphicsLayer;
                        Log.i(TAG, "handleMessage: ---------绘制graphics图层----------" + arcgis_graphics.length + "----layerNum--->" + mapView.getLayers().length);
                    }
                    break;
                case DRAW_MAP_POINT_FAIL:
                    showMessage("服务器连接失败!");
                    break;
                default:
                    break;
            }
            return false;
        }

    });

    private void initArcgisView(View view) {
        mapView = (MapView) view.findViewById(R.id.arcgis_mapview);  //设置mapview状态监听
        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (STATUS.INITIALIZED == status) {//初始化完成
                    ArcGISRuntime.setClientId("9yNxBahuPiGPbsdi");//去水印
                    Log.i(TAG, "onStatusChanged: --------------arcgis view去水印");
                    //设置地图边界范围
                    // Envelope envelop = new Envelope(-1461951.242766908,3395786.1019278415,1574687.3812669083,4704490.8412850015);
                    Envelope envelop = new Envelope(-1033743.8509, 2811607.7306, 1146479.9894, 4614353.5503);
                    // mapView.setMaxExtent(envelop);
                    //加载地图服务
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int internetStatus = CheckNetwork.getNetWorkState(context);
                            switch (internetStatus) {
                                case 0:
                                    showMessage("没有网络连接,无法请求地图服务......");
                                    break;
                                case 1:
                                    requestMapService();
                                    break;
                                case 2:
                                    requestMapService();
                                    break;
                                default:
                                    break;
                            }

                        }
                    }).start();

                    initMethod();
                    //
                    if (m_location != null) {
                        mapView.centerAndZoom(m_location.getLatitude(), m_location.getLongitude(), 9);
                    }

                    if (!txt_pName.getText().toString().trim().equals("")) {
                        findBhqhcdByBhqid(bhqid);
                    }

                }
            }
        });

    }
    private void initLocalArcgisView(View view) {
        mapView = (MapView) view.findViewById(R.id.arcgis_mapview);  //设置mapview状态监听
        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (STATUS.INITIALIZED == status) {//初始化完成
                    ArcGISRuntime.setClientId("9yNxBahuPiGPbsdi");//去水印
                    Log.i(TAG, "onStatusChanged: --------------arcgis view去水印");
                    //设置地图边界范围
                    // Envelope envelop = new Envelope(-1461951.242766908,3395786.1019278415,1574687.3812669083,4704490.8412850015);
                    Envelope envelop = new Envelope(-1033743.8509, 2811607.7306, 1146479.9894, 4614353.5503);
                    requestLocalMapService();
                    initMethod();
                    if (m_location != null) {
                        mapView.centerAndZoom(m_location.getLatitude(), m_location.getLongitude(), 9);
                    }
                    //判断是否显示监测点
                    if (!txt_pName.getText().toString().trim().equals("")) {
                        findBhqhcdByBhqid(bhqid);
                    }
                }
            }
        });

    }
    /*请求地图服务
    * */
    private void requestMapService() {
        dynamicLayer = new ArcGISDynamicMapServiceLayer(http_gis + "/arcgis/rest/services/bhq/MapServer");
        Message message = Message.obtain();
        if (dynamicLayer != null) {
            message.what = ARCGIS_MAP_SUCCEED;
            my_handler.sendMessage(message);
        }
    }
    /*
    * 请求本地离线地图.tpk文件
    * */

    private void requestLocalMapService() {
        localTiledImgLayer = new ArcGISLocalTiledLayer(localImgFolder.getAbsolutePath()+"/"+localImgName);
        mapView.addLayer(localTiledImgLayer);
        if(m_location != null){
            mapView.centerAndZoom(m_location.getLatitude(),m_location.getLongitude(),13);
        }
    }
    /**
     * 初始化定位方法
     */
    private void initMethod() {
        locdisMag = mapView.getLocationDisplayManager();//显示MapView设备的当前位置
        locdisMag.setAccuracyCircleOn(true);//启用或禁用的显示精度圆。
        locdisMag.setAllowNetworkLocation(true);//网络可以用来确定当前位置
        locdisMag.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);//设置自动平移模式，默认OFF
        locdisMag.setShowPings(true);//位置更新时显示象征
        locdisMag.setLocationListener(m_locationListener);//设置位置监听
        locdisMag.start();
        mapView.setOnSingleTapListener(new OnSingleTapListener() {//
            @Override
            public void onSingleTap(float screenX, float screenY) {
                if (FlagBean.dcFlag) {//通过触摸屏幕坐标点画出
                    drawcoordGeometry(mapView.toMapPoint(screenX, screenY));
                }
                //show_call_out1(screenX,screenY);//周围点是否有气窗弹出
                if (null != graphicsLayer) {
                    show_call_out(screenX, screenY);
                }
            }
        });
        Log.i(TAG, "initMethod: -------------------->" + mapView.getLayers().length);
    }

    public void arcgisZoomOut(View view) {
        mapView.zoomout(true);
    }

    public void arcgisZoomIn(View view) {
        mapView.zoomin(true);
    }

    //==========================================================================================================
    private LocationListener m_locationListener = new LocationListener() {
        @Override//位置改变时调用
        public void onLocationChanged(Location location) {
            m_location = location;
            Log.i("TAG", "主界面onLocationChanged:-----------------> " + "位置改变");
            if (FlagBean.scFlag) { //通过实时获得的经纬度画出
                if (location != null) {
                    // drawGeometryGraphic(coordinateMethod(new Point(location.getLongitude(),location.getLatitude())));
                } else {
                    showMessage("无法获得当前位置");
                }
            }
            if (FlagBean.GPSFlag) {//开启GPS航迹
                //showMessage("111111111111");
                if (location != null) {
                    //drawGpsGraphic(coordinateMethod(new Point(location.getLongitude(),location.getLatitude())));//arcgis画出GPS航迹
                    //1.百度地图下画航迹
                    //LatLng latLng =  latLngConVert(new LatLng(location.getLatitude(),location.getLongitude()));
                   // TempData.pointList.add(latLng);
                    //if (TempData.pointList.size() < 2) {
                        //showMessage("fffff");
                   //     drawDot(latLng);
                  //  } else {
                       // showMessage("LLLLL");
                  //      drawDot(latLng);
                   //     drawPolyLine(TempData.pointList);
                   // }
                    //2.将点保存到本地文件中
                    //writeFile(location.getLongitude()+"<------>"+location.getLatitude()+"         "+currtDateTime()+"\r\n");
                   // FileUtils.writeFile(TempData.file,location.getLongitude()+"<------>"+location.getLatitude()+"         "+currtDateTime()+"\r\n");
                    Log.i(TAG, "=================显示GPS航迹======================= ");
                } else {
                    showMessage("无法获得当前位置");
                }
            }
        }

        @Override//状态改变时调用
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("TAG", "onStatusChanged: " + provider + " 状态改变");
        }

        @Override//provider生效时调用
        public void onProviderEnabled(String provider) {
            Log.i("TAG", "onProviderEnabled: " + provider + " 生效");
        }

        @Override//provider失效时调用
        public void onProviderDisabled(String provider) {
            Log.i("TAG", "onProviderDisabled: " + provider + " 失效");
        }
    };
   //==========================================arcgis画线和面============================================
   //  设定绘制的类型
    private Symbol symbol=null;
    private SimpleFillSymbol fillSymbol=null;
    private Point dhdptStart=null;
    private Point dhdptPrevious=null;
    private Polygon dhdpolygon=null;
    private boolean isChoose;
    double totalLength=0;
    int pointNum=0;//用于控制多边形周长计算
    private int PolygonUid,PolygonLengthUid;
    private boolean drawcoordGeometry(Point wgspoint) {
        if(isChoose==true){
            dhdptPrevious=null;
            dhdptStart=null;
            dhdpolygon=null;
            totalLength=0;
            pointNum=0;
        }
        Point ptCurrent;
        if (FlagBean.dcFlag) {
             ptCurrent = wgspoint;
        } else {
             ptCurrent = (Point) GeometryEngine.project(wgspoint, SpatialReference.create(SpatialReference.WKID_WGS84), mapView.getSpatialReference());
        }

        if (drawType==Geometry.Type.POINT) {
            Graphic pGraphic=new Graphic(ptCurrent, symbol);
            TempData.arcgis_Dcgraphicslayer.addGraphic(pGraphic);
        }
        else {
            pointNum++;
            if (dhdptStart==null) {
                dhdptStart=ptCurrent;
                Graphic pgraphic = null;
                if (FlagBean.dcFlag) {
                    pgraphic = new Graphic(dhdptStart, new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                } else {
                    pgraphic=new Graphic(dhdptStart,new SimpleMarkerSymbol(Color.RED, 30, SimpleMarkerSymbol.STYLE.CIRCLE));
                }
                TempData.arcgis_Dcgraphicslayer.addGraphic(pgraphic);
            }
            else {
                TempData.arcgis_Dcgraphicslayer.removeGraphic(PolygonUid);
                Graphic pGraphic;
                if (FlagBean.dcFlag) {
                   pGraphic=new Graphic(ptCurrent,new SimpleMarkerSymbol(Color.RED, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                } else {
                   pGraphic=new Graphic(ptCurrent,new SimpleMarkerSymbol(Color.RED, 30, SimpleMarkerSymbol.STYLE.CIRCLE));
                }

                TempData.arcgis_Dcgraphicslayer.addGraphic(pGraphic);
                Line line=new Line();
                line.setStart(dhdptPrevious);
                line.setEnd(ptCurrent);
                totalLength+=line.calculateLength2D();
                if(drawType==Geometry.Type.POLYLINE){
                    Polyline polyline=new Polyline();
                    polyline.addSegment(line, true);
                    Graphic iGraphicSymbol=new Graphic(polyline, symbol);
                    TempData.arcgis_Dcgraphicslayer.addGraphic(iGraphicSymbol);
                    txt_showInfo.setText("长度:"+String.format("%.2f",line.calculateLength2D())+" (米) <======> 总长度："+String.format("%.2f",totalLength)+" 米");

                }
                else if (drawType==Geometry.Type.POLYGON) {
                    if(dhdpolygon==null){
                        dhdpolygon=new Polygon();
                    }
                    dhdpolygon.addSegment(line, false);
                    double lastlineLen=0;
                    if (pointNum > 2) {
                        Line lastline=new Line();
                        lastline.setStart(dhdptStart);
                        lastline.setEnd(ptCurrent);
                        lastlineLen=lastline.calculateLength2D();
                    }
                    //多边形周长   totalLength+lastline.calculateLength2D()或者 polygon.calculateLength2D() ;多边形面积：polygon.calculateArea2D()
                    //TextSymbol polygonLengthSymbol = new TextSymbol(14,String.format("%.2f",totalLength+lastlineLen)+" m ; "+String.format("%.2f",Math.abs(polygon.calculateArea2D()))+" m2",Color.RED);

                    //Toast.makeText(MainActivity.this,String.format("%.2f",Math.abs(dhdpolygon.calculateArea2D()))+" m2",Toast.LENGTH_LONG).show();
                    txt_showInfo.setText("面积:"+String.format("%.2f",Math.abs(dhdpolygon.calculateArea2D()))+" (平方米)");
                    Graphic gGraphic=new Graphic(dhdpolygon, fillSymbol);
                    PolygonUid = TempData.arcgis_Dcgraphicslayer.addGraphic(gGraphic);
                }
            }
        }
        //writeFile(wgspoint.getX()+"<------>"+wgspoint.getY()+"         "+currtDateTime()+"\r\n");
        TempData.pointList.add(new LatLng(wgspoint.getY(), wgspoint.getX()));
        dhdptPrevious = ptCurrent;
          isChoose = false;

        return false;
    }
    //---------------------离线地图下,根据经纬度画线和面-----------------------------------------------------
    public  void setLocalGetValue(View view){
        //showMessage("离线地图画线和面");
        if(FlagBean.scFlag){
            if(m_location != null){
                if (mapType != 1) {
                    drawcoordGeometry(new Point(m_location.getLongitude(),m_location.getLatitude()));
                } else {
                    LatLng _latlon = latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude()));
                    if (drawType.equals(Geometry.Type.POLYLINE)) {
                        TempData.pointList.add(_latlon);
                        if (TempData.pointList.size() < 2) {
                            drawDot(_latlon);
                        } else {
                            drawDot(_latlon);
                            drawPolyLine(TempData.pointList);
                        }
                    } else if (drawType.equals(Geometry.Type.POLYGON)) {
                        TempData.pointList.add(_latlon);
                        if (TempData.pointList.size() < 3) {
                            drawDot(_latlon);
                            //showMessage("还需 " + (3 - TempData.pointList.size()) + " 个点显示面");
                        } else {
                            drawDot(_latlon);
                            drawPolyGonByGps(TempData.pointList, isBaiduPoint);
                        }
                    }
                }
                showMessage("点获取成功");
            }else{
                showMessage("获取经纬度失败...");
            }
        }else{
            showMessage("请选择GPS测量再操作!");
        }
    }
    public void setLocalGetValueEnd(View view){//清除图层信息、结束文件流操作
        //1.弹出对话框,输入文件名
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("输入文件名");
        builder.setIcon(R.drawable.rename_dialog_title_icon);
        View reNameView = LayoutInflater.from(context).inflate(R.layout.rename_dialog, null);
        final EditText reNameEdit = (EditText) reNameView.findViewById(R.id.rename_dialog_filename);
        builder.setView(reNameView);
        final AlertDialog reNameDialog =  builder.create();
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reNameDialog.dismiss();
            }
        });
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //1.定义文件
                definedFileAndFos(reNameEdit.getText().toString().trim());
                //2.将获得的点数据存入文件
               int len =  TempData.pointList.size();
                for (int i = 0; i < len; i++) {
                    writeFile(TempData.pointList.get(i).longitude+","+TempData.pointList.get(i).latitude+"\r\n");
                }
                showMessage("success");
                btnsetlocalvalue.setVisibility(View.GONE);
                btnsavelocalvalue.setVisibility(View.GONE);
                if (mapType != 1) {
                    FlagBean.scFlag = false;
                    mapView.removeLayer( TempData.arcgis_Dcgraphicslayer);
                    TempData.arcgis_Dcgraphicslayer = null;
                    TempData.pointList.clear();
                } else {
                    baiduMap.clear();
                }
                txt_showInfo.setText("");
                //3.关闭对话框
                reNameDialog.dismiss();
            }
        });
        builder.show();
        if(fos!=null){
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
    //------------离线地图中动态添加图层---------------------------
    public void localDynamicAddLayer(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("添加自定义图层");
        final String[] localLayers = localLayerFolder.list();
        builder.setItems(localLayers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (localTiledOtherLayer != null) {
                    mapView.removeLayer(localTiledOtherLayer);
                }
                localTiledOtherLayer= new ArcGISLocalTiledLayer(localLayerFolder+"/"+localLayers[which]);
                mapView.addLayer(localTiledOtherLayer);
                Log.i(TAG, "onClick: -----------dhd----------"+mapView.getLayers().length);
            }
        });
        builder.show();
    }
    //-----------------------------------------
    private File file = null;
    private FileOutputStream fos = null;
    private void definedFileAndFos(String fileName){
        // 定义文件输出流
        file = new File(localFileFolder.getAbsolutePath(), fileName+"("+currtDateTime()+").txt");
        try {
            fos = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void writeFile(String info){
        if(fos != null){
            try {
                Log.i("TAG", "writeFile:=================> "+file.getAbsolutePath()+"-----------------------"+info+"------date:-----"+currtDateTime());
                fos.write(info.getBytes());
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //--------------获取系统当前时间----------------------
    private String currtDateTime() {
        String dataInfo = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss:sss");
        Date currtData = new Date(System.currentTimeMillis());
        dataInfo = simpleDateFormat.format(currtData);

        /*Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        dataInfo=year+"年"+month+"月"+day+"日"+hour+"时"+minute+"分"+second+"秒"+millisecond+"毫秒";*/
        return  dataInfo;
    }

    //-----------------------------------临时组织本地数据-----------------------------------------------

    private List<BhqHbhcBean> loadDataBySqlLite(String sql,String[] bindArgs) {//加载数据
        bhqCursor = dbManager.queryEntity(sql, bindArgs);
        List<BhqHbhcBean> list = new ArrayList<BhqHbhcBean>();
        while (bhqCursor.moveToNext()) {
            Log.i(TAG, "loadDataBySqlLite: "+bhqCursor.getString(bhqCursor.getColumnIndex("bhqid")));
            BhqHbhcBean bhqinfoBean = new BhqHbhcBean();
            bhqinfoBean.setOBJECTID(bhqCursor.getInt(bhqCursor.getColumnIndex("objectid")));
            bhqinfoBean.setJD(bhqCursor.getDouble(bhqCursor.getColumnIndex("jd")));
            bhqinfoBean.setWD(bhqCursor.getDouble(bhqCursor.getColumnIndex("wd")));
            list.add(bhqinfoBean);
        }
        bhqCursor.close();
        System.out.println(list.size()+"???????????????????"+bhqCursor.getCount());
        return list;
    }
    private void insertDataBySqlLite(List<BhqHbhcBean> listResult){
        if (listResult != null) {
            int len = listResult.size();
            for (int i = 0 ; i < len ; i++) {
                dbManager.updateBySql("insert into bhqpointInfo(objectid,wd,jd,bhqid) values(?,?,?,?)",new Object[]{listResult.get(i).getOBJECTID(),listResult.get(i).getWD(),listResult.get(i).getJD(),listResult.get(i).getBHQID()});
            }
        }
    }
//--------------------------------------------------------------------------------------------------------
}
