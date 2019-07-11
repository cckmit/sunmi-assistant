package com.sunmi.assistant.order.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * @author yinhui
 * @since 2019-07-01
 */
public class OrderInfo implements Parcelable {

    // 订单类型tag
    private static final String ORDER_TYPE_TAG_NORMAL = "payment-order-type-normal";
    private static final String ORDER_TYPE_TAG_REFUNDS = "payment-order-type-refund";

    public static final int ORDER_TYPE_ALL = 0;
    public static final int ORDER_TYPE_NORMAL = 1;
    public static final int ORDER_TYPE_REFUNDS = 2;

    public static final HashMap<String, Integer> ORDER_TYPE_MAP = new HashMap<>();

    static {
        ORDER_TYPE_MAP.put(ORDER_TYPE_TAG_NORMAL, ORDER_TYPE_NORMAL);
        ORDER_TYPE_MAP.put(ORDER_TYPE_TAG_REFUNDS, ORDER_TYPE_REFUNDS);
    }

    private int id;
    private String no;
    private float amount;
    private int orderType;
    private String purchaseType;
    private long purchaseTime;

    public OrderInfo(int id, String no, float amount, int orderType, String purchaseType, long purchaseTime) {
        this.id = id;
        this.no = no;
        this.amount = amount;
        this.orderType = orderType;
        this.purchaseType = purchaseType;
        this.purchaseTime = purchaseTime;
    }

    public int getId() {
        return id;
    }

    public String getNo() {
        return no;
    }

    public float getAmount() {
        return amount;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public boolean isOrderNormal() {
        return orderType == ORDER_TYPE_NORMAL;
    }

    protected OrderInfo(Parcel in) {
        id = in.readInt();
        no = in.readString();
        amount = in.readFloat();
        orderType = in.readInt();
        purchaseType = in.readString();
        purchaseTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(no);
        dest.writeFloat(amount);
        dest.writeInt(orderType);
        dest.writeString(purchaseType);
        dest.writeLong(purchaseTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

}
