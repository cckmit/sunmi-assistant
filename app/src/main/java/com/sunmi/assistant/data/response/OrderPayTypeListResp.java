package com.sunmi.assistant.data.response;

import java.util.List;

public class OrderPayTypeListResp {

    private List<PayType> purchase_type_list;

    public List<PayType> getPurchase_type_list() {
        return purchase_type_list;
    }

    public static class PayType {

        private int id;
        private String tag;
        private String name;

        public int getId() {
            return id;
        }

        public String getTag() {
            return tag;
        }

        public String getName() {
            return name;
        }
    }

}
