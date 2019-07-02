package com.sunmi.assistant.ui.activity.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by YangShiJie on 2019/6/27.
 */
public class AuthStoreInfo {
    private List<SaasUserInfoListBean> SaasUserInfoList;

    public List<SaasUserInfoListBean> getSaasUserInfoList() {
        return SaasUserInfoList;
    }

    public void setSaasUserInfoList(List<SaasUserInfoListBean> SaasUserInfoList) {
        this.SaasUserInfoList = SaasUserInfoList;
    }

    public static class SaasUserInfoListBean implements Serializable {
        /**
         * shop_no : S001300002
         * shop_name : 其实我是螺蛳粉
         * address :
         * contact : 15366658110
         * saas_name : 科脉赢钱
         * saas_source : 5
         */

        private String shop_no;
        private String shop_name;
        private String address;
        private String contact;
        private String saas_name;
        private int saas_source;

        public String getShop_no() {
            return shop_no;
        }

        public void setShop_no(String shop_no) {
            this.shop_no = shop_no;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getSaas_name() {
            return saas_name;
        }

        public void setSaas_name(String saas_name) {
            this.saas_name = saas_name;
        }

        public int getSaas_source() {
            return saas_source;
        }

        public void setSaas_source(int saas_source) {
            this.saas_source = saas_source;
        }
    }


//    private String shopName;
//    private String platform;
//    private int shopId;
//
//    public String getShopName() {
//        return shopName;
//    }
//
//    public void setShopName(String shopName) {
//        this.shopName = shopName;
//    }
//
//    public String getPlatform() {
//        return platform;
//    }
//
//    public void setPlatform(String platform) {
//        this.platform = platform;
//    }
//
//    public int getShopId() {
//        return shopId;
//    }
//
//    public void setShopId(int shopId) {
//        this.shopId = shopId;
//    }
//

}
