package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

public class AgeCustomer implements Comparable<AgeCustomer> {

    private int shopId;
    private String shopName;
    private SparseArray<Integer> count;

    public static boolean isDesc = true;
    public static int ageCode;

    public AgeCustomer(int shopId, String shopName, SparseArray<Integer> count) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.count = count;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public int getAgeCount() {
        return count.get(ageCode) != null ? count.get(ageCode) : 0;
    }

    @Override
    public int compareTo(AgeCustomer o) {
        if (isDesc) {
            return o.getAgeCount() - getAgeCount();
        } else {
            return getAgeCount() - o.getAgeCount();
        }
    }
}
