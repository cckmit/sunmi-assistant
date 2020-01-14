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
         * time : 2019-09-11 12:00
         * passenger_count : 12
         * pass_passenger_count : 12
         */

        @SerializedName("time")
        private String time;
        @SerializedName("passenger_count")
        private int passengerCount;
        @SerializedName("pass_passenger_count")
        private int passPassengerCount;

        public String getTime() {
            return time;
        }

        public int getPassengerCount() {
            return passengerCount;
        }

        public int getPassPassengerCount() {
            return passPassengerCount;
        }
    }
}
