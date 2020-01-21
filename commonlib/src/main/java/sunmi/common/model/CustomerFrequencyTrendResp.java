package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-01-13.
 */
public class CustomerFrequencyTrendResp {

    @SerializedName("frequency_list")
    private List<Item> frequencyList;

    public List<Item> getFrequencyList() {
        return frequencyList;
    }

    public static class Item {
        /**
         * time : 2019-09-11 00:00
         * passenger_count : 12
         * uniq_passenger_count : 12
         */

        @SerializedName("time")
        private String time;
        @SerializedName("passenger_count")
        private int passengerCount;
        @SerializedName("uniq_passenger_count")
        private int uniqPassengerCount;

        public String getTime() {
            return time;
        }

        public int getPassengerCount() {
            return passengerCount;
        }

        public int getUniqPassengerCount() {
            return uniqPassengerCount;
        }

    }
}
