package com.yaotu.proj.studydemo.bean.tableBean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/26.
 */

public class KczyJsonBean {
    private String key;
    private String yhdh;
    private String ischecked;
    private List<KczyRecordBean> record;

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

    public void setIschecked(String ischecked) {
        this.ischecked = ischecked;
    }

    public List<KczyRecordBean> getRecord() {
        return record;
    }

    public void setRecord(List<KczyRecordBean> record) {
        this.record = record;
    }
}
