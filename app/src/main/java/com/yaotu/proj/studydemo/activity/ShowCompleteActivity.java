package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yaotu.proj.studydemo.BLL.AssartOperate;
import com.yaotu.proj.studydemo.BLL.DevelopOperate;
import com.yaotu.proj.studydemo.BLL.EntityFactory;
import com.yaotu.proj.studydemo.BLL.IndustryOperate;
import com.yaotu.proj.studydemo.BLL.KczyOperate;
import com.yaotu.proj.studydemo.BLL.NewEnertyOperate;
import com.yaotu.proj.studydemo.BLL.Operate;
import com.yaotu.proj.studydemo.BLL.TkEnterpriseOperate;
import com.yaotu.proj.studydemo.BLL.TravelOperate;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.BhqInfoBean;
import com.yaotu.proj.studydemo.bean.PlaceBean;
import com.yaotu.proj.studydemo.bean.tableBean.AssartBean;
import com.yaotu.proj.studydemo.bean.tableBean.AssartJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.AssartRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.KczyJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.KczyRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.QyInfoBean;
import com.yaotu.proj.studydemo.bean.tableBean.TKJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkEnterpriseBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkRecord;
import com.yaotu.proj.studydemo.bean.tableBean.TravelBean;
import com.yaotu.proj.studydemo.bean.tableBean.TravelJsonBean;
import com.yaotu.proj.studydemo.bean.tableBean.TravelRecordBean;
import com.yaotu.proj.studydemo.bean.tableBean.kczyBean;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.customclass.UpdateLocalTableData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Response;

/**
 * 显示已经完成的外业点，和未完成的外业点
 * Created by Administrator on 2017/2/27.
 */

public class ShowCompleteActivity extends AppCompatActivity {
    private Context context = ShowCompleteActivity.this;
    private final String TAG = this.getClass().getSimpleName();
    private ListView listView;
    private List<PlaceBean> listData;
    private Spinner spinner_bhq;//所属保护区
    private TextView qyType_txt;//企业类型
    private DBManager dbManager;
    private Cursor cursor;
    private myBhqBaseAdapter spinnerAdapter;
    private myListAdapter myListviewAdapter;
    private List<BhqInfoBean> bhqInfoList = new ArrayList<BhqInfoBean>();
    private List<QyInfoBean> listQyInfo = new ArrayList<>();
    private String qyTypeStr = "";//选中查询企业类型
    private ProgressDialog progresDialog;

    private final int UPLOADSUCCEED = 0X110;
    private final int UPLOADFAIL = 0X111;
    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPLOADSUCCEED:
                    progresDialog.dismiss();
                    removeMethod(recordPosition);
                    break;
                case UPLOADFAIL:
                    progresDialog.dismiss();
                    showMessage("上传失败...");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_complete);
        listView = (ListView) findViewById(R.id.complete_list_view);
        qyType_txt = (TextView) findViewById(R.id.qyType_txt);
        spinner_bhq = (Spinner) findViewById(R.id.spinner_bhq);

        initMethod();
        /*初始化时显示提示信息*/
        spinnerAdapter = new myBhqBaseAdapter();
        BhqInfoBean bhq = new BhqInfoBean();
        bhq.setBHQNAME("暂无数据...");
        bhqInfoList.add(bhq);
        spinner_bhq.setAdapter(spinnerAdapter);
        /**/
        myListviewAdapter = new myListAdapter();
        listView.setAdapter(myListviewAdapter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    private void initMethod() {
        final String[] qyItems = new String[]{"探矿企业", "工业企业", "矿产资源开发企业", "旅游资源开发企业", "新能源项目", "开垦活动", "其他开发建设活动"};
        qyType_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setItems(qyItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: ---------->" + qyItems[which]);
                        qyType_txt.setText(qyItems[which]);
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
            }
        });
        /*文本框改变触发事件，动态添加spinner
        * */
        qyType_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qyTypeStr = qyType_txt.getText().toString().trim();
                if ("".equals(qyTypeStr)) {
                    return;
                }
                if (qyTypeStr.equals("探矿企业")) {
                    qyeryLocalData("TkqyInfo");
                } else if (qyTypeStr.equals("工业企业")) {
                    qyeryLocalData("GyqyInfo");
                } else if (qyTypeStr.equals("矿产资源开发企业")) {
                    qyeryLocalData("KczyqyInfo");
                } else if (qyTypeStr.equals("旅游资源开发企业")) {
                    qyeryLocalData("TravelqyInfo");
                } else if (qyTypeStr.equals("新能源项目")) {
                    qyeryLocalData("NewEnergyqyInfo");
                } else if (qyTypeStr.equals("开垦活动")) {
                    qyeryLocalData("AssartqyInfo");
                } else if (qyTypeStr.equals("其他开发建设活动")) {
                    qyeryLocalData("DevelopqyInfo");
                }
                if (bhqInfoList.size() > 0) {
                    SqliteListView(bhqInfoList.get(0).getBHQID(), TempData.username);
                }
                spinnerAdapter.notifyDataSetChanged();


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (bhqInfoList.size() == 0) {
                    listQyInfo.clear();
                    myListviewAdapter.notifyDataSetChanged();
                }

            }
        });
        /*spinner选中事件
        * */
        spinner_bhq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelected: --------" + bhqInfoList.get(position).getBHQID());//从集合中获取被选择的数据项
                String bhqid = bhqInfoList.get(position).getBHQID();
                SqliteListView(bhqid, TempData.username);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*
         listview---------->长按删除某一项数据
        * */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alter = new AlertDialog.Builder(context);
                alter.setMessage("确定要删除该数据吗?");
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
        /*
           listview ---------------->点击查看详细信息
        * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //showMessage("显示详细信息");
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setView(showDetailInfoDialog(listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc()));
                dialog.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*进行修改*/
                        updateLocalData(listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getBhqmc(), listQyInfo.get(position).getPlaceid());
                        dialog.dismiss();
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

    }

    /*用来填充Spinner
    * */
    class myBhqBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bhqInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return bhqInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_show_complete_spainner_item, null);
                holder = new ViewHolder();
                holder.bhqmc_txt = (TextView) convertView.findViewById(R.id.bhqmc_txt);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            holder.bhqmc_txt.setText(bhqInfoList.get(position).getBHQNAME());
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
            return listQyInfo.size();
        }

        @Override
        public Object getItem(int position) {
            return listQyInfo.get(position);
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
                holder.qyName = (TextView) convertView.findViewById(R.id.complete_list_qyName);
                holder.txtTbr = (TextView) convertView.findViewById(R.id.complete_list_tbr);
                holder.upload = (Button) convertView.findViewById(R.id.uploadBtn);
                convertView.setTag(holder);
            }
            holder = (myHolder) convertView.getTag();

            holder.qyName.setText(listQyInfo.get(position).getQymc());
            holder.txtTbr.setText(listQyInfo.get(position).getTbr());
            holder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*上传每一项内容*/
                    int netType = CheckNetwork.getNetWorkState(context);
                    if (netType == 0) {//none
                        showMessage("请打开网络连接");
                    } else if (netType == 1) {//wifi
                        upload(position);
                    } else if (netType == 2) {//mobile
                        upload(position);
                    }

                }
            });
            return convertView;
        }

        class myHolder {
            TextView qyName;
            TextView txtTbr;
            Button upload;
        }
    }

    //=======================================================================================================
    private void upload(final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("是否要上传(" + listQyInfo.get(position).getQymc() + ")到服务器?");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progresDialog = new ProgressDialog(context);
                progresDialog.setMessage("正在上传数据...");
                            /* 方法一：setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
                            方法二：setCanceleable(false);调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用
                            * */
                //progresDialog.setCancelable(false);
                progresDialog.setCanceledOnTouchOutside(false);
                progresDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        threadFlag = false;
                    }
                });
                progresDialog.show();
                upLoadData(position);
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
    //=======================================================================================================
    /*根据不同企业，查询出所填表信息的保护区信息
    * */

    private void qyeryLocalData(String tabName) {
        bhqInfoList.clear();
        dbManager = new DBManager(context);
        cursor = dbManager.queryEntity("select distinct bhqid,bhqmc from " + tabName + " where username = ?", new String[]{TempData.username});
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            BhqInfoBean bean = new BhqInfoBean();
            bean.setBHQID(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBHQNAME(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bhqInfoList.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //=================================删除表中数据=======================================================
    private void removeMethod(int position) {
        if (qyTypeStr.equals("探矿企业")) {
            boolean reault = removeSqliteData("TkqyInfo", "bhqid = ? and placeid = ?  and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (reault) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("TkqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }
        } else if (qyTypeStr.equals("工业企业")) {
            boolean result = removeSqliteData("GyqyInfo", "bhqid = ? and placeid = ?   and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (result) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("GyqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }

        } else if (qyTypeStr.equals("矿产资源开发企业")) {
            boolean result = removeSqliteData("KczyqyInfo", "bhqid = ? and placeid = ?   and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (result) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("KczyqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }

        } else if (qyTypeStr.equals("旅游资源开发企业")) {
            boolean result = removeSqliteData("TravelqyInfo", "bhqid = ? and placeid = ?   and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (result) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("TravelqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }

        } else if (qyTypeStr.equals("新能源项目")) {
            boolean result = removeSqliteData("NewEnergyqyInfo", "bhqid = ? and placeid = ?   and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (result) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("NewEnergyqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }

        } else if (qyTypeStr.equals("开垦活动")) {
            boolean result = removeSqliteData("AssartqyInfo", "bhqid = ? and placeid = ?   and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (result) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("AssartqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }

        } else if (qyTypeStr.equals("其他开发建设活动")) {
            boolean result = removeSqliteData("DevelopqyInfo", "bhqid = ? and placeid = ?   and username = ?", new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getPlaceid(), TempData.username});
            if (result) {
                listQyInfo.remove(position);
                myListviewAdapter.notifyDataSetChanged();
                qyeryLocalData("DevelopqyInfo");
                if (listQyInfo.size() == 0) {
                    spinnerAdapter.notifyDataSetChanged();
                }

            } else {
                showMessage("删除失败...");
            }

        }
    }

    private boolean removeSqliteData(String table, String where, String[] args) {
        dbManager = new DBManager(context);
        boolean result = dbManager.deleteTableData(table, where, args);
        return result;
    }

    //=============================================================================================
    /*填充ListView*/
    private void SqliteListView(String bhqid, String username) {

        QueryLocalTableData qutable = new QueryLocalTableData(context);
        String sql = "";
        String[] whereParam = new String[]{bhqid, username};
        if (qyTypeStr.equals("探矿企业")) {
            sql = "select * from TkqyInfo where bhqid = ? and username=?";
        } else if (qyTypeStr.equals("工业企业")) {
            sql = "select * from GyqyInfo where bhqid = ? and username=?";
        } else if (qyTypeStr.equals("矿产资源开发企业")) {
            sql = "select * from KczyqyInfo where bhqid = ? and username=?";
        } else if (qyTypeStr.equals("旅游资源开发企业")) {
            sql = "select * from TravelqyInfo where bhqid = ? and username=?";
        } else if (qyTypeStr.equals("新能源项目")) {
            sql = "select * from NewEnergyqyInfo where bhqid = ? and username=?";
        } else if (qyTypeStr.equals("开垦活动")) {
            sql = "select * from AssartqyInfo where bhqid = ? and username=?";
        } else if (qyTypeStr.equals("其他开发建设活动")) {
            sql = "select * from DevelopqyInfo where bhqid = ? and username=?";
        }
        if (!"".equals(sql)) {
            listQyInfo = qutable.queryqyInfo(sql, whereParam);
        }
        myListviewAdapter.notifyDataSetChanged();
    }

    //------------------------单独上传每一项数据---------------------------------------
    private int recordPosition = 0;//作用：记录position值，用于把数据上传成功之后更新ListView

    private void upLoadData(int position) {
        recordPosition = position;
        QueryLocalTableData query = new QueryLocalTableData(context);
        String sql = "";
        String[] bindArgs = null;
        if (qyTypeStr.equals("探矿企业")) {
            sql = "select * from TkqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            TkEnterpriseBean bean = query.queryTkqyInfo(sql, bindArgs);
            List<TkRecord> Records = new ArrayList<>();
            TkRecord tkRecord = new TkRecord();
            tkRecord.setObjectid(bean.getPlaceid());
            tkRecord.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            tkRecord.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            tkRecord.setBean(bean);
            Records.add(tkRecord);
            TKJsonBean jsonbean = new TKJsonBean();
            jsonbean.setKey("02");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
            //showMessage("上传" + listQyInfo.get(position).getQymc());
        } else if (qyTypeStr.equals("工业企业")) {
            sql = "select * from GyqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            IndustryBean bean = query.queryGyqyInfo(sql, bindArgs);
            List<IndustryRecordBean> Records = new ArrayList<>();
            IndustryRecordBean inRecordBean = new IndustryRecordBean();
            inRecordBean.setObjectid(bean.getPlaceid());
            inRecordBean.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            inRecordBean.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            inRecordBean.setBean(bean);
            Records.add(inRecordBean);
            IndustryJsonBean jsonbean = new IndustryJsonBean();
            jsonbean.setKey("03");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
        } else if (qyTypeStr.equals("矿产资源开发企业")) {
            sql = "select * from KczyqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            kczyBean bean = query.queryKczyQyInfo(sql, bindArgs);
            List<KczyRecordBean> Records = new ArrayList<>();
            KczyRecordBean kcRecord = new KczyRecordBean();
            kcRecord.setObjectid(bean.getPlaceid());
            kcRecord.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            kcRecord.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            kcRecord.setBean(bean);
            Records.add(kcRecord);
            KczyJsonBean jsonbean = new KczyJsonBean();
            jsonbean.setKey("01");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
        } else if (qyTypeStr.equals("旅游资源开发企业")) {
            sql = "select * from TravelqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            TravelBean bean = query.queryTravelQyInfo(sql, bindArgs);
            List<TravelRecordBean> Records = new ArrayList<>();
            TravelRecordBean trRecord = new TravelRecordBean();
            trRecord.setObjectid(bean.getPlaceid());
            trRecord.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            trRecord.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            trRecord.setBean(bean);
            Records.add(trRecord);
            TravelJsonBean jsonbean = new TravelJsonBean();
            jsonbean.setKey("04");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
        } else if (qyTypeStr.equals("新能源项目")) {
            sql = "select * from NewEnergyqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            NewEnergyBean bean = query.queryNewEnergyQyInfo(sql, bindArgs);
            List<NewEnergyRecordBean> Records = new ArrayList<>();
            NewEnergyRecordBean neRecord = new NewEnergyRecordBean();
            neRecord.setObjectid(bean.getPlaceid());
            neRecord.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            neRecord.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            neRecord.setBean(bean);
            Records.add(neRecord);
            NewEnergyJsonBean jsonbean = new NewEnergyJsonBean();
            jsonbean.setKey("05");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
        } else if (qyTypeStr.equals("开垦活动")) {
            sql = "select * from AssartqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            AssartBean bean = query.queryAssartQyInfo(sql, bindArgs);
            List<AssartRecordBean> Records = new ArrayList<>();
            AssartRecordBean asRecord = new AssartRecordBean();
            asRecord.setObjectid(bean.getPlaceid());
            asRecord.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            asRecord.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            asRecord.setBean(bean);
            Records.add(asRecord);
            AssartJsonBean jsonbean = new AssartJsonBean();
            jsonbean.setKey("06");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
        } else if (qyTypeStr.equals("其他开发建设活动")) {
            sql = "select * from DevelopqyInfo where bhqid = ? and jsxmmc = ? and username = ?";
            bindArgs = new String[]{listQyInfo.get(position).getBhqid(), listQyInfo.get(position).getQymc(), listQyInfo.get(position).getTbr()};
            DevelopConstructionBean bean = query.queryDevelopQyInfo(sql, bindArgs);
            List<DevelopConstructionRecordBean> Records = new ArrayList<>();
            DevelopConstructionRecordBean dcRecord = new DevelopConstructionRecordBean();
            dcRecord.setObjectid(bean.getPlaceid());
            dcRecord.setCenterpointx(Double.parseDouble(bean.getCenterpointx()));
            dcRecord.setCenterpointy(Double.parseDouble(bean.getCenterpointy()));
            dcRecord.setBean(bean);
            Records.add(dcRecord);
            DevelopConstructionJsonBean jsonbean = new DevelopConstructionJsonBean();
            jsonbean.setKey("07");
            jsonbean.setYhdh(TempData.username);
            if (bean.getJsxmid().equals("0")) {
                jsonbean.setIschecked("21");
            } else {
                jsonbean.setIschecked("22");
            }
            jsonbean.setRecord(Records);
            Gson gson = new Gson();
            String jsonStr = gson.toJson(jsonbean);
            myUploadThread myThread = new myUploadThread(jsonStr);
            Log.i(TAG, "upLoadData: ------------->" + jsonStr);
            new Thread(myThread).start();
        }
    }

    private boolean threadFlag = true;

    class myUploadThread implements Runnable {
        String jsonStr = "";
        String ipUrl = getResources().getString(R.string.http_url);
        Message message = Message.obtain();

        public myUploadThread(String jsonStr) {
            this.jsonStr = jsonStr;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (threadFlag) {
                Response response = ParseIntentData.getDataPostByJson(ipUrl + "/WebEsriApp/actionapi/cjwBhqJsxm/SaveBhqJsxmInfo", jsonStr);
                Log.i(TAG, "run: ---------上传数据值：" + jsonStr);
                if (response != null && response.code() == 200) {//判断是否有返回结果，有结果则结束线程
                    try {
                        String value = response.body().string();
                        if (value.contains("success")) {
                            message.what = UPLOADSUCCEED;
                            myHandler.sendMessage(message);
                        } else {
                            message.what = UPLOADFAIL;
                            myHandler.sendMessage(message);
                        }
                        Log.i(TAG, "run: ------------数据上传返回结果：" + value + "---------上传数据值：" + jsonStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    threadFlag = false;
                } else {
                    message.what = UPLOADFAIL;
                    myHandler.sendMessage(message);
                    threadFlag = false;
                }

                Log.i(TAG, "run: -----------" + response);
            }

        }
    }

    //==========================================显示dialog信息========================================
    Operate operate;

    private View showDetailInfoDialog(String bhqid, String jsxmmc) {
        QueryLocalTableData queryLocal = new QueryLocalTableData(context);
        String[] whereParam = new String[]{bhqid, jsxmmc, TempData.username};
        String sql = "";
        View view = null;
        if (qyTypeStr.equals("探矿企业")) {
            sql = "select * from TkqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            TkEnterpriseBean bean = queryLocal.queryTkqyInfo(sql, whereParam);
            Log.i(TAG, "showDetailInfoDialog: ------------->" + bean.getBhqmc());
            operate = EntityFactory.getTkOperate(new TkEnterpriseOperate(context));
            view = operate.getView(bean);
        } else if (qyTypeStr.equals("工业企业")) {
            sql = "select * from GyqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            IndustryBean bean = queryLocal.queryGyqyInfo(sql, whereParam);
            operate = EntityFactory.getIndustryOperate(new IndustryOperate(context));
            view = operate.getView(bean);
        } else if (qyTypeStr.equals("矿产资源开发企业")) {
            sql = "select * from KczyqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            kczyBean bean = queryLocal.queryKczyQyInfo(sql, whereParam);
            operate = EntityFactory.getKczyOperate(new KczyOperate(context));
            view = operate.getView(bean);
        } else if (qyTypeStr.equals("旅游资源开发企业")) {
            sql = "select * from TravelqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            TravelBean bean = queryLocal.queryTravelQyInfo(sql, whereParam);
            Log.i(TAG, "showDetailInfoDialog: ----------mmmmm-----------" + bean.getYyqk());
            operate = EntityFactory.getTravelOperate(new TravelOperate(context));
            view = operate.getView(bean);
        } else if (qyTypeStr.equals("新能源项目")) {
            sql = "select * from NewEnergyqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            NewEnergyBean bean = queryLocal.queryNewEnergyQyInfo(sql, whereParam);
            operate = EntityFactory.getNewOperate(new NewEnertyOperate(context));
            view = operate.getView(bean);
        } else if (qyTypeStr.equals("开垦活动")) {
            sql = "select * from AssartqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            AssartBean bean = queryLocal.queryAssartQyInfo(sql, whereParam);
            operate = EntityFactory.getAssartOperate(new AssartOperate(context));
            view = operate.getView(bean);
        } else if (qyTypeStr.equals("其他开发建设活动")) {
            sql = "select * from DevelopqyInfo where bhqid = ? and jsxmmc = ? and username =?";
            DevelopConstructionBean bean = queryLocal.queryDevelopQyInfo(sql, whereParam);
            operate = EntityFactory.getDevelopOperate(new DevelopOperate(context));
            view = operate.getView(bean);
        }

        return view;
    }

    private void updateLocalData(String bhqid, String bhqmc, String jsxmid) {
        UpdateLocalTableData upDate = new UpdateLocalTableData(context);
        boolean result = false;
        if (qyTypeStr.equals("探矿企业")) {
            TkEnterpriseBean bean = (TkEnterpriseBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            Log.i(TAG, "updateLocalData: ------------------>" + bean.getBhqmc() + "-----" + bean.getPhotoPath());
            result = upDate.UpdateTkqyInfo(bean);
        } else if (qyTypeStr.equals("工业企业")) {
            IndustryBean bean = (IndustryBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            result = upDate.UpdateGyqyInfo(bean);
        } else if (qyTypeStr.equals("矿产资源开发企业")) {
            kczyBean bean = (kczyBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            result = upDate.UpdateKczyQyInfo(bean);
        } else if (qyTypeStr.equals("旅游资源开发企业")) {
            TravelBean bean = (TravelBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            Log.i(TAG, "updateLocalData: -----------------dhd------>" + bean.getYyqk());
            result = upDate.UpdateTravelQyInfo(bean);
        } else if (qyTypeStr.equals("新能源项目")) {
            NewEnergyBean bean = (NewEnergyBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            result = upDate.UpdateNewEnergyQyInfo(bean);
        } else if (qyTypeStr.equals("开垦活动")) {
            AssartBean bean = (AssartBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            result = upDate.UpdateAssartQyInfo(bean);
        } else if (qyTypeStr.equals("其他开发建设活动")) {
            DevelopConstructionBean bean = (DevelopConstructionBean) operate.getEntityBean(bhqid, bhqmc, jsxmid);
            result = upDate.UpdateDevelopQyInfo(bean);
        }
        if (result) {
            showMessage("修改成功");
        } else {
            showMessage("修改失败");
        }

    }
    //================================================================================================

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            Log.i(TAG, "onKeyDown: ---------------------点击了返回按钮!");
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
