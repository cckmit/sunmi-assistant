package com.sunmi.assistant.dashboard.data;

public class NewOldCustomer implements Comparable<NewOldCustomer> {

    private String shopName;
    private int newCount;
    private int oldCount;

    public static boolean isDesc;
    public static boolean isSortByOld;

    public NewOldCustomer(String shopName, int newCount, int oldCount) {
        this.shopName = shopName;
        this.newCount = newCount;
        this.oldCount = oldCount;
    }

    public String getShopName() {
        return shopName;
    }

    public int getCount() {
        if (isSortByOld) {
            return oldCount;
        } else {
            return newCount;
        }
    }

    @Override
    public int compareTo(NewOldCustomer o) {
        if (isSortByOld) {
            if (isDesc) {
                return o.oldCount - oldCount;
            } else {
                return oldCount - o.oldCount;
            }
        } else {
            if (isDesc) {
                return o.newCount - newCount;
            } else {
                return newCount - o.newCount;
            }
        }
    }
}
