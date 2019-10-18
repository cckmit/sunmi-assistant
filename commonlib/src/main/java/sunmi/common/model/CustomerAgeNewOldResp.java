package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public class CustomerAgeNewOldResp {

    @SerializedName("count_list")
    private List<CountListBean> countList;

    public List<CountListBean> getCountList() {
        return countList;
    }

    public static class CountListBean {
        /**
         * age_range_code : 0
         * stranger_count : 12
         * regular_count : 13
         */

        @SerializedName("age_range_code")
        private int ageRangeCode;
        @SerializedName("stranger_count")
        private int strangerCount;
        @SerializedName("regular_count")
        private int regularCount;

        public int getAgeRangeCode() {
            return ageRangeCode;
        }

        public int getStrangerCount() {
            return strangerCount;
        }

        public int getRegularCount() {
            return regularCount;
        }

    }
}
