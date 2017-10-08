package com.yaotu.proj.studydemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
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
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.Bimp;
import com.yaotu.proj.studydemo.bean.ImageItem;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static com.yaotu.proj.studydemo.R.id.item_popupwindows_Photo;

/**
 * Created by Administrator on 2017/3/14.
 */

public class TableOneActivity extends AppCompatActivity {
    private LinearLayout show_mapview_LinearLayout;//地图显示view
    private AlertDialog.Builder updata_dialog, save_dialog;
    private Context context = TableOneActivity.this;
    private Intent intent;
    private String p_Name;//要操作当前采集地的名称
    private final String TAG = this.getClass().getSimpleName();
    private EditText editText_name, editText_date, editText_area, editText_perimeter;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locMag;
    private Location m_location;
    private TextView txt_showInfo;
    private ToggleButton toggleButton;
    private LinearLayout itemLayout;
    private Spinner spinner_grass;
    private EditText editText_szy,editText_yc,editText_fgd,editText_whq,editText_xz,editText_gz;
    private List<String> spinnerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tableone);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        initMethod();
        customGridView();
        customPopwindow();
    }

    private void initView() {
        mapView = (MapView) findViewById(R.id.report_mapview);
        baiduMap = mapView.getMap();
        show_mapview_LinearLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        show_mapview_LinearLayout.setVisibility(View.GONE);
        editText_name = (EditText) findViewById(R.id.report1_editText_name);
        editText_date = (EditText) findViewById(R.id.report1_editText_date);
        editText_area = (EditText) findViewById(R.id.report1_editText_area);
        editText_perimeter = (EditText) findViewById(R.id.report1_editText_perimeter);
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        toggleButton = (ToggleButton) findViewById(R.id.tableone_tbtn);
        //--------------------------------------------------------------
        itemLayout = (LinearLayout) findViewById(R.id.activity_tableone_content_itemLayout);
        itemLayout.setVisibility(View.GONE);
        //---------------------------------------------------------------
        spinner_grass = (Spinner) findViewById(R.id.spinner_grass);
        //----------------------------------------------------------------
        editText_szy = (EditText) findViewById(R.id.report1_item_editText_szy);
        editText_yc = (EditText) findViewById(R.id.report1_item_editText_yc);
        editText_fgd = (EditText) findViewById(R.id.report1_item_editText_fgd);
        editText_whq = (EditText) findViewById(R.id.report1_item_editText_whq);
        editText_xz = (EditText) findViewById(R.id.report1_item_editText_xz);
        editText_gz = (EditText) findViewById(R.id.report1_item_editText_gz);
        //----------------------------------------------------------------
        intent = getIntent();
        p_Name = intent.getStringExtra("p_name");
        Log.i("TAG", "initView:==============> " + p_Name);
        editText_name.setText(p_Name);
    }
    private  void initMethod(){
        locMag = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locMag.getProviders(true);
        for (String provider : providers) {
            m_location = locMag.getLastKnownLocation(provider);
            if (null != m_location) {
                localization(m_location);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(),m_location.getLongitude())),16));
            }
            locMag.requestLocationUpdates(provider,1 * 1000,0,m_locationListener);
        }
        TempData.pointList.clear();//存储点的list清空,防止画多边形时有之前点。
       toggleButton.setOnCheckedChangeListener(toglistener);
        //---------------------------------
        spinnerList.add("无芒隐子草");
        spinnerList.add("小针茅");
        spinnerList.add("阿氏旋花");
        spinnerList.add("木地肤");
        spinnerList.add("沙苔草");
        spinnerList.add("猪毛菜");
        spinnerList.add("多根葱");
        spinnerList.add("冷蒿");
        spinner_grass.setAdapter(new SpinnerAdapter());
        spinner_grass.setOnItemSelectedListener(spinnerListener);
    }

    private LocationListener m_locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            m_location = location;
            Log.i(TAG, "样表一onLocationChanged:----------------------->"+"位置改变");
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
    private CompoundButton.OnCheckedChangeListener toglistener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               // showMessage("on");
                itemLayout.setVisibility(View.GONE);
                editText_szy.setText("");
                editText_gz.setText("");
                editText_xz.setText("");
                editText_whq.setText("");
                editText_yc.setText("");
                editText_fgd.setText("");
            }else {
              //showMessage("off");
                itemLayout.setVisibility(View.VISIBLE);
            }
        }
    };
    private String tempSpinnerValue ="";
    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            editText_szy.setText("");
            editText_gz.setText("");
            editText_xz.setText("");
            editText_whq.setText("");
            editText_yc.setText("");
            editText_fgd.setText("");
            tempSpinnerValue = spinnerList.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    //=========================================================
    class SpinnerAdapter extends  BaseAdapter{
        @Override
        public int getCount() {
            return spinnerList.size();
        }

        @Override
        public Object getItem(int position) {
            return spinnerList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.spinner_grass,null);
            TextView grassName = (TextView) view.findViewById(R.id.spinner_grass_text_name);
            grassName.setText(spinnerList.get(position));
            return view;
        }
    }
    //定位
    private void localization(Location loc){
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
    private LatLng latLngConVert(LatLng sourceLatLng){
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }
    /**
     * 按钮提交，保存数据到sqllite Table1中
     *
     * @param view
     */
    public void submitMethod(View view) {
       /* final String[] p_state = new String[1];//表示place表中的标记字段
        save_dialog = new AlertDialog.Builder(context);
        save_dialog.setTitle("提示消息");
        save_dialog.setMessage("确定要保存信息吗？");
        save_dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //保存到数据库
                *//**
         * 1.将数据写入表table1中(先判断该数据在表中是否存在，如果存在则更新)
         * 2.在place表中找到该采集地信息，将p_state字段中字符串的第一个字符替换为1.
         *//*
                DBManager dbManager = new DBManager(context);
                Cursor table1_cursor = dbManager.queryEntity("select * from table1 where p_name = ? ", new String[]{p_Name});//查询table1信息表中是否存在该数据
                Cursor place_cursor = dbManager.queryEntity("select p_state from place where p_name = ?", new String[]{p_Name});
                if (place_cursor.moveToNext()) {
                    p_state[0] = place_cursor.getString(place_cursor.getColumnIndex("p_state"));
                    Log.i(TAG, "onClick: =====================>" + p_state[0]);
                }
                if (table1_cursor.moveToNext()) {//table1中已经存在改信息，提示是否修改
                    boolean flag1 = dbManager.updateBySql("update table1 set mj = ?, zc = ? where p_name = ?", new Object[]{edit_mj.getText().toString().trim(), edit_zc.getText().toString().trim(), edit_mc.getText().toString().trim()});
                    StringBuffer sbf = new StringBuffer(p_state[0]);
                    sbf.setCharAt(0, '1');
                    boolean flag2 = dbManager.updateBySql("update place set p_state = ? where p_name = ?", new String[]{sbf.toString(),p_Name});
                    if (flag1 && flag2) {
                        showMessage("修改成功");
                    } else {
                        showMessage("修改失败");
                    }
                } else {//table1中不存在该信息，insert
                    boolean flag1 = dbManager.updateBySql("insert into table1(p_name,mj,zc) values(?,?,?)", new Object[]{edit_mc.getText().toString().trim(), edit_mj.getText().toString().trim(), edit_zc.getText().toString().trim()});
                    StringBuffer sbf = new StringBuffer(p_state[0]);
                    sbf.setCharAt(0, '1');
                    boolean flag2 = dbManager.updateBySql("update place set p_state = ? where p_name = ?", new String[]{sbf.toString(),p_Name});
                    if (flag1 & flag2) {
                        showMessage("保存成功");
                    } else {
                        showMessage("保存失败");
                    }
                }
                dialog.dismiss();
            }
        });
        save_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        save_dialog.show();*/
        final String[] p_state = new String[1];//表示place表中的标记字段
        final DBManager dbManager = new DBManager(context);
        Cursor table1_cursor = dbManager.queryEntity("select * from table1 where p_name = ? ", new String[]{p_Name});//查询table1信息表中是否存在该数据
        Cursor place_cursor = dbManager.queryEntity("select p_state from place where p_name = ?", new String[]{p_Name});
        if (place_cursor.moveToNext()) {
            p_state[0] = place_cursor.getString(place_cursor.getColumnIndex("p_state"));
            Log.i(TAG, "onClick: =====================>" + p_state[0]);
        }
        if (table1_cursor.moveToNext()) {//table1中已经存在改信息，提示是否修改
            updata_dialog = new AlertDialog.Builder(context);
            updata_dialog.setMessage("该样表数据已存在，是否修改？");
            updata_dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean flag1 = dbManager.updateBySql("update table1 set mj = ? where p_name = ?", new Object[]{editText_area.getText().toString().trim(), editText_name.getText().toString().trim()});
                    StringBuffer sbf = new StringBuffer(p_state[0]);
                    sbf.setCharAt(0, '1');
                    boolean flag2 = dbManager.updateBySql("update place set p_state = ? where p_name = ?", new String[]{sbf.toString(), p_Name});
                    if (flag1 && flag2) {
                        showMessage("修改成功");
                    } else {
                        showMessage("修改失败");
                    }

                }
            });
            updata_dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            updata_dialog.show();
        } else {
            save_dialog = new AlertDialog.Builder(context);
            save_dialog.setMessage("是否保存信息？");
            save_dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean flag1 = dbManager.updateBySql("insert into table1(p_name,mj) values(?,?)", new Object[]{editText_name.getText().toString().trim(), editText_area.getText().toString().trim()});
                    StringBuffer sbf = new StringBuffer(p_state[0]);
                    sbf.setCharAt(0, '1');
                    boolean flag2 = dbManager.updateBySql("update place set p_state = ? where p_name = ?", new String[]{sbf.toString(), p_Name});
                    if (flag1 && flag2) {
                        showMessage("保存成功");
                    } else {
                        showMessage("保存失败");
                    }
                }
            });
            save_dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            save_dialog.show();
        }

    }
    public void returnUpMethod(View view){//返回按钮事件
        locMag.removeUpdates(m_locationListener);
        finish();
    }
    /**
     * button onclick
     */
    public void measuerArea(View view) {//meauser;测量
        show_mapview_LinearLayout.setVisibility(View.VISIBLE);
    }

    public void getValueMethod(View view) {//获取值
        show_mapview_LinearLayout.setVisibility(View.GONE);
        editText_area.setText(String.valueOf(area));
        editText_perimeter.setText(String.valueOf(permiter));
    }

    public void moveCurrentPosition(View view) {//移动到当前位置
        showMessage("move current position");
        if (null != m_location) {
            localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLngConVert(new LatLng(m_location.getLatitude(),m_location.getLongitude())),16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
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
            TempData.pointList.add(latLng);
            if (TempData.pointList.size() < 3) {
                drawDot(latLng);
                showMessage("还需 "+(3 - TempData.pointList.size())+" 个点显示面");
            } else {
                drawPolyGonByGps(TempData.pointList,isBaiduLatlng);
            }
        } else {
            showMessage("获得经纬度失败...");
        }
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://标题actionbar中的返回菜单
                locMag.removeUpdates(m_locationListener);
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }
    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            locMag.removeUpdates(m_locationListener);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //===============================================================================================
    /**
     * 画Dot,Polyline,Polygon
     */
    private Overlay overlay_polygon,overlay_polyline;
    private void drawDot(LatLng latLng){
       /* DotOptions dotOptions = new DotOptions()
                .center(latLng).color(R.color.blue)
                .visible(true).radius(8);*/
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark);
        OverlayOptions markerOptions = new MarkerOptions()
                .position(latLng).icon(bitmapDescriptor).visible(true);
        baiduMap.addOverlay(markerOptions);

    }
    private void drawPolyLine(List<LatLng> points){//至少两个点
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
    private void drawPolyGonByGps(List<LatLng> latLngs,boolean isBaiduPoint){//至少三个点
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
    //====================================photo========================================================
    /**
     *
     */
    private GridView gridView;
    private GridAdapter adapter;
    private static final int TAKE_PICTURE = 0x000001;
    private static final int TAKE_ALBUM_PICTURE=0x000002;
    public void customGridView() {
        gridView = (GridView) findViewById(R.id.gridvidw);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(context);
        adapter.update();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == Bimp.SelectBitmap.size()) {
                    popupWindow.showAtLocation(gridView, Gravity.BOTTOM, 0, 0);//parent参数可以很随意，只要是activity中的view都可以
                } else {

                }
            }
        });
    }

    /**
     * 创建popupWINDOW
     */
    private PopupWindow popupWindow;
    private LinearLayout layout_popwindow;

    public void customPopwindow() {

        View view = getLayoutInflater().inflate(R.layout.popwindow_layout, null);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.popParent);
        layout_popwindow = (LinearLayout) view.findViewById(R.id.popLinearLayout);
        Button btn_camera = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button btn_photo = (Button) view.findViewById(item_popupwindows_Photo);

        popupWindow = new PopupWindow(context);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(view);

        parent.setOnClickListener(new View.OnClickListener() {//点击空白处，退出popupWindow
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        btn_camera.setOnClickListener(new View.OnClickListener() {//点击事件，调用相机拍照
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
                popupWindow.dismiss();
            }
        });
        btn_photo.setOnClickListener(new View.OnClickListener() {//点击事件，从相册中选取
            @Override
            public void onClick(View v) {//打开选取相册界面

                Intent openAlbumIntent = new Intent(context, ShowPhotoActivity.class);
                startActivityForResult(openAlbumIntent,TAKE_ALBUM_PICTURE);
                popupWindow.dismiss();
            }
        });
    }



    /**
     * 自定义Adapter
     */
    private class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int pic_show_num=Integer.parseInt(context.getString(R.string.pic_totalNum));
            if (Bimp.SelectBitmap.size() >=pic_show_num ) {
                return pic_show_num;
            }
            return (Bimp.SelectBitmap.size() + 1);

        }

        public void update() {
            loading();
        }

        @Override
        public Object getItem(int position) {
            return Bimp.SelectBitmap.get(position).getBitmap();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.gridview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.gridview_item_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.SelectBitmap.size()) {
                viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
                System.out.println(position + "-----------------dd---------------");
            } else {
                viewHolder.imageView.setImageBitmap(Bimp.SelectBitmap.get(position).getBitmap());
                Log.i(TAG, "getView: "+ "hkehehehehehe");
            }

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.SelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            Log.i("TAG", "run: " + "11111111111111111");
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            Log.i("TAG", "run: " + "22222222222222222222");
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE://返回pictuer
                if (Bimp.SelectBitmap.size() < 8 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    File file= FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    takePhoto.setImagePath(file.getAbsolutePath());
                    takePhoto.setImageName(file.getName());
                    Bimp.SelectBitmap.add(takePhoto);
                    //发送广播更新系统相册
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(FileUtils.SDPATH+"pic_"+fileName+".jpg"));
                    Log.i(TAG, "onActivityResult: "+FileUtils.SDPATH+"pic_"+fileName+".jpg");
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                }
                break;
            case TAKE_ALBUM_PICTURE:
                if (Bimp.SelectBitmap.size()<8 && resultCode == ShowPhotoActivity.RESULT_OK) {

                }
                break;
            default:
                break;
        }
    }
    public void photoReset(View view){
        Bimp.SelectBitmap.clear();
        Bimp.max = 0;
        adapter.update();
    }
    public void saveGrass(View view){
        showMessage("保存成功!");
        editText_szy.setText("");
        editText_gz.setText("");
        editText_xz.setText("");
        editText_whq.setText("");
        editText_yc.setText("");
        editText_fgd.setText("");
        spinnerList.remove(tempSpinnerValue);
        spinner_grass.setAdapter(new SpinnerAdapter());
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.update();
    }
}
