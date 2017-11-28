package com.yaotu.proj.studydemo.customclass;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.yaotu.proj.studydemo.bean.tableBean.AssartBean;
import com.yaotu.proj.studydemo.bean.tableBean.DevelopConstructionBean;
import com.yaotu.proj.studydemo.bean.tableBean.IndustryBean;
import com.yaotu.proj.studydemo.bean.tableBean.NewEnergyBean;
import com.yaotu.proj.studydemo.bean.tableBean.TkEnterpriseBean;
import com.yaotu.proj.studydemo.bean.tableBean.TravelBean;
import com.yaotu.proj.studydemo.bean.tableBean.XchcModel;
import com.yaotu.proj.studydemo.bean.tableBean.kczyBean;
import com.yaotu.proj.studydemo.util.DBManager;

/**
 * Created by Administrator on 2017/6/26.
 */

public class InsertLocalTableData {
    private Context context;
    private DBManager dbManager;
    public InsertLocalTableData(Context context){
        this.context = context;
        if (dbManager == null) {
            dbManager = new DBManager(context);
        }
    }
    /*
    * 探矿企业信息
    * */
    public boolean InsertTkqyInfo(TkEnterpriseBean bean, String upState){
        boolean result = false;
        if (bean != null) {
            result =  dbManager.updateBySql("insert into TkqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,fzjg,zjyxqb,zjyxqe,bjbhqsj,kqslqk,ktfs,kqsx,hbpzwh,isbhq,scqk,zgcs,centerpointx,centerpointy,mjzb,tjmj"+
                    ",hxmj,hcmj,symj,trzj,ncz,visbhq,ybhqgx,vkqsx,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),
                    bean.getJsxmmc(),bean.getFzjg(),bean.getZjyxqb(),bean.getZjyxqe(),bean.getBjbhqsj(),bean.getKqslqk(),bean.getKtfs(),bean.getKqsx(),bean.getHbpzwh(),bean.getIsbhq()
                    ,bean.getScqk(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),bean.getHcmj(),bean.getSymj(),bean.getTrzj(),
                    bean.getNcz(),bean.getVisbhq(),bean.getYbhqgx(),bean.getVkqsx(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        Log.i("TAG", "InsertTkqyInfo: -------------------->");
        return result;
    }
    /*工业企业
    * */
    public boolean InsertGyqyInfo(IndustryBean bean ,String upState){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into GyqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,jsxmgm,bjbhqsj,lsyg,qysx,hbpzwh,isbhq,ishbys,yxqk,zgcs," +
                    "centerpointx,centerpointy,mjzb,tjmj,hxmj,hcmj,symj,trzj,ncz,yswjbh,ybhqgx,vyxqk,visbhq,vishbys,vqysx,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new String[]{
                    bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),bean.getJsxmmc(),bean.getJsxmgm(),bean.getBjbhqsj(),bean.getLsyg(),bean.getQysx(),bean.getHbpzwh(),
                    bean.getIsbhq(),bean.getIshbys(),bean.getYxqk(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),
                    bean.getHcmj(),bean.getSymj(),bean.getTrzj(),bean.getNcz(),bean.getYswjbh(),bean.getYbhqgx(),bean.getVyxqk(),bean.getVisbhq(),bean.getVishbys(),bean.getVqysx(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
    /*矿产资源企业
    * */
    public boolean InsertKczyQyInfo(kczyBean bean ,String upState){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into KczyqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,jsxmgm,fzjg,zjyxqb,zjyxqe,bjbhqsj,kqslqk,kcfs,kqsx," +
                    "hbpzwh,isbhq,ishbys,scqk,zgcs,centerpointx,centerpointy,mjzb,tjmj,hxmj,hcmj,symj,trzj,ncz,yswjbh,ybhqgx,vkcfs,visbhq,vishbys,vscqk,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{
                    bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),bean.getJsxmmc(),bean.getJsxmgm(),bean.getFzjg(),bean.getZjyxqb(),bean.getZjyxqe(),bean.getBjbhqsj(),bean.getKqslqk(),
                    bean.getKcfs(),bean.getKqsx(),bean.getHbpzwh(),bean.getIsbhq(),bean.getIshbys(),bean.getScqk(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),
                    bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),bean.getHcmj(),bean.getSymj(),bean.getTrzj(),bean.getNcz(),bean.getYswjbh(),bean.getYbhqgx(),bean.getVkcfs(),
                    bean.getVisbhq(),bean.getVishbys(),bean.getVscqk(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
    /*旅游资源开发企业
    * */
    public boolean InsertTravelQyInfo(TravelBean bean,String upState){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into TravelqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,jsxmgm,bjbhqsj,lsyg,qysx,hbpzwh," +
                    "isbhq,ishbys,yyqk,zgcs,centerpointx,centerpointy,mjzb,tjmj,hxmj,hcmj,symj,trzj,ncz,yswjbh,ybhqgx,vqysx,visbhq,vishbys,vyyqk,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{
                    bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),bean.getJsxmmc(),bean.getJsxmgm(),bean.getBjbhqsj(),bean.getLsyg(),bean.getQysx(),bean.getHbpzwh(),
                    bean.getIsbhq(),bean.getIshbys(),bean.getYyqk(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),bean.getHcmj(),
                    bean.getSymj(),bean.getTrzj(),bean.getNcz(),bean.getYswjbh(),bean.getYbhqgx(),bean.getVqysx(),bean.getVisbhq(),bean.getVishbys(),bean.getVyyqk(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
    /*新能源项目
    * */
    public boolean InsertNewEnergyQyInfo(NewEnergyBean bean, String upState){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into NewEnergyqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,jsxmlx,jsxmgm,bjbhqsj,qysx,hbpzwh,isbhq," +
                    "ishbys,scqk,zgcs,centerpointx,centerpointy,mjzb,tjmj,hxmj,hcmj,symj,trzj,ncz,yswjbh,ybhqgx,vqyxs,visbhq,vishbys,vscqk,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{
                    bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),bean.getJsxmmc(),bean.getJsxmlx(),bean.getJsxmgm(),bean.getBjbhqsj(),bean.getQysx(),bean.getHbpzwh(),
                    bean.getIsbhq(),bean.getIshbys(),bean.getScqk(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),
                    bean.getHcmj(),bean.getSymj(),bean.getTrzj(),bean.getNcz(),bean.getYswjbh(),bean.getYbhqgx(),bean.getVqyxs(),bean.getVisbhq(),bean.getVishbys(),bean.getVscqk(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
    /*开发建设活动
    * */
    public boolean InsertDevelopQyInfo(DevelopConstructionBean bean,String upState){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into DevelopqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,gm,hdlx,bjbhqsj,hbpzwh,isbhq,ishbys,scqk,zgcs," +
                    "centerpointx,centerpointy,mjzb,tjmj,hxmj,hcmj,symj,kkmj,trzj,ncz,ybhqgx,visbhq,vishbys,vscqk,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{
                    bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),bean.getKfjsmc(),bean.getGm(),bean.getHdlx(),bean.getBjbhqsj(),bean.getHbpzwh(),bean.getIsbhq(),
                    bean.getIshbys(),bean.getScqk(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),bean.getHcmj(),
                    bean.getSymj(),bean.getKkmj(),bean.getTrzj(),bean.getNcz(),bean.getYbhqgx(),bean.getVisbhq(),bean.getVishbys(),bean.getVscqk(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
    /*开垦活动
    * */
    public boolean InsertAssartQyInfo(AssartBean bean, String upState){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into AssartqyInfo(bhqid,bhqmc,bhqjb,jsxmid,jsxmmc,kkmj,zwzl,bjbhqsj,isgz,zgcs,centerpointx,centerpointy,mjzb," +
                    "tjmj,hxmj,hcmj,symj,trzj,ncz,ybhqgx,photoPath,upState,username,usertel,placeid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{bean.getBhqid(),bean.getBhqmc(),bean.getJsxmjb(),bean.getJsxmid(),
            bean.getKkqkmc(),bean.getKkmj(),bean.getZwzl(),bean.getBjbhqsj(),bean.getIsgz(),bean.getZgcs(),bean.getCenterpointx(),bean.getCenterpointy(),
            bean.getMjzb(),bean.getTjmj(),bean.getHxmj(),bean.getHcmj(),bean.getSymj(),bean.getTrzj(),bean.getNcz(),bean.getYbhqgx(),bean.getPhotoPath(),upState,bean.getUsername(),bean.getUsertel(),bean.getPlaceid()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
    /*现场核查
    * */
    public boolean insertXchcInfo(XchcModel bean){
        boolean result = false;
        if (bean != null) {
            result = dbManager.updateBySql("insert into xchcInfoTab(jsxmid,jsxmmc,jsxmlx,jd,wd,szbhqgnq,scale,bhlx,currentstatus,lsyg,hbspxg,styxbh,qtsm,objectid," +
                    "szbhqmc,jsxmlxdm,ischeckeddm,yhdh,szbhqid,isarchived,bhlxdm,jsxmlxdetails,photourl,photoname,szbhqjb,szbhqjbdm,yhmc) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new String[]{bean.getJsxmid(),bean.getJsxmmc(),
            bean.getJsxmlx(),String.valueOf(bean.getJd()),String.valueOf(bean.getWd()),bean.getSzbhqgnq(),bean.getScale(),bean.getBhlx(),bean.getCurrentstatus(),
            bean.getLsyg(),bean.getHbspxg(),bean.getStyxph(),bean.getQtsm(),String.valueOf(bean.getObjectid()),bean.getSzbhqmc(),bean.getJsxmlxdm(),bean.getIscheckeddm(),
            bean.getYhdh(),bean.getSzbhqid(),bean.getIsarchived(),bean.getBhlxdm(),bean.getJsxmlxdetails(),bean.getPhotourl(),bean.getPhotoName(),bean.getSzbhqjb(),bean.getSzbhqjbdm(),bean.getSubmitter()});
        }
        if (dbManager != null) {
            dbManager.closeDB();
        }
        return result;
    }
}
