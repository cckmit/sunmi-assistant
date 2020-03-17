package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TotalRealtimeSalesTrendResp {

    @SerializedName("time_list")
    private List<Item> list;

    public List<Item> getList() {
        return list;
    }

    public static class Item {
        /**
         * time : 2020-02-27 00:00:00
         * total_count : 0
         */

        @SerializedName("time")
        private String time;
        @SerializedName("total_count")
        private int orderCount;
        @SerializedName("order_amount")
        private double orderAmount;

        public String getTime() {
            return time;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public double getOrderAmount() {
            return orderAmount;
        }
    }
}
