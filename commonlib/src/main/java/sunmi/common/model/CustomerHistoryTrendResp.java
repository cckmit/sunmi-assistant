package sunmi.common.model;

import android.annotation.SuppressLint;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import sunmi.common.exception.TimeDateException;

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

    /**
     * @param period 是 number 1：今日，2：本周，3：本月，4：昨日
     */
    public synchronized void init(int period) throws TimeDateException {
        if (countList == null || countList.isEmpty()) {
            return;
        }
        for (Item item : countList) {
            item.init(period);
        }
    }

    public static class Item {

        private static final int TIME_PERIOD_TODAY = 1;
        private static final int TIME_PERIOD_WEEK = 2;
        private static final int TIME_PERIOD_MONTH = 3;
        private static final int TIME_PERIOD_YESTERDAY = 4;

        @SuppressLint("SimpleDateFormat")
        private static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat")
        private static final SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        /**
         * time : 2019-09-11 12:00
         * stranger_count : 12
         * regular_count : 12
         * total_count : 12
         * pass_count : 321
         * entry_head_count : 321
         */

        private long timestamp = -1;

        @SerializedName("time")
        private String timeStr;
        @SerializedName("stranger_count")
        private int strangerCount;
        @SerializedName("regular_count")
        private int regularCount;
        @SerializedName("total_count")
        private int totalCount;
        @SerializedName("pass_count")
        private int passCount;
        @SerializedName("entry_head_count")
        private int entryHeadCount;

        /**
         * @param period 是 number 1：今日，2：本周，3：本月，4：昨日
         */
        private void init(int period) throws TimeDateException {
            if (timestamp > 0) {
                return;
            }
            String pattern = "";
            try {
                switch (period) {
                    case TIME_PERIOD_TODAY:
                        // fall through
                    case TIME_PERIOD_YESTERDAY:
                        pattern = FORMAT_DATE_TIME.toPattern();
                        timestamp = FORMAT_DATE_TIME.parse(timeStr).getTime();
                        break;
                    case TIME_PERIOD_WEEK:
                        // fall through
                    case TIME_PERIOD_MONTH:
                        pattern = FORMAT_DATE.toPattern();
                        timestamp = FORMAT_DATE.parse(timeStr).getTime();
                        break;
                    default:
                }
            } catch (ParseException e) {
                String msg = "Pattern: \"" + pattern + "\", Source: \"" + timeStr + "\"";
                throw new TimeDateException("Time parse FAILED.",
                        TimeDateException.CODE_PARSE_ERROR, msg);
            }
            if (timestamp < 0) {
                throw new TimeDateException("Time period ERROR.",
                        TimeDateException.CODE_PERIOD_ERROR, "Period: " + period);
            }
        }

        public long getTime() {
            if (timestamp < 0) {
                throw new RuntimeException("Customer history model HAS NOT initialized.");
            }
            return timestamp;
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

        public int getPassCount() {
            return passCount;
        }

        public int getEntryHeadCount() {
            return entryHeadCount;
        }
    }
}
