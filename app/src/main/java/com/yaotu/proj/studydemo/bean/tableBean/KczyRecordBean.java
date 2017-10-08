package com.yaotu.proj.studydemo.bean.tableBean;

/**
 * Created by Administrator on 2017/9/21.
 */

public class KczyRecordBean {
    private String objectid;
    private double centerpointx;
    private double centerpointy;
    private kczyBean bean;

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public double getCenterpointx() {
        return centerpointx;
    }

    public void setCenterpointx(double centerpointx) {
        this.centerpointx = centerpointx;
    }

    public double getCenterpointy() {
        return centerpointy;
    }

    public void setCenterpointy(double centerpointy) {
        this.centerpointy = centerpointy;
    }

    public kczyBean getBean() {
        return bean;
    }

    public void setBean(kczyBean bean) {
        this.bean = bean;
    }
}
