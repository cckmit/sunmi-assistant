package com.sunmi.assistant.data.response;

import java.util.List;

public class OrderPayTypeRankResp {

    private int total_count;
    private float total_amount;
    private List<PayTypeRankItem> purchase_type_list;

    public int getTotal_count() {
        return total_count;
    }

    public float getTotal_amount() {
        return total_amount;
    }

    public List<PayTypeRankItem> getPurchase_type_list() {
        return purchase_type_list;
    }

    public static class PayTypeRankItem {

        private String purchase_type_tag;
        private String purchase_type_name;
        private int count;
        private float amount;

        public String getPurchase_type_tag() {
            return purchase_type_tag;
        }

        public String getPurchase_type_name() {
            return purchase_type_name;
        }

        public int getCount() {
            return count;
        }

        public float getAmount() {
            return amount;
        }
    }
}
