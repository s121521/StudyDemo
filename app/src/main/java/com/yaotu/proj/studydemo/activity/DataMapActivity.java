package com.yaotu.proj.studydemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.adapter.AllPhotoAdapter;
import com.yaotu.proj.studydemo.bean.PlaceBean;
import com.yaotu.proj.studydemo.util.DBManager;
import com.yaotu.proj.studydemo.util.MyGeoDataBase;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Administrator on 2017/2/7.
 */

public class DataMapActivity extends AppCompatActivity {
    private ListView my_listview;
    private TextView txt_showinfo;
    private BaseAdapter baseAdapter,m_adapter;
    private  final int  RESULT_CODE = 1;
    private  List<PlaceBean> list;
    private final String TAG = this.getClass().getSimpleName();
    private final Context context = DataMapActivity.this;
    private DBManager dbManager;
    private Cursor cursor;
    private ProgressDialog gressDialog;
    private AlertDialog.Builder dialog;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what ==1) {
                list = loadDataBySqlLite();
                my_listview.setAdapter(new myBaseAdapter(list));
                gressDialog.dismiss();
            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datamap);
        my_listview = (ListView) findViewById(R.id.listview_mapdata);
        txt_showinfo = (TextView) findViewById(R.id.txt_data_hint);
        gressDialog = new ProgressDialog(context);
        gressDialog.setMessage("数据加载中!请稍候...");
        gressDialog.show();
        dbManager = new DBManager(context);//初始化DBManager
        //cursor = dbManager.queryEntity("select * from place", null);

       /*2017-02-03注解
       if (cursor.getCount() == 0) {//place表内容为空
            final MyGeoDataBase data_util = new MyGeoDataBase();
            list = data_util.getListEntity();
            final int len = list.size();

            new Thread(new Runnable() {
               @Override
               public void run() {
                   for (int i = 0 ; i < len ; i++) {
                       dbManager.updateBySql("insert into place(p_name,p_type,p_state) values (?,?,?)",new Object[]{list.get(i).getP_name(),list.get(i).getP_type(),list.get(i).getP_state()} );
                       Log.i(TAG, "onCreate: ================="+i);
                   }

                   Message message = Message.obtain();
                   message.what = 1;
                   handler.sendMessage(message);
               }
           }).start();
        } else {
            list = loadDataBySqlLite();
            Log.i(TAG, "onCreate: sdfsdfsdf=================");
            my_listview.setAdapter(new myBaseAdapter(list));
            gressDialog.dismiss();
        }*/
        //==================================================================================================
        /*baseAdapter=new BaseAdapter() {//扩展BaseAdapter，实现对listview的填充
            @Override
            public int getCount() {//指定一共包含多少项
                return list.size();
            }

            @Override
            public Object getItem(int position) {//返回position处的列表项的内容
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {//返回第position处的列表ID
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {//返回的view作为每一项的组件
                View view= LayoutInflater.from(DataMapActivity.this).inflate(R.layout.activity_datamap_list_item,null);
                ImageView image = (ImageView) view.findViewById(R.id.img_dataImage);
                final TextView p_name = (TextView) view.findViewById(R.id.txt_dataName);
                p_name.setText(list.get(position).getP_name());
                Log.i(TAG, "getView: ========================>"+list.size());
                view.setOnClickListener(new View.OnClickListener() {//为每一个view设置单击事件
                    @Override
                    public void onClick(View v) {
                        Intent intent=getIntent();
                        intent.putExtra("dataName", p_name.getText().toString());
                        intent.putExtra("tabType", list.get(position).getP_type());
                        DataMapActivity.this.setResult(RESULT_CODE,intent);
                        DataMapActivity.this.finish();
                    }
                });
                return view;
            }
        };*/


        /*MyGeoDataBase data_util = new MyGeoDataBase();

        list = data_util.getListEntity();
        if (list.size()<=0) {
            //showMessage("请检查手机是否加载.geodatabase数据");
            my_listview.setVisibility(View.GONE);
            txt_showinfo.setText("请检查手机是否加载.geodatabase数据");
            return;
        }
        Log.i(TAG, "ListData------------>"+list.size());
        baseAdapter=new BaseAdapter() {//扩展BaseAdapter，实现对listview的填充
            @Override
            public int getCount() {//指定一共包含多少项
                return list.size();
            }

            @Override
            public Object getItem(int position) {//返回position处的列表项的内容
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {//返回第position处的列表ID
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {//返回的view作为每一项的组件
                View view= LayoutInflater.from(DataMapActivity.this).inflate(R.layout.activity_datamap_list_item,null);
                ImageView image = (ImageView) view.findViewById(R.id.img_dataImage);
                final TextView p_name = (TextView) view.findViewById(R.id.txt_dataName);
                p_name.setText(list.get(position).getP_name());
                view.setOnClickListener(new View.OnClickListener() {//为每一个view设置单击事件
                    @Override
                    public void onClick(View v) {
                        Intent intent=getIntent();
                        intent.putExtra("dataName", p_name.getText().toString());
                        intent.putExtra("tabType", list.get(position).getP_type());
                        DataMapActivity.this.setResult(RESULT_CODE,intent);
                        DataMapActivity.this.finish();
                    }
                });
                return view;
            }
        };
        my_listview.setAdapter(baseAdapter);*/
        my_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//长按删除listview 中item项
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog = new AlertDialog.Builder(context);
                dialog.setMessage("是否删除本条数据?");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       boolean result =  dbManager.updateBySql("delete from place where p_name = ? ",new Object[]{list.get(position).getP_name()});
                        if (result) {
                            list.remove(position);
                            m_adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            showMessage("删除失败 请重试...");
                        }
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
        my_listview.setOnItemClickListener(onItemClickListener);
    }
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=getIntent();
            intent.putExtra("dataName",list.get(position).getP_name());
            intent.putExtra("tabType", list.get(position).getP_type());
            intent.putExtra("p_lat", list.get(position).getLatitude());
            intent.putExtra("p_log", list.get(position).getLongitude());
            DataMapActivity.this.setResult(RESULT_CODE,intent);
            DataMapActivity.this.finish();
        }
    };

    class myBaseAdapter extends BaseAdapter {
        private List<PlaceBean> list;
        public myBaseAdapter(List<PlaceBean> _list) {
            this.list = _list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view= LayoutInflater.from(DataMapActivity.this).inflate(R.layout.activity_datamap_list_item,null);
            ImageView image = (ImageView) view.findViewById(R.id.img_dataImage);
            TextView p_name = (TextView) view.findViewById(R.id.txt_dataName);
            p_name.setText(list.get(position).getP_name());
            Log.i(TAG, "getView: ========================>"+list.size());
            /*view.setOnClickListener(new View.OnClickListener() {//为每一个view设置单击事件
                @Override
                public void onClick(View v) {
                    Intent intent=getIntent();
                    intent.putExtra("dataName", p_name.getText().toString());
                    intent.putExtra("tabType", list.get(position).getP_type());
                    intent.putExtra("p_lat", list.get(position).getLatitude());
                    intent.putExtra("p_log", list.get(position).getLongitude());
                    DataMapActivity.this.setResult(RESULT_CODE,intent);
                    DataMapActivity.this.finish();
                }
            });*/
            return view;
        }
    }

    /**
     *
     * @return2017-05-03注解
     */
   /* private List<PlaceBean> loadDataBySqlLite() {//加载数据
        cursor = dbManager.queryEntity("select * from place", null);
        List<PlaceBean> list = new ArrayList<PlaceBean>();
        while (cursor.moveToNext()) {
            Log.i(TAG, "loadDataBySqlLite: "+cursor.getString(cursor.getColumnIndex("p_name")));
            PlaceBean placeBean = new PlaceBean();
            placeBean.setP_name(cursor.getString(cursor.getColumnIndex("p_name")));
            placeBean.setP_type(cursor.getString(cursor.getColumnIndex("p_type")));
            placeBean.setP_state(cursor.getString(cursor.getColumnIndex("p_state")));
            list.add(placeBean);
        }
        //cursor.close();
        System.out.println(list.size()+"???????????????????"+cursor.getCount());
        return list;
    }*/
    private List<PlaceBean> loadDataBySqlLite() {//加载数据
        cursor = dbManager.queryEntity("select * from place", null);
        List<PlaceBean> list = new ArrayList<PlaceBean>();
        while (cursor.moveToNext()) {
            Log.i(TAG, "loadDataBySqlLite: "+cursor.getString(cursor.getColumnIndex("p_name")));
            PlaceBean placeBean = new PlaceBean();
            placeBean.setP_name(cursor.getString(cursor.getColumnIndex("p_name")));
            placeBean.setLongitude(cursor.getString(cursor.getColumnIndex("p_log")));
            placeBean.setLatitude(cursor.getString(cursor.getColumnIndex("p_lat")));
            placeBean.setP_type(cursor.getString(cursor.getColumnIndex("p_type")));
            placeBean.setP_state(cursor.getString(cursor.getColumnIndex("p_state")));
            list.add(placeBean);
        }
        //cursor.close();
        System.out.println(list.size()+"???????????????????"+cursor.getCount());
        return list;
    }
    //=========================================
    /**
     * 自定义菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "新增采样地");
        return super.onCreateOptionsMenu(menu);
    }
    private final int   REQUEST_CODE_ADDDATA = 0X00001;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Log.i("TAG", "新增采样地: ");
                Intent intent = new Intent(DataMapActivity.this, AddSamplePlotActivity.class);//Sample plot 样地、样区
                //startActivity(intent);//启动其他Activity
               startActivityForResult(intent,REQUEST_CODE_ADDDATA);   //启动其他Activity并返回结果
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADDDATA:
                if (resultCode == 1) {
                    m_adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param msg
     */
    private void showMessage(String msg) {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list = loadDataBySqlLite();
        Log.i(TAG, "onCreate: sdfsdfsdf=================");
        m_adapter = new myBaseAdapter(list);
        my_listview.setAdapter(m_adapter);
        gressDialog.dismiss();
    }

    /**
     *
     */

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        if (null != cursor) {
            cursor.close();
        }
        dbManager.closeDB();//关闭时应释放DB
        super.onDestroy();
    }
}
