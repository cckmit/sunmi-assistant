package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerRealtimeTrendResp {

    @SerializedName("time_list")
    private List<Item> timeList;

    public List<Item> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<Item> timeList) {
        this.timeList = timeList;
    }

    public static class Item {
        /**
         * time : 2020-02-27 00:00:00
         * total_count : 0
         */

        @SerializedName("time")
        private String time;
        @SerializedName("total_count")
        private int totalCount;
        @SerializedName("order_count")

        private int orderCount;
        @SerializedName("order_amount")
        private double orderAmount;

        public String getTime() {
            return time;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public double getOrderAmount() {
            return orderAmount;
        }
    }
}
