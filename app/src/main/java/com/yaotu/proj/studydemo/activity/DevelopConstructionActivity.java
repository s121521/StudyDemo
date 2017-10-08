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
import android.support.v7.widget.RecyclerView;
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
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionRecordBean;
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
* 开发建设活动
* */
public class DevelopConstructionActivity extends AppCompatActivity {
    private Context context = DevelopConstructionActivity.this;
    private final String TAG = this.getClass().getSimpleName();
    private MapValueType valueType = null;
    private Intent intent;
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
    private LinearLayout mapLayout;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn;
    private ImageButton reset_btn;//地图button
    private TextView txt_showInfo;

    private InitMap initMap;
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    //----------一下内容国家统一下发
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String jcdId;//监测点ID
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
        setContentView(R.layout.activity_develop_construction);
        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);
        intent = getIntent();
        bhqid = intent.getStringExtra("bhqid");
        bhqmc = intent.getStringExtra("bhqmc");
        bhqjb = intent.getStringExtra("bhqjb");
        Log.i(TAG, "onCreate: -------placeid------>"+intent.getStringExtra("placeid"));
        Log.i(TAG, "onCreate: -------longitude------>"+intent.getDoubleExtra("longitude",0));
        Log.i(TAG, "onCreate: -------latitude------>"+intent.getDoubleExtra("latitude",0));
        jcdId = intent.getStringExtra("placeid");
        jcdLongitude =intent.getDoubleExtra("longitude",0) ;
        jcdlatitude =intent.getDoubleExtra("latitude",0) ;
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initView() {
        de_photo_img = (ImageView) findViewById(R.id.de_photo_img);
        de_canleImage = (TextView) findViewById(R.id.de_canleImage);
        de_bhqmc_etxt = (EditText) findViewById(R.id.de_bhqmc_etxt);
        de_jb_etxt = (EditText) findViewById(R.id.de_jb_etxt);
        de_hdmc_etxt = (EditText) findViewById(R.id.de_hdmc_etxt);
        de_lx_etxt = (EditText) findViewById(R.id.de_lx_etxt);
        de_gm_etxt = (EditText) findViewById(R.id.de_gm_etxt);
        de_zxzb_etxt = (EditText) findViewById(R.id.de_zxzb_etxt);
        de_xmmzb_etxt = (EditText) findViewById(R.id.de_xmmzb_etxt);
        de_scmj_etxt = (EditText) findViewById(R.id.de_scmj_etxt);
        de_sjqh_groupRadio = (RadioGroup) findViewById(R.id.de_sjqh_groupRadio);
        de_sjqh_radio1 = (RadioButton) findViewById(R.id.de_sjqh_radio1);
        de_sjqh_radio2 = (RadioButton) findViewById(R.id.de_sjqh_radio2);
        de_hbpfwh_etxt = (EditText) findViewById(R.id.de_hbpfwh_etxt);
        de_sfsbhq_dmz = (TextView) findViewById(R.id.de_sfsbhq_dmz);
        de_sfsbhq_etxt = (EditText) findViewById(R.id.de_sfsbhq_etxt);
        de_sftghbys_dmz = (TextView) findViewById(R.id.de_sftghbys_dmz);
        de_scqk_dmz = (TextView) findViewById(R.id.de_scqk_dmz);
        de_sftghbys_etxt = (EditText) findViewById(R.id.de_sftghbys_etxt);
        de_scqk_etxt = (EditText) findViewById(R.id.de_scqk_etxt);
        de_syq_etxt = (EditText) findViewById(R.id.de_syq_etxt);
        de_hcq_etxt = (EditText) findViewById(R.id.de_hcq_etxt);
        de_hxq_etxt = (EditText) findViewById(R.id.de_hxq_etxt);
        de_lsqk_etxt = (EditText) findViewById(R.id.de_lsqk_etxt);
        de_hxq_txt = (TextView) findViewById(R.id.de_hxq_txt);
        de_hcq_txt = (TextView) findViewById(R.id.de_hcq_txt);
        de_syq_txt = (TextView) findViewById(R.id.de_syq_txt);

        de_bhqmc_etxt.setText(bhqmc);
        de_jb_etxt.setText(bhqjb);
        mapView = (MapView) findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
        de_zxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
    }

    private void initMethod() {
         /*
        * 打开相册或照相机
        * */
        de_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        de_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                de_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
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
        /*双击获取项目面坐标
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
    }

    //---------------------------------------------------------------------
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

                    de_photo_img.setImageBitmap(bitmap);
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
                de_photo_img.setImageBitmap(bitmap);
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

    //=========================================================================================
    public void getValueMethod(View view) {//获取值
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
        //editText_area.setText(String.valueOf(area));
        // editText_perimeter.setText(String.valueOf(permiter));
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
        //showMessage("move current position");
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开或网络连接");
        }
    }

    //=============================提交/保存========================================================
    public void submitBtnMethod(View view) {
        int netType = CheckNetwork.getNetWorkState(context);
        if (de_hdmc_etxt.getText().toString().trim().equals("")) {
            showMessage("活动名称不能为空!");
            return;
        }
        if (netType == 0) {//none
            showMessage("请打开网络连接");
        } else if (netType == 1) {//wifi
            upload();
        } else if (netType == 2) {//mobile
            upload();
        }
    }

    public void saveBtnMethod(View view) {
        if (de_hdmc_etxt.getText().toString().trim().equals("")) {
            showMessage("活动名称不能为空!");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DevelopConstructionBean deBean = getDevelopCoustructionBean();
                if (IsExist(deBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertDevelopQyInfo(deBean,0+"");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
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

    //==============================================================================================
    private boolean threadFlag;
    private void upload(){
        //1.组织要提交的数据
        DevelopConstructionJsonBean jsonBean = new DevelopConstructionJsonBean();
        List<DevelopConstructionRecordBean> records  = new ArrayList<>();
        DevelopConstructionBean bean = getDevelopCoustructionBean();
        DevelopConstructionRecordBean recordBean = new DevelopConstructionRecordBean();
        recordBean.setObjectid(TempData.placeid);
        recordBean.setCenterpointx(TempData.longitude);
        recordBean.setCenterpointy(TempData.latitude);
        recordBean.setBean(bean);
        records.add(recordBean);
        jsonBean.setKey("07");
        jsonBean.setYhdh(TempData.username);
        jsonBean.setIschecked("21");
        jsonBean.setRecord(records);

        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------开发建设上传数据----------->"+jsonStr);
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
                        Message message = Message.obtain();
                        while (threadFlag) {
                            Response response = ParseIntentData.getDataPostByJson(IPURL +"/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
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
    public void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    //=============================================================================================

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
    //============================初始化表单数据============================================
    public DevelopConstructionBean getDevelopCoustructionBean(){
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
        bean.setJsxmjb(de_jb_etxt.getText().toString().trim());
        bean.setKfjsmc(de_hdmc_etxt.getText().toString().trim());
        bean.setJsxmid("0");
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

        bean.setPhotoPath(photoPath);
        bean.setPlaceid(jcdId);
        //bean.setIschecked("21");
        bean.setUsername(TempData.username);
        return bean;
    }
    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(DevelopConstructionBean bean){
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql ="select * from DevelopqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(),bean.getKfjsmc(),bean.getUsername()};
        List<DevelopConstructionBean> list = query.queryDevelopQyInfos(sql,strs);
        if (list != null) {
            if (list.size() >0) {
                return true;//存在
            }
        }
        return false;//不存在
    }
    /*插入本地数据
    * */
    private boolean insertDevelopQyInfo(DevelopConstructionBean bean,String upState){

        boolean result = false;
        if (bean != null) {
            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertDevelopQyInfo(bean, upState);
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
