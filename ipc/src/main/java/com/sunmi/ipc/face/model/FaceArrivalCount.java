package com.sunmi.ipc.face.model;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-11.
 */
public class FaceArrivalCount {

    /**
     * total_count : 11
     */

    @SerializedName("total_count")
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
