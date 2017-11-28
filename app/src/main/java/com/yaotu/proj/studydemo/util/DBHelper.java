package com.yaotu.proj.studydemo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/3/14.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb.db";
    private static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table place (p_id int,p_name varchar,p_log varchar,p_lat varchar,p_type varchar,p_state varchar)");//创建采集地表--- place
        db.execSQL("create table table1 (p_name varchar, mj varchar,zc varchar)");//创建样例表一--- table1
        //==================================================================================================================================================
        db.execSQL("create table users(yhdh varchar,pwd varchar,yhmc varchar)");//用户信息表
        db.execSQL("create table nopassbhqInfo(bhq_id varchar,bhq_name varchar)");//未通过保护区信息表
        db.execSQL("create table bhqInfo (bhq_id varchar,bhq_name varchar,bhq_level varchar,bhq_level_dm varchar,yhdh varchar)");//创建保护区基本信息表 bhqInfo
        db.execSQL("create table codeType(dmlb varchar,dmz varchar,jb varchar,lb varchar,dmmc1 varchar,dmmc2 varchar,dmmc3 varchar,sxh varchar,bz varchar)");//创建代码类别表
        db.execSQL("create table TkqyInfo(bhqid varchar,bhqmc varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,jsxmgm varchar,fzjg varchar,zjyxqb varchar,zjyxqe varchar,bjbhqsj varchar,kqslqk varchar," +
                "ktfs varchar,kqsx varchar,hbpzwh varchar,isbhq varchar,scqk varchar,zgcs varchar,centerpointx varchar,centerpointy varchar,mjzb varchar," +
                "tjmj varchar,hxmj varchar,hcmj varchar,symj varchar,trzj varchar,ncz varchar,visbhq varchar,ybhqgx varchar,vkqsx varchar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");//探矿企业表TkqyInfo
        db.execSQL("create table GyqyInfo(bhqid varchar,bhqmc varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,jsxmgm varchar,bjbhqsj varchar,lsyg varchar," +
                "qysx varchar,hbpzwh varchar,isbhq varchar,ishbys varchar,yxqk varchar,zgcs varchar,centerpointx varchar,centerpointy varchar,mjzb varchar," +
                "tjmj varchar,hxmj varchar,hcmj varchar,symj varchar,trzj varchar,ncz varchar,yswjbh varchar,ybhqgx varchar,vyxqk varchar,visbhq varchar,vishbys varchar,vqysx varchar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");//工业企业表GyqyInfo
        db.execSQL("create table KczyqyInfo(bhqid varchar,bhqmc varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,jsxmgm varchar,fzjg varchar,zjyxqb varchar,zjyxqe varchar,bjbhqsj varchar," +
                "kqslqk varchar,kcfs varchar,kqsx varchar,hbpzwh varchar,isbhq varchar,ishbys varchar,scqk varchar,zgcs varchar,centerpointx varchar,centerpointy varchar," +
                "mjzb varchar,tjmj varchar,hxmj varchar,hcmj varchar,symj varchar,trzj varchar,ncz varchar,yswjbh varchar,ybhqgx varchar,vkcfs varchar,visbhq varchar,vishbys varchar,vscqk varchar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");//矿产资源企业表tkEnterprise
        db.execSQL("create table TravelqyInfo(bhqid varchar,bhqmc  varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,jsxmgm varchar,bjbhqsj varchar,lsyg varchar," +
                "qysx varchar,hbpzwh varchar,isbhq varchar,ishbys varchar,yyqk varchar,zgcs varchar,centerpointx varchar,centerpointy varchar,mjzb varchar," +
                "tjmj varchar,hxmj varchar,hcmj varchar,symj varchar,trzj varchar,ncz varchar,yswjbh varchar,ybhqgx varchar,vqysx varchar,visbhq varchar,vishbys varchar,vyyqk varchar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");//旅游资源企业表TravelqyInfo
        db.execSQL("create table NewEnergyqyInfo(bhqid varchar,bhqmc varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,jsxmlx varchar,jsxmgm varchar,bjbhqsj varchar,qysx varchar," +
                 "hbpzwh varchar,isbhq varchar,ishbys varchar,scqk varchar,zgcs varchar,centerpointx varchar,centerpointy varchar,mjzb varchar,tjmj varchar,hxmj varchar,hcmj varchar," +
                 "symj varchar,trzj varchar,ncz varchar,yswjbh varchar,ybhqgx varchar,vqyxs varchar,visbhq varchar,vishbys varchar,vscqk varcgar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");//新能源项目表NewEnergyqyInfo
        db.execSQL("create table DevelopqyInfo(bhqid varchar,bhqmc varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,gm varchar,hdlx varchar,bjbhqsj varchar,hbpzwh varchar,isbhq varchar,ishbys varchar," +
                "scqk varchar,zgcs varchar,centerpointx varchar,centerpointy varchar,mjzb varchar,tjmj varchar,hxmj varchar,hcmj varchar,symj varchar,kkmj varchar,trzj varchar,ncz varchar,ybhqgx varchar," +
                "visbhq varchar,vishbys varchar,vscqk varchar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");//开发建设活动表DevelopqyInfo
        db.execSQL("create table AssartqyInfo(bhqid varchar,bhqmc varchar,bhqjb varchar,jsxmid varchar,jsxmmc varchar,kkmj varchar,zwzl varchar,bjbhqsj varchar,isgz varchar," +
                "zgcs varchar,centerpointx varchar,centerpointy varchar,mjzb varchar,tjmj varchar,hxmj varchar,hcmj varchar,symj varchar,trzj varchar,ncz varchar,ybhqgx varchar,photoPath varchar,upState varchar,username varchar,usertel varchar,placeid varchar)");// 开垦活动表AssartqyInfo

        db.execSQL("create table xchcInfoTab(jsxmid varchar,jsxmmc varchar,jsxmlx varchar,jd varchar,wd varchar,szbhqgnq varchar,scale varchar," +
                    "bhlx varchar,currentstatus varchar,lsyg varchar,hbspxg varchar,styxbh varchar,qtsm varchar,objectid varchar,szbhqmc varchar," +
                    "jsxmlxdm varchar,ischeckeddm varchar,yhdh varchar,szbhqid varchar,isarchived varchar,bhlxdm varchar,jsxmlxdetails varchar," +
                    "photourl varchar,photoname varchar,szbhqjb varchar,szbhqjbdm varchar,yhmc varchar)");
        //-------------------------------------------------------------
        db.execSQL("create table bhqpointInfo(objectid varchar,wd varchar,jd varchar,bhqid varchar,yhdh varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("alter table place add m int");
    }
}
