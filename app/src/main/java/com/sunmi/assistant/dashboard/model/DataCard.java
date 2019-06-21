package com.sunmi.assistant.dashboard.model;

import com.sunmi.assistant.dashboard.DataRefreshHelper;

/**
 * 小卡片数据
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class DataCard extends BaseRefreshCard<DataCard> {
    public String title;
    public float data = 1000;
    public String dataFormat;
    public String trendName;
    public float trendData;

    public DataCard(String title, String dataFormat,
                    DataRefreshHelper<DataCard> helper) {
        super(helper);
        this.title = title;
        this.dataFormat = dataFormat;
    }
}
