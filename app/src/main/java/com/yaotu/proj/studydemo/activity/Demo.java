package com.yaotu.proj.studydemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yaotu.proj.studydemo.R;
import com.yaotu.proj.studydemo.bean.nopassJsxmBean.NoPassTkqy;

public class Demo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Intent intent = getIntent();
        NoPassTkqy bean = (NoPassTkqy) intent.getSerializableExtra("demo");
        Log.i("TAG", "onCreate: "+bean);
    }
}
