package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.DetailedInfoBean;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassBhqInfo;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassInfo;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/*
* 审核未通过，回退数据
* */
public class BackDataActivity extends AppCompatActivity {
    private Context currContext ;
    private final String TAG = this.getClass().getSimpleName();
    private TextView txtBackdataQytype;//显示7张不同表信息
    private Spinner spBackdataBhqmc;//显示某一张表下，有哪些保护区名称
    private ListView lvBackdataInfos;//显示某一张表下，某一保护区下的所有监测点信息列表
    private String IPURL;//网络连接IP地址
    private final int REQUEST_BHQINFODATA_SUCCESS = 0x001;//保护区信息数据请求成功
    private final int REQUEST_BHQINFODATA_FAIL = 0x002;//保护区信息数据请求失败
    private final int REQUEST_JSXMINFODATA_SUCCESS = 0x003;//建设项目信息数据请求成功
    private final int REQUEST_JSXMINFODATA_FAIL = 0x0004;//建设项目信息数据请求失败
    private int netState;//网络状态
    private String jsxmlx;//建设项目类型
    private ProgressDialog mProgressDialog;
    private List<NoPassBhqInfo> bhqInfos;//未通过保护区信息
    private List<DetailedInfoBean> detailedInfo;//未通过建设项目信息
    private BaseAdapter noPassbhqInfoAdapter,noPassJsxmInfoAdapter;//未通过保护区信息Adapter,未通过建设项目信息Adapter
    private boolean isTxtBackdataQytypeChanged = false;//判断TxtBackdataQytype是否改变
    private String bdBhqmc,bdBhqid;//保护区名称和ID
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_BHQINFODATA_SUCCESS:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    noPassbhqInfoAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_BHQINFODATA_FAIL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(currContext,"数据请求失败",Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_JSXMINFODATA_SUCCESS:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    noPassJsxmInfoAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_JSXMINFODATA_FAIL:
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                    Toast.makeText(currContext,"数据请求失败",Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_back_data);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        currContext = this;
        bhqInfos = new ArrayList<>();
        detailedInfo = new ArrayList<>();
        IPURL = getResources().getString(R.string.http_url);
        initView();
    }

    /*初始化view*/
    private void initView(){
        txtBackdataQytype = (TextView) findViewById(R.id.txtBackdataQytype);
        spBackdataBhqmc = (Spinner) findViewById(R.id.spBackdataBhqmc);
        lvBackdataInfos = (ListView) findViewById(R.id.lvBackdataInfos);
        final String[] qyItems = new String[]{"探矿企业", "工业企业", "矿产资源开发企业", "旅游资源开发企业", "新能源项目", "开垦活动", "其他开发建设活动"};
        txtBackdataQytype.setOnClickListener(new View.OnClickListener() {//文本onClick事件
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(currContext);
                builder.setItems(qyItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        txtBackdataQytype.setText(qyItems[which]);
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        txtBackdataQytype.addTextChangedListener(new TextWatcher() {//文本changed事件
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bhqInfos.clear();
                noPassbhqInfoAdapter.notifyDataSetChanged();
                Log.i(TAG, "beforeTextChanged: -------------");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String qyType = txtBackdataQytype.getText().toString().trim();
                if (qyType.equals("探矿企业")) {
                    jsxmlx = "02";
                }
                if (qyType.equals("工业企业")) {
                    jsxmlx = "03";
                }
                if (qyType.equals("矿产资源开发企业")) {
                    jsxmlx = "01";
                }
                if (qyType.equals("旅游资源开发企业")) {
                    jsxmlx = "04";
                }
                if (qyType.equals("新能源项目")) {
                    jsxmlx = "05";
                }
                if (qyType.equals("开垦活动")) {
                    jsxmlx = "06";
                }
                if (qyType.equals("其他开发建设活动")) {
                    jsxmlx = "07";
                }
                Log.i(TAG, "onTextChanged: --------------");
                bhqInfoIntenetData(jsxmlx);
                isTxtBackdataQytypeChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        spBackdataBhqmc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bdBhqid = bhqInfos.get(position).getBHQID();
                jsxmInfoIntenetData(jsxmlx,bdBhqid);
                Log.i(TAG, "onItemSelected: ------------");
                isTxtBackdataQytypeChanged = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {//当我们的adapter为空的时候就会调用到这个方法.example: bhqInfos.clear();noPassbhqInfoAdapter.notifyDataSetChanged();时执行此方法

            }
        });
        noPassbhqInfoAdapter = new myBhqBaseAdapter();
        noPassJsxmInfoAdapter = new myListAdapter();
        spBackdataBhqmc.setAdapter(noPassbhqInfoAdapter);
        lvBackdataInfos.setAdapter(noPassJsxmInfoAdapter);
        //让选中的item进行跑马灯，选中另一个，上一个不进行动画
        lvBackdataInfos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == id){
                   TextView mTextView = (TextView) view.findViewById(R.id.txt_backdata_backreason);
                    view.isFocusableInTouchMode();
                    view.requestFocusFromTouch();
                    mTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                }
            }
        });

        lvBackdataInfos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (jsxmlx.equals("01")) {//矿产资源
                    intent.setClass(currContext, KczyDevelopEnterpriseActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM01DetailedInfo());
                }
                if (jsxmlx.equals("02")) {//探矿企业
                    intent.setClass(currContext, TKEnterpriseActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM02DetailedInfo());
                }
                if (jsxmlx.equals("03")) {//工业企业
                    intent.setClass(currContext, IndustryEnterpriseActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM03DetailedInfo());
                }
                if (jsxmlx.equals("04")) {//旅游资源开发企业
                    intent.setClass(currContext, TravelDevelopEnterpriseActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM04DetailedInfo());
                }
                if (jsxmlx.equals("05")) {//新能源项目
                    intent.setClass(currContext, NewEnergyProjActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM05DetailedInfo());
                }
                if (jsxmlx.equals("06")) {//开垦活动
                    intent.setClass(currContext, AssartActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM06DetailedInfo());
                }
                if (jsxmlx.equals("07")) {//其他开发建设活动
                    intent.setClass(currContext, DevelopConstructionActivity.class);
                    intent.putExtra("bdData", detailedInfo.get(position).getJSXM07DetailedInfo());
                }
                intent.putExtra("type", "update");//执行更新操作
                intent.putExtra("bdBhqmc",bdBhqmc);
                intent.putExtra("bdjd", detailedInfo.get(position).getJSXMBasicInfo().getCENTERPOINTX());
                intent.putExtra("bdwd", detailedInfo.get(position).getJSXMBasicInfo().getCENTERPOINTY());
                intent.putExtra("bdobjid",String.valueOf(detailedInfo.get(position).getJSXMBasicInfo().getOBJECTID()));
                startActivity(intent);
                return false;
            }
        });
    }
    /*用来填充Spinner
    * */
    class myBhqBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bhqInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return bhqInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(currContext).inflate(R.layout.activity_show_complete_spainner_item, null);
                holder = new ViewHolder();
                holder.bhqmc_txt = (TextView) convertView.findViewById(R.id.bhqmc_txt);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            holder.bhqmc_txt.setText(bhqInfos.get(position).getBHQNAME());
            bdBhqmc = holder.bhqmc_txt.getText().toString().trim();
            return convertView;
        }

        class ViewHolder {
            TextView bhqmc_txt;
        }
    }
    /*填充Listview
   * */
    class myListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return detailedInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return detailedInfo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_backdata_lv_item, null);
                holder = new ViewHolder();
                holder.qyName = (TextView) convertView.findViewById(R.id.txt_backdata_qyname);
                holder.backReason = (TextView) convertView.findViewById(R.id.txt_backdata_backreason);
                holder.backReason.setSelected(true);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (jsxmlx.equals("01")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM01DetailedInfo().getJSXMMC());
            }
            if (jsxmlx.equals("02")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM02DetailedInfo().getJSXMMC());
            }
            if (jsxmlx.equals("03")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM03DetailedInfo().getJSXMMC());
            }
            if (jsxmlx.equals("04")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM04DetailedInfo().getJSXMMC());
            }
            if (jsxmlx.equals("05")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM05DetailedInfo().getJSXMMC());
            }
            if (jsxmlx.equals("06")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM06DetailedInfo().getKKQKMC());
            }
            if (jsxmlx.equals("07")) {
                holder.qyName.setText(detailedInfo.get(position).getJSXM07DetailedInfo().getKFJSMC());
            }
            //holder.backReason.setText("我爱你中国，亲爱的母亲，我为你骄傲，我为你自豪。。。");
            holder.backReason.setText(detailedInfo.get(position).getJSXMBasicInfo().getREASON());
            return convertView;
        }

         class ViewHolder {
           public TextView qyName;
           public TextView backReason;
        }
    }
    //===================================intenetData================================================
    private void bhqInfoIntenetData(final String jsxmlx){//通过建设项目类型和用户名获取未通过保护区信息
        bhqInfos.clear();
        mProgressDialog = new ProgressDialog(currContext);
        mProgressDialog.setMessage("请稍候...");
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Response response = ParseIntentData.getDataGetByString(IPURL+"/bhqService/BhqJsxm/GetAllNotPassedPoints?yhdh="+TempData.username+"&jsxmlx="+jsxmlx+"&bhqid=");
                if (response != null && response.code() == 200) {//请求成功
                    Gson gson = new Gson();
                    try {
                        NoPassInfo bean =  gson.fromJson(response.body().string(), new TypeToken<NoPassInfo>() {}.getType());
                        bhqInfos = bean.getBhqList();
                        message.what = REQUEST_BHQINFODATA_SUCCESS;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {//请求失败
                    message.what = REQUEST_BHQINFODATA_FAIL;
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }
    private void jsxmInfoIntenetData(final String jsxmlx, final String bhqid){//通过建设项目类型和用户名,保护区ID获取未通过建设项目信息
        //detailedInfo.clear();
        if (!isTxtBackdataQytypeChanged) {
            mProgressDialog = new ProgressDialog(currContext);
            mProgressDialog.setMessage("请稍候...");
            mProgressDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Response response = ParseIntentData.getDataGetByString(IPURL+"/bhqService/BhqJsxm/GetAllNotPassedPoints?yhdh="+TempData.username+"&jsxmlx="+jsxmlx+"&bhqid="+bhqid);
                if (response != null && response.code() == 200) {//请求成功
                    Gson gson = new Gson();
                    try {
                        NoPassInfo bean =  gson.fromJson(response.body().string(), new TypeToken<NoPassInfo>() {}.getType());
                        detailedInfo = bean.getDetailedInfo();
                        message.what = REQUEST_JSXMINFODATA_SUCCESS;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {//请求失败
                    message.what = REQUEST_JSXMINFODATA_FAIL;
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }
    //===================================onOptionsItemSelected======================================
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
    //================================Activity生命周期===============================================

    @Override
    protected void onResume() {
        super.onResume();
        noPassJsxmInfoAdapter.notifyDataSetChanged();
        Log.i(TAG, "onResume: -------------------------------");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        jsxmInfoIntenetData(jsxmlx,bdBhqid);
        Log.i(TAG, "onRestart: ----------------------------");
    }
    //==============================================================================================
}
