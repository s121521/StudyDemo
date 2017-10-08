package com.yaotu.proj.studydemo.customclass;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/19.
 * 矿权属性对话框下拉列表
 */

public class CustomDialog {
    private final int COMPLETE_DATA = 0x110;
    private Context context;
    private AlertDialog.Builder dialog;
    private ListView listView;
    private List<CodeTypeBean> listData;
    private EditText editText;
    private TextView dmz;
    private String codeType, title;
    private DBManager dbManager;
    private ConnectivityManager connectivityManager = null;
    private ProgressDialog progressdialog;
    private Cursor cursor;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case COMPLETE_DATA:
                    progressdialog.dismiss();
                    View view = LayoutInflater.from(context).inflate(R.layout.type_dialog_listview, null);
                    View title_view = LayoutInflater.from(context).inflate(R.layout.dialog_title, null);
                    TextView title_txt = (TextView) title_view.findViewById(R.id.dialog_title_item);
                    listView = (ListView) view.findViewById(R.id.dialog_listview);
                    listView.setAdapter(new myAdapter());
                    dialog = new AlertDialog.Builder(context);
                    //dialog.setTitle(title);
                    title_txt.setText(title);
                    dialog.setCustomTitle(title_view);
                    dialog.setView(view);
                    dialog.create();
                    final AlertDialog alertDialog = dialog.show();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            editText.setText(listData.get(position).getDMMC1());
                            dmz.setText(listData.get(position).getDMZ());
                            alertDialog.dismiss();
                        }
                    });

                    break;
            }
            return false;
        }
    });

    public CustomDialog(Context context,TextView dmz, EditText dmmc1, String title, String codeType) {
        this.context = context;
        this.editText = dmmc1;
        this.title = title;
        this.codeType = codeType;
        this.dmz = dmz;

    }

    /*
    * */
    public void showQksxDialog() {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
       /* if (networkInfo != null && networkInfo.isConnected()) {
            // Toast.makeText(context,"有网",Toast.LENGTH_SHORT).show();
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // Toast.makeText(context,"WIFI",Toast.LENGTH_SHORT).show();
                initData();
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Toast.makeText(context,"MOBILE",Toast.LENGTH_SHORT).show();
                initData();
            }
        } else {
            //Toast.makeText(context, "请联网在进行操作...", Toast.LENGTH_SHORT).show();
            localData();
        }*/
        localData();

    }

    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;
            if (convertView == null) {
                viewHold = new ViewHold();
                convertView = LayoutInflater.from(context).inflate(R.layout.type_dialog_item, null);
                viewHold.dmz = (TextView) convertView.findViewById(R.id.type_dmz);
                viewHold.dmmc1 = (TextView) convertView.findViewById(R.id.dmmc1);
                convertView.setTag(viewHold);
            } else {
                viewHold = (ViewHold) convertView.getTag();
            }
            viewHold.dmz.setText(listData.get(position).getDMZ());
            viewHold.dmmc1.setText(listData.get(position).getDMMC1());
            return convertView;
        }

        class ViewHold {
            TextView dmz;
            TextView dmmc1;
        }
    }

    private void initData() {
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("正在加载，请稍后……");
        progressdialog.setProgressStyle(android.R.animator.fade_in);
        progressdialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder builder = new FormBody.Builder();
                builder.add("code", codeType);
                String myIp = context.getResources().getString(R.string.http_url);
                Response response = ParseIntentData.getDataPostByString(myIp + "/bhqService/BhqJsxm/GetCodeDetails", builder);
                Gson gson = new Gson();
                Type type = new TypeToken<List<CodeTypeBean>>() {}.getType();
                try {
                    listData = gson.fromJson(response.body().string(), type);
                    if (listData != null) {
                        Log.i("TAG", "run:-------------- " + listData.toString());
                        Message message = Message.obtain();
                        message.what = COMPLETE_DATA;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void localData(){//加载本地数据
        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("正在加载，请稍后……");
        progressdialog.setProgressStyle(android.R.animator.fade_in);
        progressdialog.show();
        dbManager = new DBManager(context);
        listData = loadDataBySqlLite("select * from codeType where dmlb = ?", new String[]{codeType});
        if (listData != null) {
            Message message = Message.obtain();
            message.what = COMPLETE_DATA;
            handler.sendMessage(message);
        }

    }
    //--------------------------从本地数据库中读取bhqInfo表数据----------------------------------
    private List<CodeTypeBean> loadDataBySqlLite(String sql, String[] bindArgs) {//加载数据
        cursor = dbManager.queryEntity(sql, bindArgs);
        List<CodeTypeBean> list = new ArrayList<CodeTypeBean>();
        while (cursor.moveToNext()) {
            CodeTypeBean codetype = new CodeTypeBean();
            codetype.setDMLB(cursor.getString(cursor.getColumnIndex("dmlb")));
            codetype.setDMZ(cursor.getString(cursor.getColumnIndex("dmz")));
            codetype.setJB(cursor.getString(cursor.getColumnIndex("jb")));
            codetype.setLB(cursor.getString(cursor.getColumnIndex("lb")));
            codetype.setDMMC1(cursor.getString(cursor.getColumnIndex("dmmc1")));
            codetype.setDMMC2(cursor.getString(cursor.getColumnIndex("dmmc2")));
            codetype.setDMMC3(cursor.getString(cursor.getColumnIndex("dmmc3")));
            codetype.setSXH(cursor.getString(cursor.getColumnIndex("sxh")));
            codetype.setBZ(cursor.getString(cursor.getColumnIndex("bz")));
            list.add(codetype);
        }
        cursor.close();
        System.out.println(list.size()+"???????????????????"+cursor.getCount());
        dbManager.closeDB();
        return list;
    }

}
