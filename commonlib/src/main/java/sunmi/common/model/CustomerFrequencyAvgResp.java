package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description: T+1客流分析-客群平均到店频率
 *
 * @author linyuanpeng on 2020-01-13.
 */
public class CustomerFrequencyAvgResp {
    @SerializedName("frequency_list")
    private List<Item> frequencyList;

    public List<Item> getFrequencyList() {
        return frequencyList;
    }

    public void setFrequencyList(List<Item> frequencyList) {
        this.frequencyList = frequencyList;
    }

    public static class Item {
        /**
         * age_range_code : 0
         * gender : 1
         * passenger_count : 12
         * uniq_passenger_count : 12
         */

        @SerializedName("age_range_code")
        private int ageRangeCode;
        @SerializedName("gender")
        private int gender;
        @SerializedName("passenger_count")
        private int passengerCount;
        @SerializedName("uniq_passenger_count")
        private int uniqPassengerCount;

        public int getAgeRangeCode() {
            return ageRangeCode;
        }

        public void setAgeRangeCode(int ageRangeCode) {
            this.ageRangeCode = ageRangeCode;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getPassengerCount() {
            return passengerCount;
        }

        public void setPassengerCount(int passengerCount) {
            this.passengerCount = passengerCount;
        }

        public int getUniqPassengerCount() {
            return uniqPassengerCount;
        }

        public void setUniqPassengerCount(int uniqPassengerCount) {
            this.uniqPassengerCount = uniqPassengerCount;
        }
    }
}
