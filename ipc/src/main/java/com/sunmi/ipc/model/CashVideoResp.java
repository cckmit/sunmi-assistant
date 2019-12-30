package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;
import com.sunmi.ipc.cash.model.CashVideo;

import java.util.List;

/**
 * @author yangShiJie
 * @date 2019-12-04
 */
public class CashVideoResp {


    /**
     * audit_video_list : [{"order_no":"B12019060414421630291","video_id":124,"video_url":"http://test.cdn.sunmi.com/VIDEO/abcdefgh.flv","snapshot_url":"http://test.cdn.sunmi.com/VIDEO/abcdefgh.flv?*********","purchase_time":1565235765,"amount":30.12,"device_id":356,"device_sn":"SS101D8BS09178","description":"************************","video_type":1,"video_tag":[1,2],"event_id":123}]
     * total_count : 105
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("audit_video_list")
    private List<CashVideo> auditVideoList;

    public int getTotalCount() {
        return totalCount;
    }

    public List<CashVideo> getAuditVideoList() {
        return auditVideoList;
    }

}
