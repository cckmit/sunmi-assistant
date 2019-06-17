package com.sunmi.assistant.dashboard.model;

import com.sunmi.assistant.dashboard.DashboardContract;

/**
 * @author yinhui
 * @since 2019-06-14
 */
public class DashboardConfig {
    public String companyName;
    public String storeName;
    public int timeSpan = DashboardContract.TIME_SPAN_TODAY;

    public DashboardConfig(String companyName, String storeName) {
        this.companyName = companyName;
        this.storeName = storeName;
    }

    public DashboardConfig(String companyName, String storeName, int timeSpan) {
        this.companyName = companyName;
        this.storeName = storeName;
        this.timeSpan = timeSpan;
    }
}
