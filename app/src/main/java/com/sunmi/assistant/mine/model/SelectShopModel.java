package com.sunmi.assistant.mine.model;

import java.util.Locale;

import sunmi.common.model.AuthStoreInfo;

/**
 * @author yinhui
 * @date 2019-08-06
 */
public class SelectShopModel {

    private boolean isChecked;

    private int shopId;
    private String shopName;
    private String shopNo;
    private int saasSource;
    private String saasName;

    public SelectShopModel(AuthStoreInfo.SaasUserInfoListBean bean) {
        this(bean, false);
    }

    public SelectShopModel(AuthStoreInfo.SaasUserInfoListBean bean, boolean isChecked) {
        this.isChecked = isChecked;
        this.shopName = bean.getShop_name();
        this.shopNo = bean.getShop_no();
        this.saasSource = bean.getSaas_source();
        this.saasName = bean.getSaas_name();
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopNo() {
        return shopNo;
    }

    public int getSaasSource() {
        return saasSource;
    }

    public String getSaasName() {
        return saasName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public void updateName(int no) {
        this.shopName = String.format(Locale.getDefault(), "%s_%02d", this.shopName, no);
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
