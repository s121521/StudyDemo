package com.yaotu.proj.studydemo.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


import com.yaotu.proj.studydemo.bean.ImageBucket;
import com.yaotu.proj.studydemo.bean.ImageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/20.
 */

public class AlbumHelper {
    private final String TAG=getClass().getSimpleName();
    private Context context;
    private ContentResolver contentResolver;
    private HashMap<String,String> thumbnailList;
    private HashMap<String,ImageBucket> bucketList;
    private static AlbumHelper instance;

    public AlbumHelper() {
    }
    public  static AlbumHelper getInstance() {
        if (null == instance) {
            instance=new AlbumHelper();
        }
        return instance;
    }

    /**
     * 初始化并创建ContentResolver对象
     * @param context
     */
    public void init(Context context) {
        if (null==this.context) {
            this.context=context;
        }
        contentResolver=context.getContentResolver();
    }

    /**
     * 获取images缩略图
     */
    public HashMap<String,String> getThumbNail() {
        thumbnailList = new HashMap<String,String>();
        String[] projection = new String[]{MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails._ID};
        Cursor cursor = contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null,null);
        if (cursor.moveToFirst()) {
            int imageID;
            String imagePath;
            do {
                imageID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                thumbnailList.put(String.valueOf(imageID), imagePath);
            } while (cursor.moveToNext());
        }
        return thumbnailList;
    }

    /**
     * 获取Images集合
     * @return
     */
    public HashMap<String, ImageBucket> buileImagesBucketList() {
        bucketList = new HashMap<String, ImageBucket>();
        getThumbNail();
        String[] columns = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,null);
        if (cursor.moveToFirst()) {
            do {
                String _id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                String bucket_id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                String bucket_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                ImageBucket imageBucket = bucketList.get(bucket_id);
                if (null==imageBucket) {
                    imageBucket=new ImageBucket();
                    bucketList.put(bucket_id, imageBucket);
                    imageBucket.setBucketName(bucket_name);
                    imageBucket.setImageItems(new ArrayList<ImageItem>());
                }
                imageBucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.setImageID(_id);
                imageItem.setImageName(name);
                imageItem.setImageSize(size);
                imageItem.setImageTitle(title);
                imageItem.setImagePath(data);
                imageItem.setThumbnailPath(thumbnailList.get(_id));
                imageBucket.getImageItems().add(imageItem);

            } while (cursor.moveToNext());
        }
        return bucketList;
    }

    public List<ImageBucket> getImagesBucketList() {
        List<ImageBucket> list = new ArrayList<ImageBucket>();
        Iterator<Map.Entry<String,ImageBucket>> iterator=buileImagesBucketList().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,ImageBucket> entry=iterator.next();
            list.add(entry.getValue());
        }
        return list;
    }
}
