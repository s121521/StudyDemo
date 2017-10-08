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
import com.yaotu.proj.studydemo.bean.tableBean.IndustryBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.TempData;

/**
 * Created by Administrator on 2017/6/30.
 */

public class IndustryOperate implements Operate<IndustryBean> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private View view;
    private EditText in_qysx_etxt;//企业属性
    private EditText in_yxqk_etxt;//运行情况
    private EditText in_bhqmc_etxt;//保护区名称
    private EditText in_jb_etxt;//级别
    private EditText in_qymc_etxt;//企业名称
    private EditText in_gm_etxt;//规模
    private EditText in_zxzb_etxt;//中心坐标
    private EditText in_xmmzb_etxt;//项目面坐标
    private EditText in_scmj_etxt;//实测面积
    private RadioGroup in_sjqh_radioGroup;//成立于保护区始建前后
    private RadioButton in_sjqh_radio1;//前
    private RadioButton in_sjqh_radio2;//后
    private String in_sjqhStr;
    private EditText in_lsyg_etxt;//历史沿革
    private EditText in_hbpfwh_etxt;//环保批复
    private EditText in_sfsbhq_etxt;//是否涉保护区
    private TextView in_qysx_dmz,in_yxqk_dmz,in_sfsbhq_dmz,in_sftghbys_dmz;
    private EditText in_sftghbys_etxt;//是否通过环保验收
    private EditText in_syq_etxt;//实验区
    private EditText in_hcq_etxt;//缓冲区
    private EditText in_hxq_etxt;//核心区
    private EditText in_lsqk_etxt;//整改落实情况
    private TextView in_syq_txt,in_hcq_txt,in_hxq_txt;
    private TextView in_canleImage;
    private ImageView in_photo_img;
    private String bhqid,bhqmc,bhqjb;
    private Intent intent;

    private MapValueType valueType = null;
    private LinearLayout mapLayout,LinearLayoutButtom;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn,reset_btn,getvelue_btn;//地图button
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private TextView txt_showInfo;

    private String placeid = "";

    private InitMap initMap;
    public IndustryOperate(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_industry_enterprise, null);
        mapLayout = (LinearLayout) view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        mapLayout.setVisibility(View.GONE);
        LinearLayoutButtom.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }
    private void initView(){
        in_qysx_etxt = (EditText) view.findViewById(R.id.in_qysx_etxt);
        in_yxqk_etxt = (EditText) view.findViewById(R.id.in_yxqk_etxt);
        in_bhqmc_etxt = (EditText) view.findViewById(R.id.in_bhqmc_etxt);
        in_jb_etxt = (EditText) view.findViewById(R.id.in_jb_etxt);
        in_photo_img = (ImageView) view.findViewById(R.id.in_photo_img);
        in_canleImage = (TextView) view.findViewById(R.id.in_canleImage);
        in_qymc_etxt = (EditText) view.findViewById(R.id.in_qymc_etxt);
        in_gm_etxt = (EditText) view.findViewById(R.id.in_gm_etxt);
        in_zxzb_etxt = (EditText) view.findViewById(R.id.in_zxzb_etxt);
        in_xmmzb_etxt = (EditText) view.findViewById(R.id.in_xmmzb_etxt);
        in_scmj_etxt = (EditText) view.findViewById(R.id.in_scmj_etxt);
        in_sjqh_radioGroup = (RadioGroup) view.findViewById(R.id.in_sjqh_radioGroup);
        in_sjqh_radio1 = (RadioButton) view.findViewById(R.id.in_sjqh_radio1);
        in_sjqh_radio2 = (RadioButton) view.findViewById(R.id.in_sjqh_radio2);
        in_lsyg_etxt = (EditText) view.findViewById(R.id.in_lsyg_etxt);
        in_hbpfwh_etxt = (EditText) view.findViewById(R.id.in_hbpfwh_etxt);
        in_sfsbhq_etxt = (EditText) view.findViewById(R.id.in_sfsbhq_etxt);
        in_sftghbys_etxt = (EditText) view.findViewById(R.id.in_sftghbys_etxt);
        in_syq_etxt = (EditText) view.findViewById(R.id.in_syq_etxt);
        in_hcq_etxt = (EditText) view.findViewById(R.id.in_hcq_etxt);
        in_hxq_etxt = (EditText) view.findViewById(R.id.in_hxq_etxt);
        in_lsqk_etxt = (EditText) view.findViewById(R.id.in_lsqk_etxt);
        in_qysx_dmz = (TextView) view.findViewById(R.id.in_qysx_dmz);
        in_yxqk_dmz = (TextView) view.findViewById(R.id.in_yxqk_dmz);
        in_sfsbhq_dmz = (TextView) view.findViewById(R.id.in_sfsbhq_dmz);
        in_sftghbys_dmz = (TextView) view.findViewById(R.id.in_sftghbys_dmz);
        in_hcq_txt = (TextView) view.findViewById(R.id.in_hcq_txt);
        in_hxq_txt = (TextView) view.findViewById(R.id.in_hxq_txt);
        in_syq_txt = (TextView) view.findViewById(R.id.in_syq_txt);

        mapView = (MapView) view.findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) view.findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) view.findViewById(R.id.reset_btn);//地图重置按钮
        getvelue_btn = (ImageButton) view.findViewById(R.id.getvelue_btn);//获取值
        imgBtn_moveCurrt = (ImageButton) view.findViewById(R.id.imgBtn_moveCurrt);
        txt_showInfo = (TextView) view.findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
    }
    private void initMethod(){
        /*打开企业属性对话框
        * */
        in_qysx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,in_qysx_dmz,in_qysx_etxt,"企业属性","QYSX");
                cd.showQksxDialog();
            }
        });
        /*打开运行情况对话框
        * */
        in_yxqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,in_yxqk_dmz,in_yxqk_etxt,"运行情况","SCQK");
                cd.showQksxDialog();
            }
        });
        /*
        * 是否涉保护区
        * */
        in_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,in_sfsbhq_dmz,in_sfsbhq_etxt,"是否设保护区","ISBHQ");
                cd.showQksxDialog();
            }
        });
        /*是否通过环保验收
        * */
        in_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,in_sftghbys_dmz,in_sftghbys_etxt,"是否通过环保验收","ISHBYS");
                cd.showQksxDialog();
            }
        });
        /*
        * 打开相册或照相机
        * */
        in_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("不能修改相片!");
            }
        });

        /*获取中心坐标
        * */
        in_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
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
        in_xmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                valueType = MapValueType.multiPoint;
                return false;
            }
        });
        /*保护区始建前后
        * */
        in_sjqh_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (in_sjqh_radio1.getId() == checkedId) {
                    in_sjqhStr = in_sjqh_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ----------------始建前后----->"+in_sjqhStr);
                } else if (in_sjqh_radio2.getId() == checkedId) {
                    in_sjqhStr = in_sjqh_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ----------------始建前后----->"+in_sjqhStr);
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

    private void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    in_zxzb_etxt.setText(String.valueOf(m_location.getLongitude())+","+String.valueOf(m_location.getLatitude()));
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
                        in_xmmzb_etxt.setText("未获得点...");
                    } else {
                        in_xmmzb_etxt.setText("[["+sb.toString()+"]]");
                        in_scmj_etxt.setText(String.format("%.2f",initMap.getArea()));
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
    public void showMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    private String photoPath="";
    @Override
    public IndustryBean getEntityBean(String bhqid, String bhqmc, String jsxmid) {
        IndustryBean ib = new IndustryBean();
        StringBuilder sb = new StringBuilder();
        if (!in_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(in_hxq_txt.getText().toString().trim() + ":" + in_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!in_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(in_hcq_txt.getText().toString().trim() + ":" + in_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!in_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(in_syq_txt.getText().toString().trim() + ":" + in_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx =sb.toString();//与保护区位置关系
        ib.setJsxmid(jsxmid);
        ib.setJsxmjb(in_jb_etxt.getText().toString().trim());
        ib.setBhqid(bhqid);
        ib.setJsxmmc(in_qymc_etxt.getText().toString().trim());
        ib.setJsxmgm(in_gm_etxt.getText().toString().trim());
        ib.setTrzj("");
        ib.setNcz("");
        ib.setBjbhqsj(in_sjqhStr);
        ib.setLsyg(in_lsyg_etxt.getText().toString().trim());
        ib.setQysx(in_qysx_dmz.getText().toString().trim());
        ib.setHbpzwh(in_hbpfwh_etxt.getText().toString().trim());
        ib.setIsbhq(in_sfsbhq_dmz.getText().toString().trim());
        ib.setIshbys(in_sftghbys_dmz.getText().toString().trim());
        ib.setYxqk(in_yxqk_dmz.getText().toString().trim());
        ib.setYbhqgx(ybhqwzgx);
        ib.setZgcs(in_lsqk_etxt.getText().toString().trim());
        ib.setVyxqk(in_yxqk_etxt.getText().toString().trim());
        ib.setVisbhq(in_sfsbhq_etxt.getText().toString().trim());
        ib.setVishbys(in_sftghbys_etxt.getText().toString().trim());
        ib.setVqysx(in_qysx_etxt.getText().toString().trim());
        String centerpoint = in_zxzb_etxt.getText().toString().trim();
        String pointx ="";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->"+centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp+1);
        }
        ib.setCenterpointx(pointx);
        ib.setCenterpointy(pointy);
        ib.setMjzb(in_xmmzb_etxt.getText().toString().trim());
        ib.setTjmj(in_scmj_etxt.getText().toString().trim());
        ib.setHxmj(in_hxq_etxt.getText().toString().trim());
        ib.setHcmj(in_hcq_etxt.getText().toString().trim());
        ib.setSymj(in_syq_etxt.getText().toString().trim());
        ib.setUsername(TempData.username);
        //ib.setUsertel(TempData.usertel);
        ib.setPlaceid(placeid);

        ib.setPhotoPath(photoPath);
        ib.setBhqmc(bhqmc);
        return ib;
    }

    @Override
    public View getView(IndustryBean industryBean) {
        if (industryBean != null) {
            in_bhqmc_etxt.setText(industryBean.getBhqmc());
            in_jb_etxt.setText(industryBean.getJsxmjb());
            in_qymc_etxt.setText(industryBean.getJsxmmc());
            in_gm_etxt.setText(industryBean.getJsxmgm());
            if ("".equals(industryBean.getCenterpointx()) && "".equals(industryBean.getCenterpointy())) {
                in_zxzb_etxt.setText("");
            } else {
                in_zxzb_etxt.setText(industryBean.getCenterpointx() + "," + industryBean.getCenterpointy());
            }
            in_xmmzb_etxt.setText(industryBean.getMjzb());
            in_scmj_etxt.setText(industryBean.getTjmj());
            if (in_sjqh_radio1.getText().toString().trim().equals(industryBean.getBjbhqsj())) {
                in_sjqh_radio1.setChecked(true);
            } else if (in_sjqh_radio2.getText().toString().trim().equals(industryBean.getBjbhqsj())) {
                in_sjqh_radio2.setChecked(true);
            }
            in_lsyg_etxt.setText(industryBean.getLsyg());
            in_qysx_etxt.setText(industryBean.getVqysx());
            in_hbpfwh_etxt.setText(industryBean.getHbpzwh());
            in_sfsbhq_etxt.setText(industryBean.getVisbhq());
            in_sftghbys_etxt.setText(industryBean.getVishbys());
            in_yxqk_etxt.setText(industryBean.getVyxqk());
            in_syq_etxt.setText(industryBean.getSymj());
            in_hxq_etxt.setText(industryBean.getHxmj());
            in_hcq_etxt.setText(industryBean.getHcmj());
            in_lsqk_etxt.setText(industryBean.getZgcs());
            photoPath = industryBean.getPhotoPath();
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            in_photo_img.setImageBitmap(bitmap);
            placeid = industryBean.getPlaceid();
        }

        return view;
    }
}
