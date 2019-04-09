package com.sunmi.ipc.model;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class WifiListResp {
    private List<ScanResultsBean> scan_results;

    public List<ScanResultsBean> getScan_results() {
        return scan_results;
    }

    public void setScan_results(List<ScanResultsBean> scan_results) {
        this.scan_results = scan_results;
    }

    public static class ScanResultsBean {
        /**
         * ssid : SUNMI_WBU
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
