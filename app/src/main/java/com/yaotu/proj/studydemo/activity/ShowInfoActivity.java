package com.yaotu.proj.studydemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Line;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.yaotu.proj.studydemo.R;

import java.util.List;

/**
 *  用来显示数据界面
 * Created by Administrator on 2017/2/27.
 */

public class ShowInfoActivity extends AppCompatActivity {
    private Button perimeter_btn;//测量周长按钮
    private Button area_btn;//测量面积按钮
    private Context context = ShowInfoActivity.this;
    private MapView info_mapview;
    private LinearLayout show_mapview_linearLayout;
    private final String TAG = getClass().getSimpleName();
    private GraphicsLayer positionLayer = new GraphicsLayer();
    private GraphicsLayer symbolLayer = new GraphicsLayer();

    private EditText perimeter_edit,area_edit;
    private TextView show_area_txt,show_perimeter_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        //getSupportActionBar().hide();继承AppCompatActivity需要通过该方法隐藏标题
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();
        info_mapview.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (status == STATUS.INITIALIZED) {
                    initMethod();
                }
            }
        });

        info_mapview.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float v, float v1) {
                //drawGeoMetry(info_mapview.toMapPoint(v,v1), Geometry.Type.POLYLINE);
                drawGeoMetry(info_mapview.toMapPoint(v,v1), Geometry.Type.POLYGON);
            }
        });
    }

    /**
     * 初始化layout.xml中的view
     */

    private void initView() {
        //perimeter_btn = (Button) findViewById(R.id.perimeter_btn);
        area_btn = (Button) findViewById(R.id.area_btn);
        info_mapview = (MapView) findViewById(R.id.info_mapview);
        show_mapview_linearLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        show_mapview_linearLayout.setVisibility(View.GONE);

        info_mapview.addLayer(positionLayer);//添加位置图层
        info_mapview.addLayer(symbolLayer);//添加自定义矢量图层

        perimeter_edit = (EditText) findViewById(R.id.yf_perimeter_edit);
        area_edit = (EditText) findViewById(R.id.yf_area_edit);
        show_area_txt = (TextView) findViewById(R.id.show_area_txt);
        show_perimeter_txt = (TextView) findViewById(R.id.show_perimeter_txt);
    }

    /**
     * 初始化基本方法
     */
    private LocationManager locMag;
    private Location my_location;
    private void initMethod() {
        locMag = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        List<String> providers = locMag.getProviders(true);//获得所有可用的Provider
        for (String provider : providers) {
            my_location = locMag.getLastKnownLocation(provider);
            if (null != my_location) {
                info_mapview.centerAt(coordinateMethod(new Point(my_location.getLongitude(),my_location.getLatitude())),true);//初始化时移动到当前位置

                Log.i(TAG, "经度: "+my_location.getLongitude()+" 纬度： "+my_location.getLatitude());
            }
            locMag.requestLocationUpdates(provider,1000*2,0,customLocListener);//注册当前可用的定位监听
        }
    }


    //
    private LocationListener customLocListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (null != location) {
                Point wpspoint = new Point(location.getLongitude(), location.getLatitude());
                showPositionMarker(coordinateMethod(wpspoint));
                Log.i(TAG, "onLocationChanged: "+ location.getProvider());
            }
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
    // 经纬度坐标转换
    private Point coordinateMethod(Point  Point) {
        return  (Point) GeometryEngine.project(Point, SpatialReference.create(SpatialReference.WKID_WGS84),info_mapview.getSpatialReference());
    }
    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this,"返回上一页",Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * layout中 perimeter_btn的OnClick方法
     * @param view
     */
    /*public void perimeterMethod(View view) {
        Intent intent = getIntent();
        intent.putExtra("flag",true);
        setResult(RESULT_OK,intent);
        Log.i("TAG", "perimeterMethod: "+"11111111111");
        finish();
    }*/
    /**
     * layout中 area_btn的OnClick方法
     * @param view
     */
    public void areaMethod(View view) {
        show_mapview_linearLayout.setVisibility(View.VISIBLE);
    }




    /**
     * 实现R.layout.activity_show_info当中
     * show_info_zoomin_btn按钮的OnClick事件
     * 地图缩小
     * @param view
     */
    public void showInfoZoomIn(View view) {
        info_mapview.zoomin(true);
    }
    /**
     * 实现R.layout.activity_show_info当中
     * show_info_zoomout_btn按钮的OnClick事件
     * 地图放大
     * @param view
     */
    public void showInfoZoomOut(View view) {
        info_mapview.zoomout(true);
    }

    /**
     * 实现activity_show_info_mapview.xml中
     * dot_btn 按钮的OnClick事件
     * 在地图上根据当前获取到的经纬度，进行矢量图的显示和计算（打点）
     * @param viwe
     */
    public void btnDotMethod(View viwe) {
        if (null != my_location) {
            Point wpspoint = new Point(my_location.getLongitude(), my_location.getLatitude());
            drawGeoMetry(coordinateMethod(wpspoint), Geometry.Type.POLYGON);
            showMessage("打点成功...");
        } else {
            showMessage("获得经纬度失败...");
        }
    }
    /**
     * 实现activity_show_info_mapview.xml中
     * reset_btn 按钮的OnClick事件
     * 重置
     * @param view
     */
    public void btnResetMethod(View view) {
        isFirst = true;
        symbolLayer.removeAll();
        show_area_txt.setText("");
        show_perimeter_txt.setText("");
    }

    /**
     * 取值
     * @param view
     */
    public void btnValueMethod(View view) {
        //判断当前获取的值不为空
        if (!("".equals(show_perimeter_txt.getText().toString().trim()))) {
            perimeter_edit.setText(getPerimeterString(polygon.calculateLength2D()));
        }
        //当有两个点的时候，面积会显示0.0，所以此时不能获取该值，反之可以获取
        if (!("".equals(show_area_txt.getText().toString().trim())) && !(show_area_txt.getText().toString().trim().substring(6,9).equals("0.0"))) {
            area_edit.setText(getAreaString(polygon.calculateArea2D()));
            Log.i(TAG, "btnValueMethod: "+show_area_txt.getText().toString().trim().substring(6,9));
        }
        isFirst = true;
        symbolLayer.removeAll();
        show_mapview_linearLayout.setVisibility(View.GONE);
        show_area_txt.setText("");
        show_perimeter_txt.setText("");
    }

    /**
     * 定位到当前位置
     * @param view
     */
    public void showInfoCurrentPosition(View view) {
        if (null != my_location) {
            info_mapview.centerAt(coordinateMethod(new Point(my_location.getLongitude(),my_location.getLatitude())),true);
        }

    }
    // 显示当前位置
    private void showPositionMarker(Point point) {
        positionLayer.removeAll();
        Graphic position_Graphic = new Graphic(point, new SimpleMarkerSymbol(Color.BLUE, 4, SimpleMarkerSymbol.STYLE.CIRCLE));
        positionLayer.addGraphic(position_Graphic);

    }

    /**
     * 根据坐标点画出矢量图
     */
    private boolean isFirst = true;
    private Point prePoint;
    private Point currtPotin;
    private Point startPoint;
    private Polyline polyline;
    private Polygon polygon;
    private int polyGonUID;
    public void drawGeoMetry(Point point,Geometry.Type drawType) {
        if (isFirst) {
            prePoint = null;
            currtPotin = null;
            startPoint = null;
            polyline = null;
            polygon = null;
            Log.i("TAG", "drawGeoMetry: ===============1================");
        }
        Log.i("TAG", "drawGeoMetry: ===============2================");
        currtPotin = point;
        if (drawType == Geometry.Type.POINT) {//当类型是点时执行
            Graphic graphic = new Graphic(currtPotin, new SimpleMarkerSymbol(Color.GREEN, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
            symbolLayer.addGraphic(graphic);
        } else {//当类型是线或多边形时执行
            if (null == startPoint) {
                startPoint = currtPotin;
                Graphic graphic = new Graphic(startPoint, new SimpleMarkerSymbol(Color.GREEN, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                symbolLayer.addGraphic(graphic);
                Log.i("TAG", "drawGeoMetry: ===============3================");
            } else {
                Graphic graphic = new Graphic(currtPotin, new SimpleMarkerSymbol(Color.GREEN, 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                symbolLayer.addGraphic(graphic);
                Line line = new Line();
                line.setStart(prePoint);
                line.setEnd(currtPotin);
                if (drawType == Geometry.Type.POLYLINE) {
                    if (null == polyline) {
                        polyline = new Polyline();
                    }
                    polyline.addSegment(line,true);
                    symbolLayer.addGraphic(new Graphic(polyline, new SimpleLineSymbol(Color.RED, 2, SimpleLineSymbol.STYLE.SOLID)));
                    Log.i("TAG", "drawGeoMetry: ===============4================");
                } else if (drawType == Geometry.Type.POLYGON) {
                    symbolLayer.removeGraphic(polyGonUID);
                    if (null == polygon) {
                        polygon = new Polygon();
                    }
                    polygon.addSegment(line,false);
                    polyGonUID = symbolLayer.addGraphic(new Graphic(polygon, new SimpleFillSymbol(Color.BLUE, SimpleFillSymbol.STYLE.SOLID).setAlpha(30)));
                    /*perimeter_edit.setText(getPerimeterString(polygon.calculateLength2D()));
                    area_edit.setText(getAreaString(polygon.calculateArea2D()));*/
                    show_area_txt.setText("样方面积: "+getAreaString(polygon.calculateArea2D()));//此处格式轻易不要改变
                    show_perimeter_txt.setText("样方周长: "+getPerimeterString(polygon.calculateLength2D()));
                    Log.i("TAG", "drawGeoMetry: ===============5================");
                }
            }
        }

        isFirst = false;
        prePoint = currtPotin;
    }
    //获得面积的输出格式
    private String getAreaString(double dValue){
        long area = Math.abs(Math.round(dValue));
        String sArea = "";
        // 顺时针绘制多边形，面积为正，逆时针绘制，则面积为负
        if(area >= 1000000){
            double dArea = area / 1000000.0;
            sArea = Double.toString(dArea) + " 平方公里";
        }
        else
            sArea = Double.toString(area) + " 平方米";

        return sArea;
    }
    //获得周长的输出格式
    private String getPerimeterString(double pValue) {
        long perimeter = Math.round(pValue);
        String sPerimeter = "";
        if (perimeter > 1000) {
            double dPerimeter = perimeter / 1000.0;
            sPerimeter = Double.toString(dPerimeter) + " 公里";
        } else {
            sPerimeter = Double.toString(perimeter) + " 米";
        }
        return sPerimeter;
    }
    /**
     * 提示信息
     * @param message
     */
    private  void showMessage(String message) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    /**
     * 重写activity方法
     */
    @Override
    protected void onPause() {
        super.onPause();
        info_mapview.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        info_mapview.unpause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
