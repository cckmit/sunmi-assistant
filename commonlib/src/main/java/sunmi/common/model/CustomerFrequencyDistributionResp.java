package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description: T+1客流分析-客群到店频率分布
 *
 * @author linyuanpeng on 2020-01-13.
 */
public class CustomerFrequencyDistributionResp {

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
         * frequency : 1
         * uniq_passenger_count : 12，
         */

        @SerializedName("frequency")
        private int frequency;
        @SerializedName("uniq_passenger_count")
        private int uniqPassengerCount;

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public int getUniqPassengerCount() {
            return uniqPassengerCount;
        }

        public void setUniqPassengerCount(int uniqPassengerCount) {
            this.uniqPassengerCount = uniqPassengerCount;
        }
    }
}
