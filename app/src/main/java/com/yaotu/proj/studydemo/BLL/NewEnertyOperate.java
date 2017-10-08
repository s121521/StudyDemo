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
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.TempData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */

public class NewEnertyOperate implements Operate<NewEnergyBean> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private View view;

    private Intent intent;
    private ImageView ne_photo_img;
    private TextView ne_canleImage;
    private EditText ne_bhqmc_etxt;//保护区名称
    private EditText ne_jb_etxt;//级别
    private EditText ne_qymc_etxt;//企业名称
    private EditText ne_lx_etxt;//建设类型
    private EditText ne_gm_etxt;//规模
    private EditText ne_zxzb_etxt;//中心坐标
    private EditText ne_xmmzb_etxt;//项目面坐标
    private EditText ne_scmj_etxt;//实测面积
    private RadioGroup ne_sjqh_radioGroup;//始建前后
    private RadioButton ne_sjqh_radio1; //前
    private RadioButton ne_sjqh_radio2; //后
    private String ne_sjqhStr;
    private EditText ne_sx_etxt;//企业属性
    private EditText ne_hbpfwh_etxt;//环保批复文号
    private EditText ne_sfsbhq_etxt;//是否涉保护区
    private EditText ne_sftghbys_etxt;//是否通过环保验收
    private EditText ne_ktscqk_etxt;//生产运行情况
    private TextView ne_qysx_dmz,ne_sfsbhq_dmz,ne_sftghbys_dmz,ne_scqk_dmz;
    private EditText ne_syq_etxt;//实验区
    private EditText ne_hcq_etxt;//缓冲区
    private EditText ne_hxq_etxt;//核心区
    private EditText ne_lsqk_etxt;//整改落实情况
    private TextView ne_hxq_txt,ne_hcq_txt,ne_syq_txt;
    private MapValueType valueType = null;
    private LinearLayout mapLayout,LinearLayoutButtom;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn,reset_btn,getvelue_btn;//地图button
    private TextView txt_showInfo;
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private InitMap initMap;
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    private String placeid = "";

    public NewEnertyOperate(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_new_energy_proj, null);
        mapLayout = (LinearLayout) view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        mapLayout.setVisibility(View.GONE);
        LinearLayoutButtom.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }
    private void initView(){
        ne_photo_img = (ImageView) view.findViewById(R.id.ne_photo_img);
        ne_canleImage = (TextView) view.findViewById(R.id.ne_canleImage);
        ne_bhqmc_etxt = (EditText) view.findViewById(R.id.ne_bhqmc_etxt);
        ne_jb_etxt = (EditText) view.findViewById(R.id.ne_jb_etxt);
        ne_qymc_etxt = (EditText) view.findViewById(R.id.ne_qymc_etxt);
        ne_lx_etxt = (EditText) view.findViewById(R.id.ne_lx_etxt);
        ne_gm_etxt = (EditText) view.findViewById(R.id.ne_gm_etxt);
        ne_zxzb_etxt = (EditText) view.findViewById(R.id.ne_zxzb_etxt);
        ne_xmmzb_etxt = (EditText) view.findViewById(R.id.ne_xmmzb_etxt);
        ne_scmj_etxt = (EditText) view.findViewById(R.id.ne_scmj_etxt);
        ne_sjqh_radioGroup = (RadioGroup) view.findViewById(R.id.ne_sjqh_radioGroup);
        ne_sjqh_radio1 = (RadioButton) view.findViewById(R.id.ne_sjqh_radio1);
        ne_sjqh_radio2 = (RadioButton) view.findViewById(R.id.ne_sjqh_radio2);
        ne_sx_etxt = (EditText) view.findViewById(R.id.ne_sx_etxt);
        ne_hbpfwh_etxt = (EditText) view.findViewById(R.id.ne_hbpfwh_etxt);
        ne_sfsbhq_etxt = (EditText) view.findViewById(R.id.ne_sfsbhq_etxt);
        ne_sftghbys_etxt = (EditText) view.findViewById(R.id.ne_sftghbys_etxt);
        ne_ktscqk_etxt = (EditText) view.findViewById(R.id.ne_ktscqk_etxt);
        ne_syq_etxt = (EditText) view.findViewById(R.id.ne_syq_etxt);
        ne_hcq_etxt = (EditText) view.findViewById(R.id.ne_hcq_etxt);
        ne_hxq_etxt = (EditText) view.findViewById(R.id.ne_hxq_etxt);
        ne_lsqk_etxt = (EditText) view.findViewById(R.id.ne_lsqk_etxt);
        ne_qysx_dmz = (TextView) view.findViewById(R.id.ne_qysx_dmz);
        ne_sfsbhq_dmz = (TextView) view.findViewById(R.id.ne_sfsbhq_dmz);
        ne_sftghbys_dmz = (TextView) view.findViewById(R.id.ne_sftghbys_dmz);
        ne_scqk_dmz = (TextView) view.findViewById(R.id.ne_scqk_dmz);
        ne_hxq_txt = (TextView) view.findViewById(R.id.ne_hxq_txt);
        ne_hcq_txt = (TextView) view.findViewById(R.id.ne_hcq_txt);
        ne_syq_txt = (TextView) view.findViewById(R.id.ne_syq_txt);


        mapView = (MapView) view.findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) view.findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) view.findViewById(R.id.reset_btn);//地图重置按钮
        getvelue_btn = (ImageButton) view.findViewById(R.id.getvelue_btn);//获取值
        imgBtn_moveCurrt = (ImageButton) view.findViewById(R.id.imgBtn_moveCurrt);
        txt_showInfo = (TextView)view.findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
    }
    private void initMethod(){

        /*始建前后
        * */
        ne_sjqh_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (ne_sjqh_radio1.getId() == checkedId) {
                    ne_sjqhStr = ne_sjqh_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ------------始建前后----------->"+ne_sjqhStr);
                } else if (ne_sjqh_radio2.getId() == checkedId) {
                    ne_sjqhStr = ne_sjqh_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ------------始建前后----------->"+ne_sjqhStr);
                }
            }
        });
        /*
        * 企业属性
        * */
        ne_sx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,ne_qysx_dmz,ne_sx_etxt,"企业属性","QYSX");
                cd.showQksxDialog();
            }
        });
        /*是否涉保护区
        * */
        ne_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,ne_sfsbhq_dmz,ne_sfsbhq_etxt,"是否涉保护区","ISBHQ");
                cd.showQksxDialog();
            }
        });
        /*是否通过环保验收
        * */
        ne_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,ne_sftghbys_dmz,ne_sftghbys_etxt,"是否通过环保验收","ISHBYS");
                cd.showQksxDialog();
            }
        });
        /*生产运行情况
        * */
        ne_ktscqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,ne_scqk_dmz,ne_ktscqk_etxt,"生产运行情况","SCQK");
                cd.showQksxDialog();
            }
        });
        /*获取中心坐标
        * */
        ne_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                dot_btn.setVisibility(View.GONE);
                reset_btn.setVisibility(View.GONE);
                valueType = MapValueType.centerCoordinate;
                return false;
            }
        });
        /*项目面坐标
        * */
        ne_xmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                valueType = MapValueType.multiPoint;
                return false;
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
    private String photoPath="";
    @Override
    public NewEnergyBean getEntityBean(String bhqid, String bhqmc, String jsxmid) {
        NewEnergyBean bean = new NewEnergyBean();
        StringBuilder sb = new StringBuilder();
        if (!ne_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(ne_hxq_txt.getText().toString().trim() + ":" + ne_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!ne_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(ne_hcq_txt.getText().toString().trim() + ":" + ne_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!ne_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(ne_syq_txt.getText().toString().trim() + ":" + ne_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx =sb.toString();//与保护区位置关系
        bean.setBhqid(bhqid);
        bean.setBhqmc(bhqmc);
        bean.setJsxmjb(ne_jb_etxt.getText().toString().trim());
        bean.setJsxmid(jsxmid);
        bean.setJsxmmc(ne_qymc_etxt.getText().toString().trim());
        bean.setJsxmgm(ne_gm_etxt.getText().toString().trim());
        bean.setJsxmlx(ne_lx_etxt.getText().toString().trim());
        bean.setTrzj("");
        bean.setNcz("");
        bean.setBjbhqsj(ne_sjqhStr);
        bean.setQysx(ne_qysx_dmz.getText().toString().trim());//ne_sx_etxt.getText().toString().trim()
        bean.setHbpzwh(ne_hbpfwh_etxt.getText().toString().trim());

        bean.setIsbhq(ne_sfsbhq_dmz.getText().toString().trim());//ne_sfsbhq_etxt.getText().toString().trim()
        bean.setIshbys(ne_sftghbys_dmz.getText().toString().trim());//ne_sftghbys_etxt.getText().toString().trim()
        bean.setScqk(ne_scqk_dmz.getText().toString().trim());//ne_ktscqk_etxt.getText().toString().trim()
        bean.setYbhqgx(ybhqwzgx);
        bean.setZgcs(ne_lsqk_etxt.getText().toString().trim());
        String centerpoint = ne_zxzb_etxt.getText().toString().trim();
        String pointx ="";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->"+centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp+1);
        }
        bean.setCenterpointx(pointx);
        bean.setCenterpointy(pointy);
        bean.setMjzb(ne_xmmzb_etxt.getText().toString().trim());
        bean.setTjmj(ne_scmj_etxt.getText().toString().trim());
        bean.setHxmj(ne_hxq_etxt.getText().toString().trim());
        bean.setHcmj(ne_hcq_etxt.getText().toString().trim());
        bean.setSymj(ne_syq_etxt.getText().toString().trim());

        bean.setVqyxs(ne_sx_etxt.getText().toString().trim());
        bean.setVisbhq(ne_sfsbhq_etxt.getText().toString().trim());
        bean.setVishbys(ne_sftghbys_etxt.getText().toString().trim());
        bean.setVscqk(ne_ktscqk_etxt.getText().toString().trim());
        bean.setPhotoPath(photoPath);
        bean.setUsername(TempData.username);
       // bean.setUsertel(TempData.usertel);
        bean.setPlaceid(placeid);
        return bean;
    }

    @Override
    public View getView(NewEnergyBean newEnergyBean) {
        if (newEnergyBean != null) {
            ne_bhqmc_etxt.setText(newEnergyBean.getBhqmc());
            ne_jb_etxt.setText(newEnergyBean.getJsxmjb());
            ne_qymc_etxt.setText(newEnergyBean.getJsxmmc());
            ne_lx_etxt.setText(newEnergyBean.getJsxmlx());
            ne_gm_etxt.setText(newEnergyBean.getJsxmgm());
            if (newEnergyBean.getCenterpointx().equals("") && newEnergyBean.getCenterpointy().equals("")) {
                ne_zxzb_etxt.setText("");
            } else {
                ne_zxzb_etxt.setText(newEnergyBean.getCenterpointx()+","+newEnergyBean.getCenterpointy());
            }
            ne_xmmzb_etxt.setText(newEnergyBean.getMjzb());
            ne_scmj_etxt.setText(newEnergyBean.getTjmj());
            if (ne_sjqh_radio1.getText().toString().trim().equals(newEnergyBean.getBjbhqsj())) {
                ne_sjqh_radio1.setChecked(true);
            } else if (ne_sjqh_radio2.getText().toString().trim().equals(newEnergyBean.getBjbhqsj())) {
                ne_sjqh_radio2.setChecked(true);
            }
            ne_sx_etxt.setText(newEnergyBean.getVqyxs());
            ne_hbpfwh_etxt.setText(newEnergyBean.getHbpzwh());
            ne_sfsbhq_etxt.setText(newEnergyBean.getVisbhq());
            ne_sftghbys_etxt.setText(newEnergyBean.getVishbys());
            ne_ktscqk_etxt.setText(newEnergyBean.getVscqk());
            ne_syq_etxt.setText(newEnergyBean.getSymj());
            ne_hcq_etxt.setText(newEnergyBean.getHcmj());
            ne_hxq_etxt.setText(newEnergyBean.getHxmj());
            ne_lsqk_etxt.setText(newEnergyBean.getZgcs());
            photoPath = newEnergyBean.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            ne_photo_img.setImageBitmap(bitmap);
            placeid = newEnergyBean.getPlaceid();
        }
        return view;
    }

    //=============================================================================================
    private void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    ne_zxzb_etxt.setText(String.valueOf(m_location.getLongitude())+","+String.valueOf(m_location.getLatitude()));
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
                        ne_xmmzb_etxt.setText("未获得点...");
                    } else {
                        ne_xmmzb_etxt.setText("[["+sb.toString()+"]]");
                        ne_scmj_etxt.setText(String.format("%.2f",initMap.getArea()));
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

}
