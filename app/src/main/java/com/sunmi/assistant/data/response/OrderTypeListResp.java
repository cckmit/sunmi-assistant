package com.sunmi.assistant.data.response;

import java.util.List;

public class OrderTypeListResp {

    private List<OrderType> order_type_list;

    public List<OrderType> getOrder_type_list() {
        return order_type_list;
    }

    public static class OrderType {

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
