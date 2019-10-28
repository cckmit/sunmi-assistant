package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public class FaceEntryHistoryResp {
    /**
     * history_list : [{"history_id":12,"img_url":"xxxxx","arrival_time":1563972589,"shop_id":123,"device_name":"智能摄像机2","device_id":567},{"history_id":12,"img_url":"xxxxx","arrival_time":1563972589,"shop_id":123,"device_name":"智能摄像机2","device_id":568}]
     * return_count : 10
     * total_count : 11
     */

    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("history_list")
    private List<EntryHistory> historyList;


    public int getTotalCount() {
        return totalCount;
    }

    public List<EntryHistory> getHistoryList() {
        return historyList;
    }

    public static class EntryHistory {
        /**
         * history_id : 12
         * img_url : xxxxx
         * arrival_time : 1563972589
         * shop_id : 123
         * device_name : 智能摄像机2
         * device_id : 567
         * "arrival_date":"2019-07-26 12:40:00"
         */

        @SerializedName("history_id")
        private int historyId;
        @SerializedName("img_url")
        private String imgUrl;
        @SerializedName("arrival_time")
        private int arrivalTime;
        @SerializedName("shop_id")
        private int shopId;
        @SerializedName("device_name")
        private String deviceName;
        @SerializedName("device_id")
        private int deviceId;
        @SerializedName("arrival_date")
        private String arrivalDate;

        public int getHistoryId() {
            return historyId;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public int getShopId() {
            return shopId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public String getArrivalDate() {
            return arrivalDate;
        }
    }
}
