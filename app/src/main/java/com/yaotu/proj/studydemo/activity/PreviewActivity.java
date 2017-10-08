package com.yaotu.proj.studydemo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.yaotu.proj.studydemo.R;

/**
 * 功能介绍：
 *    该界面用于预览图片的显示；
 *      1：可以删除预览图片
 *      2：可以将选中的图片跳转到主界面显示用于提交上传
 * Created by Administrator on 2017/2/24.
 */

public class PreviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
    }
}
