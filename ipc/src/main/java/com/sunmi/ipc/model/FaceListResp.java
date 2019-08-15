package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

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

    public static class Face {
        /**
         * face_id : 1
         * name : Mike
         * gender : 1
         * age_range_code : 2
         * group_id : 3
         * arrival_count : 3
         * create_time : 1561375084
         * last_arrival_time : 1561375084
         * img_url : https://cdn.sunmi.com/****
         */

        @SerializedName("face_id")
        private int faceId;
        @SerializedName("name")
        private String name;
        @SerializedName("gender")
        private int gender;
        @SerializedName("age_range_code")
        private int ageRangeCode;
        @SerializedName("group_id")
        private int groupId;
        @SerializedName("arrival_count")
        private int arrivalCount;
        @SerializedName("create_time")
        private int createTime;
        @SerializedName("last_arrival_time")
        private int lastArrivalTime;
        @SerializedName("img_url")
        private String imgUrl;

        public int getFaceId() {
            return faceId;
        }

        public String getName() {
            return name;
        }

        public int getGender() {
            return gender;
        }

        public int getAgeRangeCode() {
            return ageRangeCode;
        }

        public int getGroupId() {
            return groupId;
        }

        public int getArrivalCount() {
            return arrivalCount;
        }

        public int getCreateTime() {
            return createTime;
        }

        public int getLastArrivalTime() {
            return lastArrivalTime;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }
}
