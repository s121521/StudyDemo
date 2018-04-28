package com.yaotu.proj.studydemo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.yaotu.proj.studydemo.bean.BhqInfoBean;
import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.customclass.CheckNetwork;
import com.yaotu.proj.studydemo.customclass.QueryLocalTableData;
import com.yaotu.proj.studydemo.customclass.TempData;
import com.yaotu.proj.studydemo.intentData.ParseIntentData;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.HttpUrlAddress;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class BhqDataActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final int REFRESH_INTENT = 0X110;//刷新服务器数据
    private static final int REFRESH_LOCAL = 0X111;//刷新本地数据
    private  final String TAG = this.getClass().getSimpleName();
    private Context context = BhqDataActivity.this;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private EditText bhq_Query_etxt;
    private BaseAdapter my_Adapter;
    private List<BhqInfoBean> listData;
    private List<CodeTypeBean> listCode;
    private DBManager dbManager;
    private Cursor bhqCursor;
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case  REFRESH_INTENT:
                    Log.i(TAG, "handleMessage: -------------->");
                    my_Adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case REFRESH_LOCAL:
                     my_Adapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_bhq_data);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRLayout);
        listView = (ListView) findViewById(R.id.bhq_ListView);
        bhq_Query_etxt = (EditText) findViewById(R.id.bhq_Query_etxt);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        /*demo_Data*/
        dbManager = new DBManager(context);
        my_Adapter = new my_BaseAdapter();
        listData = loadDataBySqlLite("select * from bhqInfo where yhdh = ?",new String[]{TempData.yhdh});
        listView.setAdapter(my_Adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(this.getResources().getColor(R.color.blue));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=getIntent();
                if (listData != null) {
                    BhqInfoBean bean = listData.get(position);
                    intent.putExtra("bhqid",bean.getBHQID());
                    intent.putExtra("bhqmc", bean.getBHQNAME());
                    intent.putExtra("bhqjb", bean.getBHQLEVEL());
                    intent.putExtra("bhqjbdm", bean.getBHQLEVELDM());
                    BhqDataActivity.this.setResult(2,intent);
                    BhqDataActivity.this.finish();
                }
            }
        });
        bhq_Query_etxt.addTextChangedListener(new TextWatcher() {//EditText文本内容变化监听方法
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String args = bhq_Query_etxt.getText().toString().trim();
                if ("".equals(args)) {
                    listData = loadDataBySqlLite("select * from bhqInfo where yhdh = ?", new String[]{TempData.yhdh});
                    my_Adapter.notifyDataSetChanged();
                } else {
                    listData = loadDataBySqlLite("select * from bhqInfo where bhq_name like ? and yhdh = ?",new String[]{"%"+args+"%",TempData.yhdh});
                    my_Adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
   class  my_BaseAdapter extends  BaseAdapter{

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
           ViewHolder viewHolder;
           if (convertView == null) {
               viewHolder = new ViewHolder();
               convertView = getLayoutInflater().inflate(R.layout.bhq_listdata_item, null);
               viewHolder.bhqIdText = (TextView) convertView.findViewById(R.id.bhqid_txt);
               viewHolder.bhqNameText = (TextView) convertView.findViewById(R.id.bhqname_txt);
               convertView.setTag(viewHolder);
           } else {
               viewHolder = (ViewHolder) convertView.getTag();
           }
           if (listData!= null) {
               BhqInfoBean item = listData.get(position);
               viewHolder.bhqIdText.setText(item.getBHQID());
               viewHolder.bhqNameText.setText(item.getBHQNAME());
               Log.i(TAG, "getView: ===============================>");
           }
           return convertView;
       }
       class ViewHolder{
           TextView bhqIdText;
           TextView bhqNameText;
       }
   }
    @Override
    public void onRefresh() {
        /*下拉刷新基本思路
        * 1.从本地数据库中获取数据，listData
        * 2.判断listData是否有值: 有值listData.size() > 0,根据本地数据展示ListView;
        *                        无值listData.size() = 0,请求服务器，从服务器获取数据，添加到本地数据库中
        * */
        listData = loadDataBySqlLite("select * from bhqInfo where yhdh = ? ",new String[]{TempData.yhdh});
        int dataNum = 0;
        if (listData != null) {
            dataNum = listData.size();
        }
        if (dataNum > 0) {//本地数据库中有值
            myHandler.sendEmptyMessageAtTime(REFRESH_LOCAL,1*1000);
        } else {
            //listData =      //服务器获取数据
            int netType = CheckNetwork.getNetWorkState(context);
            if (netType == 0) {//none
                showMessage("请打开网络连接");
            } else if (netType == 1) {//wifi
                findBhqInfosByInternet();
            } else if (netType == 2) {//mobile
                findBhqInfosByInternet();
            }
        }

    }
    public void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        Log.i("TAG", "showMessage:-------------------> hhhhhhhhhh");
    }
    //--------------------------从本地数据库中读取bhqInfo表数据----------------------------------
    private List<BhqInfoBean> loadDataBySqlLite(String sql,String[] bindArgs) {//加载数据
        bhqCursor = dbManager.queryEntity(sql, bindArgs);
        List<BhqInfoBean> list = new ArrayList<BhqInfoBean>();
        while (bhqCursor.moveToNext()) {
            Log.i(TAG, "loadDataBySqlLite: "+bhqCursor.getString(bhqCursor.getColumnIndex("bhq_id")));
            BhqInfoBean bhqinfoBean = new BhqInfoBean();
            bhqinfoBean.setBHQID(bhqCursor.getString(bhqCursor.getColumnIndex("bhq_id")));
            bhqinfoBean.setBHQNAME(bhqCursor.getString(bhqCursor.getColumnIndex("bhq_name")));
            bhqinfoBean.setBHQLEVEL(bhqCursor.getString(bhqCursor.getColumnIndex("bhq_level")));
            bhqinfoBean.setBHQLEVELDM(bhqCursor.getString(bhqCursor.getColumnIndex("bhq_level_dm")));
            list.add(bhqinfoBean);
        }
        bhqCursor.close();
        System.out.println(list.size()+"???????????????????"+bhqCursor.getCount());
        return list;
    }
    private void insertDataBySqlLite(List<BhqInfoBean> listResult){
        if (listResult != null) {
            int len = listResult.size();
            for (int i = 0 ; i < len ; i++) {
                dbManager.updateBySql("insert into bhqInfo(bhq_id,bhq_name,bhq_level,bhq_level_dm,yhdh) values(?,?,?,?,?)",new Object[]{listResult.get(i).getBHQID(),listResult.get(i).getBHQNAME(),listResult.get(i).getBHQLEVEL(),listResult.get(i).getBHQLEVELDM(), TempData.yhdh});
            }
        }
    }

    private void insertCodeTypeBySql(List<CodeTypeBean> list){
        int len =0;
        if (list != null) {
            len = list.size();
            for (int i = 0; i < len; i++) {
               // Log.i(TAG, "insertCodeTypeBySql: ---------------->dmlb:"+list.get(i).getDMLB()+"----dmmc1:"+list.get(i).getDMMC1()+"----dmz:"+list.get(i).getDMZ());
                dbManager.updateBySql("insert into codeType(dmlb,dmz,jb,lb,dmmc1,dmmc2,dmmc3,sxh,bz) values(?,?,?,?,?,?,?,?,?)",new Object[]{list.get(i).getDMLB(),
                        list.get(i).getDMZ(),list.get(i).getJB(),list.get(i).getLB(),list.get(i).getDMMC1(),list.get(i).getDMMC2(),list.get(i).getDMMC3(),list.get(i).getSXH(),list.get(i).getBZ()});
            }
        }

    }
    private boolean removeDataBySqlLite() {
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
        boolean result1 = dbManager.deleteTableData("bhqInfo", "yhdh = ? ", new String[]{TempData.yhdh});
        boolean result2 = dbManager.deleteTableData("codeType", null, null);
        boolean result3 = dbManager.deleteTableData("bhqpointInfo", "yhdh = ?", new String[]{TempData.yhdh});
        if (result1 && result2 && result3) {
            return true;
        }
        return false;
    }
    //----------------------联网获取数据---------------------------------
    private void findBhqInfosByInternet() {
        final String ipUrl = HttpUrlAddress.getHttpUrl();//getResources().getString(R.string.http_url);
        new Thread(new Runnable() {
            @Override
            public void run() {//获取保护区基本信息数据

                //Response response = ParseIntentData.getDataGetByString(ipUrl+"/bhqService/BhqJsxm/GetBhqBasicInfoJson");
                String data = "{\"yhdh\":\"" + TempData.yhdh + "\"}";
                Response response = ParseIntentData.getDataPostByJson(ipUrl + "/bhqService/BhqJsxm/GetBhqBasicInfo", data);
                Gson gson = new Gson();
                Type type = new TypeToken<List<BhqInfoBean>>() {
                }.getType();
                try {
                    if (response != null && response.code() == 200) {
                        listData = gson.fromJson(response.body().string(), type);
                        Log.i(TAG, "run: -------------->success:" + response.code() + listData.toString());
                        insertDataBySqlLite(listData);
                        myHandler.sendEmptyMessage(REFRESH_INTENT);
                        Log.i(TAG, "run: -------------->" + response.code());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        QueryLocalTableData queryLocalTableData = new QueryLocalTableData(context);
        listCode = queryLocalTableData.loadAllCodeDataBySqlLite("select * from codeType", null);
        if (listCode.size() <= 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {//获取类别信息
                    List<CodeTypeBean> listCodeType = new ArrayList<CodeTypeBean>();
                    Response response = ParseIntentData.getDataGetByString(ipUrl + "/bhqService/BhqJsxm/GetAllRelatedCodeDetails");
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<CodeTypeBean>>() {
                    }.getType();
                    try {
                        if (response != null && response.code() == 200) {
                            listCodeType = gson.fromJson(response.body().string(), type);
                            Log.i(TAG, "run: ---------listCodeType----->success:" + response.code());
                            insertCodeTypeBySql(listCodeType);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
    //==============================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "清除本地数据");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("是否清除本地数据？");
                alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean result = removeDataBySqlLite();
                        if (result) {
                            showMessage("删除成功!");
                            listData.clear();
                            my_Adapter.notifyDataSetChanged();
                        } else {
                            showMessage("删除失败!");
                            my_Adapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    //==============================================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bhqCursor != null) {
            bhqCursor.close();
        }
        dbManager.closeDB();
    }

}
