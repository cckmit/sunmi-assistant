package com.sunmi.assistant.data.response;

import java.util.List;

public class OrderListResponse {

    private List<OrderItem> order_list;
    private int total_count;

    public List<OrderItem> getOrder_list() {
        return order_list;
    }

    public int getTotal_count() {
        return total_count;
    }

    public static class OrderItem {

        private int id;
        private String order_no;
        private String order_type;
        private int order_type_id;
        private String ipc_device_name;
        private String payment_device_name;
        private String payment_device_sn;
        private String purchase_type;
        private int purchase_type_id;
        private long purchase_time;
        private float amount;

        public int getId() {
            return id;
        }

        public String getOrder_no() {
            return order_no;
        }

        public String getOrder_type() {
            return order_type;
        }

        public int getOrder_type_id() {
            return order_type_id;
        }

        public String getIpc_device_name() {
            return ipc_device_name;
        }

        public String getPayment_device_name() {
            return payment_device_name;
        }

        public String getPayment_device_sn() {
            return payment_device_sn;
        }

        public String getPurchase_type() {
            return purchase_type;
        }

        public int getPurchase_type_id() {
            return purchase_type_id;
        }

        public long getPurchase_time() {
            return purchase_time;
        }

        public float getAmount() {
            return amount;
        }
    }
}
