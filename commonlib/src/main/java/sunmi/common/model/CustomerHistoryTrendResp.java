package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2019-10-15
 */
public class CustomerHistoryTrendResp {

    @SerializedName("count_list")
    private List<Item> countList;

    public List<Item> getCountList() {
        return countList;
    }

    public static class Item {
        /**
         * time : 2019-09-11 12:00
         * stranger_count : 12
         * regular_count : 12
         * total_count : 12
         */

        @SerializedName("time")
        private String time;
        @SerializedName("stranger_count")
        private int strangerCount;
        @SerializedName("regular_count")
        private int regularCount;
        @SerializedName("total_count")
        private int totalCount;

        public String getTime() {
            return time;
        }

        public int getStrangerCount() {
            return strangerCount;
        }

        public int getRegularCount() {
            return regularCount;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }
}
