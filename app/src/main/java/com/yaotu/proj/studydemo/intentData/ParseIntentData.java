package com.yaotu.proj.studydemo.intentData;

import android.graphics.Bitmap;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by Administrator on 2017/6/19.
 */

public class ParseIntentData {
    /*通过post方法以字符串格式上传参数
    * */
    public static Response getDataPostByString(String url, FormBody.Builder builder) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
       /* FormBody.Builder builder = new FormBody.Builder();
        builder.add("name","张三");
        builder.add("age","20");
        builder.add("sex","女");*/
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /*通过Get方法以字符串格式上传参数*/
    public static Response getDataGetByString(String url) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        Request request = new Request.Builder().url(url).build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /*以JSON格式传递参数 */
    public static Response getDataPostByJson(String url, String jsonStr) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        /*String jsonStr = "{\"code\":\"李四\",\"name\":\"25\"}";*/
        RequestBody requestBody = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /*上传单张图片
    * */
    public static Response upLoadImage(String ipurl, String imagUrl) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        File imageFile = new File(imagUrl);
        MediaType mediaType = MediaType.parse("image/png");
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("img", imageFile.getName(), RequestBody.create(mediaType, imageFile));
        builder.addFormDataPart("tab", "01");

        RequestBody requestBody = builder.build();
        //构建请求
        Request request = new Request.Builder().url(ipurl).post(requestBody).build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Response upLoadImages(String reqUrl, Map<String, String> params, String pic_key, List<File> files) {
        OkHttpClient client = new OkHttpClient();
        Response response = null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        MediaType mediaType = MediaType.parse("image/png");
        //遍历map中所有参数到builder
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key));
            }
        }
        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
        if (files != null) {
            for (File file : files) {
                builder.addFormDataPart(pic_key, file.getName(), RequestBody.create(mediaType, file));
            }
        }
        //构建请求体
        RequestBody requestBody = builder.build();
        Request.Builder RequestBuilder = new Request.Builder();
        RequestBuilder.url(reqUrl);// 添加URL地址
        RequestBuilder.post(requestBody);
        Request request = RequestBuilder.build();

        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
    /*
    * Androlid中不同API获取Bitmap的大小：
    * */


    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)   //API 19
        {
            return bitmap.getAllocationByteCount();//返回用于存储此位图的像素的已分配内存的大小
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)  //API 12
        {
            return bitmap.getByteCount();//此方法的结果不再用于确定位图的内存使用情况.返回可用于存储此位图像素的最小字节数。
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }
}
