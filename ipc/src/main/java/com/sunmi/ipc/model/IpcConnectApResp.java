package com.sunmi.ipc.model;

import java.io.Serializable;

/**
 * Created by YangShiJie on 2019/7/17.
 */
public class IpcConnectApResp {

    /**
     * wireless : {"ssid":"default","key_mgmt":"WPA-PSK"}
     */

    private WirelessBean wireless;

    public WirelessBean getWireless() {
        return wireless;
    }

    public void setWireless(WirelessBean wireless) {
        this.wireless = wireless;
    }

    public static class WirelessBean implements Serializable {
        /**
         * ssid : default
         * key_mgmt : WPA-PSK
         */

        private String ssid;
        private String key_mgmt;

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getKey_mgmt() {
            return key_mgmt;
        }

        public void setKey_mgmt(String key_mgmt) {
            this.key_mgmt = key_mgmt;
        }
    }
}
