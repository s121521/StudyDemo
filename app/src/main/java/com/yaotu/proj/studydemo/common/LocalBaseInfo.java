package com.yaotu.proj.studydemo.common;

import android.database.Cursor;

import com.yaotu.proj.studydemo.bean.CodeTypeBean;
import com.yaotu.proj.studydemo.util.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class LocalBaseInfo {
    private static Cursor cursor;
    //--------------------------从本地数据库中读取bhqInfo表数据----------------------------------
    public static List<CodeTypeBean> loadDataBySqlLite(String sql, String[] bindArgs, DBManager dbManager) {//加载数据
        cursor = dbManager.queryEntity(sql, bindArgs);
        List<CodeTypeBean> list = new ArrayList<CodeTypeBean>();
        while (cursor.moveToNext()) {
            CodeTypeBean codetype = new CodeTypeBean();
            codetype.setDMLB(cursor.getString(cursor.getColumnIndex("dmlb")));
            codetype.setDMZ(cursor.getString(cursor.getColumnIndex("dmz")));
            codetype.setJB(cursor.getString(cursor.getColumnIndex("jb")));
            codetype.setLB(cursor.getString(cursor.getColumnIndex("lb")));
            codetype.setDMMC1(cursor.getString(cursor.getColumnIndex("dmmc1")));
            codetype.setDMMC2(cursor.getString(cursor.getColumnIndex("dmmc2")));
            codetype.setDMMC3(cursor.getString(cursor.getColumnIndex("dmmc3")));
            codetype.setSXH(cursor.getString(cursor.getColumnIndex("sxh")));
            codetype.setBZ(cursor.getString(cursor.getColumnIndex("bz")));
            list.add(codetype);
        }
        cursor.close();
        dbManager.closeDB();
        return list;
    }
}
