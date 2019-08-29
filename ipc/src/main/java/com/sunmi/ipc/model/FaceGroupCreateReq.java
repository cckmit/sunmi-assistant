package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;
import com.sunmi.ipc.face.model.FaceGroup;

import java.util.ArrayList;
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
    private List<Group> faceList;

    public FaceGroupCreateReq(int companyId, int shopId, String name, String mark, int capacity) {
        this.companyId = companyId;
        this.shopId = shopId;
        this.faceList = new ArrayList<>(1);
        this.faceList.add(new Group(name, mark, capacity));
    }

    public static class Group {
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

        public Group(String name, String mark, int capacity) {
            this.name = name == null ? "" : name;
            this.type = FaceGroup.FACE_GROUP_TYPE_CUSTOM;
            this.threshold = 0;
            this.period = 0;
            this.mark = mark == null ? "" : mark;
            this.capacity = capacity;
            this.targetId = 0;
        }
    }
}
