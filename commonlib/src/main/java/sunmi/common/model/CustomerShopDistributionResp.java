package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerShopDistributionResp {

    @SerializedName("shop_list")
    private List<ShopListBean> shopList;

    public List<ShopListBean> getShopList() {
        return shopList;
    }

    public void setShopList(List<ShopListBean> shopList) {
        this.shopList = shopList;
    }

    public static class ShopListBean {
        /**
         * shop_id : 10350
         * shop_name : 上海杨浦区大学路683号商米科技股份有限公司门店2
         * count_list : [{"age_range_code":1,"gender":1,"uniq_count":12},{"age_range_code":1,"gender":2,"uniq_count":11},{"age_range_code":8,"gender":1,"uniq_count":0},{"age_range_code":8,"gender":2,"uniq_count":0}]
         */

        @SerializedName("shop_id")
        private int shopId;
        @SerializedName("shop_name")
        private String shopName;
        @SerializedName("count_list")
        private List<CountListBean> countList;

        public int getShopId() {
            return shopId;
        }

        public String getShopName() {
            return shopName;
        }


        public List<CountListBean> getCountList() {
            return countList;
        }

        public void setCountList(List<CountListBean> countList) {
            this.countList = countList;
        }

    }
}
