package com.yaotu.proj.studydemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ToggleButton;


import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.ImageItem;
import com.yaotu.proj.studydemo.util.AsyncImageLoad;

import java.util.List;

/**
 * 用来显示系统相册的适配器
 * Created by Administrator on 2017/2/20.
 */

public class AllPhotoAdapter extends BaseAdapter {
    private Context context;
    private List<ImageItem> dataList;;
    private List<ImageItem> dataSelectList;
    private AsyncImageLoad asyncImageLoad;

    public AllPhotoAdapter() {
    }
    public AllPhotoAdapter(Context context, List<ImageItem> dataList, List<ImageItem> dataSelectList) {
        this.context=context;
        this.dataList=dataList;
        this.dataSelectList=dataSelectList;
        asyncImageLoad=new AsyncImageLoad();
    }
    AsyncImageLoad.ImageCallback imageCallback=new AsyncImageLoad.ImageCallback() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
            if (imageView != null && bitmap != null) {
                String url= (String) params[0];
                if (null != url && url.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    };
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //存放列表项控件句柄
    private class ViewHolder{
        public ImageView imageView;
        public CheckBox checkBoxImg;
        public ToggleButton toggle_button;
    }
    ViewHolder viewHolder=null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_show_photo,null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.show_photo_imageView);
            viewHolder.checkBoxImg = (CheckBox) convertView.findViewById(R.id.checkBox_imgItem);
            viewHolder.toggle_button = (ToggleButton) convertView.findViewById(R.id.toggle_button);
             convertView.setTag(viewHolder);
        } else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        if ( dataList != null && dataList.size()>0)
        {
            final ImageItem imageItem = dataList.get(position);
            viewHolder.imageView.setTag(imageItem.getImagePath());//做标记
            viewHolder.checkBoxImg.setTag(position);//做标记
            viewHolder.toggle_button.setTag(position);//做标记

            asyncImageLoad.loadBitMap(viewHolder.imageView, imageItem.getThumbnailPath(), imageItem.getImagePath(), imageCallback);


        }
        //view 的 OnClickListener事件
        viewHolder.toggle_button.setOnClickListener(new myOnClickListener(viewHolder.checkBoxImg,viewHolder.toggle_button,position));


        //判断历史选中项
        if (dataSelectList.contains(dataList.get(position))) {
            viewHolder.toggle_button.setChecked(true);
            viewHolder.checkBoxImg.setChecked(true);
        } else {
            viewHolder.toggle_button.setChecked(false);
            viewHolder.checkBoxImg.setChecked(false);
        }
        return convertView;
    }

    /**
     *  自定义onclickListener
     */
    class myOnClickListener implements View.OnClickListener {
        CheckBox checkBox;
        ToggleButton toggleButton;
        int position;
        public myOnClickListener(CheckBox checkBox,ToggleButton toggleButton,int position) {
            this.checkBox=checkBox;
            this.toggleButton=toggleButton;
            this.position=position;
        }
        @Override
        public void onClick(View view) {
                /*以下部分可以在其他地方封装实现
                if (toggleButton.isChecked()) {
                    checkBox.setChecked(true);//显示被选中状态
                    Bimp.tempSelectBitmap.add(dataList.get(position));//添加到选中图片的临时列表中

                } else {
                    checkBox.setChecked(false);//显示未选中状态
                    Bimp.tempSelectBitmap.remove(dataList.get(position));

                }*/
                myOnItemClickListener.onItemClick(checkBox,toggleButton,position);


        }
    };

    /**
     * 定义接口，实现接口方法
     */
    private OnItemClickListener myOnItemClickListener;
    public void setMyOnItemClickListener(OnItemClickListener listener) {
        this.myOnItemClickListener=listener;
    }
    public interface OnItemClickListener{
        void onItemClick(CheckBox checkBox, ToggleButton toggleButton, int position);
    }

}
