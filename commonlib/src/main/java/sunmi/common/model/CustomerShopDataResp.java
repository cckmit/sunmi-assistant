package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerShopDataResp {

    @SerializedName("shop_list")
    private List<Item> shopList;

    public List<Item> getShopList() {
        return shopList;
    }

    public void setShopList(List<Item> shopList) {
        this.shopList = shopList;
    }

    public static class Item {
        /**
         * shop_id : 10350
         * shop_name : 上海杨浦区大学路683号商米科技股份有限公司门店2
         * total_count : 487
         */

        @SerializedName("shop_id")
        private String shopId;
        @SerializedName("shop_name")
        private String shopName;
        @SerializedName("total_count")
        private String totalCount;

        private int orderCount;
        @SerializedName("order_amount")
        private double orderAmount;

        public String getShopId() {
            return shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public double getOrderAmount() {
            return orderAmount;
        }
    }
}
