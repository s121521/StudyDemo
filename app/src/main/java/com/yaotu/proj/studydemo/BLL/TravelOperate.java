package com.yaotu.proj.studydemo.BLL;

import android.content.Context;
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
import com.yaotu.proj.studydemo.BLL.Operate;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.tableBean.TravelBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.TempData;

/**
 * Created by Administrator on 2017/6/30.
 */

public class TravelOperate implements Operate<TravelBean> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private View view;
    private ImageView tr_photo_img;
    private TextView tr_canleImage;
    private EditText tr_bhqmc_etxt;//保护区名称
    private EditText tr_jb_etxt;//级别
    private EditText tr_qymc_etxt;//企业名称
    private EditText tr_gm_etxt;//规模
    private EditText tr_zxzb_etxt;//中心坐标
    private EditText tr_xmmzb_etxt;//项目面坐标
    private EditText tr_scmj_etxt;//实测面积
    private RadioGroup tr_sjqh_radioGroup;//始建前后
    private RadioButton tr_sjqh_radio1;//前
    private RadioButton tr_sjqh_radio2;//后
    private String tr_sjqhStr;
    private EditText tr_kqslqk_etxt;//历史沿革
    private EditText tr_qysx_etxt;//企业属性
    private EditText tr_hbpfwh_etxt;//环保批复文号
    private EditText tr_sfsbhq_etxt;//是否涉保护区
    private EditText tr_sftghbys_etxt;//是否通过环保验收

    private EditText tr_yyqk_etxt;//营业情况
    private TextView tr_qysx_dmz,tr_sfsbhq_dmz,tr_sftghbys_dmz,tr_yyqk_dmz;
    private EditText tr_syq_etxt;//实验区
    private EditText tr_hcq_etxt;//缓冲区
    private EditText tr_hxq_etxt;//核心区
    private EditText tr_lsqk_etxt;//整改落实情况
    private TextView tr_hxq_txt,tr_hcq_txt,tr_syq_txt;

    private MapValueType valueType = null;
    private LinearLayout mapLayout,LinearLayoutButtom;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn, reset_btn,getvelue_btn;//地图button
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private TextView txt_showInfo;
    private String placeid = "";
    private InitMap initMap;
    public TravelOperate(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_travel_develop_enterprise, null);
        mapLayout = (LinearLayout) view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        mapLayout.setVisibility(View.GONE);
        LinearLayoutButtom.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }
    private void initView() {
        tr_photo_img = (ImageView) view.findViewById(R.id.tr_photo_img);
        tr_canleImage = (TextView) view.findViewById(R.id.tr_canleImage);
        tr_bhqmc_etxt = (EditText) view.findViewById(R.id.tr_bhqmc_etxt);
        tr_jb_etxt = (EditText) view.findViewById(R.id.tr_jb_etxt);
        tr_qymc_etxt = (EditText) view.findViewById(R.id.tr_qymc_etxt);
        tr_gm_etxt = (EditText) view.findViewById(R.id.tr_gm_etxt);
        tr_zxzb_etxt = (EditText) view.findViewById(R.id.tr_zxzb_etxt);
        tr_xmmzb_etxt = (EditText) view.findViewById(R.id.tr_xmmzb_etxt);
        tr_scmj_etxt = (EditText) view.findViewById(R.id.tr_scmj_etxt);
        tr_sjqh_radioGroup = (RadioGroup) view.findViewById(R.id.tr_sjqh_radioGroup);
        tr_sjqh_radio1 = (RadioButton) view.findViewById(R.id.tr_sjqh_radio1);
        tr_sjqh_radio2 = (RadioButton) view.findViewById(R.id.tr_sjqh_radio2);
        tr_kqslqk_etxt = (EditText) view.findViewById(R.id.tr_kqslqk_etxt);
        tr_qysx_etxt = (EditText) view.findViewById(R.id.tr_qysx_etxt);
        tr_hbpfwh_etxt = (EditText) view.findViewById(R.id.tr_hbpfwh_etxt);
        tr_sfsbhq_etxt = (EditText) view.findViewById(R.id.tr_sfsbhq_etxt);
        tr_sftghbys_etxt = (EditText) view.findViewById(R.id.tr_sftghbys_etxt);
        tr_yyqk_etxt = (EditText) view.findViewById(R.id.tr_yyqk_etxt);
        tr_syq_etxt = (EditText) view.findViewById(R.id.tr_syq_etxt);
        tr_hcq_etxt = (EditText) view.findViewById(R.id.tr_hcq_etxt);
        tr_hxq_etxt = (EditText) view.findViewById(R.id.tr_hxq_etxt);
        tr_lsqk_etxt = (EditText) view.findViewById(R.id.tr_lsqk_etxt);
        tr_qysx_dmz = (TextView) view.findViewById(R.id.tr_qysx_dmz);
        tr_sfsbhq_dmz = (TextView) view.findViewById(R.id.tr_sfsbhq_dmz);
        tr_sftghbys_dmz = (TextView) view.findViewById(R.id.tr_sftghbys_dmz);
        tr_yyqk_dmz = (TextView) view.findViewById(R.id.tr_yyqk_dmz);
        tr_hxq_txt = (TextView) view.findViewById(R.id.tr_hxq_txt);
        tr_hcq_txt = (TextView) view.findViewById(R.id.tr_hcq_txt);
        tr_syq_txt = (TextView) view.findViewById(R.id.tr_syq_txt);

        mapView = (MapView) view.findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) view.findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) view.findViewById(R.id.reset_btn);//地图重置按钮
        getvelue_btn = (ImageButton) view.findViewById(R.id.getvelue_btn);//获取值
        imgBtn_moveCurrt = (ImageButton) view.findViewById(R.id.imgBtn_moveCurrt);
        txt_showInfo = (TextView) view.findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
    }

    private void initMethod() {
         /*
        * 打开相册或照相机
        * */
        tr_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("不可以修改相片!");
            }
        });

        /*始建前后
        * */
        tr_sjqh_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (tr_sjqh_radio1.getId() == checkedId) {
                    tr_sjqhStr = tr_sjqh_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ------------始建前后----------->" + tr_sjqhStr);
                } else if (tr_sjqh_radio2.getId() == checkedId) {
                    tr_sjqhStr = tr_sjqh_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ------------始建前后----------->" + tr_sjqhStr);
                }
            }
        });
        /* 企业属性
        * */
        tr_qysx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,tr_qysx_dmz,tr_qysx_etxt, "企业属性", "QYSX");
                cd.showQksxDialog();
            }
        });
        /*是否涉保护区
        * */
        tr_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,tr_sfsbhq_dmz,tr_sfsbhq_etxt, "是否涉保护区", "ISBHQ");
                cd.showQksxDialog();
            }
        });
        /*是否通过环保验收
        * */
        tr_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,tr_sftghbys_dmz,tr_sftghbys_etxt, "是否通过环保验收", "ISHBYS");
                cd.showQksxDialog();
            }
        });
        /*营业情况
        * */
        tr_yyqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,tr_yyqk_dmz,tr_yyqk_etxt, "营业情况", "SCQK");
                cd.showQksxDialog();
            }
        });

        /*获取中心坐标
        * */
        tr_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                dot_btn.setVisibility(View.GONE);
                reset_btn.setVisibility(View.GONE);
                valueType = MapValueType.centerCoordinate;
                return false;
            }
        });
        /*获取项目面坐标
        * */
        tr_xmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
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
    //===============================map===========================================================
    private void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    tr_zxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
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
                        tr_xmmzb_etxt.setText("未获得点...");
                    } else {
                        tr_xmmzb_etxt.setText("[[" + sb.toString() + "]]");
                        tr_scmj_etxt.setText(String.format("%.2f", initMap.getArea()));
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
            Log.i(TAG, "getDotMethod: ----------------->" + TempData.pointList.toString());
            if (TempData.pointList.size() < 3) {
                initMap.drawDot(latLng);
                showMessage("还需 " + (3 - TempData.pointList.size()) + " 个点显示面");
            } else {
                initMap.drawPolyGonByGps(TempData.pointList, isBaiduLatlng);
            }
        } else {
            showMessage("获得经纬度失败...");
        }
    }

    private void moveCurrentPosition() {//移动到当前位置
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
    }

    private String photoPath = "";
    @Override
    public TravelBean getEntityBean(String bhqid, String bhqmc, String jsxmid) {
        TravelBean bean = new TravelBean();
        StringBuilder sb = new StringBuilder();
        if (!tr_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(tr_hxq_txt.getText().toString().trim() + ":" + tr_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!tr_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(tr_hcq_txt.getText().toString().trim() + ":" + tr_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!tr_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(tr_syq_txt.getText().toString().trim() + ":" + tr_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx =sb.toString();//与保护区位置关系
        bean.setBhqid(bhqid);
        bean.setBhqmc(bhqmc);
        bean.setJsxmid(jsxmid);
        bean.setJsxmmc(tr_qymc_etxt.getText().toString().trim());
        bean.setJsxmjb(tr_jb_etxt.getText().toString().trim());
        bean.setJsxmgm(tr_gm_etxt.getText().toString().trim());
        bean.setTrzj("");
        bean.setNcz("");
        bean.setBjbhqsj(tr_sjqhStr);
        bean.setLsyg(tr_kqslqk_etxt.getText().toString().trim());
        bean.setQysx(tr_qysx_dmz.getText().toString().trim());//tr_qysx_etxt.getText().toString().trim()
        bean.setHbpzwh(tr_hbpfwh_etxt.getText().toString().trim());
        bean.setIsbhq(tr_sfsbhq_dmz.getText().toString().trim());//tr_sfsbhq_etxt.getText().toString().trim()
        bean.setIshbys(tr_sftghbys_dmz.getText().toString().trim());//tr_sftghbys_etxt.getText().toString().trim()
        bean.setYyqk(tr_yyqk_dmz.getText().toString().trim());//tr_yyqk_etxt.getText().toString().trim()
        bean.setYbhqgx(ybhqwzgx);
        bean.setZgcs(tr_lsqk_etxt.getText().toString().trim());
        String centerpoint = tr_zxzb_etxt.getText().toString().trim();
        String pointx = "";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->" + centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp + 1);
        }
        bean.setCenterpointx(pointx);
        bean.setCenterpointy(pointy);
        bean.setMjzb(tr_xmmzb_etxt.getText().toString().trim());
        bean.setTjmj(tr_scmj_etxt.getText().toString().trim());
        bean.setHxmj(tr_hxq_etxt.getText().toString().trim());
        bean.setHcmj(tr_hcq_etxt.getText().toString().trim());
        bean.setSymj(tr_syq_etxt.getText().toString().trim());

        bean.setVqysx(tr_qysx_etxt.getText().toString().trim());
        bean.setVisbhq(tr_sfsbhq_etxt.getText().toString().trim());
        bean.setVishbys(tr_sftghbys_etxt.getText().toString().trim());
        bean.setVyyqk(tr_yyqk_etxt.getText().toString().trim());
        bean.setPhotoPath(photoPath);
        bean.setUsername(TempData.username);
        //bean.setUsertel(TempData.usertel);
        bean.setPlaceid(placeid);
        Log.i(TAG, "getEntityBean: ---------"+bean.getSymj());
        return bean;
    }

    @Override
    public View getView(TravelBean travelBean) {
        if (travelBean != null) {
            Log.i(TAG, "getView: travelBean------------>"+travelBean.getJsxmmc()+"------"+travelBean.getSymj());
            tr_bhqmc_etxt.setText(travelBean.getBhqmc());
            tr_jb_etxt.setText(travelBean.getJsxmjb());
            tr_qymc_etxt.setText(travelBean.getJsxmmc());
            tr_gm_etxt.setText(travelBean.getJsxmgm());
            if ("".equals(travelBean.getCenterpointx()) && "".equals(travelBean.getCenterpointy())) {
                tr_zxzb_etxt.setText("");
            } else {
                tr_zxzb_etxt.setText(travelBean.getCenterpointx()+","+travelBean.getCenterpointy());
            }
            tr_xmmzb_etxt.setText(travelBean.getMjzb());
            tr_scmj_etxt.setText(travelBean.getTjmj());
            if (tr_sjqh_radio1.getText().toString().trim().equals(travelBean.getBjbhqsj())) {
                tr_sjqh_radio1.setChecked(true);
            } else if (tr_sjqh_radio2.getText().toString().trim().equals(travelBean.getBjbhqsj())) {
                tr_sjqh_radio2.setChecked(true);
            }
            tr_kqslqk_etxt.setText(travelBean.getLsyg());
            tr_qysx_etxt.setText(travelBean.getVqysx());
            tr_hbpfwh_etxt.setText(travelBean.getHbpzwh());
            tr_sfsbhq_etxt.setText(travelBean.getVisbhq());
            tr_sftghbys_etxt.setText(travelBean.getVishbys());
            tr_yyqk_etxt.setText(travelBean.getVyyqk());
            tr_syq_etxt.setText(travelBean.getSymj());
            tr_hcq_etxt.setText(travelBean.getHcmj());
            tr_hxq_etxt.setText(travelBean.getHxmj());
            tr_lsqk_etxt.setText(travelBean.getZgcs());
            photoPath = travelBean.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            tr_photo_img.setImageBitmap(bitmap);

            photoPath = travelBean.getPhotoPath();
            placeid = travelBean.getPlaceid();
        }

        return view;
    }


    private void showMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
