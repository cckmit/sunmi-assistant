package com.sunmi.assistant.dashboard.data.response;

import java.util.List;

public class ShopListResponse {

    private List<ShopInfo> shop_list;
    private int total_count;
    private int return_count;

    public List<ShopInfo> getShop_list() {
        return shop_list;
    }

    public int getTotal_count() {
        return total_count;
    }

    public int getReturn_count() {
        return return_count;
    }

    public static class ShopInfo {

        private int shop_id;
        private String shop_name;
        private int type_one;
        private int type_two;
        private String type_name;
        private int business_status;
        private int province;
        private int city;
        private int area;
        private String address;
        private String region;
        private String business_hours;
        private String contact_person;
        private String contact_tel;
        private String contact_email;
        private int created_time;
        private int modified_time;

        public int getShop_id() {
            return shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public int getType_one() {
            return type_one;
        }

        public int getType_two() {
            return type_two;
        }

        public String getType_name() {
            return type_name;
        }

        public int getBusiness_status() {
            return business_status;
        }

        public int getProvince() {
            return province;
        }

        public int getCity() {
            return city;
        }

        public int getArea() {
            return area;
        }

        public String getAddress() {
            return address;
        }

        public String getRegion() {
            return region;
        }

        public String getBusiness_hours() {
            return business_hours;
        }

        public String getContact_person() {
            return contact_person;
        }

        public String getContact_tel() {
            return contact_tel;
        }

        public String getContact_email() {
            return contact_email;
        }

        public int getCreated_time() {
            return created_time;
        }

        public int getModified_time() {
            return modified_time;
        }
    }
}
