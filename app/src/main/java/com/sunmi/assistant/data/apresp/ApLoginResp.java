package com.sunmi.assistant.data.apresp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author yangShiJie
 * @date 2020-01-09
 */
public class ApLoginResp {

    /**
     * account : {"token":"848245647a59a79ded66296b6fcbc5a0"}
     */

    @SerializedName("account")
    private AccountBean account;

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }

    public static class AccountBean implements Serializable {
        /**
         * token : 848245647a59a79ded66296b6fcbc5a0
         */

        @SerializedName("token")
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
