package com.sunmi.ipc.model;

import java.io.Serializable;

/**
 * @author yinhui
 * @date 2019-07-15
 */
public class IpcNewFirmwareResp implements Serializable {

    private String latest_bin_version;
    private int upgrade_required;
    private String url;

    /**
     * 最新版本号
     */
    public String getLatest_bin_version() {
        return latest_bin_version;
    }

    /**
     * 是否需要更新，0-不需要，1-需要
     */
    public int getUpgrade_required() {
        return upgrade_required;
    }

    /**
     * 固件地址
     */
    public String getUrl() {
        return url;
    }
}
