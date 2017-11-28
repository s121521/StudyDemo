package com.yaotu.proj.studydemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.BhqInfoBean;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassXchcInfo;
import com.yaotu.proj.studydemo.bean.tableBean.XchcModel;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class XchcBackDataActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private Context mContext;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<NoPassXchcInfo> mNoPassXchcInfos;//当前用户下所有未通过数据
    private BaseAdapter spAdapter,lvAdapter;
    private String ipURl;
    private static final int REFRESH_INTENT = 0X110;//刷新服务器数据
    private static final int REFRESH_INTENT_FAIL = 0x1101;//刷新服务器数据失败
    private static final int REFRESH_LOCAL = 0X111;//刷新本地数据
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  REFRESH_INTENT:
                    lvAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    if (mNoPassXchcInfos != null && mNoPassXchcInfos.size() > 0) {
                        //showMessage("数据请求成功！");
                    } else {
                        showMessage("暂无回退数据!");
                    }
                    break;
                case REFRESH_INTENT_FAIL:
                    showMessage("数据请求失败");
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case REFRESH_LOCAL:
                    //my_Adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xchc_back_data);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
    }
    private void initView(){
        mContext = XchcBackDataActivity.this;
        mListView = (ListView) findViewById(R.id.backData_xchc_lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.xchc_backdata_sf);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(this.getResources().getColor(R.color.blue));
        lvAdapter = new ListViewAdapter();
        mListView.setAdapter(lvAdapter);
        /*实现listview点击事件
        * */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(mContext, XchcActivity.class);
                intent.putExtra("bdData",mNoPassXchcInfos.get(position));
                intent.putExtra("type", "backdata");//执行更新操作
                startActivity(intent);
            }
        });

    }
    //===============================定义内部类===================================================

    class ListViewAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mNoPassXchcInfos == null ? 0 : mNoPassXchcInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mNoPassXchcInfos == null ? null :mNoPassXchcInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
           myHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.xchc_backdata_listview_item, null);
                holder = new myHolder();
                holder.jsxmName = (TextView) convertView.findViewById(R.id.xchc_backdata_item_jsxmmc);
                holder.backReason = (TextView) convertView.findViewById(R.id.xchc_backdata_item_backreason);
                convertView.setTag(holder);
            }
            holder = (myHolder) convertView.getTag();
            if (mNoPassXchcInfos != null) {
                holder.jsxmName .setText(mNoPassXchcInfos.get(position).getJSXMMC());
                holder.backReason .setText(mNoPassXchcInfos.get(position).getREASON());
                holder.backReason.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        //builder.setTitle("回退原因");
                        builder.setMessage(mNoPassXchcInfos.get(position).getREASON());

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        builder.show();

                    }
                });
            }
            return  convertView;
        }
        class myHolder {
            TextView jsxmName;
            TextView backReason;
        }
    }
    //=============================================================================================
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
    //=====================================Activity生命周期=========================================

    @Override
    protected void onResume() {
        super.onResume();
        ipURl = getResources().getString(R.string.http_url);
        requestIntentData();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //==============================实现OnRefreshListener接口方法=================================
    @Override
    public void onRefresh() {
        //实现下拉刷新
        int netType = CheckNetwork.getNetWorkState(mContext);
        if (netType == 0) {//none
            showMessage("请打开网络连接");
        } else{
            requestIntentData();
        }

    }
    //=================================请求网络数据============================================
    private void requestIntentData(){
        /*请求当前用户审核未通过数据
        * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = "{\"yhdh\":\"" + TempData.yhdh + "\"}";
                Response response = ParseIntentData.getDataPostByJson(ipURl + "/bhqService/BhqJsxm/GetAllNotPassedYGHCPoints", data);
                Gson gson = new Gson();
                Type type = new TypeToken<List<NoPassXchcInfo>>() {}.getType();
                if (response != null && response.code() == 200) {
                    try {
                        mNoPassXchcInfos = gson.fromJson(response.body().string(), type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myHandler.sendEmptyMessage(REFRESH_INTENT);
                } else {
                    myHandler.sendEmptyMessage(REFRESH_INTENT_FAIL);
                }
            }
        }).start();
    }
    //================================提示消息===========================================
    private void showMessage(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
}
