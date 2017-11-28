package com.yaotu.proj.studydemo.customclass;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/10/13.
 */

public class ItemTextView  extends AppCompatTextView {

    public ItemTextView(Context context) {
        super(context);
    }

    public ItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
