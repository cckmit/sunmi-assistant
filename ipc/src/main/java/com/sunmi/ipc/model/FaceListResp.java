package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;
import com.sunmi.ipc.face.model.Face;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceListResp {
    /**
     * total_count : 2
     * face_list : [{"face_id":1,"name":"Mike","gender":1,"age_range_code":2,"group_id":3,"arrival_count":3,"create_time":1561375084,"last_arrival_time":1561375084,"img_url":"https://cdn.sunmi.com/****"},{"face_id":3,"name":"Lily","gender":2,"age_range_code":2,"group_id":3,"arrival_count":2,"create_time":1561375084,"last_arrival_time":1561375084,"img_url":"https://cdn.sunmi.com/****"}]
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("face_list")
    private List<Face> faceList;

    public int getTotalCount() {
        return totalCount;
    }

    public List<Face> getFaceList() {
        return faceList;
    }

}
