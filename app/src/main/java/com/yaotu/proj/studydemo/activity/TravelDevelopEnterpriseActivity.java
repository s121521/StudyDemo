package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.gson.Gson;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.tableBean.TravelBean;
import com.yaotu.proj.studydemo.bean.tableBean.TravelJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.TravelRecordBean;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.InsertLocalTableData;
import com.yaotu.proj.studydemo.customclass.PhotoImageSize;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/*
* 旅游资源开发企业
* */
public class TravelDevelopEnterpriseActivity extends AppCompatActivity {
    private Context context = TravelDevelopEnterpriseActivity.this;
    private final String TAG = this.getClass().getSimpleName();
    private Intent intent;
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
    private String bhqid, bhqmc, bhqjb;

    private MapValueType valueType = null;
    private LinearLayout mapLayout;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn, reset_btn;//地图button
    private TextView txt_showInfo;
    //----------一下内容国家统一下发
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String jcdId;//监测点ID
    private InitMap initMap;
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    private ProgressDialog progressDialog;
    private final int UPLOADSUCCEED = 0X110;
    private final int UPLOADFAIL = 0X111;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOADSUCCEED:
                    progressDialog.dismiss();
                    break;
                case UPLOADFAIL:
                    progressDialog.dismiss();

                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_develop_enterprise);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        intent = getIntent();
        bhqid = intent.getStringExtra("bhqid");
        bhqmc = intent.getStringExtra("bhqmc");
        bhqjb = intent.getStringExtra("bhqjb");
        Log.i(TAG, "onCreate: -------placeid------>"+intent.getStringExtra("placeid"));
        Log.i(TAG, "onCreate: -------longitude------>"+intent.getDoubleExtra("longitude",0));
        Log.i(TAG, "onCreate: -------latitude------>"+intent.getDoubleExtra("latitude",0));
        jcdId = intent.getStringExtra("placeid");
        jcdLongitude = intent.getDoubleExtra("longitude",0);
        jcdlatitude = intent.getDoubleExtra("latitude",0);
        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
    }

    private void initView() {
        tr_photo_img = (ImageView) findViewById(R.id.tr_photo_img);
        tr_canleImage = (TextView) findViewById(R.id.tr_canleImage);
        tr_bhqmc_etxt = (EditText) findViewById(R.id.tr_bhqmc_etxt);
        tr_jb_etxt = (EditText) findViewById(R.id.tr_jb_etxt);
        tr_qymc_etxt = (EditText) findViewById(R.id.tr_qymc_etxt);
        tr_gm_etxt = (EditText) findViewById(R.id.tr_gm_etxt);
        tr_zxzb_etxt = (EditText) findViewById(R.id.tr_zxzb_etxt);
        tr_xmmzb_etxt = (EditText) findViewById(R.id.tr_xmmzb_etxt);
        tr_scmj_etxt = (EditText) findViewById(R.id.tr_scmj_etxt);
        tr_sjqh_radioGroup = (RadioGroup) findViewById(R.id.tr_sjqh_radioGroup);
        tr_sjqh_radio1 = (RadioButton) findViewById(R.id.tr_sjqh_radio1);
        tr_sjqh_radio2 = (RadioButton) findViewById(R.id.tr_sjqh_radio2);
        tr_kqslqk_etxt = (EditText) findViewById(R.id.tr_kqslqk_etxt);
        tr_qysx_etxt = (EditText) findViewById(R.id.tr_qysx_etxt);
        tr_hbpfwh_etxt = (EditText) findViewById(R.id.tr_hbpfwh_etxt);
        tr_sfsbhq_etxt = (EditText) findViewById(R.id.tr_sfsbhq_etxt);
        tr_sftghbys_etxt = (EditText) findViewById(R.id.tr_sftghbys_etxt);
        tr_yyqk_etxt = (EditText) findViewById(R.id.tr_yyqk_etxt);
        tr_syq_etxt = (EditText) findViewById(R.id.tr_syq_etxt);
        tr_hcq_etxt = (EditText) findViewById(R.id.tr_hcq_etxt);
        tr_hxq_etxt = (EditText) findViewById(R.id.tr_hxq_etxt);
        tr_lsqk_etxt = (EditText) findViewById(R.id.tr_lsqk_etxt);
        tr_qysx_dmz = (TextView) findViewById(R.id.tr_qysx_dmz);
        tr_sfsbhq_dmz = (TextView) findViewById(R.id.tr_sfsbhq_dmz);
        tr_sftghbys_dmz = (TextView) findViewById(R.id.tr_sftghbys_dmz);
        tr_yyqk_dmz = (TextView) findViewById(R.id.tr_yyqk_dmz);
        tr_hxq_txt = (TextView) findViewById(R.id.tr_hxq_txt);
        tr_hcq_txt = (TextView) findViewById(R.id.tr_hcq_txt);
        tr_syq_txt = (TextView) findViewById(R.id.tr_syq_txt);

        mapView = (MapView) findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();

        tr_bhqmc_etxt.setText(bhqmc);
        tr_jb_etxt.setText(bhqjb);
        tr_zxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
    }

    private void initMethod() {
         /*
        * 打开相册或照相机
        * */
        tr_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        tr_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tr_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
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

    }

    //===============================map===========================================================
    public void getValueMethod(View view) {//获取值
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

    public void resetMethod(View view) {//重置
        TempData.pointList.clear();
        baiduMap.clear();
        txt_showInfo.setText("");
    }

    private boolean isBaiduLatlng = true;

    public void getDotMethod(View view) {//打点
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

    public void moveCurrentPosition(View view) {//移动到当前位置
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
    }

    //================================================================================================
    /*
    打开拍照对话框
    * */
    private final int OPEN_RESULT = 1; // 相机
    private final int PICK_RESULT = 2;// 本地相册
    private AlertDialog.Builder dialog;

    public void showPhotoDialog() {
        dialog = new AlertDialog.Builder(context);
        final String[] items = new String[]{"拍照", "从相册选择"};
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Log.i("TAG", "onClick: 0" + items[which]);
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, OPEN_RESULT);
                        dialog.dismiss();
                        break;
                    case 1:
                        Log.i("TAG", "onClick: 1" + items[which]);
                        intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_RESULT);
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.create();
        dialog.show();
    }

    private String photoPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_RESULT:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    // Log.i("TAG", "onActivityResult: ====================>"+uri.getPath());
                    photoPath = uri.getPath();
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(uri.getPath());

                    tr_photo_img.setImageBitmap(bitmap);
                    Log.i("TAG", "onActivityResult:-------相册相片路径---------> " + photoPath + "---字节数---->" + bitmap.getByteCount() + "<========>" + bitmap.getRowBytes() * bitmap.getHeight());
                }
                break;
            case OPEN_RESULT:
                /**
                 * 通过这种方法取出的拍摄会默认压缩，因为如果相机的像素比较高拍摄出来的图会比较高清，
                 * 如果图太大会造成内存溢出（OOM），因此此种方法会默认给图片进行压缩
                 */
                String fileName = String.valueOf(System.currentTimeMillis());
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                File file = FileUtils.saveBitmap(bitmap, fileName);
                photoPath = file.getAbsolutePath();
                Log.i("TAG", "onActivityResult:-------相机相片路径---------> " + photoPath + "----->" + file.getPath());
                tr_photo_img.setImageBitmap(bitmap);
                //发送广播更新系统相册
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(FileUtils.SDPATH + "pic_" + fileName + ".jpg"));
                intent.setData(uri);
                context.sendBroadcast(intent);
                break;
            default:
                break;
        }
    }

    //=================================提交、保存===========================================================
    public void submitBtnMethod(View view) {
        if (tr_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
            return;
        }
        int netType = CheckNetwork.getNetWorkState(context);
        if (netType == 0) {//none
            showMessage("请打开网络连接");
        } else if (netType == 1) {//wifi
            upload();
        } else if (netType == 2) {//mobile
            upload();
        }
    }

    public void saveBtnMethod(View view) {
        if (tr_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TravelBean travelBean = getTravelBean();
                if (IsExist(travelBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertTravelQyInfo(travelBean, 0 + "");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
                    if (inResult) {
                        showMessage("保存成功!");
                    } else {
                        showMessage("保存失败...");
                    }
                }

            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }
    //==========================================================================================
    private boolean threadFlag ;
    private void upload(){
        //1.组织要提交的数据
        TravelJsonBean jsonBean = new TravelJsonBean();
        List<TravelRecordBean> records = new ArrayList<>();
        TravelBean bean = getTravelBean();
        TravelRecordBean recordBean = new TravelRecordBean();
        recordBean.setObjectid(TempData.placeid);
        recordBean.setCenterpointx(TempData.longitude);
        recordBean.setCenterpointy(TempData.latitude);
        recordBean.setBean(bean);
        records.add(recordBean);
        jsonBean.setKey("04");
        jsonBean.setYhdh(TempData.username);
        jsonBean.setIschecked("21");
        jsonBean.setRecord(records);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------旅游开发上传数据----------->"+jsonStr);
        //2.连接服务器上传
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否上传数据？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                threadFlag = true;
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("正在上传数据...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        threadFlag = false;
                    }
                });
                progressDialog.show();
                final String IPURL = getResources().getString(R.string.http_url);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (threadFlag) {
                        Response response = ParseIntentData.getDataPostByJson(IPURL +"/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
                        Message message = Message.obtain();
                        if (response != null && response.code() == 200) {
                            try {
                                String responseValue = response.body().string();
                                if (responseValue.equals("success")) {
                                    message.what = UPLOADSUCCEED;
                                    myHandler.sendMessage(message);
                                } else if (responseValue.equals("fail")) {
                                    message.what = UPLOADFAIL;
                                    myHandler.sendMessage(message);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            threadFlag = false;
                        } else {
                            message.what = UPLOADFAIL;
                            myHandler.sendMessage(message);
                            threadFlag = false;
                        }
                    }

                    }
                }).start();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        dialog.create().show();

    }

    //============================================================================================
    public void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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

    //=================================初始化表单数据=================================================
    private TravelBean getTravelBean() {
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
        bean.setJsxmid("0");
        bean.setJsxmmc(tr_qymc_etxt.getText().toString().trim());
        bean.setJsxmjb(bhqjb);
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
        bean.setPlaceid(jcdId);
        //bean.setIschecked("21");
        bean.setUsername(TempData.username);
        return bean;
    }

    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(TravelBean bean) {
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql = "select * from TravelqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(), bean.getJsxmmc(),bean.getUsername()};
        List<TravelBean> list = query.queryTravelQyInfos(sql, strs);
        if (list != null) {
            if (list.size() > 0) {
                return true;//存在
            }
        }
        return false;//不存在
    }

    /*插入本地数据
    * */
    private boolean insertTravelQyInfo(TravelBean bean, String upState) {

        boolean result = false;
        if (bean != null) {

            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertTravelQyInfo(bean, upState);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
