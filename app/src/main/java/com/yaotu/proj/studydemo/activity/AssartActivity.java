package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
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
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPasskkhdqy;
import com.yaotu.proj.studydemo.bean.tableBean.AssartBean;
import com.yaotu.proj.studydemo.bean.tableBean.AssartJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.AssartRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkEnterpriseBean;
import com.yaotu.proj.studydemo.common.LocalBaseInfo;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.HandleImage;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.InsertLocalTableData;
import com.yaotu.proj.studydemo.customclass.PhotoImageSize;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;

/*
* 开垦活动
* */
public class AssartActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Context context = AssartActivity.this;
    private MapValueType valueType = null;
    private Intent intent;
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
    private TextView as_hxq_txt, as_hcq_txt, as_syq_txt;

    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private LinearLayout mapLayout;

    private String bhqid, bhqmc, bhqjb, bhqjbdm;
    private ImageButton dot_btn, reset_btn;//地图button
    private TextView txt_showInfo;
    private InitMap initMap;
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    //----------一下内容国家统一下发
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String jcdId;//监测点ID
    private String activityType;
    private NoPasskkhdqy mNoPasskkhdqy;
    private String jsxmID = "0";//添加为0；更行为原有id;
    private ProgressDialog progressDialog;
    private final int UPLOADSUCCEED = 0X000;//数据上传成功
    private final int UPLOADFAIL = 0X001;//数据上传失败
    private final int UPLOADPICSUCCEED = 0X002;//照片上传成功
    private final int UPLOADPICFAIL = 0X003;//照片上传失败
    private DBManager dbManager;
    private String dataUplocadSucceed = "";
    private String IPURL;
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
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assart);
        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);

        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        IPURL = getResources().getString(R.string.http_url);
    }

    private void initView() {
        as_photo_img = (ImageView) findViewById(R.id.as_photo_img);
        as_canleImage = (TextView) findViewById(R.id.as_canleImage);
        as_bhqmc_etxt = (EditText) findViewById(R.id.as_bhqmc_etxt);
        as_jb_etxt = (EditText) findViewById(R.id.as_jb_etxt);
        as_kkqkmc_etxt = (EditText) findViewById(R.id.as_kkqkmc_etxt);
        as_kkmj_etxt = (EditText) findViewById(R.id.as_kkmj_etxt);
        as_zwzl_etxt = (EditText) findViewById(R.id.as_zwzl_etxt);
        as_zxzb_etxt = (EditText) findViewById(R.id.as_zxzb_etxt);
        as_xmmzb_etxt = (EditText) findViewById(R.id.as_xmmzb_etxt);
        as_scmj_etxt = (EditText) findViewById(R.id.as_scmj_etxt);
        as_sjqh_groupRadio = (RadioGroup) findViewById(R.id.as_sjqh_groupRadio);
        as_sjqh_radio1 = (RadioButton) findViewById(R.id.as_sjqh_radio1);
        as_sjqh_radio2 = (RadioButton) findViewById(R.id.as_sjqh_radio2);
        as_syq_etxt = (EditText) findViewById(R.id.as_syq_etxt);
        as_hcq_etxt = (EditText) findViewById(R.id.as_hcq_etxt);
        as_hxq_etxt = (EditText) findViewById(R.id.as_hxq_etxt);
        as_sfgz_groupRadio = (RadioGroup) findViewById(R.id.as_sfgz_groupRadio);
        as_sfgz_radio1 = (RadioButton) findViewById(R.id.as_sfgz_radio1);
        as_sfgz_radio2 = (RadioButton) findViewById(R.id.as_sfgz_radio2);
        as_lsqk_etxt = (EditText) findViewById(R.id.as_lsqk_etxt);
        as_hcq_txt = (TextView) findViewById(R.id.as_hcq_txt);
        as_hxq_txt = (TextView) findViewById(R.id.as_hxq_txt);
        as_syq_txt = (TextView) findViewById(R.id.as_syq_txt);

        mapView = (MapView) findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
        as_bhqmc_etxt.setText(bhqmc);
        as_jb_etxt.setText(bhqjb);

    }

    private void initMethod() {
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
        //双击获取项目面坐标

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
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        as_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                as_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
            }
        });
        /*始建前后/是否耕种*/
        as_sjqh_groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (as_sjqh_radio1.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------始建前后------->" + as_sjqh_radio1.getText().toString());
                    as_Sjqh = as_sjqh_radio1.getText().toString().trim();
                } else if (as_sjqh_radio2.getId() == checkedId) {
                    Log.i(TAG, "onCheckedChanged: ------始建前后------->" + as_sjqh_radio2.getText().toString());
                    as_Sjqh = as_sjqh_radio2.getText().toString().trim();
                }
            }
        });
        as_sfgz_groupRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (as_sfgz_radio1.getId() == checkedId) {
                    as_Sfgz = as_sfgz_radio1.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: -----是否耕种------->" + as_Sfgz);
                } else if (as_sfgz_radio2.getId() == checkedId) {
                    as_Sfgz = as_sfgz_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: -----是否耕种------->" + as_Sfgz);
                }
            }
        });
    }

    //=========================================================================================
    public void getValueMethod(View view) {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    as_zxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
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
                        as_xmmzb_etxt.setText("未获得点...");
                    } else {
                        as_xmmzb_etxt.setText("[[" + sb.toString() + "]]");
                        as_scmj_etxt.setText(String.format("%.2f", initMap.getArea()));
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

    //=========================================================================================
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
                    as_photo_img.setImageBitmap(bitmap);
                    Log.i("TAG", "onActivityResult:-------相册相片路径---------> " + photoPath);
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
                Bitmap smallBitmap = PhotoImageSize.revitionImageSize(photoPath);
                if (null != smallBitmap) {
                    as_photo_img.setImageBitmap(bitmap);
                }

                upDateDcim(file.getName());
                break;
            default:
                break;
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

    }
    //===============================提交/保存=======================================================

    public void submitBtnMethod(View view) {
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AssartBean assartBean = getAssartBean();
                if (IsExist(assartBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertAssartQyInfo(assartBean, 0 + "");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
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
    private boolean threadFlag = true;
    private boolean picthreadFlag = true;

    private void upload() {
        //1.组织要提交的数据
        AssartJsonBean jsonBean = new AssartJsonBean();
        List<AssartRecordBean> records = new ArrayList<>();
        final AssartBean bean = getAssartBean();
        AssartRecordBean record = new AssartRecordBean();
        record.setObjectid(jcdId);
        record.setCenterpointx(jcdLongitude);
        record.setCenterpointy(jcdlatitude);
        record.setBean(bean);
        records.add(record);
        jsonBean.setKey("06");
        if (activityType.equals("add")) {
            jsonBean.setIschecked("21");
        } else {
            jsonBean.setIschecked("22");
        }
        jsonBean.setYhdh(TempData.yhdh);
        jsonBean.setRecord(records);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------开垦活动上传数据----------->" + jsonStr);
        //2.连接服务器上传
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否上传数据？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                threadFlag = true;
                picthreadFlag = true;
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("正在上传数据...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        threadFlag = false;
                        picthreadFlag = false;
                    }
                });
                progressDialog.show();


                new Thread(new Runnable() {//上传数据
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                       /* try {
                            Thread.sleep(2 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while (threadFlag) {
                            Message message = Message.obtain();
                            Response response = ParseIntentData.getDataPostByJson(IPURL + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
                            if (response != null && response.code() == 200) {
                                try {
                                    String responseValue = response.body().string();
                                    Log.i(TAG, "run: -----------------dd---------->"+responseValue);
                                    dataUplocadSucceed = responseValue;
                                    message.what = UPLOADSUCCEED;
                                    myHandler.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                threadFlag = false;
                            } else {
                                message.what = UPLOADFAIL;
                                myHandler.sendMessage(message);
                                threadFlag = false;
                            }
                        }*/
                        message.what = UPLOADSUCCEED;
                        myHandler.sendMessage(message);
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

    //=============================================================================================
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (initMap == null) {
            initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
        }
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
            as_bhqmc_etxt.setText(bhqmc);
            as_jb_etxt.setText(bhqjb);
            as_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            jsxmID = "0";
        } else {
            mNoPasskkhdqy = (NoPasskkhdqy) intent.getSerializableExtra("bdData");
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
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        initMap.stopLocationListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //==============================初始化表单数据===================================================
    public AssartBean getAssartBean() {
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
        String ybhqwzgx = sb.toString();//与保护区位置关系
        ab.setBhqid(bhqid);
        ab.setBhqmc(bhqmc);
        ab.setJsxmid(jsxmID);
        ab.setKkqkmc(as_kkqkmc_etxt.getText().toString().trim());
        ab.setJsxmjb(bhqjbdm);//as_jb_etxt.getText().toString().trim()
        ab.setKkmj(as_kkmj_etxt.getText().toString().trim());
        ab.setTrzj("");
        ab.setNcz("");
        ab.setZwzl(as_zwzl_etxt.getText().toString().trim());
        ab.setBjbhqsj(as_Sjqh);
        ab.setIsgz(as_Sfgz);
        ab.setYbhqgx(ybhqwzgx);
        ab.setZgcs(as_lsqk_etxt.getText().toString().trim());
        String centerpoint = as_zxzb_etxt.getText().toString().trim();
        String pointx = "";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->" + centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp + 1);
        }
        ab.setCenterpointx(pointx);
        ab.setCenterpointy(pointy);
        ab.setMjzb(as_xmmzb_etxt.getText().toString().trim());
        ab.setTjmj(as_scmj_etxt.getText().toString().trim());
        ab.setHxmj(as_hxq_etxt.getText().toString().trim());
        ab.setHcmj(as_hcq_etxt.getText().toString().trim());
        ab.setSymj(as_syq_etxt.getText().toString());
        ab.setPhotoPath(photoPath);
        ab.setPlaceid(jcdId);
        ab.setUsername(TempData.yhdh);
        //ab.setIschecked("21");//审核状态：默认-->21;未通过--->0;提交--->22

        return ab;
    }

    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(AssartBean bean) {
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql = "select * from AssartqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(), bean.getKkqkmc(), bean.getUsername()};
        List<AssartBean> list = query.queryAssartQyInfos(sql, strs);
        if (list != null) {
            if (list.size() > 0) {
                return true;//存在
            }
        }
        return false;//不存在
    }

    /*插入本地数据
    * */
    private boolean insertAssartQyInfo(AssartBean bean, String upState) {

        boolean result = false;
        if (bean != null) {
            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertAssartQyInfo(bean, upState);
        }
        return result;
    }

    //==========================上传图片＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    private void uploadPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                int bitMapSize = ParseIntentData.getBitmapSize(bitmap);
                Log.i(TAG, "run: -------bitmap大小----->" + bitMapSize);
                //if (bitMapSize > 202800) {
                    File file = FileUtils.saveBitmap(PhotoImageSize.revitionImageSize(photoPath), String.valueOf(System.currentTimeMillis()));
                    photoPath = file.getAbsolutePath();
                    upDateDcim(file.getName());
                //}
                String url = "http://192.168.0.21/Demo04/Home/androidImage";
                //String url = IPURL+"/BhqJsxm/Upload";
                Response response = upLoadPhoto(url, photoPath, new AssartBean());
                //Response response = ParseIntentData.upLoadImage(url, photoPath, dataUplocadSucceed);
                if (response != null && response.code() == 200) {
                    message.what = UPLOADPICSUCCEED;
                } else {
                    message.what = UPLOADPICFAIL;
                }
                myHandler.sendMessage(message);
            }
        }).start();

    }

    private Response upLoadPhoto(String ipUrl, String photoPath, AssartBean bean) {
        Response response = null;
        List<File> files = new ArrayList<>();
        File file = new File(photoPath);
        files.add(file);
        Map<String, String> params = new HashMap<>();
        params.put("jsxmmc", bean.getKkqkmc());
        params.put("hello", "world");
        response = ParseIntentData.upLoadImages(ipUrl, params, "PicKey", files);
        return response;
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

    //========================================================================================
    private void setViewData() {
        if (mNoPasskkhdqy != null) {
            jsxmID = mNoPasskkhdqy.getJSXMID();
            bhqid = mNoPasskkhdqy.getBHQID();
            as_bhqmc_etxt.setText(bhqmc);
            as_kkqkmc_etxt.setText(mNoPasskkhdqy.getKKQKMC());
            CodeTypeBean bhqjbB = codeType("BHQJB", mNoPasskkhdqy.getJSXMJB() == null ? "" : mNoPasskkhdqy.getJSXMJB().toString().trim());
            if (bhqjbB != null) {
                as_jb_etxt.setText(bhqjbB.getDMMC1());
                bhqjbdm = bhqjbB.getDMZ();
            }
            as_kkmj_etxt.setText(String.valueOf(mNoPasskkhdqy.getKKMJ()));
            as_zwzl_etxt.setText(mNoPasskkhdqy.getZWZL());
            as_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            as_scmj_etxt.setText(String.valueOf(mNoPasskkhdqy.getTJMJ()));
            if (as_sjqh_radio1.getText().equals(mNoPasskkhdqy.getBJBHQSJ() == null ? "" : mNoPasskkhdqy.getBJBHQSJ().toString().trim())) {
                as_sjqh_radio1.setChecked(true);
            } else if (as_sjqh_radio2.getText().equals(mNoPasskkhdqy.getBJBHQSJ() == null ? "" : mNoPasskkhdqy.getBJBHQSJ().toString().trim())) {
                as_sjqh_radio2.setChecked(true);
            }
            as_hxq_etxt.setText(String.valueOf(mNoPasskkhdqy.getHXMJ()));
            as_hcq_etxt.setText(String.valueOf(mNoPasskkhdqy.getHCMJ()));
            as_syq_etxt.setText(String.valueOf(mNoPasskkhdqy.getSYMJ()));
            if (as_sfgz_radio1.getText().equals(mNoPasskkhdqy.getISGZ() == null ? "" : mNoPasskkhdqy.getISGZ().toString().trim())) {
                as_sfgz_radio1.setChecked(true);
            } else if (as_sfgz_radio2.getText().equals(mNoPasskkhdqy.getISGZ() == null ? "" : mNoPasskkhdqy.getISGZ().toString().trim())) {
                as_sfgz_radio2.setChecked(true);
            }
            as_lsqk_etxt.setText(mNoPasskkhdqy.getZGCS());
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
    private void upDateDcim(String fileName){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(FileUtils.SDPATH + fileName + ".jpg"));
        Log.i(TAG, "upDateDcim: -------------filename:"+fileName);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
}
