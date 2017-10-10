package com.yaotu.proj.studydemo.customclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.esri.android.map.GraphicsLayer;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.activity.AssartActivity;
import com.yaotu.proj.studydemo.activity.BackDataActivity;
import com.yaotu.proj.studydemo.activity.DevelopConstructionActivity;
import com.yaotu.proj.studydemo.activity.IndustryEnterpriseActivity;
import com.yaotu.proj.studydemo.activity.KczyDevelopEnterpriseActivity;
import com.yaotu.proj.studydemo.activity.MainActivity;
import com.yaotu.proj.studydemo.activity.NewEnergyProjActivity;
import com.yaotu.proj.studydemo.activity.ShowCompleteActivity;
import com.yaotu.proj.studydemo.activity.TKEnterpriseActivity;
import com.yaotu.proj.studydemo.activity.TableOneActivity;
import com.yaotu.proj.studydemo.activity.TableTwoActivity;
import com.yaotu.proj.studydemo.activity.TravelDevelopEnterpriseActivity;
import com.yaotu.proj.studydemo.bean.FlagBean;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yt_pc06 on 2016/12/21.
 */

public class CustomWinMenu {
    private Context context;
    private int width, height;
    private JSONArray jsonArray;
    private PopupWindow popupWindow;
    private LinearLayout containerLayout;
    private BaiduMap _baiduMap;
    private com.esri.android.map.MapView mapView;
    private TextView txtview_flag, txt_pName;//标记当前地图上是否标记采集点，保存采集地样表类型
    private String menu_flag;   //实现动态创建样例表菜单中子菜单
    private TextView _txt_showInfo;
    private String bhqid, bhqmc, bhqjb;
    private ImageButton btnsavelocalvalue,btnsetlocalvalue;

    //private String dyna
    public CustomWinMenu(Context _context, JSONArray _jsonArray, int _width, int _height, BaiduMap _baiduMap, com.esri.android.map.MapView mapView, TextView txt_pType, TextView txt_pName, TextView txt_showInfo, String menuFlag, String bhqid, String bhqmc, String bhqjb,ImageButton btnsavelocalvalue,ImageButton btnsetlocalvalue) {
        this.context = _context;
        this._baiduMap = _baiduMap;
        this.mapView = mapView;
        this.txtview_flag = txt_pType;
        this.txt_pName = txt_pName;
        this._txt_showInfo = txt_showInfo;
        this.menu_flag = menuFlag;
        this.jsonArray = _jsonArray;
        this.width = _width;
        this.height = _height;
        this.bhqid = bhqid;
        this.bhqmc = bhqmc;
        this.bhqjb = bhqjb;
        this.btnsavelocalvalue = btnsavelocalvalue;
        this.btnsetlocalvalue = btnsetlocalvalue;
        containerLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_main_custommenu_item_container, null);
        try {
            setSubMenu();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        popupWindow = new PopupWindow(containerLayout, width == 0 ? LinearLayout.LayoutParams.WRAP_CONTENT : width, height == 0 ? LinearLayout.LayoutParams.WRAP_CONTENT : height);

    }

    //========================================创建子菜单========================================================================================
    private String report_type;//要显示的采样表信息，例如：0001 解析字符串 0：表示该表不显示，1：表示该表显示

    private void setSubMenu() throws JSONException {
        report_type = txtview_flag.getText().toString().trim();
        //String report_type = "1101";//测试用

        int len = jsonArray.length();
        for (int i = 0; i < len; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Log.i("TAG", "setSubMenu:============> " + len + "=========" + report_type);
            if (menu_flag.equals("样例表")) {
                if (!FlagBean.isHaveOverLay) {//
                    containerLayout = null;
                    showMessage("请选择一个自然保护区再进行操作...");
                    return;
                }
                if ("".equals(TempData.placeid)) {
                    containerLayout = null;
                    showMessage("请选中一个监测点再填表...");
                    return;
                }
                if (report_type.equals("")) {
                    containerLayout = null;
                    showMessage("该自然保护区暂无样表数据...");
                    return;
                }
                switch (report_type.charAt(i)) {
                    case '0'://不添加样例表
                        break;
                    case '1':
                        method(i, jsonObject);
                        break;
                    default:
                        break;
                }

            } else {
                method(i, jsonObject);
            }

        }
    }

    private void method(int i, JSONObject jsonObject) throws JSONException {
        Log.i("TAG", "method:=================method ");
        LinearLayout pop_item_layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.activity_main_custommenu_item_container_item, null);
        pop_item_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
        TextView popItemmenu = (TextView) pop_item_layout.findViewById(R.id.pop_item_textView);
        View pop_item_line = pop_item_layout.findViewById(R.id.pop_item_line);
        popItemmenu.setText(jsonObject.getString("title"));
        if (i + 1 == jsonArray.length()) {
            pop_item_line.setVisibility(View.GONE);
        }
        pop_item_layout.setOnClickListener(new myPopuMenuClickListener(jsonObject.getString("title")));
        containerLayout.addView(pop_item_layout);
    }

    //===========================================================================================
    public void showAtLocation(View parent) {
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        if (null != containerLayout) {
            containerLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        int x = location[0] - 5;
        int y = parent.getHeight() - (parent.getHeight() / 3);
        popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, x, y);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 使其聚集
        popupWindow.setFocusable(true);
        // 刷新状态
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            // 在dismiss中恢复透明度
            @Override
            public void onDismiss() {
            }
        });
    }

    //=======================隐藏菜单======================================================
    private Intent intent;

    public void dismiss() {
        popupWindow.dismiss();
    }

    //=======================定义popupMenu菜单点击事件=====================================================================
    class myPopuMenuClickListener implements View.OnClickListener {
        private String menu_value;

        public myPopuMenuClickListener(String value) {
            this.menu_value = value;
        }

        @Override
        public void onClick(View view) {

            if (menu_value.equals(context.getString(R.string.submenu_report1_name))) {//样表一
                /*intent = new Intent(context, TableOneActivity.class);
                intent.putExtra("p_name", txt_pName.getText().toString().trim());//标记样表类型*/
                intent = new Intent(context, TKEnterpriseActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report2_name))) {//样表二
                // intent = new Intent(context, TableTwoActivity.class);
                //intent.putExtra("type",FlagBean.SAMPLE_REPORT2);//标记样表类型
                intent = new Intent(context, IndustryEnterpriseActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report3_name))) {//样表三
                intent = new Intent(context, KczyDevelopEnterpriseActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report4_name))) {//样表四
                intent = new Intent(context, TravelDevelopEnterpriseActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report5_name))) {
                intent = new Intent(context, NewEnergyProjActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report6_name))) {
                intent = new Intent(context, AssartActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report7_name))) {
                intent = new Intent(context, DevelopConstructionActivity.class);
                intent.putExtra("bhqid", bhqid);
                intent.putExtra("bhqmc", bhqmc);
                intent.putExtra("bhqjb", bhqjb);
                intent.putExtra("placeid", TempData.placeid);
                intent.putExtra("longitude", TempData.longitude);
                intent.putExtra("latitude", TempData.latitude);
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_gps))) {//GPS航迹

                FlagBean.GPSFlag = true;
                TempData.file = FileUtils.definedFile("gps航迹");
                showMessage("GPS航迹"+TempData.file);
                Log.i("TAG", "onClick:---------gps航迹file------> "+TempData.file);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_way_analyze))) {//路径分析
                showMessage("路径分析");
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_report_data))) {//清除样表数据
                if ("".equals(txtview_flag.getText().toString().trim())) {
                    showMessage("请选择要清除的样表采集地再进行操作");
                } else {
                    //提示对话框，是否清除该地点所采集的样表数据
                    //showMessage(txtview_flag.getText().toString().trim() + " 的样表数据将被清除");
                    DBManager dbManager = new DBManager(context);
                    boolean update_result = dbManager.updateBySql("update place set p_state = ? where p_name = ?", new Object[]{"0000", txt_pName.getText().toString().trim()});
                    boolean del_result = dbManager.updateBySql("delete from table1 where p_name = ?", new Object[]{txt_pName.getText().toString().trim()});
                    if (update_result && del_result) {
                        showMessage("删除成功!!!");
                    } else {
                        showMessage("fail...");
                    }
                    dbManager.closeDB();
                }
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_layer))) {//清除图层
                _baiduMap.clear();
                if (TempData.temp_graphicslayer != null) {
                    Log.i("TAG", "onClick: --------temp_graphicslayer-----"+mapView.getLayers().length);
                    if (mapView.getLayers().length > 2) {
                        mapView.removeLayer(TempData.temp_graphicslayer);
                        TempData.temp_graphicslayer = null;
                    }
                }
                if (TempData.arcgis_Dcgraphicslayer != null) {
                    TempData.arcgis_Dcgraphicslayer.removeAll();
                    mapView.removeLayer(TempData.arcgis_Dcgraphicslayer);
                    TempData.arcgis_Dcgraphicslayer = null;
                    btnsavelocalvalue.setVisibility(View.INVISIBLE);
                    btnsetlocalvalue.setVisibility(View.INVISIBLE);
                }
                TempData.pointList.clear();
                TempData.placeid = "";
                TempData.latitude = 0;
                TempData.longitude = 0;
                FlagBean.dcFlag = false;
                _txt_showInfo.setText("");
                txt_pName.setText("");
                FlagBean.isHaveOverLay = false;
                TempData.file = null;
                FlagBean.GPSFlag = false;
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_complete))) {
                Intent intent = new Intent(context, ShowCompleteActivity.class);
                intent.putExtra("p_name", txt_pName.getText().toString().trim());
                context.startActivity(intent);
                dismiss();
            } else if (menu_value.equals(context.getString(R.string.submenu_backdata))) {
                Intent intent = new Intent(context, BackDataActivity.class);
                context.startActivity(intent);
                dismiss();
            }

        }
    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }
}
