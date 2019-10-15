package com.sunmi.ipc.face.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-10.
 */
public class FaceArrivalLogResp {

    /**
     * history_list : [{"company_id":123,"shop_id":123,"history_id":12,"img_url":"xxxxx","gender":1,"age_range":1,"group_id":1,"arrival_time":1563972589,"arrival_date":"2019-09-02 09:50:53","device_name":"智能摄像机2","device_id":567}]
     * total_count : 11
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("history_list")
    private List<HistoryListBean> historyList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<HistoryListBean> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<HistoryListBean> historyList) {
        this.historyList = historyList;
    }

    public static class HistoryListBean {
        /**
         * company_id : 123
         * shop_id : 123
         * history_id : 12
         * img_url : xxxxx
         * gender : 1
         * age_range : 1
         * group_id : 1
         * arrival_time : 1563972589
         * arrival_date : 2019-09-02 09:50:53
         * device_name : 智能摄像机2
         * device_id : 567
         */

        @SerializedName("company_id")
        private int companyId;
        @SerializedName("shop_id")
        private int shopId;
        @SerializedName("face_id")
        private int faceId;
        @SerializedName("history_id")
        private int historyId;
        @SerializedName("img_url")
        private String imgUrl;
        @SerializedName("gender")
        private int gender;
        @SerializedName("age_range")
        private int ageRange;
        @SerializedName("group_id")
        private int groupId;
        @SerializedName("arrival_time")
        private int arrivalTime;
        @SerializedName("arrival_date")
        private String arrivalDate;
        @SerializedName("device_name")
        private String deviceName;
        @SerializedName("device_id")
        private int deviceId;

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int companyId) {
            this.companyId = companyId;
        }

        public int getShopId() {
            return shopId;
        }

        public void setShopId(int shopId) {
            this.shopId = shopId;
        }

        public int getFaceId() {
            return faceId;
        }

        public void setFaceId(int faceId) {
            this.faceId = faceId;
        }

        public int getHistoryId() {
            return historyId;
        }

        public void setHistoryId(int historyId) {
            this.historyId = historyId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getAgeRange() {
            return ageRange;
        }

        public void setAgeRange(int ageRange) {
            this.ageRange = ageRange;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(int arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public String getArrivalDate() {
            return arrivalDate;
        }

        public void setArrivalDate(String arrivalDate) {
            this.arrivalDate = arrivalDate;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }
    }
}
