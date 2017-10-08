package com.yaotu.proj.studydemo.customclass;

import android.content.Context;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.yaotu.proj.studydemo.bean.tableBean.AssartBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyBean;
import com.yaotu.proj.studydemo.bean.tableBean.QyInfoBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkEnterpriseBean;
import com.yaotu.proj.studydemo.bean.tableBean.TravelBean;
import com.yaotu.proj.studydemo.bean.tableBean.kczyBean;
import com.yaotu.proj.studydemo.util.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/23.
 */

public class QueryLocalTableData {
    private Context context;
    private DBManager dbManager;
    private Cursor cursor;
    public QueryLocalTableData(Context context){
        this.context = context;
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
    }
    //=================查询企业信息================================
    public List<QyInfoBean> queryqyInfo(String sql, String [] bingArgs){
        List<QyInfoBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
           QyInfoBean bean = new QyInfoBean();
           bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
           bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
           bean.setQymc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
           bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));
           bean.setTbr(TempData.username);
           list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        Log.i("TAG", "queryqyInfo:------------> "+list);
        return list;
    }
    //---------查询TkqyInfo表------------------------
    public List<TkEnterpriseBean> queryTkqyInfos(String sql,String [] bingArgs){
        List<TkEnterpriseBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            TkEnterpriseBean bean = new TkEnterpriseBean();
            bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            bean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            bean.setJsxmmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            bean.setFzjg(cursor.getString(cursor.getColumnIndex("fzjg")));
            bean.setZjyxqb(cursor.getString(cursor.getColumnIndex("zjyxqb")));
            bean.setZjyxqe(cursor.getString(cursor.getColumnIndex("zjyxqe")));
            bean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            bean.setKqslqk(cursor.getString(cursor.getColumnIndex("kqslqk")));
            bean.setKtfs(cursor.getString(cursor.getColumnIndex("ktfs")));
            bean.setKqsx(cursor.getString(cursor.getColumnIndex("kqsx")));
            bean.setHbpzwh(cursor.getString(cursor.getColumnIndex("hbpzwh")));
            bean.setIsbhq(cursor.getString(cursor.getColumnIndex("isbhq")));
            bean.setScqk(cursor.getString(cursor.getColumnIndex("scqk")));
            bean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            bean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            bean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            bean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            bean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            bean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            bean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            bean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            bean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            bean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            bean.setVisbhq(cursor.getString(cursor.getColumnIndex("visbhq")));
            bean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));
            bean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            bean.setVkqsx(cursor.getString(cursor.getColumnIndex("vkqsx")));
            bean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            bean.setUsertel(cursor.getString(cursor.getColumnIndex("usertel")));
            bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));
            list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public TkEnterpriseBean queryTkqyInfo(String sql,String [] bingArgs){
        List<TkEnterpriseBean> list = queryTkqyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
    //===================查询工业企业表=========================================================
    public List<IndustryBean> queryGyqyInfos(String sql,String [] bingArgs){
        List<IndustryBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            IndustryBean Ibean = new IndustryBean();
            Ibean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            Ibean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            Ibean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            Ibean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            Ibean.setJsxmmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            Ibean.setJsxmgm(cursor.getString(cursor.getColumnIndex("jsxmgm")));
            Ibean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            Ibean.setLsyg(cursor.getString(cursor.getColumnIndex("lsyg")));
            Ibean.setQysx(cursor.getString(cursor.getColumnIndex("qysx")));
            Ibean.setHbpzwh(cursor.getString(cursor.getColumnIndex("hbpzwh")));
            Ibean.setIsbhq(cursor.getString(cursor.getColumnIndex("isbhq")));
            Ibean.setIshbys(cursor.getString(cursor.getColumnIndex("ishbys")));
            Ibean.setYxqk(cursor.getString(cursor.getColumnIndex("yxqk")));
            Ibean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            Ibean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            Ibean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            Ibean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            Ibean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            Ibean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            Ibean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            Ibean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            Ibean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            Ibean.setUpState(cursor.getString(cursor.getColumnIndex("upState")));
            Ibean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            Ibean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            Ibean.setYswjbh(cursor.getString(cursor.getColumnIndex("yswjbh")));
            Ibean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));
            Ibean.setVyxqk(cursor.getString(cursor.getColumnIndex("vyxqk")));
            Ibean.setVisbhq(cursor.getString(cursor.getColumnIndex("visbhq")));
            Ibean.setVishbys(cursor.getString(cursor.getColumnIndex("vishbys")));
            Ibean.setVqysx(cursor.getString(cursor.getColumnIndex("vqysx")));
            Ibean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            Ibean.setUsertel(cursor.getString(cursor.getColumnIndex("usertel")));
            Ibean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));
            list.add(Ibean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public IndustryBean queryGyqyInfo(String sql,String [] bingArgs){
        List<IndustryBean> list = queryGyqyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
    //==================查询矿产资源开发企业=================================================
    public List<kczyBean> queryKczyQyInfos(String sql,String [] bingArgs){
        List<kczyBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            kczyBean bean = new kczyBean();
            bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            bean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            bean.setJsxmmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            bean.setJsxmgm(cursor.getString(cursor.getColumnIndex("jsxmgm")));
            bean.setFzjg(cursor.getString(cursor.getColumnIndex("fzjg")));
            bean.setZjyxqb(cursor.getString(cursor.getColumnIndex("zjyxqb")));
            bean.setZjyxqe(cursor.getString(cursor.getColumnIndex("zjyxqe")));
            bean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            bean.setKqslqk(cursor.getString(cursor.getColumnIndex("kqslqk")));
            bean.setKcfs(cursor.getString(cursor.getColumnIndex("kcfs")));
            bean.setKqsx(cursor.getString(cursor.getColumnIndex("kqsx")));
            bean.setHbpzwh(cursor.getString(cursor.getColumnIndex("hbpzwh")));
            bean.setIsbhq(cursor.getString(cursor.getColumnIndex("isbhq")));
            bean.setIshbys(cursor.getString(cursor.getColumnIndex("ishbys")));
            bean.setScqk(cursor.getString(cursor.getColumnIndex("scqk")));
            bean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            bean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            bean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            bean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            bean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            bean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            bean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            bean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            bean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            bean.setUpstate(cursor.getString(cursor.getColumnIndex("upState")));
            bean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            bean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            bean.setYswjbh(cursor.getString(cursor.getColumnIndex("yswjbh")));
            bean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));
            bean.setVkcfs(cursor.getString(cursor.getColumnIndex("vkcfs")));
            bean.setVisbhq(cursor.getString(cursor.getColumnIndex("visbhq")));
            bean.setVishbys(cursor.getString(cursor.getColumnIndex("vishbys")));
            bean.setVscqk(cursor.getString(cursor.getColumnIndex("vscqk")));
            bean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            bean.setUsertel(cursor.getString(cursor.getColumnIndex("usertel")));
            bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));
            list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public kczyBean queryKczyQyInfo(String sql,String [] bingArgs){
        List<kczyBean> list = queryKczyQyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
    //===========================查询旅游资源开发企业============================================
    public List<TravelBean> queryTravelQyInfos(String sql,String [] bingArgs){
        List<TravelBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            TravelBean bean = new TravelBean();
            bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            bean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            bean.setJsxmmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            bean.setJsxmgm(cursor.getString(cursor.getColumnIndex("jsxmgm")));
            bean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            bean.setLsyg(cursor.getString(cursor.getColumnIndex("lsyg")));
            bean.setQysx(cursor.getString(cursor.getColumnIndex("qysx")));
            bean.setHbpzwh(cursor.getString(cursor.getColumnIndex("hbpzwh")));
            bean.setIsbhq(cursor.getString(cursor.getColumnIndex("isbhq")));
            bean.setIshbys(cursor.getString(cursor.getColumnIndex("ishbys")));
            bean.setYyqk(cursor.getString(cursor.getColumnIndex("yyqk")));
            bean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            bean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            bean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            bean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            bean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            bean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            bean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            bean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            bean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));

            bean.setUpstate(cursor.getString(cursor.getColumnIndex("upState")));
            bean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            bean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            bean.setYswjbh(cursor.getString(cursor.getColumnIndex("yswjbh")));
            bean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));

            bean.setVqysx(cursor.getString(cursor.getColumnIndex("vqysx")));
            bean.setVisbhq(cursor.getString(cursor.getColumnIndex("visbhq")));
            bean.setVishbys(cursor.getString(cursor.getColumnIndex("vishbys")));
            bean.setVyyqk(cursor.getString(cursor.getColumnIndex("vyyqk")));
            bean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            bean.setUsertel(cursor.getString(cursor.getColumnIndex("usertel")));
            bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));

            list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public TravelBean queryTravelQyInfo(String sql,String [] bingArgs){
        List<TravelBean> list = queryTravelQyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
    //=================================查询新能源企业============================================
    public List<NewEnergyBean> queryNewEnergyQyInfos(String sql, String [] bingArgs){
        List<NewEnergyBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            NewEnergyBean bean = new NewEnergyBean();

            bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            bean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            bean.setJsxmmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            bean.setJsxmlx(cursor.getString(cursor.getColumnIndex("jsxmlx")));
            bean.setJsxmgm(cursor.getString(cursor.getColumnIndex("jsxmgm")));
            bean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            bean.setQysx(cursor.getString(cursor.getColumnIndex("qysx")));
            bean.setHbpzwh(cursor.getString(cursor.getColumnIndex("hbpzwh")));
            bean.setIsbhq(cursor.getString(cursor.getColumnIndex("isbhq")));
            bean.setIshbys(cursor.getString(cursor.getColumnIndex("ishbys")));
            bean.setScqk(cursor.getString(cursor.getColumnIndex("scqk")));
            bean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            bean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            bean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            bean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            bean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            bean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            bean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            bean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            bean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            bean.setUpstate(cursor.getString(cursor.getColumnIndex("upState")));

            bean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            bean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            bean.setYswjbh(cursor.getString(cursor.getColumnIndex("yswjbh")));
            bean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));

            bean.setVqyxs(cursor.getString(cursor.getColumnIndex("vqyxs")));
            bean.setVscqk(cursor.getString(cursor.getColumnIndex("vscqk")));
            bean.setVisbhq(cursor.getString(cursor.getColumnIndex("visbhq")));
            bean.setVishbys(cursor.getString(cursor.getColumnIndex("vishbys")));
            bean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            bean.setUsertel(cursor.getString(cursor.getColumnIndex("usertel")));
            bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));

            list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public NewEnergyBean queryNewEnergyQyInfo(String sql,String [] bingArgs){
        List<NewEnergyBean> list = queryNewEnergyQyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
    //=======================开发建设活动=======================================================
    public List<DevelopConstructionBean> queryDevelopQyInfos(String sql, String [] bingArgs){
        List<DevelopConstructionBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            DevelopConstructionBean bean = new DevelopConstructionBean();

            bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            bean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            bean.setKfjsmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            bean.setGm(cursor.getString(cursor.getColumnIndex("gm")));
            bean.setHdlx(cursor.getString(cursor.getColumnIndex("hdlx")));
            bean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            bean.setHbpzwh(cursor.getString(cursor.getColumnIndex("hbpzwh")));
            bean.setIsbhq(cursor.getString(cursor.getColumnIndex("isbhq")));
            bean.setIshbys(cursor.getString(cursor.getColumnIndex("ishbys")));
            bean.setScqk(cursor.getString(cursor.getColumnIndex("scqk")));
            bean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            bean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            bean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            bean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            bean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            bean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            bean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            bean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            bean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            bean.setUpstate(cursor.getString(cursor.getColumnIndex("upState")));
            bean.setKkmj(cursor.getString(cursor.getColumnIndex("kkmj")));
            bean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            bean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            bean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));

            bean.setVisbhq(cursor.getString(cursor.getColumnIndex("visbhq")));
            bean.setVishbys(cursor.getString(cursor.getColumnIndex("vishbys")));
            bean.setVscqk(cursor.getString(cursor.getColumnIndex("vscqk")));
            bean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));

            list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public DevelopConstructionBean queryDevelopQyInfo(String sql, String [] bingArgs){
        List<DevelopConstructionBean> list = queryDevelopQyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
    //=======================开垦活动=======================================================
    public List<AssartBean> queryAssartQyInfos(String sql, String [] bingArgs){
        List<AssartBean> list = new ArrayList<>();
        cursor = dbManager.queryEntity(sql, bingArgs);
        while (cursor.moveToNext()) {
            AssartBean bean = new AssartBean();
            bean.setBhqid(cursor.getString(cursor.getColumnIndex("bhqid")));
            bean.setBhqmc(cursor.getString(cursor.getColumnIndex("bhqmc")));
            bean.setJsxmjb(cursor.getString(cursor.getColumnIndex("bhqjb")));
            bean.setJsxmid(cursor.getString(cursor.getColumnIndex("jsxmid")));
            bean.setKkqkmc(cursor.getString(cursor.getColumnIndex("jsxmmc")));
            bean.setKkmj(cursor.getString(cursor.getColumnIndex("kkmj")));
            bean.setZwzl(cursor.getString(cursor.getColumnIndex("zwzl")));
            bean.setBjbhqsj(cursor.getString(cursor.getColumnIndex("bjbhqsj")));
            bean.setIsgz(cursor.getString(cursor.getColumnIndex("isgz")));
            bean.setZgcs(cursor.getString(cursor.getColumnIndex("zgcs")));
            bean.setCenterpointx(cursor.getString(cursor.getColumnIndex("centerpointx")));
            bean.setCenterpointy(cursor.getString(cursor.getColumnIndex("centerpointy")));
            bean.setMjzb(cursor.getString(cursor.getColumnIndex("mjzb")));
            bean.setTjmj(cursor.getString(cursor.getColumnIndex("tjmj")));
            bean.setHxmj(cursor.getString(cursor.getColumnIndex("hxmj")));
            bean.setHcmj(cursor.getString(cursor.getColumnIndex("hcmj")));
            bean.setSymj(cursor.getString(cursor.getColumnIndex("symj")));
            bean.setPhotoPath(cursor.getString(cursor.getColumnIndex("photoPath")));
            bean.setUpstate(cursor.getString(cursor.getColumnIndex("upState")));
            bean.setTrzj(cursor.getString(cursor.getColumnIndex("trzj")));
            bean.setNcz(cursor.getString(cursor.getColumnIndex("ncz")));
            bean.setYbhqgx(cursor.getString(cursor.getColumnIndex("ybhqgx")));
            bean.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            bean.setUsertel(cursor.getString(cursor.getColumnIndex("usertel")));
            bean.setPlaceid(cursor.getString(cursor.getColumnIndex("placeid")));
            list.add(bean);
        }
        if (cursor != null) {
            cursor.close();
            if (dbManager != null) {
                dbManager.closeDB();
            }
        }
        return list;
    }
    public AssartBean queryAssartQyInfo(String sql, String [] bingArgs){
        List<AssartBean> list = queryAssartQyInfos(sql,bingArgs);
        return list.size()>0?list.get(0):null;
    }
}
