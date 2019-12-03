package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangShiJie
 * @date 2019-12-03
 */
public class CashOrderResp {

    /**
     * id : 24
     * order_no : 1552533654366677
     * order_type : 正常销售
     * order_type_id : 1
     * order_type_tag : payment-order-type-normal
     * purchase_time : 1551912928
     * purchase_type : 支付宝
     * purchase_type_id : 9
     * purchase_type_tag : payment-purchase-type-alipay
     * amount : 10.01
     * total_quantity : 23
     * product_list : [{"name":"apple","quantity":12},{"name":"apple","quantity":12}]
     */

    @SerializedName("id")
    private int id;
    @SerializedName("order_no")
    private String orderNo;
    @SerializedName("order_type")
    private String orderType;
    @SerializedName("order_type_id")
    private String orderTypeId;
    @SerializedName("order_type_tag")
    private String orderTypeTag;
    @SerializedName("purchase_time")
    private int purchaseTime;
    @SerializedName("purchase_type")
    private String purchaseType;
    @SerializedName("purchase_type_id")
    private String purchaseTypeId;
    @SerializedName("purchase_type_tag")
    private String purchaseTypeTag;
    @SerializedName("amount")
    private double amount;
    @SerializedName("total_quantity")
    private int totalQuantity;
    @SerializedName("product_list")
    private List<ProductListBean> productList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(String orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public String getOrderTypeTag() {
        return orderTypeTag;
    }

    public void setOrderTypeTag(String orderTypeTag) {
        this.orderTypeTag = orderTypeTag;
    }

    public int getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(int purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getPurchaseTypeId() {
        return purchaseTypeId;
    }

    public void setPurchaseTypeId(String purchaseTypeId) {
        this.purchaseTypeId = purchaseTypeId;
    }

    public String getPurchaseTypeTag() {
        return purchaseTypeTag;
    }

    public void setPurchaseTypeTag(String purchaseTypeTag) {
        this.purchaseTypeTag = purchaseTypeTag;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public List<ProductListBean> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductListBean> productList) {
        this.productList = productList;
    }

    public static class ProductListBean implements Serializable {
        /**
         * name : apple
         * quantity : 12
         */

        @SerializedName("name")
        private String name;
        @SerializedName("quantity")
        private int quantity;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
