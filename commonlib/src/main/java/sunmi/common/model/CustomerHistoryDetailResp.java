package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-10-16
 */
public class CustomerHistoryDetailResp {

    @SerializedName("count_list")
    private List<Item> list;

    public List<Item> getList() {
        return list;
    }

    public static class Item {
        /**
         * age_range_code : 1
         * male_count : 0
         * male_regular_count : 0
         * female_count : 0
         * female_regular_count : 0
         * female_rush_hour : -1
         * male_rush_hour : -1
         */

        @SerializedName("age_range_code")
        private int ageRangeCode;
        @SerializedName("male_count")
        private int maleCount;
        @SerializedName("male_regular_count")
        private int maleRegularCount;
        @SerializedName("female_count")
        private int femaleCount;
        @SerializedName("female_regular_count")
        private int femaleRegularCount;
        @SerializedName("female_rush_hour")
        private int femaleRushHour;
        @SerializedName("male_rush_hour")
        private int maleRushHour;

        public int getAgeRangeCode() {
            return ageRangeCode;
        }

        public int getMaleCount() {
            return maleCount;
        }

        public int getMaleRegularCount() {
            return maleRegularCount;
        }

        public int getFemaleCount() {
            return femaleCount;
        }

        public int getFemaleRegularCount() {
            return femaleRegularCount;
        }

        public int getFemaleRushHour() {
            return femaleRushHour;
        }

        public int getMaleRushHour() {
            return maleRushHour;
        }
    }
}
