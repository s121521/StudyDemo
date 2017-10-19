package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.baidu.mapapi.ModuleName;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassKczyqy;
import com.yaotu.proj.studydemo.bean.tableBean.KczyJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.KczyRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.kczyBean;
import com.yaotu.proj.studydemo.common.LocalBaseInfo;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.DateDialog;
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

/*矿产资源开发企业*/
public class KczyDevelopEnterpriseActivity extends AppCompatActivity {
    private Context context = KczyDevelopEnterpriseActivity.this;
    private final String TAG = this.getClass().getSimpleName();
    private Intent intent;
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
    private String bhqid,bhqmc,bhqjb,bhqjbdm;
    private TextView kc_hxq_txt,kc_hcq_txt,kc_syq_txt;

    private MapValueType valueType = null;
    private LinearLayout mapLayout;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn,reset_btn;//地图button
    private TextView txt_showInfo;
    private InitMap initMap;
    //----------一下内容国家统一下发
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String jcdId;//监测点ID
    private String activityType;
    private NoPassKczyqy mNoPassKczyqy;
    private String jsxmID = "0";//添加为0；更行为原有id;
    private DBManager dbManager;
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
                    showMessage("上传成功");
                    break;
                case UPLOADFAIL:
                    progressDialog.dismiss();
                    showMessage("上传失败...");
                    break;
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kczy_develop_enterprise);
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
    }
    private void initView(){
        kc_photo_img = (ImageView) findViewById(R.id.kc_photo_img);
        kc_canleImage = (TextView) findViewById(R.id.kc_canleImage);
        kc_bhqmc_etxt = (EditText) findViewById(R.id.kc_bhqmc_etxt);
        kc_jb_etxt = (EditText) findViewById(R.id.kc_jb_etxt);
        kc_qymc_etxt = (EditText) findViewById(R.id.kc_qymc_etxt);
        kc_gm_etxt = (EditText) findViewById(R.id.kc_gm_etxt);
        kc_fzjg_etxt = (EditText) findViewById(R.id.kc_fzjg_etxt);
        kc_zjyxqb_etxt = (EditText) findViewById(R.id.kc_zjyxqb_etxt);
        kc_zjyxqe_etxt = (EditText) findViewById(R.id.kc_zjyxqe_etxt);
        kc_zxzb_etxt = (EditText) findViewById(R.id.kc_zxzb_etxt);
        kc_xmmzb_etxt = (EditText) findViewById(R.id.kc_xmmzb_etxt);
        kc_scmj_etxt = (EditText) findViewById(R.id.kc_scmj_etxt);
        kc_sjqh_radioGroup = (RadioGroup) findViewById(R.id.kc_sjqh_radioGroup);
        kc_sjqh_radio1 = (RadioButton) findViewById(R.id.kc_sjqh_radio1);
        kc_sjqh_radio2 = (RadioButton) findViewById(R.id.kc_sjqh_radio2);
        kc_kqslqk_etxt = (EditText) findViewById(R.id.kc_kqslqk_etxt);
        kc_kcfs_etxt = (EditText) findViewById(R.id.kc_kcfs_etxt);
        kc_kqsx_etxt = (EditText) findViewById(R.id.kc_kqsx_etxt);
        kc_hbpfwh_etxt = (EditText) findViewById(R.id.kc_hbpfwh_etxt);
        kc_sfsbhq_etxt = (EditText) findViewById(R.id.kc_sfsbhq_etxt);
        kc_sftghbys_etxt = (EditText) findViewById(R.id.kc_sftghbys_etxt);
        kc_scqk_etxt = (EditText) findViewById(R.id.kc_scqk_etxt);
        kc_syq_etxt = (EditText) findViewById(R.id.kc_syq_etxt);
        kc_hcq_etxt = (EditText) findViewById(R.id.kc_hcq_etxt);
        kc_hxq_etxt = (EditText) findViewById(R.id.kc_hxq_etxt);
        kc_lsqk_etxt = (EditText) findViewById(R.id.kc_lsqk_etxt);
        kc_kcfs_dmz = (TextView) findViewById(R.id.kc_kcfs_dmz);
        kc_sfsbhq_dmz = (TextView) findViewById(R.id.kc_sfsbhq_dmz);
        kc_sftghbys_dmz = (TextView) findViewById(R.id.kc_sftghbys_dmz);
        kc_scqk_dmz = (TextView) findViewById(R.id.kc_scqk_dmz);

        kc_hxq_txt = (TextView) findViewById(R.id.kc_hxq_txt);
        kc_hcq_txt = (TextView) findViewById(R.id.kc_hcq_txt);
        kc_syq_txt = (TextView) findViewById(R.id.kc_syq_txt);

        mapView = (MapView) findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();

        kc_bhqmc_etxt.setText(bhqmc);
        kc_jb_etxt.setText(bhqjb);
    }
    private void initMethod(){
         /*
        * 打开相册或照相机
        * */
        kc_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        kc_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kc_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
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
    }
    //=====================map==================================================================
    public void getValueMethod(View view) {//获取值
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
    public void moveCurrentPosition(View view) {//移动到当前位置
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(),m_location.getLongitude())),16));
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
                        startActivityForResult(intent,OPEN_RESULT);
                        dialog.dismiss();
                        break;
                    case 1:
                        Log.i("TAG", "onClick: 1" + items[which]);
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,PICK_RESULT);
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
                        photoPath = HandleImage.handleImgeOnKitKat(context,data);
                    }else {
                        photoPath = HandleImage.handleImageBeforeKitKat(context,data);
                    }
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(photoPath);
                    kc_photo_img.setImageBitmap(bitmap);
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
                kc_photo_img.setImageBitmap(bitmap);
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
    //=======================================提交、保存============================================
    public void submitBtnMethod(View view){
        int netType = CheckNetwork.getNetWorkState(context);
        if (kc_qymc_etxt.getText().toString().trim().equals("")) {
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
    public void saveBtnMethod(View view){
        if (kc_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空!");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                kczyBean industryBean = getKczyBean();
                if (IsExist(industryBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertKczyqyInfo(industryBean,0+"");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
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
    private void upload(){
        //1.组织要提交的数据
        KczyJsonBean jsonBean = new KczyJsonBean();
        List<KczyRecordBean> records = new ArrayList<>();
        kczyBean bean = getKczyBean();
        KczyRecordBean recordBean = new KczyRecordBean();
        recordBean.setObjectid(jcdId);
        recordBean.setCenterpointx(jcdLongitude);
        recordBean.setCenterpointy(jcdlatitude);
        recordBean.setBean(bean);
        records.add(recordBean);
        jsonBean.setKey("01");
        jsonBean.setYhdh(TempData.username);
        if (activityType.equals("add")) {
            jsonBean.setIschecked("21");
        } else {
            jsonBean.setIschecked("22");
        }
        jsonBean.setRecord(records);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------矿产资源上传数据----------->"+jsonStr);
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
                            Response response = ParseIntentData.getDataPostByJson(IPURL + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo",jsonStr);
                            Message message = Message.obtain();
                            if (response != null && response.code() == 200) {
                                try {
                                    String responseValue = response.body().string();
                                    if (responseValue.contains("success")) {
                                        message.what = UPLOADSUCCEED;
                                        myHandler.sendMessage(message);
                                    } else{
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
    //-------------------------------------------------------------------------------------------
    public void showMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    //----------------------------------------------------------------------------------------------

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
            jcdLongitude =intent.getDoubleExtra("longitude",0);
            jcdlatitude =intent.getDoubleExtra("latitude",0);
            //------------------------------
            kc_bhqmc_etxt.setText(bhqmc);
            kc_jb_etxt.setText(bhqjb);
            kc_zxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
            jsxmID = "0";
        } else {
            mNoPassKczyqy = (NoPassKczyqy) intent.getSerializableExtra("bdData");
            bhqmc = intent.getStringExtra("bdBhqmc");
            jcdLongitude = intent.getDoubleExtra("bdjd",0);
            jcdlatitude = intent.getDoubleExtra("bdwd", 0);
            jcdId = intent.getStringExtra("bdobjid");
            Log.i(TAG, "onResume: ----------bhqmc:"+bhqmc);
            //--------------------------------------------
            setViewData();//获取更新数据
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //==========================================初始化表单数据=======================================
    private kczyBean getKczyBean(){
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
        bean.setJsxmjb(bhqjbdm);//kc_jb_etxt.getText().toString().trim()
        bean.setJsxmid(jsxmID);
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

        bean.setPhotoPath(photoPath);
        bean.setPlaceid(jcdId);
        bean.setUsername(TempData.username);

        return bean;
    }

    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(kczyBean bean){
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql ="select * from kczyqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(),bean.getJsxmmc(),bean.getUsername()};
        List<kczyBean> list = query.queryKczyQyInfos(sql,strs);
        if (list != null) {
            if (list.size() >0) {
                return true;//存在
            }
        }
        return false;//不存在
    }
    /*插入本地数据
    * */
    private boolean insertKczyqyInfo(kczyBean bean,String upState){

        boolean result = false;
        if (bean != null) {
            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertKczyQyInfo(bean, upState);
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
    private void setViewData(){
        if (mNoPassKczyqy != null) {
            jsxmID = mNoPassKczyqy.getJSXMID();
            bhqid = mNoPassKczyqy.getBHQID();

            kc_bhqmc_etxt.setText(bhqmc);
            CodeTypeBean bhqjbB = codeType("BHQJB", mNoPassKczyqy.getJSXMJB()==null?"":mNoPassKczyqy.getJSXMJB().toString().trim());
            if (bhqjbB != null) {
                kc_jb_etxt.setText(bhqjbB.getDMMC1());
                bhqjbdm = bhqjbB.getDMZ();
            }
            kc_qymc_etxt.setText(mNoPassKczyqy.getJSXMMC());
            kc_gm_etxt.setText(mNoPassKczyqy.getJSXMGM());
            kc_fzjg_etxt.setText(mNoPassKczyqy.getFZJG());
            if (mNoPassKczyqy.getZJYXQB() != null) {
                kc_zjyxqb_etxt.setText(dateConversion(mNoPassKczyqy.getZJYXQB()));
            }
            if (mNoPassKczyqy.getZJYXQE() != null) {
                kc_zjyxqe_etxt.setText(dateConversion(mNoPassKczyqy.getZJYXQE()));
            }

            if (kc_sjqh_radio1.getText().equals(mNoPassKczyqy.getBJBHQSJ()==null?"":mNoPassKczyqy.getBJBHQSJ().toString().trim())) {
                kc_sjqh_radio1.setChecked(true);
            } else if ( kc_sjqh_radio2.getText().equals(mNoPassKczyqy.getBJBHQSJ()==null?"":mNoPassKczyqy.getBJBHQSJ().toString().trim())) {
                kc_sjqh_radio2.setChecked(true);
            }
            kc_kqslqk_etxt.setText(mNoPassKczyqy.getKQSLQK());
            CodeTypeBean kcfsB = codeType("KCFS", mNoPassKczyqy.getKCFS() == null ? "" : mNoPassKczyqy.getKCFS().toString().trim());
            if (kcfsB != null) {
                kc_kcfs_etxt.setText(kcfsB.getDMMC1());
                kc_kcfs_dmz.setText(kcfsB.getDMZ());
            }
            kc_kqsx_etxt.setText(mNoPassKczyqy.getKQSX());
            kc_hbpfwh_etxt.setText(mNoPassKczyqy.getHBPZWH());
            CodeTypeBean isBhqB = codeType("ISBHQ", mNoPassKczyqy.getISBHQ()==null?"":mNoPassKczyqy.getISBHQ().toString().trim());
            if (isBhqB != null) {
                kc_sfsbhq_etxt.setText(isBhqB.getDMMC1());
                kc_sfsbhq_dmz.setText(isBhqB.getDMZ());
            }
            CodeTypeBean isHbysB = codeType("ISHBYS", mNoPassKczyqy.getISHBYS() == null ? "" :mNoPassKczyqy.getISHBYS().toString().trim());
            if (isHbysB != null) {
                kc_sftghbys_etxt.setText(isHbysB.getDMMC1());
                kc_sftghbys_dmz.setText(isBhqB.getDMZ());
            }
            CodeTypeBean scqkB = codeType("SCQK", mNoPassKczyqy.getSCQK() == null ? "" : mNoPassKczyqy.getSCQK().toString().trim());
            if (scqkB != null) {
                kc_scqk_etxt.setText(scqkB.getDMMC1());
                kc_scqk_dmz.setText(scqkB.getDMZ());
            }
            kc_zxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
            kc_xmmzb_etxt.setText(mNoPassKczyqy.getMJZB());
            kc_scmj_etxt.setText(String.valueOf(mNoPassKczyqy.getTJMJ()));
            kc_hxq_etxt.setText(String.valueOf(mNoPassKczyqy.getHXMJ()));
            kc_hcq_etxt.setText(String.valueOf(mNoPassKczyqy.getHCMJ()));
            kc_syq_etxt.setText(String.valueOf(mNoPassKczyqy.getSYMJ()));
            kc_lsqk_etxt.setText(mNoPassKczyqy.getZGCS());

        }
    }
    private String dateConversion(String str){// /Date(1499097600000)/用Java怎么转换成yyyy-MM-dd的格式
        str=str.replace("/Date(","").replace(")/","");
        Date date = new Date(Long.parseLong(str));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Log.i(TAG, "dateConversion: ---------------"+str);
        return format.format(date);
    }
    private CodeTypeBean codeType(String dmlb, String dmz){
        dbManager = new DBManager(context);
        String sql = "select * from codeType where dmlb = ? and dmz = ?";
        String[] bindArgs = new String[]{dmlb,dmz};
        List<CodeTypeBean> list = LocalBaseInfo.loadDataBySqlLite(sql,bindArgs,dbManager);
        return list.size() == 0 ? null : list.get(0);
    }
}
