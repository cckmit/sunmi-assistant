package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.List;

import sunmi.common.exception.TimeDateException;

/**
 * @author yinhui
 * @date 2019-09-18
 */
public class CustomerRateResp {

    @SerializedName("count_list")
    private List<Item> countList;

    public List<Item> getCountList() {
        return countList;
    }

    /**
     * @param period 是 number 1：今日，2：本周，3：本月，4：昨日
     */
    public void init(int period) throws TimeDateException {
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

        private static final long MILLIS_PER_HOUR = 3600000;
        private static final long MILLIS_PER_DAY = 3600000 * 24;

        private static Calendar temp = Calendar.getInstance();

        /**
         * time : 1
         * order_count : 11
         * passenger_flow_count : 123
         */

        private long timestamp = -1;
        @SerializedName("time")
        private int timeIndex;
        @SerializedName("order_count")
        private int orderCount;
        @SerializedName("passenger_flow_count")
        private int passengerFlowCount;

        /**
         * @param period 是 number 1：今日，2：本周，3：本月，4：昨日
         */
        private void init(int period) throws TimeDateException {
            if (timestamp > 0) {
                return;
            }
            temp.setTimeInMillis(System.currentTimeMillis());
            int year = temp.get(Calendar.YEAR);
            int month = temp.get(Calendar.MONTH);
            int day = temp.get(Calendar.DATE);
            temp.clear();

            if (period == TIME_PERIOD_TODAY) {
                temp.set(year, month, day);
                timestamp = temp.getTimeInMillis() + (timeIndex - 1) * MILLIS_PER_HOUR;

            } else if (period == TIME_PERIOD_YESTERDAY) {
                temp.set(year, month, day);
                temp.add(Calendar.DATE, -1);
                timestamp = temp.getTimeInMillis() + (timeIndex - 1) * MILLIS_PER_HOUR;

            } else if (period == TIME_PERIOD_WEEK) {
                temp.setFirstDayOfWeek(Calendar.MONDAY);
                temp.set(year, month, day);
                int dayOfWeek = temp.get(Calendar.DAY_OF_WEEK);
                int offset = temp.getFirstDayOfWeek() - dayOfWeek;
                temp.add(Calendar.DATE, offset > 0 ? offset - 7 : offset);
                timestamp = temp.getTimeInMillis() + (timeIndex - 1) * MILLIS_PER_DAY;

            } else if (period == TIME_PERIOD_MONTH) {
                temp.set(year, month, timeIndex);
                timestamp = temp.getTimeInMillis();
            }
            if (timestamp < 0) {
                throw new TimeDateException("Time period ERROR.",
                        TimeDateException.CODE_PERIOD_ERROR, "Period: " + period);
            }
        }

        public long getTime() {
            if (timestamp < 0) {
                throw new RuntimeException("Customer rate model HAS NOT initialized.");
            }
            return timestamp;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public int getPassengerFlowCount() {
            return passengerFlowCount;
        }

    }
}
