package com.yaotu.proj.studydemo.customclass;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;

import com.yaotu.proj.studydemo.R;

/**
 * Created by Administrator on 2017/9/28.
 */

public class RenameDialog extends Dialog {
    private Context context;
    private String title;
    public RenameDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public RenameDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init(){
        View view = LayoutInflater.from(context).inflate(R.layout.rename_dialog,null);
        setContentView(view);
    }
}
