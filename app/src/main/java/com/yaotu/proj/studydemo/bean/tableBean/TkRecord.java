package com.yaotu.proj.studydemo.bean.tableBean;

/**
 * Created by Administrator on 2017/9/20.
 */

public class TkRecord {
    private String objectid;
    private double centerpointx;
    private double centerpointy;
    private TkEnterpriseBean bean;

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

    public TkEnterpriseBean getBean() {
        return bean;
    }

    public void setBean(TkEnterpriseBean bean) {
        this.bean = bean;
    }
}
