package com.yaotu.proj.studydemo.BLL;

import android.view.View;

/**
 * Created by Administrator on 2017/6/28.
 */

public interface Operate<T> {
    T getEntityBean(String bhqid,String bhqmc,String jsxmid);
    View getView(T t);
}
