package com.yaotu.proj.studydemo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.yaotu.proj.studydemo.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 实现异步加载图片
 */
public class AsyncImageLoad extends Activity {

	public Handler h = new Handler();
	public final String TAG = getClass().getSimpleName();
	private HashMap<String, SoftReference<Bitmap>> imageCache;

	public AsyncImageLoad() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();//图片缓存

	}
	
	public void put(String path, Bitmap bmp) {
		if (!TextUtils.isEmpty(path) && bmp != null) {
			imageCache.put(path, new SoftReference<Bitmap>(bmp));
		}
	}

	public void loadBitMap(final ImageView iv, final String thumbPath, final String sourcePath, final ImageCallback callback) {

		if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
			Log.e(TAG, "no paths pass in");
			return;
		}
		final String path;
		final boolean isThumbPath;
		if (!TextUtils.isEmpty(thumbPath)) {
			path = thumbPath;
			isThumbPath = true;
		} else if (!TextUtils.isEmpty(sourcePath)) {
			path = sourcePath;
			isThumbPath = false;
		} else {
			// iv.setImageBitmap(null);
			return;
		}
		// 首先判断是否在缓存中
		// 但有个问题是：ImageCache可能会越来越大，以至用户内存用光，所以要用SoftReference（弱引用）来实现
		if (imageCache.containsKey(path)) {
			SoftReference<Bitmap> reference = imageCache.get(path);
			Bitmap bmp = reference.get();
			if (bmp != null) {
				if (callback != null) {
					callback.imageLoad(iv, bmp, sourcePath);
				}
				iv.setImageBitmap(bmp);
				Log.d(TAG, "hit cache");
				return;
			}
		}
		iv.setImageBitmap(null);
		// 尝试从URL中加载
		new Thread() {
			Bitmap thumb;
			public void run() {
				try {
					if (isThumbPath) {
						thumb = BitmapFactory.decodeFile(thumbPath);
						if (thumb == null) {
							thumb = revitionImageSize(sourcePath);						
						}						
					} else {
						thumb = revitionImageSize(sourcePath);											
					}
				} catch (Exception e) {
					
				}
				if (thumb == null) {
					thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);

				}
				Log.i(TAG, "-------thumb------"+thumb);

				put(path, thumb);

				if (callback != null) {
					h.post(new Runnable() {
						@Override
						public void run() {
							callback.imageLoad(iv, thumb, sourcePath);
						}
					});
				}
			}
		}.start();

	}

	/**
	 * 图片压缩方法
	 * @param path
	 * @return
	 * @throws IOException
     */
	public Bitmap revitionImageSize(String path)  {
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

	public interface ImageCallback {
		//数据类型和参数名之间加上省略号，他的参数就是可变的，可以和不确定个实参匹配。	可变参数不能直接获取，需要下标引用
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
	}
}
