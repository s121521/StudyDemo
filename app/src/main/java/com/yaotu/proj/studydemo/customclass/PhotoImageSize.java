package com.yaotu.proj.studydemo.customclass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator on 2017/6/20.
 */

public class PhotoImageSize {
    /**
     * 图片压缩方法
     * @param path
     * @return
     * @throws IOException
     */
    public static Bitmap revitionImageSize(String path)  {
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;

            while (true) {
                // 这一步是根据要设置的大小，使宽和高都能满足
                if ((options.outWidth >> i <= 256) && (options.outHeight >> i <= 256)) {
                    in = new BufferedInputStream(new FileInputStream(new File(path)));//重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
                    options.inSampleSize = (int) Math.pow(2.0D, i);//设置该属性能够实现图片的压缩,这个参数表示 新生成的图片为原始图片的几分之一。
                    options.inJustDecodeBounds = false;// 这里之前设置为了true，所以要改为false，否则就创建不出图片
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
