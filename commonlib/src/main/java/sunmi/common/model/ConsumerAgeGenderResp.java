package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public class ConsumerAgeGenderResp {

    @SerializedName("count_list")
    private List<CountListBean> countList;

    public List<CountListBean> getCountList() {
        return countList;
    }

    public static class CountListBean {
        /**
         * age_range_code : 0
         * male_count : 12
         * female_count : 13
         */

        @SerializedName("age_range_code")
        private int ageRangeCode;
        @SerializedName("male_count")
        private int maleCount;
        @SerializedName("female_count")
        private int femaleCount;

        public int getAgeRangeCode() {
            return ageRangeCode;
        }

        public int getMaleCount() {
            return maleCount;
        }

        public int getFemaleCount() {
            return femaleCount;
        }

    }
}
