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
         * age_range_code : 0
         * male_count : 12
         * male_regular_count : 10
         * male_rush_hour : 11
         * male_uniq_count : 11
         * female_count : 13
         * female_regular_count : 10
         * female_rush_hour : 11
         * female_uniq_count : 11
         */

        @SerializedName("age_range_code")
        private int ageRangeCode;
        @SerializedName("male_count")
        private int maleCount;
        @SerializedName("male_regular_count")
        private int maleRegularCount;
        @SerializedName("male_rush_hour")
        private String maleRushHour;
        @SerializedName("male_uniq_count")
        private int maleUniqCount;
        @SerializedName("female_count")
        private int femaleCount;
        @SerializedName("female_regular_count")
        private int femaleRegularCount;
        @SerializedName("female_rush_hour")
        private String femaleRushHour;
        @SerializedName("female_uniq_count")
        private int femaleUniqCount;

        public int getAgeRangeCode() {
            return ageRangeCode;
        }

        public int getMaleCount() {
            return maleCount;
        }

        public int getMaleRegularCount() {
            return maleRegularCount;
        }

        public String getMaleRushHour() {
            return maleRushHour;
        }

        public int getMaleUniqCount() {
            return maleUniqCount;
        }

        public int getFemaleCount() {
            return femaleCount;
        }

        public int getFemaleRegularCount() {
            return femaleRegularCount;
        }

        public String getFemaleRushHour() {
            return femaleRushHour;
        }

        public int getFemaleUniqCount() {
            return femaleUniqCount;
        }

    }
}
