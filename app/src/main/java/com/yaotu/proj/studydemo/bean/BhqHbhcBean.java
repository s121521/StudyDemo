package com.yaotu.proj.studydemo.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/13.
 *保护区环保核查地
 */

public class BhqHbhcBean implements Serializable{

    /**
     * OBJECTID : 104
     * ID : 19
     * BHQMC : 科尔沁国家级自然保护区
     * BHQLX : 居民点
     * BHQGNQ : 实验区
     * BHQK : 新增
     * LON : 0
     * LAT : 0
     * GNQBM : 230
     * BHQID : NMG15222200155
     * JD : 121.8786942
     * WD : 45.02698444
     */

    private int OBJECTID;
    private int ID;
    private String BHQMC;
    private String BHQLX;
    private String BHQGNQ;
    private String BHQK;
    private int LON;
    private int LAT;
    private String GNQBM;
    private String BHQID;
    private double JD;
    private double WD;
    private String SHSJ;
    private String HCLX;

    public int getOBJECTID() {
        return OBJECTID;
    }

    public void setOBJECTID(int OBJECTID) {
        this.OBJECTID = OBJECTID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getBHQMC() {
        return BHQMC;
    }

    public void setBHQMC(String BHQMC) {
        this.BHQMC = BHQMC;
    }

    public String getBHQLX() {
        return BHQLX;
    }

    public void setBHQLX(String BHQLX) {
        this.BHQLX = BHQLX;
    }

    public String getBHQGNQ() {
        return BHQGNQ;
    }

    public void setBHQGNQ(String BHQGNQ) {
        this.BHQGNQ = BHQGNQ;
    }

    public String getBHQK() {
        return BHQK;
    }

    public void setBHQK(String BHQK) {
        this.BHQK = BHQK;
    }

    public int getLON() {
        return LON;
    }

    public void setLON(int LON) {
        this.LON = LON;
    }

    public int getLAT() {
        return LAT;
    }

    public void setLAT(int LAT) {
        this.LAT = LAT;
    }

    public String getGNQBM() {
        return GNQBM;
    }

    public void setGNQBM(String GNQBM) {
        this.GNQBM = GNQBM;
    }

    public String getBHQID() {
        return BHQID;
    }

    public void setBHQID(String BHQID) {
        this.BHQID = BHQID;
    }

    public double getJD() {
        return JD;
    }

    public void setJD(double JD) {
        this.JD = JD;
    }

    public double getWD() {
        return WD;
    }

    public void setWD(double WD) {
        this.WD = WD;
    }

    public String getSHSJ() {
        return SHSJ;
    }

    public void setSHSJ(String SHSJ) {
        this.SHSJ = SHSJ;
    }

    public String getHCLX() {
        return HCLX;
    }

    public void setHCLX(String HCLX) {
        this.HCLX = HCLX;
    }
}
