package com.yaotu.proj.studydemo.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/20.
 */

public class ImageBucket {
    public int count;
    private String bucketName;
    private List<ImageItem> imageItems;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }
}
