package com.yaotu.proj.studydemo.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Administrator on 2017/3/13.
 */

public class DBManager  {
    private DBHelper helper;
    private SQLiteDatabase database ;
    public DBManager(Context context) {
        helper = new DBHelper(context);
        database = helper.getWritableDatabase();
    }

    /**
     * 执行insert、update 操作
     * @param sql
     * @param bindArgs
     * @return
     */
    public boolean updateBySql(String sql,Object[] bindArgs) {
        boolean flag = false;
        database.beginTransaction();//开始事务
        try {
            database.execSQL(sql,bindArgs);
            flag = true;
            database.setTransactionSuccessful();//设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();//结束事务
        }
        return flag;
    }
    /**
     * 删除表中数据
     *
     * @return
     */
    public boolean deleteTableData(String table,String whereClause,String[] whereArgs) {
        boolean flag = false;
        database.beginTransaction();//开始事务
        try {
            database.delete(table,whereClause,whereArgs);
            flag = true;
            database.setTransactionSuccessful();//设置事务成功完成
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            database.endTransaction();//结束事务
        }
        return flag;
    }
    public Cursor queryEntity(String sql,String[] selectionArgs ) {
        Cursor cursor = null;
        cursor = database.rawQuery(sql,selectionArgs);
        return cursor;
    }
    /**
     * close database
     */
    public void closeDB() {
        database.close();
    }

}
