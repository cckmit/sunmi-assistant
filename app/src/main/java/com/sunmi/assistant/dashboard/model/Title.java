package com.sunmi.assistant.dashboard.model;

/**
 * Title数据，包括公司名和门店名
 *
 * @author yinhui
 * @since 2019-06-13
 */
public class Title extends BaseRefreshCard<Title> {
    public String companyName;
    public String shopName;

    public Title() {
        super(null);
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
