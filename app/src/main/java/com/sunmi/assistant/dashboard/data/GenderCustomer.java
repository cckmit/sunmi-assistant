package com.sunmi.assistant.dashboard.data;

public class GenderCustomer implements Comparable<GenderCustomer> {

    private int shopId;
    private String shopName;
    private int maleCount;
    private int femaleCount;

    public static boolean isDesc = true;
    public static boolean isSortByMale;

    public GenderCustomer(int shopId, String shopName, int maleCount, int femaleCount) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.maleCount = maleCount;
        this.femaleCount = femaleCount;
    }

    public int getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public int getCount() {
        if (isSortByMale) {
            return maleCount;
        } else {
            return femaleCount;
        }
    }

    @Override
    public int compareTo(GenderCustomer o) {
        if (isSortByMale) {
            if (isDesc) {
                return o.maleCount - maleCount;
            } else {
                return maleCount - o.maleCount;
            }
        } else {
            if (isDesc) {
                return o.femaleCount - femaleCount;
            } else {
                return femaleCount - o.femaleCount;
            }
        }
    }
}
