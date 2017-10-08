package com.yaotu.proj.studydemo.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.FlagBean;
import com.yaotu.proj.studydemo.util.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 */

public class AddSamplePlotActivity extends AppCompatActivity {
    private Context context = AddSamplePlotActivity.this;
    private final String TAG = this.getClass().getSimpleName();
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private CheckBox sampleplot_checkBox_report1,sampleplot_checkBox_report2,sampleplot_checkBox_report3,sampleplot_checkBox_report4,sampleplot_checkBox_report5;
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private EditText sampleplot_editText_ID,sampleplot_editText__date,sampleplot_editText_jlr,sampleplot_editText__latlng;
    private DBManager dbManager;
    private LinearLayout addSampleplot_showmap_baiduView;
    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locMag;
    private Location m_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsampleplot);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        dbManager = new DBManager(context);
    }
    private void initView(){
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_dxdm);
        sampleplot_checkBox_report1 = (CheckBox) findViewById(R.id.sampleplot_checkBox_report1);
        sampleplot_checkBox_report2 = (CheckBox) findViewById(R.id.sampleplot_checkBox_report2);
        sampleplot_checkBox_report3 = (CheckBox) findViewById(R.id.sampleplot_checkBox_report3);
        sampleplot_checkBox_report4 = (CheckBox) findViewById(R.id.sampleplot_checkBox_report4);
        sampleplot_checkBox_report5 = (CheckBox) findViewById(R.id.sampleplot_checkBox_report5);
        // 将所有的checkbox放到一个集合中
        checkBoxList.add(sampleplot_checkBox_report1);
        checkBoxList.add(sampleplot_checkBox_report2);
        checkBoxList.add(sampleplot_checkBox_report3);
        checkBoxList.add(sampleplot_checkBox_report4);
        checkBoxList.add(sampleplot_checkBox_report5);
        //-------------------------------------------------------------------
        sampleplot_editText_ID = (EditText) findViewById(R.id.sampleplot_editText_ID);
        sampleplot_editText__date = (EditText) findViewById(R.id.sampleplot_editText__date);
        sampleplot_editText_jlr = (EditText) findViewById(R.id.sampleplot_editText_jlr);
        sampleplot_editText__latlng = (EditText) findViewById(R.id.sampleplot_editText__latlng);
        //-------------------------------------------------------------------------
        addSampleplot_showmap_baiduView = (LinearLayout) findViewById(R.id.addSampleplot_showmap_Layout);
        addSampleplot_showmap_baiduView.setVisibility(View.GONE);
        mapView = (MapView) findViewById(R.id.addsampleplot_mapview);
        baiduMap = mapView.getMap();
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
        setListener();

    }
    private void setListener(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                //showMessage(radioButton.getText().toString());
            }
        });
    }
    /**
     * 提示信息
     */
    private void showMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://标题actionbar中的返回菜单
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }
    /**
     * btn onclick
     */

    public void addMethod(View view){
        if (null != radioButton) {
            showMessage(radioButton.getText().toString());
        }
        String id = sampleplot_editText_ID.getText().toString().trim();
        String data = sampleplot_editText__date.getText().toString().trim();
        String jlr = sampleplot_editText_jlr.getText().toString().trim();
        String latlog = sampleplot_editText__latlng.getText().toString().trim();
        //showMessage("".equals(latlog)+"---------"+"".equals(id));
        if ("".equals(id)) {
            showMessage("样地编号不能为空");
            return;
        } else if ("".equals(latlog)) {
            showMessage("经纬度不能为空");
            return;
        } else if (!latlog.contains(",")) {
            showMessage("经纬度格式不正确...");
            return;
        }
        String  latitude =latlog.substring(0,latlog.indexOf(",") );
        String longitude = latlog.substring(latlog.indexOf(",")+ 1 );
        Log.i(TAG, "addMethod: ========>"+latlog+"========"+latlog.indexOf(",")+"////////"+longitude+"---------->"+latitude);
        StringBuilder report_type_builder = new StringBuilder();
        String report_type ="";
        int flagChexBoxIsChecked = 0;
        for (int i = 0 ; i < checkBoxList.size() ; i++) {
            //showMessage(checkBoxList.get(i).isChecked()+"");
            if (checkBoxList.get(i).isChecked()) {
                report_type_builder.append("1");
                flagChexBoxIsChecked ++;
            } else {
                report_type_builder.append("0");
            }
        }
        if (flagChexBoxIsChecked == 0) {
            showMessage("请至少选择一个样表类型...");
            return;
        }
        report_type = report_type_builder.toString();
        //showMessage(report_type);
        boolean result = dbManager.updateBySql("insert into place(p_name,p_log,p_lat,p_type,p_state) values (?,?,?,?,?)",new Object[]{id,longitude,latitude,report_type,"0000"});
        if (result) {
            showMessage("success");
            AddSamplePlotActivity.this.setResult(1);//临时resultcode
            locMag.removeUpdates(m_locationListener);
            finish();
        } else {
            showMessage("fail...");
        }

    }

    public void getLatlog(View view){
        addSampleplot_showmap_baiduView.setVisibility(View.VISIBLE);
    }
    public void getValueMethod(View view){
        addSampleplot_showmap_baiduView.setVisibility(View.GONE);
        if (null != m_location) {
            sampleplot_editText__latlng.setText(m_location.getLatitude() + "," + m_location.getLongitude());
        } else {
            showMessage("定位失败,检查软件是否授权定位权限!");
        }

    }
    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();
    }
    //===============================================================================================
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
        // 当不需要定位图层时关闭定位图层
        // baiduMap.setMyLocationEnabled(false);

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
    private LocationListener m_locationListener =new LocationListener() {
        @Override//位置改变时调用
        public void onLocationChanged(Location location) {
            m_location = location;
            Log.i(TAG, "添加样地onLocationChanged: ------------------>"+"位置改变");
        }

        @Override//状态改变时调用
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("TAG", "onStatusChanged: "+provider+" 状态改变");
        }

        @Override//provider生效时调用
        public void onProviderEnabled(String provider) {
            Log.i("TAG", "onProviderEnabled: "+provider+" 生效");
        }

        @Override//provider失效时调用
        public void onProviderDisabled(String provider) {
            Log.i("TAG", "onProviderDisabled: "+provider+" 失效");
        }
    };
}
