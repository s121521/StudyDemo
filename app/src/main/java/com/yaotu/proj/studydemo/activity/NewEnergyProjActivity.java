package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassKczyqy;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassNewqy;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyRecordBean;
import com.yaotu.proj.studydemo.common.LocalBaseInfo;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.HandleImage;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.InsertLocalTableData;
import com.yaotu.proj.studydemo.customclass.PhotoImageSize;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Response;

/*
*
* 新能源项目
* */
public class NewEnergyProjActivity extends AppCompatActivity {
    private Context context = NewEnergyProjActivity.this;
    private final String TAG = this.getClass().getSimpleName();
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
    private TextView ne_qysx_dmz, ne_sfsbhq_dmz, ne_sftghbys_dmz, ne_scqk_dmz;
    private EditText ne_syq_etxt;//实验区
    private EditText ne_hcq_etxt;//缓冲区
    private EditText ne_hxq_etxt;//核心区
    private EditText ne_lsqk_etxt;//整改落实情况
    private TextView ne_hxq_txt, ne_hcq_txt, ne_syq_txt;
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
    private String activityType;
    private NoPassNewqy mNoPassNewqy;
    private String jsxmID = "0";//添加为0；更行为原有id;
    private DBManager dbManager;
    private InitMap initMap;
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    private String bhqid, bhqmc, bhqjb, bhqjbdm;

    private ProgressDialog progressDialog;
    private final int UPLOADSUCCEED = 0X110;
    private final int UPLOADFAIL = 0X111;
    private final int UPLOADPICSUCCEED = 0X002;//照片上传成功
    private final int UPLOADPICFAIL = 0X003;//照片上传失败
    private String IPURL;
    private String dataUplocadSucceed;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOADSUCCEED:
                    progressDialog.dismiss();
                    if (!"".equals(photoPath)) {//上传相片
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setMessage("数据上传成功！是否上传相片?");
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                uploadPic();

                            }
                        });
                        builder.show();
                    }else {
                        showMessage("上传成功");
                    }
                    break;
                case UPLOADFAIL:
                    progressDialog.dismiss();
                    showMessage("上传失败...");
                    break;
                case UPLOADPICSUCCEED:
                    showMessage("照片上传成功!");
                    break;
                case UPLOADPICFAIL:
                    showMessage("照片上传失败!");
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_energy_proj);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
        IPURL = getResources().getString(R.string.http_url);
    }

    private void initView() {
        ne_photo_img = (ImageView) findViewById(R.id.ne_photo_img);
        ne_canleImage = (TextView) findViewById(R.id.ne_canleImage);
        ne_bhqmc_etxt = (EditText) findViewById(R.id.ne_bhqmc_etxt);
        ne_jb_etxt = (EditText) findViewById(R.id.ne_jb_etxt);
        ne_qymc_etxt = (EditText) findViewById(R.id.ne_qymc_etxt);
        ne_lx_etxt = (EditText) findViewById(R.id.ne_lx_etxt);
        ne_gm_etxt = (EditText) findViewById(R.id.ne_gm_etxt);
        ne_zxzb_etxt = (EditText) findViewById(R.id.ne_zxzb_etxt);
        ne_xmmzb_etxt = (EditText) findViewById(R.id.ne_xmmzb_etxt);
        ne_scmj_etxt = (EditText) findViewById(R.id.ne_scmj_etxt);
        ne_sjqh_radioGroup = (RadioGroup) findViewById(R.id.ne_sjqh_radioGroup);
        ne_sjqh_radio1 = (RadioButton) findViewById(R.id.ne_sjqh_radio1);
        ne_sjqh_radio2 = (RadioButton) findViewById(R.id.ne_sjqh_radio2);
        ne_sx_etxt = (EditText) findViewById(R.id.ne_sx_etxt);
        ne_hbpfwh_etxt = (EditText) findViewById(R.id.ne_hbpfwh_etxt);
        ne_sfsbhq_etxt = (EditText) findViewById(R.id.ne_sfsbhq_etxt);
        ne_sftghbys_etxt = (EditText) findViewById(R.id.ne_sftghbys_etxt);
        ne_ktscqk_etxt = (EditText) findViewById(R.id.ne_ktscqk_etxt);
        ne_syq_etxt = (EditText) findViewById(R.id.ne_syq_etxt);
        ne_hcq_etxt = (EditText) findViewById(R.id.ne_hcq_etxt);
        ne_hxq_etxt = (EditText) findViewById(R.id.ne_hxq_etxt);
        ne_lsqk_etxt = (EditText) findViewById(R.id.ne_lsqk_etxt);
        ne_qysx_dmz = (TextView) findViewById(R.id.ne_qysx_dmz);
        ne_sfsbhq_dmz = (TextView) findViewById(R.id.ne_sfsbhq_dmz);
        ne_sftghbys_dmz = (TextView) findViewById(R.id.ne_sftghbys_dmz);
        ne_scqk_dmz = (TextView) findViewById(R.id.ne_scqk_dmz);
        ne_hxq_txt = (TextView) findViewById(R.id.ne_hxq_txt);
        ne_hcq_txt = (TextView) findViewById(R.id.ne_hcq_txt);
        ne_syq_txt = (TextView) findViewById(R.id.ne_syq_txt);
        ne_bhqmc_etxt.setText(bhqmc);
        ne_jb_etxt.setText(bhqjb);

        mapView = (MapView) findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
    }

    private void initMethod() {
         /*
        * 打开相册或照相机
        * */
        ne_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        ne_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ne_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
            }
        });
        /*始建前后
        * */
        ne_sjqh_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (ne_sjqh_radio1.getId() == checkedId) {
                    ne_sjqhStr = ne_sjqh_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ------------始建前后----------->" + ne_sjqhStr);
                } else if (ne_sjqh_radio2.getId() == checkedId) {
                    ne_sjqhStr = ne_sjqh_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ------------始建前后----------->" + ne_sjqhStr);
                }
            }
        });
        /*
        * 企业属性
        * */
        ne_sx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, ne_qysx_dmz, ne_sx_etxt, "企业属性", "QYSX");
                cd.showQksxDialog();
            }
        });
        /*是否涉保护区
        * */
        ne_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, ne_sfsbhq_dmz, ne_sfsbhq_etxt, "是否涉保护区", "ISBHQ");
                cd.showQksxDialog();
            }
        });
        /*是否通过环保验收
        * */
        ne_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, ne_sftghbys_dmz, ne_sftghbys_etxt, "是否通过环保验收", "ISHBYS");
                cd.showQksxDialog();
            }
        });
        /*生产运行情况
        * */
        ne_ktscqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, ne_scqk_dmz, ne_ktscqk_etxt, "生产运行情况", "SCQK");
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
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
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
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT > 19) {
                        //4.4及以上系统使用这个方法处理图片
                        photoPath = HandleImage.handleImgeOnKitKat(context, data);
                    } else {
                        photoPath = HandleImage.handleImageBeforeKitKat(context, data);
                    }
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(photoPath);

                    ne_photo_img.setImageBitmap(bitmap);
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
                ne_photo_img.setImageBitmap(bitmap);
                //发送广播更新系统相册
               upDateDcim(file.getName());
                break;
            default:
                break;
        }
    }

    //=================================map=========================================================
    public void getValueMethod(View view) {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    ne_zxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
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
                        ne_xmmzb_etxt.setText("未获得点...");
                    } else {
                        ne_xmmzb_etxt.setText("[[" + sb.toString() + "]]");
                        ne_scmj_etxt.setText(String.format("%.2f", initMap.getArea()));
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

    //=============================================================================================
    public void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //==========================提交、保存=========================================================
    public void submitBtnMethod(View view) {
        int netType = CheckNetwork.getNetWorkState(context);
        if (ne_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
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
        if (ne_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewEnergyBean newEnergyBean = getNewEnergyBean();
                if (IsExist(newEnergyBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertNewEnergyQyInfo(newEnergyBean, 0 + "");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
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

    //============================================================================================
    private boolean threadFlag;

    private void upload() {
        //1.组织要提交的数据
        NewEnergyJsonBean jsonBean = new NewEnergyJsonBean();
        List<NewEnergyRecordBean> records = new ArrayList<>();
        NewEnergyBean bean = getNewEnergyBean();
        NewEnergyRecordBean recordBean = new NewEnergyRecordBean();
        recordBean.setObjectid(jcdId);
        recordBean.setCenterpointx(jcdLongitude);
        recordBean.setCenterpointy(jcdlatitude);
        recordBean.setBean(bean);
        records.add(recordBean);
        jsonBean.setKey("05");
        jsonBean.setYhdh(TempData.yhdh);
        if (activityType.equals("add")) {
            jsonBean.setIschecked("21");
        } else {
            jsonBean.setIschecked("22");
        }
        jsonBean.setRecord(records);

        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------新能源上传数据----------->" + jsonStr);
        //2.连接服务器上传
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否上传数据？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (threadFlag) {
                            Response response = ParseIntentData.getDataPostByJson(IPURL + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
                            Message message = Message.obtain();
                            if (response != null && response.code() == 200) {
                                try {
                                    String responseValue = response.body().string();
                                    dataUplocadSucceed = responseValue;
                                    message.what = UPLOADSUCCEED;
                                    myHandler.sendMessage(message);
                                    threadFlag = false;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

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
    private void uploadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                int bitMapSize = ParseIntentData.getBitmapSize(bitmap);
                Log.i(TAG, "run: -------bitmap大小----->" + bitMapSize);
                if (bitMapSize > 202800) {
                    File file = FileUtils.saveBitmap(PhotoImageSize.revitionImageSize(photoPath), String.valueOf(System.currentTimeMillis()));
                    photoPath = file.getAbsolutePath();
                    upDateDcim(file.getName());
                }
                String url = IPURL + "/BhqJsxm/Upload";
                Response response = ParseIntentData.upLoadImage(url, photoPath, dataUplocadSucceed);
                if (response != null && response.code() == 200) {
                    message.what = UPLOADPICSUCCEED;
                } else {
                    message.what = UPLOADPICFAIL;
                }
                myHandler.sendMessage(message);
            }
        }).start();
    }

    //==============================================================================================

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        intent = getIntent();
        activityType = intent.getStringExtra("type");
        if (activityType.equals("add")) {
            bhqid = intent.getStringExtra("bhqid");
            bhqmc = intent.getStringExtra("bhqmc");
            bhqjb = intent.getStringExtra("bhqjb");
            bhqjbdm = intent.getStringExtra("bhqjbdm");
            jcdId = intent.getStringExtra("placeid");
            jcdLongitude = intent.getDoubleExtra("longitude", 0);
            jcdlatitude = intent.getDoubleExtra("latitude", 0);
            //------------------------------
            ne_bhqmc_etxt.setText(bhqmc);
            ne_jb_etxt.setText(bhqjb);
            ne_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            jsxmID = "0";
        } else {
            mNoPassNewqy = (NoPassNewqy) intent.getSerializableExtra("bdData");
            bhqmc = intent.getStringExtra("bdBhqmc");
            jcdLongitude = intent.getDoubleExtra("bdjd", 0);
            jcdlatitude = intent.getDoubleExtra("bdwd", 0);
            jcdId = intent.getStringExtra("bdobjid");
            Log.i(TAG, "onResume: ----------bhqmc:" + bhqmc);
            //--------------------------------------------
            setViewData();//获取更新数据
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //======================初始化表单数据=====================================================
    public NewEnergyBean getNewEnergyBean() {
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
        String ybhqwzgx = sb.toString();//与保护区位置关系
        bean.setBhqid(bhqid);
        bean.setBhqmc(bhqmc);
        bean.setJsxmjb(bhqjbdm);//ne_jb_etxt.getText().toString().trim()
        bean.setJsxmid(bhqjbdm);
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
        bean.setPlaceid(jcdId);
        bean.setUsername(TempData.yhdh);
        return bean;
    }

    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(NewEnergyBean bean) {
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql = "select * from NewEnergyqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(), bean.getJsxmmc(), bean.getUsername()};
        List<NewEnergyBean> list = query.queryNewEnergyQyInfos(sql, strs);
        if (list != null) {
            if (list.size() > 0) {
                return true;//存在
            }
        }
        return false;//不存在
    }

    /*插入本地数据
    * */
    private boolean insertNewEnergyQyInfo(NewEnergyBean bean, String upState) {

        boolean result = false;
        if (bean != null) {
            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertNewEnergyQyInfo(bean, upState);
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

    //==============================================================================================
    private void setViewData() {
        if (mNoPassNewqy != null) {
            bhqid = mNoPassNewqy.getBHQID();
            jsxmID = mNoPassNewqy.getJSXMID();
            ne_bhqmc_etxt.setText(bhqmc);
            ne_qymc_etxt.setText(mNoPassNewqy.getJSXMMC());
            CodeTypeBean bhqjbB = codeType("BHQJB", mNoPassNewqy.getJSXMJB() == null ? "" : mNoPassNewqy.getJSXMJB().toString().trim());
            if (bhqjbB != null) {
                ne_jb_etxt.setText(bhqjbB.getDMMC1());
                bhqjbdm = bhqjbB.getDMZ();
            }
            ne_lx_etxt.setText(mNoPassNewqy.getJSXMLX());
            ne_gm_etxt.setText(mNoPassNewqy.getJSXMGM());
            if (ne_sjqh_radio1.getText().equals(mNoPassNewqy.getBJBHQSJ() == null ? "" : mNoPassNewqy.getBJBHQSJ().toString().trim())) {
                ne_sjqh_radio1.setChecked(true);
            } else if (ne_sjqh_radio2.getText().equals(mNoPassNewqy.getBJBHQSJ() == null ? "" : mNoPassNewqy.getBJBHQSJ().toString().trim())) {
                ne_sjqh_radio2.setChecked(true);
            }
            CodeTypeBean qysxB = codeType("QYSX", mNoPassNewqy.getQYSX() == null ? "" : mNoPassNewqy.getQYSX().toString().trim());
            if (qysxB != null) {
                ne_qysx_dmz.setText(qysxB.getDMZ());
                ne_sx_etxt.setText(qysxB.getDMMC1());
            }
            ne_hbpfwh_etxt.setText(mNoPassNewqy.getHBPZWH());
            CodeTypeBean isBhqB = codeType("ISBHQ", mNoPassNewqy.getISBHQ() == null ? "" : mNoPassNewqy.getISBHQ().toString().trim());
            if (isBhqB != null) {
                ne_sfsbhq_etxt.setText(isBhqB.getDMMC1());
                ne_sfsbhq_dmz.setText(isBhqB.getDMZ());
            }
            CodeTypeBean isHbysB = codeType("ISHBYS", mNoPassNewqy.getISHBYS() == null ? "" : mNoPassNewqy.getISHBYS().toString().trim());
            if (isHbysB != null) {
                ne_sftghbys_etxt.setText(isHbysB.getDMMC1());
                ne_sftghbys_dmz.setText(isHbysB.getDMZ());
            }
            CodeTypeBean scqkB = codeType("SCQK", mNoPassNewqy.getSCQK() == null ? "" : mNoPassNewqy.getSCQK().toString().trim());
            if (scqkB != null) {
                ne_scqk_dmz.setText(scqkB.getDMZ());
                ne_ktscqk_etxt.setText(scqkB.getDMMC1());
            }
            ne_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            ne_xmmzb_etxt.setText(mNoPassNewqy.getMJZB());
            ne_scmj_etxt.setText(String.valueOf(mNoPassNewqy.getTJMJ()));
            ne_syq_etxt.setText(String.valueOf(mNoPassNewqy.getSYMJ()));
            ne_hcq_etxt.setText(String.valueOf(mNoPassNewqy.getHCMJ()));
            ne_hxq_etxt.setText(String.valueOf(mNoPassNewqy.getHXMJ()));
            ne_lsqk_etxt.setText(mNoPassNewqy.getZGCS());
        }
    }

    private CodeTypeBean codeType(String dmlb, String dmz) {
        dbManager = new DBManager(context);
        String sql = "select * from codeType where dmlb = ? and dmz = ?";
        String[] bindArgs = new String[]{dmlb, dmz};
        List<CodeTypeBean> list = LocalBaseInfo.loadDataBySqlLite(sql, bindArgs, dbManager);
        return list.size() == 0 ? null : list.get(0);
    }
    //发送广播更新系统相册
    private void upDateDcim(String fileName) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(FileUtils.SDPATH + fileName + ".jpg"));
        Log.i(TAG, "upDateDcim: -------------filename:" + fileName);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
