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
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.tableBean.kczyBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.DateDialog;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.TempData;

/**
 * Created by Administrator on 2017/6/30.
 */

public class KczyOperate implements Operate<kczyBean> {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private View view;
    private ImageView kc_photo_img;
    private TextView kc_canleImage;
    private EditText kc_bhqmc_etxt;//保护区名称
    private EditText kc_jb_etxt;//级别
    private EditText kc_qymc_etxt;//企业名称
    private EditText kc_gm_etxt;//规模
    private EditText kc_fzjg_etxt;//发证机关
    private EditText kc_zjyxqb_etxt;//证件有效期限(开始)
    private EditText kc_zjyxqe_etxt;//证件有效期限(结束)
    private EditText kc_zxzb_etxt;//中心坐标
    private EditText kc_xmmzb_etxt;//项目面坐标
    private EditText kc_scmj_etxt;//实测面积
    private RadioGroup kc_sjqh_radioGroup;//始建前后
    private RadioButton kc_sjqh_radio1;//前
    private RadioButton kc_sjqh_radio2;//后
    private String kc_sjqhStr;
    private EditText kc_kqslqk_etxt;//矿权设立情况
    private EditText kc_kcfs_etxt;//开采方式
    private TextView kc_kcfs_dmz,kc_sfsbhq_dmz,kc_sftghbys_dmz,kc_scqk_dmz;
    private EditText kc_kqsx_etxt;//矿权属性
    private EditText kc_hbpfwh_etxt;//环保批复文号
    private EditText kc_sfsbhq_etxt;//是否涉保护区
    private EditText kc_sftghbys_etxt;//是否通过环保验收
    private EditText kc_scqk_etxt;//生产情况
    private EditText kc_syq_etxt;//实验区
    private EditText kc_hcq_etxt;//缓冲区
    private EditText kc_hxq_etxt;//核心区
    private EditText kc_lsqk_etxt;//整改落实情况

    private TextView kc_hxq_txt,kc_hcq_txt,kc_syq_txt;

    private MapValueType valueType = null;
    private LinearLayout mapLayout,LinearLayoutButtom;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn,reset_btn,getvelue_btn;//地图button
    private ImageButton imgBtn_moveCurrt;//地图移动到当前位置
    private TextView txt_showInfo;
    private InitMap initMap;
    private String placeid = "";
    public  KczyOperate(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.activity_kczy_develop_enterprise, null);
        mapLayout = (LinearLayout)view.findViewById(R.id.show_mapview_LinearLayout);
        LinearLayoutButtom = (LinearLayout) view.findViewById(R.id.LinearLayoutButtom);
        mapLayout.setVisibility(View.GONE);
        LinearLayoutButtom.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }
    private void initView(){
        kc_photo_img = (ImageView) view.findViewById(R.id.kc_photo_img);
        kc_canleImage = (TextView) view.findViewById(R.id.kc_canleImage);
        kc_bhqmc_etxt = (EditText) view.findViewById(R.id.kc_bhqmc_etxt);
        kc_jb_etxt = (EditText) view.findViewById(R.id.kc_jb_etxt);
        kc_qymc_etxt = (EditText) view.findViewById(R.id.kc_qymc_etxt);
        kc_gm_etxt = (EditText) view.findViewById(R.id.kc_gm_etxt);
        kc_fzjg_etxt = (EditText) view.findViewById(R.id.kc_fzjg_etxt);
        kc_zjyxqb_etxt = (EditText) view.findViewById(R.id.kc_zjyxqb_etxt);
        kc_zjyxqe_etxt = (EditText) view.findViewById(R.id.kc_zjyxqe_etxt);
        kc_zxzb_etxt = (EditText) view.findViewById(R.id.kc_zxzb_etxt);
        kc_xmmzb_etxt = (EditText) view.findViewById(R.id.kc_xmmzb_etxt);
        kc_scmj_etxt = (EditText) view.findViewById(R.id.kc_scmj_etxt);
        kc_sjqh_radioGroup = (RadioGroup) view.findViewById(R.id.kc_sjqh_radioGroup);
        kc_sjqh_radio1 = (RadioButton) view.findViewById(R.id.kc_sjqh_radio1);
        kc_sjqh_radio2 = (RadioButton) view.findViewById(R.id.kc_sjqh_radio2);
        kc_kqslqk_etxt = (EditText) view.findViewById(R.id.kc_kqslqk_etxt);
        kc_kcfs_etxt = (EditText) view.findViewById(R.id.kc_kcfs_etxt);
        kc_kqsx_etxt = (EditText) view.findViewById(R.id.kc_kqsx_etxt);
        kc_hbpfwh_etxt = (EditText) view.findViewById(R.id.kc_hbpfwh_etxt);
        kc_sfsbhq_etxt = (EditText) view.findViewById(R.id.kc_sfsbhq_etxt);
        kc_sftghbys_etxt = (EditText) view.findViewById(R.id.kc_sftghbys_etxt);
        kc_scqk_etxt = (EditText) view.findViewById(R.id.kc_scqk_etxt);
        kc_syq_etxt = (EditText) view.findViewById(R.id.kc_syq_etxt);
        kc_hcq_etxt = (EditText) view.findViewById(R.id.kc_hcq_etxt);
        kc_hxq_etxt = (EditText) view.findViewById(R.id.kc_hxq_etxt);
        kc_lsqk_etxt = (EditText) view.findViewById(R.id.kc_lsqk_etxt);
        kc_kcfs_dmz = (TextView) view.findViewById(R.id.kc_kcfs_dmz);
        kc_sfsbhq_dmz = (TextView) view.findViewById(R.id.kc_sfsbhq_dmz);
        kc_sftghbys_dmz = (TextView) view.findViewById(R.id.kc_sftghbys_dmz);
        kc_scqk_dmz = (TextView) view.findViewById(R.id.kc_scqk_dmz);

        kc_hxq_txt = (TextView) view.findViewById(R.id.kc_hxq_txt);
        kc_hcq_txt = (TextView) view.findViewById(R.id.kc_hcq_txt);
        kc_syq_txt = (TextView) view.findViewById(R.id.kc_syq_txt);

        mapView = (MapView) view.findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) view.findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) view.findViewById(R.id.reset_btn);//地图重置按钮
        getvelue_btn = (ImageButton) view.findViewById(R.id.getvelue_btn);//获取值
        imgBtn_moveCurrt = (ImageButton) view.findViewById(R.id.imgBtn_moveCurrt);
        txt_showInfo = (TextView) view.findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();

    }
    private void initMethod(){
         /*
        * 打开相册或照相机
        * */
        kc_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("不能修改相片!");
            }
        });

        /*开采方式对话框
        * */
        kc_kcfs_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,kc_kcfs_dmz,kc_kcfs_etxt,"开采方式","KCFS");
                cd.showQksxDialog();
            }
        });
        /*是否涉保护区
        * */
        kc_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,kc_sfsbhq_dmz,kc_sfsbhq_etxt,"是否涉保护区","ISBHQ");
                cd.showQksxDialog();
            }
        });
        /*是否通过环保验收
        * */
        kc_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,kc_sftghbys_dmz,kc_sftghbys_etxt,"是否通过环保验收","ISHBYS");
                cd.showQksxDialog();
            }
        });
        /*生产情况
        * */
        kc_scqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context,kc_scqk_dmz,kc_scqk_etxt,"生产情况","SCQK");
                cd.showQksxDialog();
            }
        });
        /*获取中心坐标
        * */
        kc_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
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
        kc_xmmzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);
                valueType = MapValueType.multiPoint;
                return false;
            }
        });
        /*始建前后
        * */
        kc_sjqh_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (kc_sjqh_radio1.getId() == checkedId) {
                    kc_sjqhStr = kc_sjqh_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: -------------始建前后------------>" + kc_sjqhStr);
                } else if (kc_sjqh_radio2.getId() == checkedId) {
                    kc_sjqhStr = kc_sjqh_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: -------------始建前后------------>" + kc_sjqhStr);
                }
            }
        });
        /*
        * 证件有效期
        * */
        kc_zjyxqb_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(context, kc_zjyxqb_etxt);
                dialog.showDataDialog();
            }
        });
        kc_zjyxqe_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(context, kc_zjyxqe_etxt);
                dialog.showDataDialog();
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
    //=====================map==================================================================
    private void getValueMethod() {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    kc_zxzb_etxt.setText(String.valueOf(m_location.getLongitude())+","+String.valueOf(m_location.getLatitude()));
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
                        kc_xmmzb_etxt.setText("未获得点...");
                    } else {
                        kc_xmmzb_etxt.setText("[["+sb.toString()+"]]");
                        kc_scmj_etxt.setText(String.format("%.2f",initMap.getArea()));
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

    @Override
    public kczyBean getEntityBean(String bhqid, String bhqmc, String jsxmid) {
        kczyBean bean = new kczyBean();
        StringBuilder sb = new StringBuilder();
        if (!kc_hxq_etxt.getText().toString().trim().equals("")) {
            sb.append(kc_hxq_txt.getText().toString().trim() + ":" + kc_hxq_etxt.getText().toString().trim() + ";");
        }
        if (!kc_hcq_etxt.getText().toString().trim().equals("")) {
            sb.append(kc_hcq_txt.getText().toString().trim() + ":" + kc_hcq_etxt.getText().toString().trim() + ";");
        }
        if (!kc_syq_etxt.getText().toString().trim().equals("")) {
            sb.append(kc_syq_txt.getText().toString().trim() + ":" + kc_syq_etxt.getText().toString().trim());
        }
        String ybhqwzgx =sb.toString();//与保护区位置关系
        bean.setBhqid(bhqid);
        bean.setBhqmc(bhqmc);
        bean.setJsxmjb(kc_jb_etxt.getText().toString().trim());
        bean.setJsxmid(jsxmid);
        bean.setJsxmmc(kc_qymc_etxt.getText().toString().trim());
        bean.setJsxmgm(kc_gm_etxt.getText().toString().trim());
        bean.setTrzj("");
        bean.setNcz("");
        bean.setFzjg(kc_fzjg_etxt.getText().toString().trim());
        bean.setZjyxqb(kc_zjyxqb_etxt.getText().toString().trim());
        bean.setZjyxqe(kc_zjyxqe_etxt.getText().toString().trim());
        bean.setBjbhqsj(kc_sjqhStr);
        bean.setKqslqk(kc_kqslqk_etxt.getText().toString().trim());
        bean.setKcfs(kc_kcfs_dmz.getText().toString().trim());
        bean.setKqsx(kc_kqsx_etxt.getText().toString().trim());
        bean.setHbpzwh(kc_hbpfwh_etxt.getText().toString().trim());
        bean.setIsbhq(kc_sfsbhq_dmz.getText().toString().trim());
        bean.setIshbys(kc_sftghbys_dmz.getText().toString().trim());
        bean.setYswjbh("");
        bean.setScqk(kc_scqk_dmz.getText().toString().trim());
        bean.setYbhqgx(ybhqwzgx);
        bean.setZgcs(kc_lsqk_etxt.getText().toString().trim());
        bean.setVkcfs(kc_kcfs_etxt.getText().toString().trim());
        bean.setVisbhq(kc_sfsbhq_etxt.getText().toString().trim());
        bean.setVishbys(kc_sftghbys_etxt.getText().toString().trim());
        bean.setVscqk(kc_scqk_etxt.getText().toString().trim());
        //将中心坐标截取x,y
        String centerpoint = kc_zxzb_etxt.getText().toString().trim();
        String pointx = "";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getTkEnterpriseBean: ------centerpoint------->" + centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp + 1);
        }
        bean.setCenterpointx(pointx);
        bean.setCenterpointy(pointy);
        bean.setMjzb(kc_xmmzb_etxt.getText().toString().trim());
        bean.setTjmj(kc_scmj_etxt.getText().toString().trim());
        bean.setHxmj(kc_hxq_etxt.getText().toString().trim());
        bean.setHcmj(kc_hcq_etxt.getText().toString().trim());
        bean.setSymj(kc_syq_etxt.getText().toString().trim());
        bean.setUsername(TempData.username);
        //bean.setUsertel(TempData.usertel);
        bean.setPlaceid(placeid);


        return bean;
    }

    @Override
    public View getView(kczyBean kczyBean) {
        if (kczyBean != null) {
            kc_bhqmc_etxt.setText(kczyBean.getBhqmc());
            kc_jb_etxt.setText(kczyBean.getJsxmjb());
            kc_qymc_etxt.setText(kczyBean.getJsxmmc());
            kc_gm_etxt.setText(kczyBean.getJsxmgm());
            kc_fzjg_etxt.setText(kczyBean.getFzjg());
            kc_zjyxqb_etxt.setText(kczyBean.getZjyxqb());
            kc_zjyxqe_etxt.setText(kczyBean.getZjyxqe());
            if ("".equals(kczyBean.getCenterpointx()) && "".equals(kczyBean.getCenterpointy())) {
                kc_zxzb_etxt.setText("");
            } else {
                kc_zxzb_etxt.setText(kczyBean.getCenterpointx()+","+kczyBean.getCenterpointy());
            }
            kc_xmmzb_etxt.setText(kczyBean.getMjzb());
            kc_scmj_etxt.setText(kczyBean.getTjmj());
            if (kc_sjqh_radio1.getText().toString().trim().equals(kczyBean.getBjbhqsj())) {
                kc_sjqh_radio1.setChecked(true);
            } else if (kc_sjqh_radio2.getText().toString().trim().equals(kczyBean.getBjbhqsj())) {
                kc_sjqh_radio2.setChecked(true);
            }
            kc_kqslqk_etxt.setText(kczyBean.getKqslqk());
            kc_kcfs_etxt.setText(kczyBean.getVkcfs());
            kc_kqsx_etxt.setText(kczyBean.getKqsx());
            kc_hbpfwh_etxt.setText(kczyBean.getHbpzwh());
            kc_scqk_etxt.setText(kczyBean.getVscqk());
            kc_sfsbhq_etxt.setText(kczyBean.getVisbhq());
            kc_sftghbys_etxt.setText(kczyBean.getVishbys());
            kc_syq_etxt.setText(kczyBean.getSymj());
            kc_hcq_etxt.setText(kczyBean.getHcmj());
            kc_hxq_etxt.setText(kczyBean.getHxmj());
            kc_lsqk_etxt.setText(kczyBean.getZgcs());
            Bitmap bitmap = BitmapFactory.decodeFile(kczyBean.getPhotoPath());
            kc_photo_img.setImageBitmap(bitmap);
            placeid = kczyBean.getPlaceid();
        }

        return view;
    }


    public void showMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
