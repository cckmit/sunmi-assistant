package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public class ConsumerRateResp {


    @SerializedName("count_list")
    private List<CountListBean> countList;

    public List<CountListBean> getCountList() {
        return countList;
    }

    public static class CountListBean {
        /**
         * time : 1
         * order_count : 11
         * passenger_flow_count : 123
         */

        @SerializedName("time")
        private int time;
        @SerializedName("order_count")
        private int orderCount;
        @SerializedName("passenger_flow_count")
        private int passengerFlowCount;

        public int getTime() {
            return time;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public int getPassengerFlowCount() {
            return passengerFlowCount;
        }

    }
}
