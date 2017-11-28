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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassXchcInfo;
import com.yaotu.proj.studydemo.bean.tableBean.XchcJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.XchcModel;
import com.yaotu.proj.studydemo.customEnum.MapValueType;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.CustomDialog;
import com.yaotu.proj.studydemo.customclass.HandleImage;
import com.yaotu.proj.studydemo.customclass.InitMap;
import com.yaotu.proj.studydemo.customclass.InsertLocalTableData;
import com.yaotu.proj.studydemo.customclass.PhotoCompress;
import com.yaotu.proj.studydemo.customclass.PhotoImageSize;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.customclass.UpdateLocalTableData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Response;
/*
* 现场核查区情况
* */

public class XchcActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Context context = XchcActivity.this;
    private Intent mIntent;
    private String activityType;//判断当前是修改界面还是新增界面
    private String bhqid,bhqmc,bhqjb,bhqjbdm,jcdId;//当前监测点所属保护区信息
    private double jcdLongitude,jcdlatitude;//当前监测点遥感监测的经纬度数据
    private final int OPEN_ALBUM = 0x001;//打开相册
    private final int OPEN_CAMERA = 0x002;//打开相机
    private String photoPath ;//获取的图片地址
    private String systemPicPath;//系统相册路径
    private  Uri photoUri;
    //--------------------view------------------------
    private ImageView photoImgV;
    private EditText xchc_bhqmc_etxt,xchc_jb_etxt,xchc_jsxmmc_etxt;
    private EditText xchc_jsxmlx_etxt,xchc_zxzb_etxt,xchc_szgnq_etxt;
    private EditText xchc_gm_etxt,xchc_bhlx_etxt,xchc_sbxz_etxt;
    private EditText xchc_lsyg_etxt,xchc_hpspxg_etxt,xchc_styxqk_etxt;
    private EditText xchc_qtsm_Etxt,xchc_objectid_etxt,xchc_jsxmlxDetail_etxt;
    private TextView xchc_canleImage,xchc_jsxmlx_dmz,xchc_bhlx_dmz;
    private Button xchc_btn_save;
    private String jsxmID,isChecked,IPURL,pointx,pointy;

    private LinearLayout mapLayout;
    private MapView mapView;
    private BaiduMap baiduMap;
    private Location m_location;
    private TextView txt_showInfo;
    private InitMap initMap;
    private MapValueType valueType;
    private ImageButton dot_btn, reset_btn;//地图button
    //--------------------------------------------
    private ProgressDialog progressDialog;
    private XchcModel dataModel;//上传数据
    private NoPassXchcInfo backDataInfo;//回退数据
    private final int UPLOADSUCCEED = 0X000;//数据上传成功
    private final int UPLOADFAIL = 0X001;//数据上传失败
    private final int UPLOADPICSUCCEED = 0X002;//照片上传成功
    private final int UPLOADPICFAIL = 0X003;//照片上传失败
    private final int PICDOWNLOADSUCCEED = 0X004;//照片下载成功
    private final int PICDOWNLOADFAIL = 0X005;//照片下载失败
    private DBManager dbManager;
    private boolean isCameraAlbumBack = false;//是否从相册或照相机返回执行onresume
    //---------------------------------------------
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOADSUCCEED://数据上传成功，如果是上传的本地修改数据则删除本次数据
                    progressDialog.dismiss();
                    Log.i(TAG, "handleMessage: ------------------>"+dataModel.getPhotourl());
                    if (!"".equals(dataModel.getPhotourl()) && dataModel.getPhotourl() != null) {//上传相片
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setMessage("数据上传成功！是否上传相片?");
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
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
                        deleteXchcInfo(dataModel);
                        finish();
                    }
                    break;
                case UPLOADFAIL:
                    progressDialog.dismiss();
                    showMessage("上传失败...");
                    break;
                case UPLOADPICSUCCEED:
                    progressDialog.dismiss();
                    showMessage("照片上传成功!");
                    deleteXchcInfo(dataModel);
                    finish();
                    break;
                case UPLOADPICFAIL:
                    progressDialog.dismiss();
                    showMessage("照片上传失败!");
                    break;
                case PICDOWNLOADSUCCEED:
                    //showMessage("照片下载成功!");
                    File picFile = (File) msg.obj;
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(picFile.getAbsolutePath());
                    photoImgV.setImageBitmap(bitmap);
                    photoPath = picFile.getAbsolutePath();
                    break;
                case PICDOWNLOADFAIL:
                    //showMessage("照片下载失败!");
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xchc);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mapLayout = (LinearLayout) findViewById(R.id.show_mapview_LinearLayout);
        mapLayout.setVisibility(View.GONE);
        initView();
        initMap = new InitMap(context, mapView, baiduMap, txt_showInfo);
        systemPicPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator;
    }
    private void initView(){//初始化view
        xchc_bhqmc_etxt = (EditText) findViewById(R.id.xchc_bhqmc_etxt);
        //xchc_jb_etxt = (EditText) findViewById(R.id.xchc_jb_etxt);
        xchc_jsxmmc_etxt = (EditText) findViewById(R.id.xchc_jsxmmc_etxt);
        xchc_jsxmlx_etxt = (EditText) findViewById(R.id.xchc_jsxmlx_etxt);
        xchc_jsxmlx_dmz = (TextView) findViewById(R.id.xchc_jsxmlx_dmz);
        xchc_zxzb_etxt = (EditText) findViewById(R.id.xchc_zxzb_etxt);
        xchc_szgnq_etxt = (EditText) findViewById(R.id.xchc_szgnq_etxt);
        xchc_gm_etxt = (EditText) findViewById(R.id.xchc_gm_etxt);
        xchc_bhlx_etxt = (EditText) findViewById(R.id.xchc_bhlx_etxt);
        xchc_bhlx_dmz = (TextView) findViewById(R.id.xchc_bhlx_dmz);
        xchc_sbxz_etxt = (EditText) findViewById(R.id.xchc_sbxz_etxt);
        xchc_lsyg_etxt = (EditText) findViewById(R.id.xchc_lsyg_etxt);
        xchc_hpspxg_etxt = (EditText) findViewById(R.id.xchc_hpspxg_etxt);
        xchc_styxqk_etxt = (EditText) findViewById(R.id.xchc_styxqk_etxt);
        xchc_qtsm_Etxt = (EditText) findViewById(R.id.xchc_qtsm_Etxt);
        xchc_objectid_etxt = (EditText) findViewById(R.id.xchc_objectid_etxt);
        xchc_jsxmlxDetail_etxt = (EditText) findViewById(R.id.xchc_jsxmlxDetail_etxt);
        xchc_canleImage = (TextView) findViewById(R.id.xchc_canleImage);
        photoImgV = (ImageView) findViewById(R.id.photoImgV);
        xchc_btn_save = (Button) findViewById(R.id.xchc_btn_save);
        dot_btn = (ImageButton) findViewById(R.id.dot_btn);//地图打点按钮
        reset_btn = (ImageButton) findViewById(R.id.reset_btn);//地图重置按钮
        mapView = (MapView) findViewById(R.id.report_mapview);
        baiduMap = mapView.getMap();
        photoImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
        xchc_canleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoImgV.setImageResource(R.drawable.icon_addpic_unfocused);
                photoPath = "";
            }
        });
        //自定义对话框，选择建设项目类型
        xchc_jsxmlx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(context,xchc_jsxmlx_dmz,xchc_jsxmlx_etxt, "建设项目类型", "RLHDLX");
                dialog.showQksxDialog();
                Log.i(TAG, "onClick: --------RLHDLX---------->"+xchc_jsxmlx_etxt.getText().toString());
            }
        });
        xchc_bhlx_etxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(context, xchc_bhlx_dmz, xchc_bhlx_etxt, "变化类型", "BHLX");
                dialog.showQksxDialog();
            }
        });
        /*判断中心坐标位置
        * */
        xchc_zxzb_etxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mapLayout.setVisibility(View.VISIBLE);//显示地图
                /*将国家核查点标注在地图上*/
                valueType = MapValueType.centerCoordinate;
                dot_btn.setVisibility(View.INVISIBLE);
                reset_btn.setVisibility(View.INVISIBLE);
                LatLng latLng = initMap.latLngConVert(new LatLng(Double.valueOf(pointy),Double.valueOf(pointx)));
                initMap.drawDotBlue(latLng);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 16));
                return false;
            }
        });
    }
    public void showPhotoDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        final String[] items = new String[]{"拍照", "从相册选择"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://启动相机代码
                       /*
                        返回缩略图
                        mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(mIntent, OPEN_CAMERA);*/
                       /*返回原图
                       * */
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            File file = new File(systemPicPath);
                            if (!file.exists()) {
                                file.mkdir();
                            }
                            String fileName = getPhotoFileName() + ".jpg";
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            photoUri = Uri.fromFile(new File(systemPicPath + fileName));
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, OPEN_CAMERA);
                        }
                        dialog.dismiss();
                        break;
                    case 1:
                        mIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        mIntent.setType("image/*");
                        startActivityForResult(mIntent, OPEN_ALBUM);
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT > 19) {
                        //4.4及以上系统使用这个方法处理图片
                        photoPath = HandleImage.handleImgeOnKitKat(context, data);
                    } else {
                        photoPath = HandleImage.handleImageBeforeKitKat(context, data);
                    }
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(photoPath);
                   photoImgV.setImageBitmap(bitmap);
                }
                isCameraAlbumBack = true;

                break;
            case OPEN_CAMERA:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null && data.getData() != null) {
                        uri = data.getData();
                    }
                    if (uri == null) {
                        if (photoUri != null) {
                            uri = photoUri;
                        }
                    }
                    //Log.i(TAG, "onActivityResult: -----------ddddddddd----------"+  ParseIntentData.getBitmapSize(BitmapFactory.decodeFile(uri.getPath())));
                    photoPath = uri.getPath();
                    Bitmap smallBitmap = PhotoImageSize.revitionImageSize(uri.getPath());//缩略图显示
                    if (null != smallBitmap) {
                        photoImgV.setImageBitmap(smallBitmap);
                        //Log.i(TAG, "onActivityResult: ---------------sssss---------"+ParseIntentData.getBitmapSize(smallBitmap));
                    }
                    isCameraAlbumBack = true;

                }
                break;
            default:
                break;
        }
    }
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntent = getIntent();
        IPURL = getResources().getString(R.string.http_url);
        activityType = mIntent.getStringExtra("type");
        if (activityType.equals("add")) {//新增数据操作
            bhqid = mIntent.getStringExtra("bhqid");
            bhqmc = mIntent.getStringExtra("bhqmc");
            bhqjb = mIntent.getStringExtra("bhqjb");
            bhqjbdm = mIntent.getStringExtra("bhqjbdm");
            jcdId = mIntent.getStringExtra("placeid");
            jcdLongitude = mIntent.getDoubleExtra("longitude", 0);
            jcdlatitude = mIntent.getDoubleExtra("latitude", 0);
            //------------------------------
            xchc_bhqmc_etxt.setText(bhqmc);
            //xchc_jb_etxt.setText(bhqjb);
            xchc_objectid_etxt.setText(jcdId);
            xchc_zxzb_etxt.setText(jcdLongitude+","+jcdlatitude);
            jsxmID = "0";
            xchc_btn_save.setText("保存");
        } else if(activityType.equals("update")){//本地修改数据操作
            dataModel = (XchcModel) mIntent.getSerializableExtra("localData");
            if (dataModel != null) {
                bhqmc = dataModel.getSzbhqmc();
                jcdLongitude = dataModel.getJd();
                jcdlatitude = dataModel.getWd();
                jcdId =String.valueOf(dataModel.getObjectid());
            }
            xchc_btn_save.setText("修改");
            if (!isCameraAlbumBack) {
                setViewData();//获取更新数据(防止修改界面在加载相片时重绘界面)
            }
        } else if (activityType.equals("backdata")) {//回退数据操作
            backDataInfo = (NoPassXchcInfo) mIntent.getSerializableExtra("bdData");
            if (!isCameraAlbumBack) {
                setBackDataView(backDataInfo);
                downLoadPic(backDataInfo.getPHOTONAME());
            }
        }

        String centerpoint = xchc_zxzb_etxt.getText().toString().trim();
        if (!"".equals(centerpoint)) {
            Log.i(TAG, "getIndustryBean: ------centerpoint------->" + centerpoint);
            int temp = centerpoint.indexOf(",");
            pointx = centerpoint.substring(0, temp);
            pointy = centerpoint.substring(temp + 1);
        }
        //downLoadPic("测试pic.jpg");
    }
    //==============================初始化表单数据===================================================
    public XchcModel getXchcModel() {
        XchcModel bean= new XchcModel();

        bean.setJsxmid(jsxmID);
        bean.setJsxmmc(xchc_jsxmmc_etxt.getText().toString().trim());
        bean.setJsxmlx(xchc_jsxmlx_etxt.getText().toString().trim());
        bean.setJd(Double.parseDouble(pointx));
        bean.setWd(Double.parseDouble(pointy));
        bean.setSzbhqgnq(xchc_szgnq_etxt.getText().toString().trim());
        bean.setScale(xchc_gm_etxt.getText().toString().trim());
        bean.setBhlx(xchc_bhlx_etxt.getText().toString().trim());
        bean.setCurrentstatus(xchc_sbxz_etxt.getText().toString().trim());
        bean.setLsyg(xchc_lsyg_etxt.getText().toString().trim());
        bean.setHbspxg(xchc_hpspxg_etxt.getText().toString().trim());
        Log.i(TAG, "getXchcModel: -------------------------bhspxg------->"+xchc_hpspxg_etxt.getText().toString().trim()+"------"+bean.getHbspxg());
        bean.setStyxph(xchc_styxqk_etxt.getText().toString().trim());
        bean.setQtsm(xchc_qtsm_Etxt.getText().toString().trim());
        Log.i(TAG, "getXchcModel: ---------------------photoPath-------->"+photoPath);
        if (!"".equals(photoPath) && photoPath != null) {
            File filePhoto = new File(photoPath);
            bean.setPhotoName(filePhoto.getName());
            bean.setPhotourl(photoPath);
        }
        bean.setObjectid(Integer.parseInt(xchc_objectid_etxt.getText().toString().trim()));
        bean.setSzbhqmc(xchc_bhqmc_etxt.getText().toString().trim());
        bean.setJsxmlxdm(xchc_jsxmlx_dmz.getText().toString().trim());
        bean.setIscheckeddm(checkedStatue);//isChecked
        bean.setYhdh(TempData.yhdh);
        bean.setSzbhqid(bhqid);
        bean.setIsarchived("0");
        bean.setBhlxdm(xchc_bhlx_dmz.getText().toString().trim());
        bean.setJsxmlxdetails(xchc_jsxmlxDetail_etxt.getText().toString().trim());
        //bean.setSzbhqjb(xchc_jb_etxt.getText().toString().trim());
        bean.setSzbhqjbdm(bhqjbdm);
        bean.setSubmitter(TempData.yhmc);

        return bean;
    }
    private String checkedStatue;
    private void setViewData() {
        if (dataModel != null) {
            jsxmID = dataModel.getJsxmid();
            bhqid = dataModel.getSzbhqid();
            xchc_bhqmc_etxt.setText(bhqmc);
            xchc_jsxmmc_etxt.setText(dataModel.getJsxmmc());
            xchc_jsxmlx_etxt.setText(dataModel.getJsxmlx());
            //xchc_jb_etxt.setText(dataModel.getSzbhqjb());
            if (dataModel.getIscheckeddm() != null && dataModel.getIscheckeddm().equals("22")) {
                checkedStatue = "22";
            } else {
                checkedStatue = "21";
            }
            bhqjbdm = dataModel.getSzbhqjbdm();
            xchc_szgnq_etxt.setText(dataModel.getSzbhqgnq());
            xchc_gm_etxt.setText(dataModel.getScale());
            xchc_zxzb_etxt.setText(jcdLongitude + "," + jcdlatitude);
            xchc_bhlx_etxt.setText(dataModel.getBhlx());
            xchc_sbxz_etxt.setText(dataModel.getCurrentstatus());
            xchc_lsyg_etxt.setText(dataModel.getLsyg());
            xchc_hpspxg_etxt.setText(dataModel.getHbspxg());
            xchc_styxqk_etxt.setText(dataModel.getStyxph());
            xchc_qtsm_Etxt.setText(dataModel.getQtsm());
            xchc_objectid_etxt.setText(String.valueOf(dataModel.getObjectid()));
            xchc_jsxmlx_dmz.setText(dataModel.getJsxmlxdm());
            xchc_bhlx_dmz.setText(dataModel.getBhlxdm());
            xchc_jsxmlxDetail_etxt.setText(dataModel.getJsxmlxdetails());
            if (!isCameraAlbumBack) {
                photoPath = dataModel.getPhotourl();
                if (photoPath != null) {
                    Bitmap bitmap = PhotoImageSize.revitionImageSize(photoPath);
                    photoImgV.setImageBitmap(bitmap);
                }
            } else {
                isCameraAlbumBack = false;
            }

            Log.i(TAG, "setViewData: -----------photoPath------->"+photoPath);


        }
    }

    private void setBackDataView(NoPassXchcInfo bean){
        if (bean != null) {
            jsxmID = bean.getJSXMID();
            bhqid = bean.getSZBHQID();
            xchc_bhqmc_etxt.setText(bean.getSZBHQMC());
            xchc_jsxmmc_etxt.setText(bean.getJSXMMC());
            xchc_jsxmlx_etxt.setText(bean.getJSXMLX());
            //xchc_jb_etxt.setText(bean.getjb);
            //bhqjbdm = dataModel.getSzbhqjbdm();
            checkedStatue ="22";//bean.getISCHECKEDDM();

            xchc_szgnq_etxt.setText(bean.getSZBHQGNQ());
            xchc_gm_etxt.setText(bean.getSCALE());
            xchc_zxzb_etxt.setText(bean.getJD() + "," + bean.getWD());
            xchc_bhlx_etxt.setText(bean.getBHLX());
            xchc_sbxz_etxt.setText(bean.getCURRENTSTATUS());
            xchc_lsyg_etxt.setText(bean.getLSYG());
            xchc_hpspxg_etxt.setText(bean.getHBSPXG());
            xchc_styxqk_etxt.setText(bean.getSTYXPH());
            xchc_qtsm_Etxt.setText(bean.getQTSM());
            Log.i(TAG, "setBackDataView: -----------bean.getobjectid------"+bean.getOBJECTID());
            xchc_objectid_etxt.setText(String.valueOf(bean.getOBJECTID()));
            xchc_jsxmlx_dmz.setText(bean.getJSXMLXDM());
            xchc_bhlx_dmz.setText(bean.getBHLXDM());
            xchc_jsxmlxDetail_etxt.setText(bean.getJSXMLXDETAILS());
        }

    }

    //=============================保存到本地数据=================================================
    public void saveBtnMethod(View view) {
        if(activityType.equals("add") || activityType.equals("backdata")) {//保存在本地数据
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("是否保存本地？");
            dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dataModel = getXchcModel();
                    if (IsExist(dataModel)) {
                        showMessage("该监测点在本地数据已经存在!");
                    } else {
                        boolean inResult = insertXchcInfo(dataModel);
                        if (inResult) {
                            showMessage("保存成功!");
                            finish();
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
        }else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("修改当前数据？");
            dataModel = getXchcModel();
            dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                boolean upResult = updateXchcInfo(dataModel);
                if (upResult) {
                    showMessage("修改成功!");
                    finish();
                } else {
                    showMessage("修改失败!");
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

    }
    /*查询本地数据库是否已经存在该企业名称
    * */
    private boolean IsExist(XchcModel bean) {
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql = "select * from xchcInfoTab where szbhqid = ? and objectid = ? and yhdh = ?";
        String[] strs = new String[]{bean.getSzbhqid(),String.valueOf(bean.getObjectid()), bean.getYhdh()};
        List<XchcModel> list = query.queryXchcInfos(sql, strs);
        if (list != null) {
            if (list.size() > 0) {
                return true;//存在
            }
        }
        return false;//不存在
    }
    /*插入本地数据
    * */
    private boolean insertXchcInfo(XchcModel bean) {

        boolean result = false;
        if (bean != null) {
            InsertLocalTableData insert = new InsertLocalTableData(context);
            result = insert.insertXchcInfo(bean);
        }
        return result;
    }
    /*修改本地数据*/
    private boolean updateXchcInfo(XchcModel bean) {

        boolean result = false;
        if (bean != null) {
            UpdateLocalTableData update = new UpdateLocalTableData(context);
            result = update.UpdateXchxInfo(bean);
        }
        return result;
    }
    /*删除本地数据
    * */
    private boolean deleteXchcInfo(XchcModel bean){
        boolean result = false;
        if (bean != null) {
            result = removeSqliteData("xchcInfoTab", "szbhqid = ? and objectid = ?  and yhdh = ?", new String[]{bean.getSzbhqid(),String.valueOf(bean.getObjectid()),bean.getYhdh()});
        }
        return result;
    }
    private boolean removeSqliteData(String table, String where, String[] args) {
        dbManager = new DBManager(context);
        boolean result = dbManager.deleteTableData(table, where, args);
        return result;
    }
    //===============================直接上传服务器==================================================
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
    private void upload() {
        //1.组织要提交的数据
        List<XchcModel> list = new ArrayList<>();
        dataModel = getXchcModel();
        XchcJsonBean jsonBean = new XchcJsonBean();
       /* if (activityType.equals("add") ||activityType.equals("update") ) {
            if (checkedStatue == "22") {
                dataModel.setIscheckeddm("22");
            } else {
                dataModel.setIscheckeddm("21");
            }
        } else if (activityType.equals("backUpdate")) {
            dataModel.setIscheckeddm("22");
        }*/
        list.add(dataModel);
        jsonBean.setList(list);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i(TAG, "onClick: ------------现场核查表上传数据----------->" + jsonStr);
        //2.连接服务器上传
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否上传数据？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("正在上传数据...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {//上传数据
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                        Response response = ParseIntentData.getDataPostByJson(IPURL + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
                        if (response != null && response.code() == 200) {
                            try {
                                String responseValue = response.body().string();
                                Log.i(TAG, "run: -----------------dd---------->"+responseValue);
                                //dataUplocadSucceed = responseValue;
                                message.what = UPLOADSUCCEED;
                                myHandler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            message.what = UPLOADFAIL;
                            myHandler.sendMessage(message);
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
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在上传照片...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                PhotoCompress photoCompress = new PhotoCompress();
                Bitmap bitmap = photoCompress.getImage(photoPath);
                File file = FileUtils.saveBitmap(bitmap, dataModel.getPhotoName());
                if (file == null) {
                    return;
                }
                String url = IPURL+"/bhqService/BhqJsxm/Upload";
                //Response response = ParseIntentData.upLoadImage(url, photoPath,null);
                Response response = ParseIntentData.upLoadImage(url, file.getAbsolutePath(),null);
                if (response != null && response.code() == 200) {
                    message.what = UPLOADPICSUCCEED;
                } else {
                    message.what = UPLOADPICFAIL;
                }
                myHandler.sendMessage(message);
            }
        }).start();

    }
    /*
    * 图片下载
    * */
    private void downLoadPic(final String picName){
        final Message message = Message.obtain();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Bitmap bitMap = ParseIntentData.downLoadPic(IPURL+"/photo/"+picName);
                Response response = ParseIntentData.downLoadPic(IPURL+"/bhqService/upload/"+picName);
                //Response response = ParseIntentData.downLoadPic("http://192.168.0.21/photo/a.jpg");
                if (response != null && response.code() == 200) {
                    InputStream ins = response.body().byteStream();
                    Bitmap picBitmap = BitmapFactory.decodeStream(ins);
                    File picFile = FileUtils.saveBitmap(picBitmap, picName);
                    message.what = PICDOWNLOADSUCCEED;
                    message.obj = picFile;
                } else {
                    message.what = PICDOWNLOADFAIL;
                }
                myHandler.sendMessage(message);
            }
        }).start();
    }
    //=============================Activity生命周期====================================================
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
        if (dbManager != null) {
            dbManager.closeDB();
        }
    }
    //发送广播更新系统相册
    private void upDateDcim(String fileName){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(FileUtils.SDPATH + fileName + ".jpg"));
        Log.i(TAG, "upDateDcim: -------------filename:"+fileName);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }
    //============================提示消息框==================================================
    private void showMessage(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
    //=====================================================================================
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
    //===============================地图操作=================================================
    public void moveCurrentPosition(View view) {//移动到当前位置
        m_location = initMap.getM_location();
        if (null != m_location) {
            initMap.localization(m_location);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(initMap.latLngConVert(new LatLng(m_location.getLatitude(), m_location.getLongitude())), 16));
        } else {
            showMessage("暂时无法定位，请确保GPS打开");
        }
    }
    public void getValueMethod(View view) {//获取值
        m_location = initMap.getM_location();
        if (m_location != null) {
            switch (valueType) {
                case centerCoordinate:
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setMessage("是否获取当前位置更改中心坐标点?");
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mapLayout.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            xchc_zxzb_etxt.setText(String.valueOf(m_location.getLongitude()) + "," + String.valueOf(m_location.getLatitude()));
                            mapLayout.setVisibility(View.GONE);
                        }
                    });
                    builder.show();
                    break;
                default:
                    break;
            }

        }

    }
    //========================================================================================

}
