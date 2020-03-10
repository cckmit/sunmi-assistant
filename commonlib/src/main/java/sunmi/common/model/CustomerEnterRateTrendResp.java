package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description: T+1客流分析-进店率趋势
 *
 * @author linyuanpeng on 2020-01-13.
 */
public class CustomerEnterRateTrendResp {

    @SerializedName("count_list")
    private List<Item> countList;

    public List<Item> getCountList() {
        return countList;
    }

    public static class Item {

        /**
         * passenger_count : 0
         * pass_passenger_count : 0
         * entry_head_count : 0
         * time : 2020-01-01 00:00
         */

        @SerializedName("time")
        private String time;
        @SerializedName("passenger_count")
        private int passengerCount;
        @SerializedName("pass_passenger_count")
        private int passPassengerCount;
        @SerializedName("entry_head_count")
        private int entryHeadCount;

        public String getTime() {
            return time;
        }

        public int getPassengerCount() {
            return passengerCount;
        }

        public int getPassPassengerCount() {
            return passPassengerCount;
        }

        public int getEntryHeadCount() {
            return entryHeadCount;
        }

    }
}
