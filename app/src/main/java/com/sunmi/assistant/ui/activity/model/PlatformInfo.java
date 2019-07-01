package com.sunmi.assistant.ui.activity.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by YangShiJie on 2019/6/26.
 */
public class PlatformInfo {

    /**
     * code : 1
     * data : {"merchant_uid":"98408","uid":42652,"status":0,"username":"","mobile":"17621912925","email":"","token":"1cd7d996e922de672d63e990659d7be9","create_shop":{"code":1,"data":[],"msg":""},"company_id":"6611"}
     * msg :
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean implements Serializable {
        /**
         * merchant_uid : 98408
         * uid : 42652
         * status : 0
         * username :
         * mobile : 17621912925
         * email :
         * token : 1cd7d996e922de672d63e990659d7be9
         * create_shop : {"code":1,"data":[],"msg":""}
         * company_id : 6611
         */

        private String merchant_uid;
        private int uid;
        private int status;
        private String username;
        private String mobile;
        private String email;
        private String token;
        private CreateShopBean create_shop;
        private String company_id;

        public String getMerchant_uid() {
            return merchant_uid;
        }

        public void setMerchant_uid(String merchant_uid) {
            this.merchant_uid = merchant_uid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public CreateShopBean getCreate_shop() {
            return create_shop;
        }

        public void setCreate_shop(CreateShopBean create_shop) {
            this.create_shop = create_shop;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public static class CreateShopBean implements Serializable {
            /**
             * code : 1
             * data : []
             * msg :
             */

            private int code;
            private String msg;
            private List<?> data;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public List<?> getData() {
                return data;
            }

            public void setData(List<?> data) {
                this.data = data;
            }
        }
    }
}
