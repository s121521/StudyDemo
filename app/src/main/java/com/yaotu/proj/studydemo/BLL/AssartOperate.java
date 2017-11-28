package com.yaotu.proj.studydemo.BLL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.SystemClock;
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
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.tableBean.AssartBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.TempData;

/**
 * Created by Administrator on 2017/6/30.
 */

public class AssartOperate implements Operate<AssartBean> {
    private final String TAG = this.getClass().getSimpleName();
    private View view;
    private Context context;
    private MapValueType valueType = null;
    private ImageView as_photo_img;//相片
    private TextView as_canleImage;
    private EditText as_bhqmc_etxt;//保护区名称
    private EditText as_jb_etxt;//保护区级别
    private EditText as_kkqkmc_etxt;//开垦区块名称
    private EditText as_kkmj_etxt;//开垦面积
    private EditText as_zwzl_etxt;//作物种类
    private EditText as_zxzb_etxt;//中心坐标
    private EditText as_xmmzb_etxt;//项目面坐标
    private EditText as_scmj_etxt;//实测面积
    private RadioGroup as_sjqh_groupRadio;//始建前后
    private RadioButton as_sjqh_radio1;//前
    private RadioButton as_sjqh_radio2;//后
    private String as_Sjqh;
    private EditText as_syq_etxt;//实验区面积
    private EditText as_hcq_etxt;//缓冲区面积
    private EditText as_hxq_etxt;//核心区面积
    private RadioGroup as_sfgz_groupRadio;//是否耕种
    private RadioButton as_sfgz_radio1;//是
    private RadioButton as_sfgz_radio2;//否
    private String as_Sfgz;
    private EditText as_lsqk_etxt;//整改落实情况
    private TextView as_hxq_txt,as_hcq_txt,as_syq_txt;

    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private LinearLayout mapLayout,LinearLayoutButtom;

    private String bhqid,bhqmc,bhqjb;
    private ImageButton dot_btn,reset_btn,getvelue_btn;//地图button
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private TextView txt_showInfo;
    private InitMap initMap;
    //----------一下内容国家统一下发
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String placeid;//监测点ID
    public AssartOperate(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_assart, null);
        mapLayout = (LinearLayout) view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        mapLayout.setVisibility(View.GONE);
        LinearLayoutButtom.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }
    private void initView(){
        as_photo_img = (ImageView) view.findViewById(R.id.as_photo_img);
        as_canleImage = (TextView) view.findViewById(R.id.as_canleImage);
        as_bhqmc_etxt = (EditText) view.findViewById(R.id.as_bhqmc_etxt);
        as_jb_etxt = (EditText) view.findViewById(R.id.as_jb_etxt);
        as_kkqkmc_etxt = (EditText) view.findViewById(R.id.as_kkqkmc_etxt);
        as_kkmj_etxt = (EditText) view.findViewById(R.id.as_kkmj_etxt);
        as_zwzl_etxt = (EditText) view.findViewById(R.id.as_zwzl_etxt);
        as_zxzb_etxt = (EditText) view.findViewById(R.id.as_zxzb_etxt);
        as_xmmzb_etxt = (EditText) view.findViewById(R.id.as_xmmzb_etxt);
        as_scmj_etxt = (EditText) view.findViewById(R.id.as_scmj_etxt);
        as_sjqh_groupRadio = (RadioGroup) view.findViewById(R.id.as_sjqh_groupRadio);
        as_sjqh_radio1 = (RadioButton) view.findViewById(R.id.as_sjqh_radio1);
        as_sjqh_radio2 = (RadioButton) view.findViewById(R.id.as_sjqh_radio2);
        as_syq_etxt = (EditText) view.findViewById(R.id.as_syq_etxt);
        as_hcq_etxt = (EditText) view.findViewById(R.id.as_hcq_etxt);
        as_hxq_etxt = (EditText) view.findViewById(R.id.as_hxq_etxt);
        as_sfgz_groupRadio = (RadioGroup) view.findViewById(R.id.as_sfgz_groupRadio);
        as_sfgz_radio1 = (RadioButton) view.findViewById(R.id.as_sfgz_radio1);
        as_sfgz_radio2 = (RadioButton)view.findViewById(R.id.as_sfgz_radio2);
        as_lsqk_etxt = (EditText) view.findViewById(R.id.as_lsqk_etxt);
        as_hcq_txt = (TextView) view.findViewById(R.id.as_hcq_txt);
        as_hxq_txt = (TextView) view.findViewById(R.id.as_hxq_txt);
        as_syq_txt = (TextView) view.findViewById(R.id.as_syq_txt);

        mapView = (MapView) view.findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) view.findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) view.findViewById(R.id.reset_btn);//地图重置按钮
        getvelue_btn = (ImageButton) view.findViewById(R.id.getvelue_btn);//获取值
        imgBtn_moveCurrt = (ImageButton) view.findViewById(R.id.imgBtn_moveCurrt);
        txt_showInfo = (TextView) view.findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
        as_bhqmc_etxt.setText(bhqmc);
        as_jb_etxt.setText(bhqjb);
    }
    private void initMethod(){
        /*
        * 长按获取中心坐标
        * */
        as_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                dot_btn.setVisibility(View.GONE);
                reset_btn.setVisibility(View.GONE);
                valueType = MapValueType.centerCoordinate;
                return false;
            }
        });
        //获取项目面坐标

        as_xmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                valueType = MapValueType.multiPoint;
                return false;
            }
        });
         /*
        * 打开相册或照相机
        * */
        as_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("不可以修改相片!");
            }
        });

        /*始建前后/是否耕种*/
        as_sjqh_groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (as_sjqh_radio1.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------始建前后------->"+as_sjqh_radio1.getText().toString());
                    as_Sjqh = as_sjqh_radio1.getText().toString().trim();
                } else if (as_sjqh_radio2.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------始建前后------->"+as_sjqh_radio2.getText().toString());
                    as_Sjqh = as_sjqh_radio2.getText().toString().trim();
                }
            }
        });
        as_sfgz_groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (as_sfgz_radio1.getId() == checkedId) {
                    as_Sfgz = as_sfgz_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: -----是否耕种------->"+as_Sfgz);
                } else if (as_sfgz_radio2.getId() == checkedId) {
                    as_Sfgz = as_sfgz_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: -----是否耕种------->"+as_Sfgz);
                }
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
    //=========================================================================================
    private void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    as_zxzb_etxt.setText(String.valueOf(m_location.getLongitude())+","+String.valueOf(m_location.getLatitude()));
                    dot_btn.setVisibility(View.VISIBLE);
                    reset_btn.setVisibility(View.VISIBLE);
                    break;
                case multiPoint:
                    Log.i(TAG, "getValueMethod: --------------->"+ TempData.pointList.toString());
                    StringBuilder sb = new StringBuilder();
                    int len = TempData.pointList.size();
                    for (int i = 0; i < len; i++) {
                        sb.append("["+TempData.latLngList.get(i).longitude+","+TempData.latLngList.get(i).latitude+"],");
                        if (i == len-1) {
                            sb.append("["+TempData.latLngList.get(i).longitude+","+TempData.latLngList.get(i).latitude+"]");
                        }
                    }
                    if (len == 0) {
                        as_xmmzb_etxt.setText("未获得点...");
                    } else {
                        as_xmmzb_etxt.setText("[["+sb.toString()+"]]");
                        as_scmj_etxt.setText(String.format("%.2f",initMap.getArea()));
                    }
                    initMap.reset();

                    break;
            }

        }
    }
    private void resetMethod() {//重置
        TempData.pointList.clear();
        baiduMap.clear();
        txt_showInfo.setText("");
    }
    private boolean isBaiduLatlng = true;
    private void getDotMethod() {//打点
        m_location = initMap.getM_location();
        if (null != m_location) {
            LatLng latLng = initMap.latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude()));
            TempData.pointList.add(latLng);//存百度坐标点
            TempData.latLngList.add(new LatLng(m_location.getLatitude(), m_location.getLongitude()));//存设备经纬度坐标点
            Log.i(TAG, "getDotMethod: ----------------->"+TempData.pointList.toString());
            if (TempData.pointList.size() < 3) {
                initMap.drawDot(latLng);
                showMessage("还需 "+(3 - TempData.pointList.size())+" 个点显示面");
            } else {
                initMap.drawPolyGonByGps(TempData.pointList,isBaiduLatlng);
            }
        } else {
            showMessage("获得经纬度失败...");
        }
    }
    private void moveCurrentPosition() {//移动到当前位置
        //showMessage("move current position");
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(),m_location.getLongitude())),16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }
    private String photoPath="";
    @Override
    public AssartBean getEntityBean(String bhqid, String bhqmc, String jsxmid) {
        AssartBean ab = new AssartBean();
        StringBuilder sb = new StringBuilder();
        if (!as_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(as_hxq_txt.getText().toString().trim() + ":" + as_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!as_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(as_hcq_txt.getText().toString().trim() + ":" + as_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!as_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(as_syq_txt.getText().toString().trim() + ":" + as_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx =sb.toString();//与保护区位置关系
        ab.setBhqid(bhqid);
        ab.setBhqmc(bhqmc);
        ab.setJsxmid(jsxmid);
        ab.setKkqkmc(as_kkqkmc_etxt.getText().toString().trim());
        ab.setJsxmjb(as_jb_etxt.getText().toString().trim());
        ab.setKkmj(as_kkmj_etxt.getText().toString().trim());
        ab.setTrzj("");
        ab.setNcz("");
        ab.setZwzl(as_zwzl_etxt.getText().toString().trim());
        ab.setBjbhqsj(as_Sjqh);
        ab.setIsgz(as_Sfgz);
        ab.setYbhqgx(ybhqwzgx);
        ab.setZgcs(as_lsqk_etxt.getText().toString().trim());
        String centerpoint = as_zxzb_etxt.getText().toString().trim();
        String pointx ="";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->"+centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp+1);
        }
        ab.setCenterpointx(pointx);
        ab.setCenterpointy(pointy);
        ab.setMjzb(as_xmmzb_etxt.getText().toString().trim());
        ab.setTjmj(as_scmj_etxt.getText().toString().trim());
        ab.setHxmj(as_hxq_etxt.getText().toString().trim());
        ab.setHcmj(as_hcq_etxt.getText().toString().trim());
        ab.setSymj(as_syq_etxt.getText().toString());
        ab.setUsername(TempData.yhdh);
        //ab.setUsertel(TempData.usertel);
        ab.setPlaceid(placeid);
        ab.setPhotoPath(photoPath);
        return ab;
    }

    @Override
    public View getView(AssartBean assartBean) {
        if (assartBean != null) {
            as_bhqmc_etxt.setText(assartBean.getBhqmc());
            as_jb_etxt.setText(assartBean.getJsxmjb());
            as_kkqkmc_etxt.setText(assartBean.getKkqkmc());
            as_kkmj_etxt.setText(assartBean.getKkmj());
            as_zwzl_etxt.setText(assartBean.getZwzl());
            if ("".equals(assartBean.getCenterpointx()) && "".equals(assartBean.getCenterpointy())) {
                as_zxzb_etxt.setText("");
            } else {
                as_zxzb_etxt.setText(assartBean.getCenterpointx()+","+assartBean.getCenterpointy());
            }
            as_xmmzb_etxt.setText(assartBean.getMjzb());
            as_scmj_etxt.setText(assartBean.getTjmj());
            if (as_sjqh_radio1.getText().toString().trim().equals(assartBean.getBjbhqsj())) {
                as_sjqh_radio1.setChecked(true);
            } else if (as_sjqh_radio2.getText().toString().trim().equals(assartBean.getBjbhqsj())) {
                as_sjqh_radio2.setChecked(true);
            }
            as_syq_etxt.setText(assartBean.getSymj());
            as_hxq_etxt.setText(assartBean.getHxmj());
            as_hcq_etxt.setText(assartBean.getHcmj());
            if (as_sfgz_radio1.getText().toString().trim().equals(assartBean.getIsgz())) {
                as_sfgz_radio1.setChecked(true);
            } else if (as_sfgz_radio2.getText().toString().trim().equals(assartBean.getIsgz())) {
                as_sfgz_radio2.setChecked(true);
            }
            as_lsqk_etxt.setText(assartBean.getZgcs());
            photoPath = assartBean.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            as_photo_img.setImageBitmap(bitmap);
            placeid = assartBean.getPlaceid();
        }

        return view;
    }
}
