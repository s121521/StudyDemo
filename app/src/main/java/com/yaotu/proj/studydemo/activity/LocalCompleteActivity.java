package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
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
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.BhqInfoBean;
import com.yaotu.proj.studydemo.bean.tableBean.XchcJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.XchcModel;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.PhotoCompress;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.FileUtils;
import com.yaotu.proj.studydemo.util.HttpUrlAddress;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class LocalCompleteActivity extends AppCompatActivity {
    private Context mContext;
    private List<BhqInfoBean> mBhqInfoBeanList;
    private List<XchcModel> mXchcModels;
    private QueryLocalTableData mQueryLocalTableData;
    private ListView mListView;
    private Spinner mSpinner;
    private BaseAdapter lvAdapter,spAdapter;
    private DBManager dbManager;
    private ProgressDialog progressDialog;
    private String IPURL;
    private final int UPLOADSUCCEED = 0X000;//数据上传成功
    private final int UPLOADFAIL = 0X001;//数据上传失败
    private final int UPLOADPICSUCCEED = 0X002;//照片上传成功
    private final int UPLOADPICFAIL = 0X003;//照片上传失败
    private XchcModel dataModel;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOADSUCCEED://数据上传成功，如果是上传的本地修改数据则删除本次数据
                    progressDialog.dismiss();
                    if (!"".equals(dataModel.getPhotourl()) && dataModel.getPhotourl() != null) {//上传相片
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
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
                        removeXchcModel(dataModel);
                        //刷新
                        mBhqInfoBeanList = queryLocalBhqInfos();
                        spAdapter.notifyDataSetChanged();
                    }
                    break;
                case UPLOADFAIL:
                    progressDialog.dismiss();
                    showMessage("上传失败...");
                    break;
                case UPLOADPICSUCCEED:
                    progressDialog.dismiss();
                    showMessage("照片上传成功!");
                    removeXchcModel(dataModel);
                    break;
                case UPLOADPICFAIL:
                    progressDialog.dismiss();
                    showMessage("照片上传失败!");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_complete);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mContext = LocalCompleteActivity.this;
        initView();
    }
    private void  initView(){
        mSpinner = (Spinner) findViewById(R.id.localComplete_sp);
        mListView = (ListView) findViewById(R.id.localComplete_lv);
        /**/
        if (mBhqInfoBeanList == null) {
            mBhqInfoBeanList = queryLocalBhqInfos();
        }
        spAdapter = new SpinnerAdapter();
        mSpinner.setAdapter(spAdapter);
        /**/
        BhqInfoBean bhqInfo = (BhqInfoBean)mSpinner.getSelectedItem();
        if (mXchcModels == null) {
            if (bhqInfo != null) {
                mXchcModels = queryLocalXchcModel(bhqInfo.getBHQID());
                Log.i("TAG", "initView: ---------------bhqid--------->"+bhqInfo.getBHQID());
            }
        }
        lvAdapter = new ListViewAdapter();
        mListView.setAdapter(lvAdapter);
        /*spinner选中事件
        * */
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String bhqid = mBhqInfoBeanList.get(position).getBHQID();
                Log.i("TAG", "onItemSelected:--------------------------./ddd-------> "+bhqid);
                mXchcModels = queryLocalXchcModel(mBhqInfoBeanList.get(position).getBHQID());
                lvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*
           listview ---------------->点击查看详细信息
        * */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //showMessage("显示详细信息");
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("查看详细信息");
                dialog.setPositiveButton("查看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, XchcActivity.class);
                        if (mXchcModels != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("localData",mXchcModels.get(position));
                            //intent.putExtra("bdData",mXchcModels.get(position));
                            Log.i("TAG", "onClick:------------------ "+mXchcModels.get(position).getHbspxg());
                            intent.putExtras(bundle);
                        }
                        intent.putExtra("type", "update");//执行更新操作
                        startActivity(intent);

                    }
                });
                dialog.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
            }
        });
        /*
         listview---------->长按删除某一项数据
        * */
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mXchcModels == null) {
                    return true;
                }
                AlertDialog.Builder alter = new AlertDialog.Builder(mContext);
                alter.setMessage("确定要删除("+mXchcModels.get(position).getJsxmmc()+")数据吗?");
                alter.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeMethod(position);
                        dialog.dismiss();
                    }
                });
                alter.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alter.create().show();
                return true;
            }
        });

    }
    //==========================菜单项================================================================
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
    //========================本地数据查询==========================================================
    /*查询当前用户所有核查完的保护区信息
    * */
    public List<BhqInfoBean> queryLocalBhqInfos(){
        if (mQueryLocalTableData == null) {
            mQueryLocalTableData = new QueryLocalTableData(mContext);
        }
        mBhqInfoBeanList = mQueryLocalTableData.queryLocalBhqInfoData("xchcInfoTab");
        return mBhqInfoBeanList;
    }
    private List<XchcModel> queryLocalXchcModel(String szbhqid) {
        if (mQueryLocalTableData == null) {
            mQueryLocalTableData = new QueryLocalTableData(mContext);
        }
        String sql = "select * from xchcInfoTab where yhdh = ? and szbhqid = ?";
        String[] argBings = new String[]{TempData.yhdh,szbhqid};
        mXchcModels = mQueryLocalTableData.queryXchcInfos(sql, argBings);
        return mXchcModels;
    }
    private void removeMethod(int position) {
        if (mXchcModels!= null) {
            boolean reault = removeSqliteData("xchcInfoTab", "szbhqid = ? and objectid = ?  and yhdh = ?", new String[]{mXchcModels.get(position).getSzbhqid(),String.valueOf(mXchcModels.get(position).getObjectid()),mXchcModels.get(position).getYhdh()});
            if (reault) {
                mXchcModels.remove(position);
                lvAdapter.notifyDataSetChanged();

            } else {
                showMessage("删除失败...");
            }
        }

    }
    private void removeXchcModel(XchcModel bean){
        if (bean != null) {
            boolean reault = removeSqliteData("xchcInfoTab", "szbhqid = ? and objectid = ?  and yhdh = ?", new String[]{bean.getSzbhqid(),String.valueOf(bean.getObjectid()),bean.getYhdh()});
            if (reault) {
                mXchcModels.remove(bean);
                lvAdapter.notifyDataSetChanged();
            } else {
                showMessage("删除失败...");
            }
        }
    }
    private boolean removeSqliteData(String table, String where, String[] args) {
        dbManager = new DBManager(mContext);
        boolean result = dbManager.deleteTableData(table, where, args);
        return result;
    }
    //===========================创建Adapter内部类==================================================
    class  SpinnerAdapter extends  BaseAdapter{


        @Override
        public int getCount() {
            return mBhqInfoBeanList == null ? 0 :mBhqInfoBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBhqInfoBeanList == null? null : mBhqInfoBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_show_complete_spainner_item, null);
                holder = new ViewHolder();
                holder.bhqmc_txt = (TextView) convertView.findViewById(R.id.bhqmc_txt);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            if (mBhqInfoBeanList!= null) {
                holder.bhqmc_txt.setText(mBhqInfoBeanList.get(position).getBHQNAME());
            }
            return convertView;
        }

        class ViewHolder {
            TextView bhqmc_txt;
        }
    }

    class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mXchcModels == null ? 0 : mXchcModels.size();
        }

        @Override
        public Object getItem(int position) {
            return mXchcModels == null ? null :mXchcModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            myHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.activity_show_complete_listview_item, null);
                holder = new myHolder();
                holder.JsxmName = (TextView) convertView.findViewById(R.id.complete_list_qyName);
                holder.Jsxmlx = (TextView) convertView.findViewById(R.id.complete_list_tbr);
                holder.upload = (Button) convertView.findViewById(R.id.uploadBtn);
                convertView.setTag(holder);
            }
            holder = (myHolder) convertView.getTag();
            if (mXchcModels != null) {
                holder.JsxmName.setText(mXchcModels.get(position).getJsxmmc());
                holder.Jsxmlx.setText(mXchcModels.get(position).getJsxmlx());
                holder.upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    /*上传每一项内容*/
                        int netType = CheckNetwork.getNetWorkState(mContext);
                        if (netType == 0) {//none
                            showMessage("请打开网络连接");
                        } else if (netType == 1) {//wifi
                            upload(position);
                        } else if (netType == 2) {//mobile
                            upload(position);
                        }

                    }
                });
            }

            return  convertView;
        }
        class myHolder {
            TextView JsxmName;
            TextView Jsxmlx;
            Button upload;
        }
    }

    //==========================消息提示===========================================================
    public void showMessage(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
    //============================Activity生命周期==================================================

    @Override
    protected void onResume() {
        super.onResume();
        IPURL = HttpUrlAddress.getHttpUrl();//getResources().getString(R.string.http_url);
        if (mQueryLocalTableData == null) {
            mQueryLocalTableData = new QueryLocalTableData(mContext);
        }
        mBhqInfoBeanList = queryLocalBhqInfos();
        spAdapter.notifyDataSetChanged();
        BhqInfoBean bhqInfo = (BhqInfoBean)mSpinner.getSelectedItem();
        if (bhqInfo != null) {
            mXchcModels = queryLocalXchcModel(bhqInfo.getBHQID());
        } else {
            mXchcModels = null;
        }
        lvAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQueryLocalTableData != null) {
            mQueryLocalTableData.closeDbManager();
        }

        if (dbManager != null) {
            dbManager.closeDB();
        }
    }
    //===================================================================
    private void upload(int position) {
        //1.组织要提交的数据
        List<XchcModel> list = new ArrayList<>();
        if (mXchcModels == null) {
            return;
        }
        dataModel  = mXchcModels.get(position);
        XchcJsonBean jsonBean = new XchcJsonBean();
        if (dataModel.getIscheckeddm() == null) {
            dataModel.setIscheckeddm("21");
        }

        list.add(dataModel);
        jsonBean.setList(list);
        Gson gson = new Gson();
        final String jsonStr = gson.toJson(jsonBean);
        Log.i("TAG", "onClick: ------------现场核查表上传数据----------->" + jsonStr);
        //2.连接服务器上传
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setMessage("是否上传数据？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("正在上传数据...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        //threadFlag = false;
                        //picthreadFlag = false;
                    }
                });
                progressDialog.show();
                new Thread(new Runnable() {//上传数据
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                        Response response = ParseIntentData.getDataPostByJson(IPURL + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
                        if (response != null && response.code() == 200) {
                            try {
                                String responseValue = response.body().string();
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
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("正在上传照片...");
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                PhotoCompress photoCompress = new PhotoCompress();

                Bitmap bitmap = photoCompress.getImage(dataModel.getPhotourl());
                File file = FileUtils.saveBitmap(bitmap, dataModel.getPhotoName());
                if (file == null) {
                    return;
                }
                String url = IPURL+"/bhqService/BhqJsxm/Upload";
                Response response = ParseIntentData.upLoadImage(url,file.getAbsolutePath(),null);
                if (response != null && response.code() == 200) {
                    message.what = UPLOADPICSUCCEED;
                } else {
                    message.what = UPLOADPICFAIL;
                }
                myHandler.sendMessage(message);
            }
        }).start();

    }
    //===================================================================
}
