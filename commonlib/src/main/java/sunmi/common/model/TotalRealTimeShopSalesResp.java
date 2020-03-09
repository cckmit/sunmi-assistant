package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2020-03-09.
 */
public class TotalRealTimeShopSalesResp {

    @SerializedName("shop_list")
    private List<Item> list;

    public static boolean isDesc;

    public List<Item> getList() {
        return list;
    }

    public void setList(List<Item> list) {
        this.list = list;
    }


    public static class Item implements Comparable<Item> {
        /**
         * shop_id : 10350
         * shop_name : 上海杨浦区大学路683号商米科技股份有限公司门店2
         * total_count : 487
         */

        @SerializedName("shop_id")
        private String shopId;
        @SerializedName("shop_name")
        private String shopName;

        @SerializedName("order_count")
        private int orderCount;
        @SerializedName("order_amount")
        private double orderAmount;


        public String getShopId() {
            return shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public double getOrderAmount() {
            return orderAmount;
        }

        @Override
        public int compareTo(Item o) {
            if (isDesc) {
                return Double.compare(o.orderAmount, orderAmount);
            } else {
                return Double.compare(orderAmount, o.orderAmount);
            }
        }
    }
}
