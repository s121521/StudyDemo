package com.yaotu.proj.studydemo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/4/28.
 */

public class SharedPreferencesUtils {
    private SharedPreferences mSharedPreferences;

    public SharedPreferencesUtils(Context context, String preferenceName) {
        mSharedPreferences = context.getSharedPreferences(preferenceName,Context.MODE_PRIVATE);
    }
    /**
     * clear all content
     */
    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public void put(String key, Object value) {
        mSharedPreferences.edit().putString(key, value.toString()).apply();
    }

    public void put(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void put(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public void put(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void put(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void put(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public void put(String key, double value) {
        Double newValue = value;
        mSharedPreferences.edit().putString(key, newValue.toString()).apply();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    public double getDouble(String key, double defValue) {
        String value = mSharedPreferences.getString(key, null);
        return value == null ? defValue : Double.parseDouble(value);
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }
    
}
