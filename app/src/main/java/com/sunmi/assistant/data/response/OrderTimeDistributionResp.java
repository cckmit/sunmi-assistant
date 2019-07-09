package com.sunmi.assistant.data.response;

import java.util.List;

public class OrderTimeDistributionResp {

    private List<PeriodItem> order_list;

    public List<PeriodItem> getOrder_list() {
        return order_list;
    }

    public static class PeriodItem {

        private long time;
        private int count;
        private float amount;

        public long getTime() {
            return time;
        }

        public int getCount() {
            return count;
        }

        public float getAmount() {
            return amount;
        }
    }
}
