package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TotalRealtimeCustomerTrendResp {

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
        private int totalCount;

        public String getTime() {
            return time;
        }

        public int getTotalCount() {
            return totalCount;
        }

    }
}
