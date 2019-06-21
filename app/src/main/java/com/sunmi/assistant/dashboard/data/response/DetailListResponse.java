package com.sunmi.assistant.dashboard.data.response;

import java.util.List;

public class DetailListResponse {

    private List<DetailItem> detail_list;

    public List<DetailItem> getDetail_list() {
        return detail_list;
    }

    public static class DetailItem {

        private String name;
        private int quantity;

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
