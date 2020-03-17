package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

public class SaleDataResp {

    /**
     * latest_order_count : 759
     * latest_order_amount : 15689.39
     * order_count : 759
     * early_order_count : 800
     * order_amount : 15689.39
     * early_order_amount : 16000.7
     */

    @SerializedName("latest_order_count")
    private int latestOrderCount;
    @SerializedName("latest_order_amount")
    private double latestOrderAmount;

    @SerializedName("order_count")
    private int orderCount;
    @SerializedName("early_order_count")
    private int earlyOrderCount;
    @SerializedName("order_amount")
    private double orderAmount;
    @SerializedName("early_order_amount")
    private double earlyOrderAmount;

    public int getLatestOrderCount() {
        return latestOrderCount;
    }

    public void setLatestOrderCount(int latestOrderCount) {
        this.latestOrderCount = latestOrderCount;
    }

    public double getLatestOrderAmount() {
        return latestOrderAmount;
    }

    public void setLatestOrderAmount(double latestOrderAmount) {
        this.latestOrderAmount = latestOrderAmount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getEarlyOrderCount() {
        return earlyOrderCount;
    }

    public void setEarlyOrderCount(int earlyOrderCount) {
        this.earlyOrderCount = earlyOrderCount;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public double getEarlyOrderAmount() {
        return earlyOrderAmount;
    }

    public void setEarlyOrderAmount(double earlyOrderAmount) {
        this.earlyOrderAmount = earlyOrderAmount;
    }
}
