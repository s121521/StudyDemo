package com.yaotu.proj.studydemo.bean.tableBean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/23.
 */

public class TKJsonBean {
    private String key;
    private String yhdh;
    private String ischecked;
    private List<TkRecord> record;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getYhdh() {
        return yhdh;
    }

    public void setYhdh(String yhdh) {
        this.yhdh = yhdh;
    }

    public String getIschecked() {
        return ischecked;
    }

    public void setIschecked(String isChecked) {
        this.ischecked = isChecked;
    }

    public List<TkRecord> getRecord() {
        return record;
    }

    public void setRecord(List<TkRecord> record) {
        this.record = record;
    }
}
