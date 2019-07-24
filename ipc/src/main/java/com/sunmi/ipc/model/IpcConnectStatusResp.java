package com.sunmi.ipc.model;

import java.io.Serializable;

/**
 * Created by YangShiJie on 2019/7/17.
 */
public class IpcConnectStatusResp {


    /**
     * wireless : {"connect_status":"0"}
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
         * connect_status : 0
         */

        private String connect_status;

        public String getConnect_status() {
            return connect_status;
        }

        public void setConnect_status(String connect_status) {
            this.connect_status = connect_status;
        }
    }
}
