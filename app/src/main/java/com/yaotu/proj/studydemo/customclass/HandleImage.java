package com.yaotu.proj.studydemo.customclass;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Created by Administrator on 2017/10/10.
 * 系统处理图片
 */

public class HandleImage {

    /**
     *4.4以下系统处理图片的方法
     * */
    public static String handleImageBeforeKitKat(Context context,Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(context,uri,null);
        return imagePath;
    }
    /**
     * 4.4及以上系统处理图片的方法
     * */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String handleImgeOnKitKat(Context context,Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context,uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(context,contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //如果是content类型的uri，则使用普通方式处理
                imagePath = getImagePath(context,uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())) {
                //如果是file类型的uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        return imagePath;
    }
    /**
     * 通过uri和selection来获取真实的图片路径
     * */
    private  static String getImagePath(Context context,Uri uri,String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
