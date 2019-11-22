package com.sunmi.assistant.pos.response;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangShiJie
 * @date 2019-11-20
 */
public class PosWarrantyResp {

    /**
     * status : 0
     * activating_time : 1538979428
     * expire_time : 1570515428
     */

    @SerializedName("status")
    private int status;
    @SerializedName("activated_time")
    private String activatedTime;
    @SerializedName("expire_time")
    private String expireTime;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(String activatedTime) {
        this.activatedTime = activatedTime;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }
}
