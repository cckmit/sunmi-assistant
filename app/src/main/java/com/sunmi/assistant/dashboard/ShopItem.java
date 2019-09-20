package com.sunmi.assistant.dashboard;

/**
 * @author yinhui
 * @date 2019-09-20
 */
public class ShopItem {
    private int shopId;
    private String shopName;
    private boolean saasExist;
    private boolean isChecked;

    public ShopItem(int shopId, String shopName, boolean saasExist) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.saasExist = saasExist;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public boolean isSaasExist() {
        return saasExist;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
