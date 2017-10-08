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
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.TempData;

/**
 * Created by Administrator on 2017/6/30.
 */

public class DevelopOperate implements Operate<DevelopConstructionBean> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private View view;
    private MapValueType valueType = null;
    private ImageView de_photo_img;
    private TextView de_canleImage;
    private EditText de_bhqmc_etxt;//保护区名称
    private EditText de_jb_etxt;//保护区级别
    private EditText de_hdmc_etxt;//活动名称
    private EditText de_lx_etxt;//类型
    private EditText de_gm_etxt;//规模
    private EditText de_zxzb_etxt;//中心坐标
    private EditText de_xmmzb_etxt;//项目面坐标
    private EditText de_scmj_etxt;//实测面积
    private RadioGroup de_sjqh_groupRadio;//始建时间前后
    private RadioButton de_sjqh_radio1;//前
    private RadioButton de_sjqh_radio2;//后
    private String de_sjqhStr;
    private EditText de_hbpfwh_etxt;//环保批复文号

    private EditText de_sfsbhq_etxt;//是否涉保护区
    private TextView de_sfsbhq_dmz,de_sftghbys_dmz,de_scqk_dmz;
    private EditText de_sftghbys_etxt;//是否通过环保验收
    private EditText de_scqk_etxt;//生产情况
    private EditText de_syq_etxt;//实验区面积
    private EditText de_hcq_etxt;//缓冲区面积
    private EditText de_hxq_etxt;//核心区面积
    private EditText de_lsqk_etxt;//整改落实情况
    private String bhqid,bhqmc,bhqjb;
    private TextView de_hxq_txt,de_hcq_txt,de_syq_txt;
    private LinearLayout mapLayout,LinearLayoutButtom;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn;
    private ImageButton reset_btn,getvelue_btn;//地图button
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private TextView txt_showInfo;
    private String placeid = "";
    private InitMap initMap;
    public DevelopOperate(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_develop_construction, null);
        mapLayout = (LinearLayout) view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        LinearLayoutButtom.setVisibility(View.GONE);
        mapLayout.setVisibility(View.GONE);

        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }
    private void initView() {
        de_photo_img = (ImageView) view.findViewById(R.id.de_photo_img);
        de_canleImage = (TextView) view.findViewById(R.id.de_canleImage);
        de_bhqmc_etxt = (EditText) view.findViewById(R.id.de_bhqmc_etxt);
        de_jb_etxt = (EditText) view.findViewById(R.id.de_jb_etxt);
        de_hdmc_etxt = (EditText) view.findViewById(R.id.de_hdmc_etxt);
        de_lx_etxt = (EditText) view.findViewById(R.id.de_lx_etxt);
        de_gm_etxt = (EditText) view.findViewById(R.id.de_gm_etxt);
        de_zxzb_etxt = (EditText) view.findViewById(R.id.de_zxzb_etxt);
        de_xmmzb_etxt = (EditText) view.findViewById(R.id.de_xmmzb_etxt);
        de_scmj_etxt = (EditText) view.findViewById(R.id.de_scmj_etxt);
        de_sjqh_groupRadio = (RadioGroup) view.findViewById(R.id.de_sjqh_groupRadio);
        de_sjqh_radio1 = (RadioButton) view.findViewById(R.id.de_sjqh_radio1);
        de_sjqh_radio2 = (RadioButton) view.findViewById(R.id.de_sjqh_radio2);
        de_hbpfwh_etxt = (EditText) view.findViewById(R.id.de_hbpfwh_etxt);
        de_sfsbhq_dmz = (TextView) view.findViewById(R.id.de_sfsbhq_dmz);
        de_sfsbhq_etxt = (EditText) view.findViewById(R.id.de_sfsbhq_etxt);
        de_sftghbys_dmz = (TextView) view.findViewById(R.id.de_sftghbys_dmz);
        de_scqk_dmz = (TextView) view.findViewById(R.id.de_scqk_dmz);
        de_sftghbys_etxt = (EditText) view.findViewById(R.id.de_sftghbys_etxt);
        de_scqk_etxt = (EditText) view.findViewById(R.id.de_scqk_etxt);
        de_syq_etxt = (EditText) view.findViewById(R.id.de_syq_etxt);
        de_hcq_etxt = (EditText) view.findViewById(R.id.de_hcq_etxt);
        de_hxq_etxt = (EditText) view.findViewById(R.id.de_hxq_etxt);
        de_lsqk_etxt = (EditText) view.findViewById(R.id.de_lsqk_etxt);
        de_hxq_txt = (TextView) view.findViewById(R.id.de_hxq_txt);
        de_hcq_txt = (TextView) view.findViewById(R.id.de_hcq_txt);
        de_syq_txt = (TextView) view.findViewById(R.id.de_syq_txt);

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
        de_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("不可以修改相片!");
            }
        });

        /*长按获取中心坐标
        * */
        de_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
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
        de_xmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                valueType = MapValueType.multiPoint;
                return false;
            }
        });
        /*始建时间前后
        * */
        de_sjqh_groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (de_sjqh_radio1.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ----------始建时间前后----------->" + de_sjqh_radio1.getText().toString().trim());
                    de_sjqhStr = de_sjqh_radio1.getText().toString().trim();
                } else if (de_sjqh_radio2.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------------始建始建前后--------->" + de_sjqh_radio2.getText().toString().trim());
                    de_sjqhStr = de_sjqh_radio2.getText().toString().trim();
                }
            }
        });

        de_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(context,de_sfsbhq_dmz,de_sfsbhq_etxt, "是否涉保护区", "ISBHQ");
                dialog.showQksxDialog();
            }
        });

        de_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(context,de_sftghbys_dmz,de_sftghbys_etxt, "是否通过环保验收", "ISHBYS");
                dialog.showQksxDialog();
            }
        });
        /*
        * 生产运行情况
        * */
        de_scqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(context,de_scqk_dmz,de_scqk_etxt, "生产运行情况", "SCQK");
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
    private void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    de_zxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
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
                        de_xmmzb_etxt.setText("未获得点...");
                    } else {
                        de_xmmzb_etxt.setText("[[" + sb.toString() + "]]");
                        de_scmj_etxt.setText(String.format("%.2f", initMap.getArea()));
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
        //showMessage("move current position");
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
    }
    public void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private String photoPath = "";
    @Override
    public DevelopConstructionBean getEntityBean(String bhqid, String bhqmc, String jsxmid) {
        DevelopConstructionBean bean = new DevelopConstructionBean();
        StringBuilder sb = new StringBuilder();
        if (!de_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(de_hxq_txt.getText().toString().trim() + ":" + de_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!de_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(de_hcq_txt.getText().toString().trim() + ":" + de_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!de_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(de_syq_txt.getText().toString().trim() + ":" + de_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx =sb.toString();//与保护区位置关系
        bean.setBhqid(bhqid);
        bean.setBhqmc(bhqmc);
        bean.setJsxmid(jsxmid);
        bean.setJsxmjb(de_jb_etxt.getText().toString().trim());
        bean.setKfjsmc(de_hdmc_etxt.getText().toString().trim());
        bean.setKkmj("");
        bean.setTrzj("");
        bean.setNcz("");

        bean.setGm(de_gm_etxt.getText().toString().trim());
        bean.setHdlx(de_lx_etxt.getText().toString().trim());
        bean.setBjbhqsj(de_sjqhStr);
        bean.setHbpzwh(de_hbpfwh_etxt.getText().toString().trim());
        bean.setIsbhq(de_sfsbhq_dmz.getText().toString().trim());//de_sfsbhq_etxt.getText().toString().trim()
        bean.setIshbys(de_sftghbys_dmz.getText().toString().trim());//de_sftghbys_etxt.getText().toString().trim()
        bean.setYswjbh("");
        bean.setScqk(de_scqk_dmz.getText().toString().trim());//de_scqk_etxt.getText().toString().trim()
        bean.setYbhqgx(ybhqwzgx);
        bean.setZgcs(de_lsqk_etxt.getText().toString().trim());
        String centerpoint = de_zxzb_etxt.getText().toString().trim();
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
        bean.setMjzb(de_xmmzb_etxt.getText().toString().trim());
        bean.setTjmj(de_scmj_etxt.getText().toString().trim());
        bean.setHxmj(de_hxq_etxt.getText().toString().trim());
        bean.setHcmj(de_hcq_etxt.getText().toString().trim());
        bean.setSymj(de_syq_etxt.getText().toString().trim());

        bean.setVisbhq(de_sfsbhq_etxt.getText().toString().trim());
        bean.setVishbys(de_sftghbys_etxt.getText().toString().trim());
        bean.setVscqk(de_scqk_etxt.getText().toString().trim());

        bean.setUsername(TempData.username);
        //bean.setUsertel(TempData.usertel);
        bean.setPlaceid(placeid);

        bean.setPhotoPath(photoPath);
        return bean;
    }

    @Override
    public View getView(DevelopConstructionBean bean) {
        if (bean != null) {
            de_bhqmc_etxt.setText(bean.getBhqmc());
            de_jb_etxt.setText(bean.getJsxmjb());
            de_hdmc_etxt.setText(bean.getKfjsmc());
            de_lx_etxt.setText(bean.getHdlx());
            de_gm_etxt.setText(bean.getGm());
            if ("".equals(bean.getCenterpointx()) && "".equals(bean.getCenterpointy())) {
                de_zxzb_etxt.setText("");
            } else {
                de_zxzb_etxt.setText(bean.getCenterpointx()+","+bean.getCenterpointy());
            }
            de_xmmzb_etxt.setText(bean.getMjzb());
            de_scmj_etxt.setText(bean.getTjmj());
            if (de_sjqh_radio1.getText().toString().trim().equals(bean.getBjbhqsj())) {
                de_sjqh_radio1.setChecked(true);
            } else if (de_sjqh_radio2.getText().toString().trim().equals(bean.getBjbhqsj())) {
                de_sjqh_radio2.setChecked(true);
            }
            de_hbpfwh_etxt.setText(bean.getHbpzwh());
            de_sfsbhq_etxt.setText(bean.getVisbhq());
            de_sftghbys_etxt.setText(bean.getVishbys());
            de_scqk_etxt.setText(bean.getVscqk());
            de_syq_etxt.setText(bean.getSymj());
            de_hcq_etxt.setText(bean.getHcmj());
            de_hxq_etxt.setText(bean.getHxmj());
            de_lsqk_etxt.setText(bean.getZgcs());
            photoPath = bean.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            de_photo_img.setImageBitmap(bitmap);
            placeid = bean.getPlaceid();
        }

        return view;
    }
}
