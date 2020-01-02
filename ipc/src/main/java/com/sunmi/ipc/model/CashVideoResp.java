package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

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
    private List<AuditVideoListBean> auditVideoList;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<AuditVideoListBean> getAuditVideoList() {
        return auditVideoList;
    }

    public void setAuditVideoList(List<AuditVideoListBean> auditVideoList) {
        this.auditVideoList = auditVideoList;
    }

    public static class AuditVideoListBean {
        /**
         * order_no : B12019060414421630291
         * video_id : 124
         * video_url : http://test.cdn.sunmi.com/VIDEO/abcdefgh.flv
         * snapshot_url : http://test.cdn.sunmi.com/VIDEO/abcdefgh.flv?*********
         * purchase_time : 1565235765
         * amount : 30.12
         * device_id : 356
         * device_sn : SS101D8BS09178
         * description : ************************
         * video_type : 1
         * video_tag : [1,2]
         * event_id : 123
         */

        @SerializedName("order_no")
        private String orderNo;
        @SerializedName("video_id")
        private int videoId;
        @SerializedName("video_url")
        private String videoUrl;
        @SerializedName("snapshot_url")
        private String snapshotUrl;
        @SerializedName("purchase_time")
        private int purchaseTime;
        @SerializedName("amount")
        private double amount;
        @SerializedName("device_id")
        private int deviceId;
        @SerializedName("device_sn")
        private String deviceSn;
        @SerializedName("description")
        private String description;
        @SerializedName("video_type")
        private int videoType;
        @SerializedName("event_id")
        private int eventId;
        @SerializedName("video_tag")
        private List<Integer> videoTag;

        private String deviceName;

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public int getVideoId() {
            return videoId;
        }

        public void setVideoId(int videoId) {
            this.videoId = videoId;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getSnapshotUrl() {
            return snapshotUrl;
        }

        public void setSnapshotUrl(String snapshotUrl) {
            this.snapshotUrl = snapshotUrl;
        }

        public int getPurchaseTime() {
            return purchaseTime;
        }

        public void setPurchaseTime(int purchaseTime) {
            this.purchaseTime = purchaseTime;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceSn() {
            return deviceSn;
        }

        public void setDeviceSn(String deviceSn) {
            this.deviceSn = deviceSn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getVideoType() {
            return videoType;
        }

        public void setVideoType(int videoType) {
            this.videoType = videoType;
        }

        public int getEventId() {
            return eventId;
        }

        public void setEventId(int eventId) {
            this.eventId = eventId;
        }

        public List<Integer> getVideoTag() {
            return videoTag;
        }

        public void setVideoTag(List<Integer> videoTag) {
            this.videoTag = videoTag;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }
    }
}
