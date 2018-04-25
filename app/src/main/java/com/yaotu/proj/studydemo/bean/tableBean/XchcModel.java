package com.yaotu.proj.studydemo.bean.tableBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 遥感数据现场核查数据
 */

public class XchcModel implements Serializable{
    private String jsxmid;//建设项目ID
    private String jsxmmc;//设备名称
    private String jsxmlx;//设备类型
    private double jd;//经度
    private double wd;//纬度
    private String szbhqgnq;//所在功能区
    private String scale;//规模
    private String bhlx;//变化类型
    private String bhlxdetails;//变化类型详情
    private String currentstatus;//设备现状
    private String lsyg;//历史沿革
    private String hbspxg;//环评审批等相关手续情况
    private String styxph;//生态影响及破坏情况
    private String qtsm;//其他需要特别说明的情况
    private String photourl;//现场照片
    private int objectid;//核查点编号
    private String ischecked;//审核状态
    private String reason;//审核未通过原因
    private String shtgsj;//审核通过时间
    private String submitter;//提交人
    private String szbhqmc;//所在保护区名称
    private String jsxmlxdm;//活动/设备类型代码
    private String szbhqgnqdm;//所在保护区功能区代码
    private String ischeckeddm;//审核状态代码
    private String yhdh;//提交人用户帐号
    private String szbhqid;// 所在保护区ID
    private String isarchived;//归档状态
    private String bhlxdm;//变化类型代码
    private String jsxmlxdetails;//活动/设备类型详情
    private String photoName;

    private String szbhqjb;
    private String szbhqjbdm;

    public String getJsxmid() {
        return jsxmid;
    }

    public void setJsxmid(String jsxmid) {
        this.jsxmid = jsxmid;
    }

    public String getJsxmmc() {
        return jsxmmc;
    }

    public void setJsxmmc(String jsxmmc) {
        this.jsxmmc = jsxmmc;
    }

    public String getJsxmlx() {
        return jsxmlx;
    }

    public void setJsxmlx(String jsxmlx) {
        this.jsxmlx = jsxmlx;
    }

    public double getJd() {
        return jd;
    }

    public void setJd(double jd) {
        this.jd = jd;
    }

    public double getWd() {
        return wd;
    }

    public void setWd(double wd) {
        this.wd = wd;
    }

    public String getSzbhqgnq() {
        return szbhqgnq;
    }

    public void setSzbhqgnq(String szbhqgnq) {
        this.szbhqgnq = szbhqgnq;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getBhlx() {
        return bhlx;
    }

    public void setBhlx(String bhlx) {
        this.bhlx = bhlx;
    }

    public String getCurrentstatus() {
        return currentstatus;
    }

    public void setCurrentstatus(String currentstatus) {
        this.currentstatus = currentstatus;
    }

    public String getLsyg() {
        return lsyg;
    }

    public void setLsyg(String lsyg) {
        this.lsyg = lsyg;
    }

    public String getHbspxg() {
        return hbspxg;
    }

    public void setHbspxg(String hbspxg) {
        this.hbspxg = hbspxg;
    }

    public String getStyxph() {
        return styxph;
    }

    public void setStyxph(String styxph) {
        this.styxph = styxph;
    }

    public String getQtsm() {
        return qtsm;
    }

    public void setQtsm(String qtsm) {
        this.qtsm = qtsm;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public int getObjectid() {
        return objectid;
    }

    public void setObjectid(int objectid) {
        this.objectid = objectid;
    }

    public String getIschecked() {
        return ischecked;
    }

    public void setIschecked(String ischecked) {
        this.ischecked = ischecked;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getShtgsj() {
        return shtgsj;
    }

    public void setShtgsj(String shtgsj) {
        this.shtgsj = shtgsj;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getSzbhqmc() {
        return szbhqmc;
    }

    public void setSzbhqmc(String szbhqmc) {
        this.szbhqmc = szbhqmc;
    }

    public String getJsxmlxdm() {
        return jsxmlxdm;
    }

    public void setJsxmlxdm(String jsxmlxdm) {
        this.jsxmlxdm = jsxmlxdm;
    }

    public String getSzbhqgnqdm() {
        return szbhqgnqdm;
    }

    public void setSzbhqgnqdm(String szbhqgnqdm) {
        this.szbhqgnqdm = szbhqgnqdm;
    }

    public String getIscheckeddm() {
        return ischeckeddm;
    }

    public void setIscheckeddm(String ischeckeddm) {
        this.ischeckeddm = ischeckeddm;
    }

    public String getYhdh() {
        return yhdh;
    }

    public void setYhdh(String yhdh) {
        this.yhdh = yhdh;
    }

    public String getSzbhqid() {
        return szbhqid;
    }

    public void setSzbhqid(String szbhqid) {
        this.szbhqid = szbhqid;
    }

    public String getIsarchived() {
        return isarchived;
    }

    public void setIsarchived(String isarchived) {
        this.isarchived = isarchived;
    }

    public String getBhlxdm() {
        return bhlxdm;
    }

    public void setBhlxdm(String bhlxdm) {
        this.bhlxdm = bhlxdm;
    }

    public String getJsxmlxdetails() {
        return jsxmlxdetails;
    }

    public void setJsxmlxdetails(String jsxmlxdetails) {
        this.jsxmlxdetails = jsxmlxdetails;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getSzbhqjb() {
        return szbhqjb;
    }

    public void setSzbhqjb(String szbhqjb) {
        this.szbhqjb = szbhqjb;
    }

    public String getSzbhqjbdm() {
        return szbhqjbdm;
    }

    public void setSzbhqjbdm(String szbhqjbdm) {
        this.szbhqjbdm = szbhqjbdm;
    }

    public String getBhlxdetails() {
        return bhlxdetails;
    }

    public void setBhlxdetails(String bhlxdetails) {
        this.bhlxdetails = bhlxdetails;
    }
}
