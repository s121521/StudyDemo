package com.yaotu.proj.studydemo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.adapter.AllPhotoAdapter;
import com.yaotu.proj.studydemo.bean.Bimp;
import com.yaotu.proj.studydemo.bean.ImageBucket;
import com.yaotu.proj.studydemo.bean.ImageItem;
import com.yaotu.proj.studydemo.util.AlbumHelper;
import com.yaotu.proj.studydemo.util.AsyncImageLoad;

import java.util.ArrayList;
import java.util.List;
import static com.yaotu.proj.studydemo.R.string.pic_totalNum;
/**
 * 利用GridView显示所有系统相册图片
 * Created by Administrator on 2017/2/20.
 */

public class ShowPhotoActivity extends Activity {
    private GridView myGridview;
    private AllPhotoAdapter gridAdapter;
    private List<ImageItem> dataList;//照片集合
    private List<ImageBucket> bucketDataList;//照片包集合
    private boolean flag;//用于标记是否传值
    private Button complete_btn;//完成按钮
    private Button preview_btn;//预览按钮
    private final String TAG = getClass().getSimpleName();
    private Context context=ShowPhotoActivity.this;
    private  int selectNum;
    private int maxNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        myGridview = (GridView) findViewById(R.id.allPhoto_gridView);
        complete_btn = (Button) findViewById(R.id.complete_btn);
        // preview_btn = (Button) findViewById(R.id.preview_btn);
        initMethod();//初始化数据
        showBtnState();
    }


    private void initMethod() {
        AlbumHelper albumHelper = AlbumHelper.getInstance();
        albumHelper.init(getApplicationContext());
        bucketDataList = albumHelper.getImagesBucketList();
        dataList = new ArrayList<ImageItem>();
        for (int i = 0; i < bucketDataList.size(); i++) {
            dataList.addAll(bucketDataList.get(i).getImageItems());
        }
        Log.i(TAG, "initMethod: " + dataList.size() + "================" + bucketDataList.size());
        gridAdapter = new AllPhotoAdapter(ShowPhotoActivity.this, dataList, Bimp.tempSelectBitmap);
        myGridview.setAdapter(gridAdapter);

        gridAdapter.setMyOnItemClickListener(new AllPhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CheckBox checkBox,ToggleButton toggleButton, int position) {
               boolean flag=toggleButton.isChecked();
                if (maxNum <= selectNum && flag) {//判断选中图数量是否大于设置限额
                    Toast.makeText(context,"你最多选择" + maxNum + "张图片",Toast.LENGTH_SHORT).show();
                    toggleButton.setChecked(false);
                    checkBox.setChecked(false);
                    Log.i(TAG, "onItemClick: "+ flag);
                    return;
                }
                Log.i(TAG, "onItemClick: "+maxNum+"  "+selectNum);
                if (flag) {
                    checkBox.setChecked(true);//显示被选中状态
                    Bimp.tempSelectBitmap.add(dataList.get(position));//添加到选中图片的临时列表中
                    Log.i(TAG, "isChecked: "+flag+" 个数:"+ Bimp.tempSelectBitmap.size());
                } else {
                    checkBox.setChecked(false);//显示未选中状态
                    Bimp.tempSelectBitmap.remove(dataList.get(position));
                    Log.i(TAG, "isChecked: "+flag+" 个数:"+ Bimp.tempSelectBitmap.size());

                }

                showBtnState();
            }
        });
        //完成按钮事件，将所选图片显示到主界面
        complete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length=Bimp.tempSelectBitmap.size();
                if (length>0) {
                    Bitmap bitmap=null;
                    for (int i = 0;i < length;i++) {
                        String thumPath= Bimp.tempSelectBitmap.get(i).getThumbnailPath();
                        String imgPath=Bimp.tempSelectBitmap.get(i).getImagePath();
                        //if (null != thumPath) {
                         //   bitmap = BitmapFactory.decodeFile(thumPath);
                          //  Log.i(TAG, "onClick: "+"????????????????"+thumPath+"---"+bitmap);
                       // } else {
                            bitmap = new AsyncImageLoad().revitionImageSize(imgPath);
                       // }
                        Bimp.tempSelectBitmap.get(i).setBitmap(bitmap);
                        Bimp.SelectBitmap.add(Bimp.tempSelectBitmap.get(i));
                    }
                    Bimp.tempSelectBitmap.clear();
                    ShowPhotoActivity.this.setResult(RESULT_OK);
                    finish();
                }

            }
        });
        //预览按钮事件，预览所选图片
       /* preview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.getKeyCode()) {
            Bimp.tempSelectBitmap.clear();
            ShowPhotoActivity.this.setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 判断按钮显示状态
     */
    private void showBtnState() {
        selectNum= Bimp.tempSelectBitmap.size();//当前选中照片的数量
        maxNum = Integer.parseInt(context.getString(pic_totalNum))- Bimp.SelectBitmap.size();//可以选择照片的最大数
        if (selectNum > 0) {
            complete_btn.setText(context.getString(R.string.finish)+"(" + selectNum + "/" + maxNum + ")");
            complete_btn.setClickable(true);
           // preview_btn.setClickable(true);

        } else {
            complete_btn.setText(context.getString(R.string.finish));
            complete_btn.setClickable(false);
           // preview_btn.setClickable(false);
        }
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        setContentView(R.layout.view_null);
        super.onDestroy();
    }


}
