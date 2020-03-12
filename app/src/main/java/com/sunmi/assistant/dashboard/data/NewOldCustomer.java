package com.sunmi.assistant.dashboard.data;

public class NewOldCustomer implements Comparable<NewOldCustomer> {

    private int shopId;
    private String shopName;
    private int newCount;
    private int oldCount;

    public static boolean isDesc = true;
    public static boolean isSortByNew;

    public NewOldCustomer(int shopId, String shopName, int newCount, int oldCount) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.newCount = newCount;
        this.oldCount = oldCount;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public int getCount() {
        if (isSortByNew) {
            return newCount;
        } else {
            return oldCount;
        }
    }

    @Override
    public int compareTo(NewOldCustomer o) {
        if (isSortByNew) {
            if (isDesc) {
                return o.newCount - newCount;
            } else {
                return newCount - o.newCount;
            }
        } else {
            if (isDesc) {
                return o.oldCount - oldCount;
            } else {
                return oldCount - o.oldCount;
            }
        }
    }
}
