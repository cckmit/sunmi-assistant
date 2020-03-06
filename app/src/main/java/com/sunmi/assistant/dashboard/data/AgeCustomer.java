package com.sunmi.assistant.dashboard.data;

import android.util.SparseArray;

public class AgeCustomer implements Comparable<AgeCustomer> {

    private String shopName;
    private SparseArray<Integer> count;

    public static boolean isDesc;
    public static int ageCode;

    public AgeCustomer(String shopName, SparseArray<Integer> count) {
        this.shopName = shopName;
        this.count = count;
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
