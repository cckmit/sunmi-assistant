package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-16
 */
public class FaceGroupCreateReq {
    /**
     * company_id : 123
     * shop_id : 456
     * facedb_list : [{"name":"生客","type":1,"threshold":5,"period":1,"mark":"生客人脸库","capacity":1000,"target_id":0}]
     */

    @SerializedName("company_id")
    private int companyId;
    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("facedb_list")
    private List<Face> faceList;

    public FaceGroupCreateReq(int companyId, int shopId, List<Face> faceList) {
        this.companyId = companyId;
        this.shopId = shopId;
        this.faceList = faceList;
    }

    public static class Face {
        /**
         * name : 生客
         * type : 1
         * threshold : 5
         * period : 1
         * mark : 生客人脸库
         * capacity : 1000
         * target_id : 0
         */

        @SerializedName("name")
        private String name;
        @SerializedName("type")
        private int type;
        @SerializedName("threshold")
        private int threshold;
        @SerializedName("period")
        private int period;
        @SerializedName("mark")
        private String mark;
        @SerializedName("capacity")
        private int capacity;
        @SerializedName("target_id")
        private int targetId;

        public Face(String name, int type, int threshold, int period, String mark, int capacity, int targetId) {
            this.name = name;
            this.type = type;
            this.threshold = threshold;
            this.period = period;
            this.mark = mark;
            this.capacity = capacity;
            this.targetId = targetId;
        }
    }
}
