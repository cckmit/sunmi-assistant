package com.sunmi.assistant.data.apresp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author yangShiJie
 * @date 2020-01-09
 */
public class ApConfigResp {

    /**
     * system : {"factory":"0","language":"zh-CN"}
     */

    @SerializedName("system")
    private SystemBean system;

    public SystemBean getSystem() {
        return system;
    }

    public void setSystem(SystemBean system) {
        this.system = system;
    }

    public static class SystemBean implements Serializable {
        /**
         * factory : 0
         * language : zh-CN
         */

        @SerializedName("factory")
        private String factory;
        @SerializedName("language")
        private String language;

        public String getFactory() {
            return factory;
        }

        public void setFactory(String factory) {
            this.factory = factory;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }
}
