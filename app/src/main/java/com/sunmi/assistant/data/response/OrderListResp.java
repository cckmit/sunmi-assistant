package com.sunmi.assistant.data.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class OrderListResp {

    private List<OrderItem> order_list;
    private int total_count;

    public List<OrderItem> getOrder_list() {
        return order_list;
    }

    public int getTotal_count() {
        return total_count;
    }

    public static class OrderItem implements Parcelable {

        private int id;
        private String order_no;
        private String order_type;
        private int order_type_id;
        private String ipc_device_name;
        private String payment_device_name;
        private String payment_device_sn;
        private String purchase_type;
        private int purchase_type_id;
        private long purchase_time;
        private float amount;

        public int getId() {
            return id;
        }

        public String getOrder_no() {
            return order_no;
        }

        public String getOrder_type() {
            return order_type;
        }

        public int getOrder_type_id() {
            return order_type_id;
        }

        public String getIpc_device_name() {
            return ipc_device_name;
        }

        public String getPayment_device_name() {
            return payment_device_name;
        }

        public String getPayment_device_sn() {
            return payment_device_sn;
        }

        public String getPurchase_type() {
            return purchase_type;
        }

        public int getPurchase_type_id() {
            return purchase_type_id;
        }

        public long getPurchase_time() {
            return purchase_time;
        }

        public float getAmount() {
            return amount;
        }


        protected OrderItem(Parcel in) {
            id = in.readInt();
            order_no = in.readString();
            order_type = in.readString();
            order_type_id = in.readInt();
            ipc_device_name = in.readString();
            payment_device_name = in.readString();
            payment_device_sn = in.readString();
            purchase_type = in.readString();
            purchase_type_id = in.readInt();
            purchase_time = in.readLong();
            amount = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(order_no);
            dest.writeString(order_type);
            dest.writeInt(order_type_id);
            dest.writeString(ipc_device_name);
            dest.writeString(payment_device_name);
            dest.writeString(payment_device_sn);
            dest.writeString(purchase_type);
            dest.writeInt(purchase_type_id);
            dest.writeLong(purchase_time);
            dest.writeFloat(amount);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
            @Override
            public OrderItem createFromParcel(Parcel in) {
                return new OrderItem(in);
            }

            @Override
            public OrderItem[] newArray(int size) {
                return new OrderItem[size];
            }
        };

    }
}
