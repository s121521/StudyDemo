package com.yaotu.proj.studydemo.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/15.
 */

public class FileUtils {
    public  static final String SDPATH = Environment.getExternalStorageDirectory()+"/MyAppFile/";

    /**
     *保存图片文件
     * @param bitMap
     * @param picName
     */
    public static File saveBitmap(Bitmap bitMap,String picName) {
        if (!isFileExist("")) {//判断存放图片的文件夹是否存在
             createDir("");//创建文件夹
            System.out.println("FileUtils.saveBitmap==========>createDir");
        }
        File f=new File(SDPATH,"pic_"+picName+".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(f);
            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.i("TAG", "保存文件路径: "+f.getAbsolutePath()+"-----------------"+f.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 创建文件夹
     * @param dirName
     */
    public static File createDir(String dirName) {
        File file = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SD卡正常挂载
            file.mkdir();
        }
        return file;
    }
    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }
    /*
    * */
    public static File definedFile(String fileName){
        // 定义文件输出流
        File file = null;
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/arcgisFile", fileName+"("+currtDateTime()+").txt");
        return file;

    }
    /*
    * */
    public static void writeFile(File file,String info){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            fos.write(info.getBytes());
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*
    * */
    //--------------获取系统当前时间----------------------
    private static String currtDateTime() {
        String dataInfo = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss:sss");
        Date currtData = new Date(System.currentTimeMillis());
        dataInfo = simpleDateFormat.format(currtData);

        /*Calendar calendar=Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        dataInfo=year+"年"+month+"月"+day+"日"+hour+"时"+minute+"分"+second+"秒"+millisecond+"毫秒";*/
        return  dataInfo;
    }
}
