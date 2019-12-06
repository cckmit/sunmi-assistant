package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-03.
 */
public class CashVideoCountResp {


    @SerializedName("stat_info_list")
    private List<CashVideoListBean> statInfoList;

    public List<CashVideoListBean> getStatInfoList() {
        return statInfoList;
    }

    public void setStatInfoList(List<CashVideoListBean> statInfoList) {
        this.statInfoList = statInfoList;
    }

}
