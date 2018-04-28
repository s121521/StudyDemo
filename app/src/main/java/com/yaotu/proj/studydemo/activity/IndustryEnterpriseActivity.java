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
import com.esri.core.geometry.AngularUnit;
import com.google.gson.Gson;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassGyqy;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryRecordBean;
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
import com.yaotu.proj.studydemo.util.HttpUrlAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/*工业企业*/
public class IndustryEnterpriseActivity extends AppCompatActivity {
    private Context context = IndustryEnterpriseActivity.this;
    private final String TAG = this.getClass().getSimpleName();
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
    private TextView in_qysx_dmz, in_yxqk_dmz, in_sfsbhq_dmz, in_sftghbys_dmz;
    private EditText in_sftghbys_etxt;//是否通过环保验收
    private EditText in_syq_etxt;//实验区
    private EditText in_hcq_etxt;//缓冲区
    private EditText in_hxq_etxt;//核心区
    private EditText in_lsqk_etxt;//整改落实情况
    private TextView in_syq_txt, in_hcq_txt, in_hxq_txt;
    private TextView in_canleImage;
    private ImageView in_photo_img;
    private String bhqid, bhqmc, bhqjb, bhqjbdm;
    private Intent intent;
    private DBManager dbManager;
    private MapValueType valueType = null;
    private LinearLayout mapLayout;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private ImageButton dot_btn, reset_btn;//地图button
    private TextView txt_showInfo;

    private InitMap initMap;
    private List<Long> times = new ArrayList<Long>();//触摸事件标志
    private double jcdLongitude;//监测点经度
    private double jcdlatitude;//监测点纬度
    private String jcdId;//监测点ID
    private String activityType;
    private NoPassGyqy mNoPassGyqy;
    private String jsxmID = "0";//添加为0；更行为原有id;
    private ProgressDialog progressDialog;
    private final int UPLOADSUCCEED = 0X000;
    private final int UPLOADFAIL = 0X001;
    private final int UPLOADPICSUCCEED = 0X002;//照片上传成功
    private final int UPLOADPICFAIL = 0X003;//照片上传失败
    private String dataUplocadSucceed;
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
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_enterprise);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);
        initView();
        initMethod();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
        IPURL = HttpUrlAddress.getHttpUrl();//getResources().getString(R.string.http_url);
    }

    private void initView() {
        in_qysx_etxt = (EditText) findViewById(R.id.in_qysx_etxt);
        in_yxqk_etxt = (EditText) findViewById(R.id.in_yxqk_etxt);
        in_bhqmc_etxt = (EditText) findViewById(R.id.in_bhqmc_etxt);
        in_jb_etxt = (EditText) findViewById(R.id.in_jb_etxt);
        in_photo_img = (ImageView) findViewById(R.id.in_photo_img);
        in_canleImage = (TextView) findViewById(R.id.in_canleImage);
        in_qymc_etxt = (EditText) findViewById(R.id.in_qymc_etxt);
        in_gm_etxt = (EditText) findViewById(R.id.in_gm_etxt);
        in_zxzb_etxt = (EditText) findViewById(R.id.in_zxzb_etxt);
        in_xmmzb_etxt = (EditText) findViewById(R.id.in_xmmzb_etxt);
        in_scmj_etxt = (EditText) findViewById(R.id.in_scmj_etxt);
        in_sjqh_radioGroup = (RadioGroup) findViewById(R.id.in_sjqh_radioGroup);
        in_sjqh_radio1 = (RadioButton) findViewById(R.id.in_sjqh_radio1);
        in_sjqh_radio2 = (RadioButton) findViewById(R.id.in_sjqh_radio2);
        in_lsyg_etxt = (EditText) findViewById(R.id.in_lsyg_etxt);
        in_hbpfwh_etxt = (EditText) findViewById(R.id.in_hbpfwh_etxt);
        in_sfsbhq_etxt = (EditText) findViewById(R.id.in_sfsbhq_etxt);
        in_sftghbys_etxt = (EditText) findViewById(R.id.in_sftghbys_etxt);
        in_syq_etxt = (EditText) findViewById(R.id.in_syq_etxt);
        in_hcq_etxt = (EditText) findViewById(R.id.in_hcq_etxt);
        in_hxq_etxt = (EditText) findViewById(R.id.in_hxq_etxt);
        in_lsqk_etxt = (EditText) findViewById(R.id.in_lsqk_etxt);
        in_qysx_dmz = (TextView) findViewById(R.id.in_qysx_dmz);
        in_yxqk_dmz = (TextView) findViewById(R.id.in_yxqk_dmz);
        in_sfsbhq_dmz = (TextView) findViewById(R.id.in_sfsbhq_dmz);
        in_sftghbys_dmz = (TextView) findViewById(R.id.in_sftghbys_dmz);
        in_hcq_txt = (TextView) findViewById(R.id.in_hcq_txt);
        in_hxq_txt = (TextView) findViewById(R.id.in_hxq_txt);
        in_syq_txt = (TextView) findViewById(R.id.in_syq_txt);

        mapView = (MapView) findViewById(R.id.report_mapview);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        txt_showInfo = (TextView) findViewById(R.id.report1_textview_area);
        baiduMap = mapView.getMap();
    }

    private void initMethod() {
        /*打开企业属性对话框
        * */
        in_qysx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, in_qysx_dmz, in_qysx_etxt, "企业属性", "QYSX");
                cd.showQksxDialog();
            }
        });
        /*打开运行情况对话框
        * */
        in_yxqk_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, in_yxqk_dmz, in_yxqk_etxt, "运行情况", "SCQK");
                cd.showQksxDialog();
            }
        });
        /*
        * 是否涉保护区
        * */
        in_sfsbhq_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, in_sfsbhq_dmz, in_sfsbhq_etxt, "是否设保护区", "ISBHQ");
                cd.showQksxDialog();
            }
        });
        /*是否通过环保验收
        * */
        in_sftghbys_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog cd = new CustomDialog(context, in_sftghbys_dmz, in_sftghbys_etxt, "是否通过环保验收", "ISHBYS");
                cd.showQksxDialog();
            }
        });
        /*
        * 打开相册或照相机
        * */
        in_photo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        /*
        * 清除选中相片
        * */
        in_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                in_photo_img.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
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
                    Log.i(TAG, "onCheckedChanged: ----------------始建前后----->" + in_sjqhStr);
                } else if (in_sjqh_radio2.getId() == checkedId) {
                    in_sjqhStr = in_sjqh_radio2.getText().toString().trim();
                    Log.i(TAG, "onCheckedChanged: ----------------始建前后----->" + in_sjqhStr);
                }
            }
        });
    }

    /**
     * @param item
     * @return
     */
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
                    in_photo_img.setImageBitmap(bitmap);
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
                in_photo_img.setImageBitmap(bitmap);
                //发送广播更新系统相册
                upDateDcim(file.getName());
                break;
            default:
                break;
        }
    }

    //=================================map============================================================
    public void getValueMethod(View view) {//获取值
        mapLayout.setVisibility(View.GONE);
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    in_zxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
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
                        in_xmmzb_etxt.setText("未获得点...");
                    } else {
                        in_xmmzb_etxt.setText("[[" + sb.toString() + "]]");
                        in_scmj_etxt.setText(String.format("%.2f", initMap.getArea()));
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

    //================================提交/保存====================================================
    public void submitBtnMethod(View view) {
        int netType = CheckNetwork.getNetWorkState(context);
        if (in_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空！");
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
        if (in_qymc_etxt.getText().toString().trim().equals("")) {
            showMessage("企业名称不能为空！");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否保存本地？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IndustryBean industryBean = getIndustryBean();
                if (IsExist(industryBean)) {
                    showMessage("该企业在本地数据已经存在!");
                } else {
                    boolean inResult = insertGyqyInfo(industryBean, 0 + "");//存入本地数据库，0--表示未上传服务器，1--表示已上传服务器
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

    //=========================================================================================
    private boolean threadFlag;

    private void upload() {
        //1.组织要提交的数据
        IndustryJsonBean jsonBean = new IndustryJsonBean();
        List<IndustryRecordBean> records = new ArrayList<>();
        IndustryBean bean = getIndustryBean();
        IndustryRecordBean recordBean = new IndustryRecordBean();
        recordBean.setObjectid(jcdId);
        recordBean.setCenterpointx(jcdLongitude);
        recordBean.setCenterpointy(jcdlatitude);
        recordBean.setBean(bean);
        records.add(recordBean);
        jsonBean.setKey("03");
        jsonBean.setYhdh(TempData.yhdh);
        if (activityType.equals("add")) {
            jsonBean.setIschecked("21");
        } else {
            jsonBean.setIschecked("22");
        }
        jsonBean.setRecord(records);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------工业企业上传数据----------->" + jsonStr);
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
                        while (threadFlag) {
                            try {
                                Thread.sleep(2 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
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
    private void uploadPic(){
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
                String url = IPURL+"/BhqJsxm/Upload";
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
    //=============================================================================================
    public void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

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
            in_bhqmc_etxt.setText(bhqmc);
            in_jb_etxt.setText(bhqjb);
            in_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            jsxmID = "0";
        } else {
            mNoPassGyqy = (NoPassGyqy) intent.getSerializableExtra("bdData");
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

    //====================================初始化表单数据========================================
    private IndustryBean getIndustryBean() {
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
        String ybhqwzgx = sb.toString();//与保护区位置关系
        ib.setJsxmid(jsxmID);
        ib.setJsxmjb(bhqjbdm);//in_jb_etxt.getText().toString().trim()
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
        String pointx = "";
        String pointy = "";
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->" + centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp + 1);
        }
        ib.setCenterpointx(pointx);
        ib.setCenterpointy(pointy);
        ib.setMjzb(in_xmmzb_etxt.getText().toString().trim());
        ib.setTjmj(in_scmj_etxt.getText().toString().trim());
        ib.setHxmj(in_hxq_etxt.getText().toString().trim());
        ib.setHcmj(in_hcq_etxt.getText().toString().trim());
        ib.setSymj(in_syq_etxt.getText().toString().trim());


        ib.setPhotoPath(photoPath);
        ib.setBhqmc(bhqmc);

        ib.setUsername(TempData.yhdh);
        ib.setPlaceid(jcdId);
        return ib;
    }

    //-----------------查询本地数据库是否已经存在该企业名称--------------------------------
    private boolean IsExist(IndustryBean bean) {
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql = "select * from GyqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
        String[] strs = new String[]{bean.getBhqid(), bean.getJsxmmc(), bean.getUsername()};
        List<IndustryBean> list = query.queryGyqyInfos(sql, strs);
        if (list != null) {
            if (list.size() > 0) {
                return true;//存在
            }
        }
        return false;//不存在
    }

    /*插入本地数据
    * */
    private boolean insertGyqyInfo(IndustryBean bean, String upState) {

        boolean result = false;
        if (bean != null) {

            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.InsertGyqyInfo(bean, upState);
        }
        return result;
    }

    //=========================================================================================
    private void setViewData() {
        if (mNoPassGyqy != null) {
            jsxmID = mNoPassGyqy.getJSXMID();
            bhqid = mNoPassGyqy.getBHQID();
            in_bhqmc_etxt.setText(bhqmc);
            in_jb_etxt.setText(mNoPassGyqy.getJSXMJB());
            CodeTypeBean bhqjbB = codeType("BHQJB", mNoPassGyqy.getJSXMJB() == null ? "" : mNoPassGyqy.getJSXMJB().toString().trim());
            if (bhqjbB != null) {
                in_jb_etxt.setText(bhqjbB.getDMMC1());
                bhqjbdm = bhqjbB.getDMZ();
            }
            in_qymc_etxt.setText(mNoPassGyqy.getJSXMMC() == null ? "" : mNoPassGyqy.getJSXMMC().toString().trim());
            in_gm_etxt.setText(mNoPassGyqy.getJSXMGM() == null ? "" : mNoPassGyqy.getJSXMGM().toString().trim());
            in_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            in_xmmzb_etxt.setText(mNoPassGyqy.getMJZB());
            in_scmj_etxt.setText(String.valueOf(mNoPassGyqy.getTJMJ()));
            if (in_sjqh_radio1.getText().equals(mNoPassGyqy.getBJBHQSJ() == null ? "" : mNoPassGyqy.getBJBHQSJ().toString().trim())) {
                in_sjqh_radio1.setChecked(true);
            } else if (in_sjqh_radio2.getText().equals(mNoPassGyqy.getBJBHQSJ() == null ? "" : mNoPassGyqy.getBJBHQSJ().toString().trim())) {
                in_sjqh_radio2.setChecked(true);
            }
            in_lsyg_etxt.setText(mNoPassGyqy.getLSYG() == null ? "" : mNoPassGyqy.getLSYG().toString().trim());
            CodeTypeBean qysxB = codeType("QYSX", mNoPassGyqy.getQYSX() == null ? "" : mNoPassGyqy.getQYSX().toString().trim());
            if (qysxB != null) {
                in_qysx_etxt.setText(qysxB.getDMMC1());
                in_qysx_dmz.setText(qysxB.getDMZ());
            }
            in_hbpfwh_etxt.setText(mNoPassGyqy.getHBPZWH() == null ? "" : mNoPassGyqy.getHBPZWH().toString().trim());
            CodeTypeBean isBhqB = codeType("ISBHQ", mNoPassGyqy.getISBHQ() == null ? "" : mNoPassGyqy.getISBHQ().toString().trim());
            if (isBhqB != null) {
                in_sfsbhq_etxt.setText(isBhqB.getDMMC1());
                in_sfsbhq_dmz.setText(isBhqB.getDMZ());
            }
            CodeTypeBean isHbysB = codeType("ISHBYS", mNoPassGyqy.getISHBYS() == null ? "" : mNoPassGyqy.getISHBYS().toString().trim());
            if (isHbysB != null) {
                in_sftghbys_etxt.setText(isHbysB.getDMMC1());
                in_sftghbys_dmz.setText(isHbysB.getDMZ());
            }
            CodeTypeBean yxqkB = codeType("SCQK", mNoPassGyqy.getYXQK() == null ? "" : mNoPassGyqy.getYXQK().toString().trim());
            if (yxqkB != null) {
                in_yxqk_etxt.setText(yxqkB.getDMMC1());
                in_yxqk_dmz.setText(yxqkB.getDMZ());
            }
            in_syq_etxt.setText(String.valueOf(mNoPassGyqy.getSYMJ()));
            in_hcq_etxt.setText(String.valueOf(mNoPassGyqy.getHCMJ()));
            in_hxq_etxt.setText(String.valueOf(mNoPassGyqy.getHXMJ()));
            in_lsqk_etxt.setText(mNoPassGyqy.getZGCS());

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
