package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceHistoryResp {
    /**
     * face_list : [{"face_id":12,"img_url":"xxxxx","name":"小明","gender":1,"age":56,"age_range":2,"group_id":13,"last_arrival_time":1563972589,"create_time":1563972388,"arrival_count":3},{"face_id":13,"img_url":"xxxxx","name":"小红","gender":1,"age":56,"age_range":2,"group_id":"会员","last_arrival_time":1563972589,"create_time":1563972388,"arrival_count":3}]
     * return_count : 10
     * total_count : 100
     */

    @SerializedName("return_count")
    private int returnCount;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("face_list")
    private List<History> faceList;

    public int getReturnCount() {
        return returnCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<History> getFaceList() {
        return faceList;
    }

    public static class History {
        /**
         * face_id : 12
         * img_url : xxxxx
         * name : 小明
         * gender : 1
         * age : 56
         * age_range : 2
         * group_id : 13
         * last_arrival_time : 1563972589
         * create_time : 1563972388
         * arrival_count : 3
         */

        @SerializedName("face_id")
        private int faceId;
        @SerializedName("img_url")
        private String imgUrl;
        @SerializedName("name")
        private String name;
        @SerializedName("gender")
        private int gender;
        @SerializedName("age")
        private int age;
        @SerializedName("age_range")
        private int ageRange;
        @SerializedName("group_id")
        private int groupId;
        @SerializedName("last_arrival_time")
        private int lastArrivalTime;
        @SerializedName("create_time")
        private int createTime;
        @SerializedName("arrival_count")
        private int arrivalCount;

        public int getFaceId() {
            return faceId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public String getName() {
            return name;
        }

        public int getGender() {
            return gender;
        }

        public int getAge() {
            return age;
        }

        public int getAgeRange() {
            return ageRange;
        }

        public int getGroupId() {
            return groupId;
        }

        public int getLastArrivalTime() {
            return lastArrivalTime;
        }

        public int getCreateTime() {
            return createTime;
        }

        public int getArrivalCount() {
            return arrivalCount;
        }
    }
}
