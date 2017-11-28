package com.yaotu.proj.studydemo.bean.nopassJsxmBean;

import java.util.List;

/**
 * Created by Administrator on 2017/10/12.
 */

public class NoPassInfo {
    private List<DetailedInfoBean> DetailedInfo;
    private List<NoPassBhqInfo> BhqList;

    public List<DetailedInfoBean> getDetailedInfo() {
        return DetailedInfo;
    }

    public void setDetailedInfo(List<DetailedInfoBean> detailedInfo) {
        DetailedInfo = detailedInfo;
    }

    public List<NoPassBhqInfo> getBhqList() {
        return BhqList;
    }

    public void setBhqList(List<NoPassBhqInfo> bhqList) {
        BhqList = bhqList;
    }
}
