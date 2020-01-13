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
    private List<CountListBean> countList;

    public List<CountListBean> getCountList() {
        return countList;
    }

    public void setCountList(List<CountListBean> countList) {
        this.countList = countList;
    }

    public static class CountListBean {
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

        public void setTime(String time) {
            this.time = time;
        }

        public int getPassengerCount() {
            return passengerCount;
        }

        public void setPassengerCount(int passengerCount) {
            this.passengerCount = passengerCount;
        }

        public int getPassPassengerCount() {
            return passPassengerCount;
        }

        public void setPassPassengerCount(int passPassengerCount) {
            this.passPassengerCount = passPassengerCount;
        }
    }
}
