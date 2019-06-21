package com.sunmi.assistant.dashboard.data.response;

import java.util.List;

public class PurchaseTypeListResponse {

    private List<PurchaseType> purchase_type_list;

    public List<PurchaseType> getPurchase_type_list() {
        return purchase_type_list;
    }

    public static class PurchaseType {

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
