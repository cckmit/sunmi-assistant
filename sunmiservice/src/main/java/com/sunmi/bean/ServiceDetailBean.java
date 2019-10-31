package com.sunmi.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class ServiceDetailBean {
    /**
     * id : 1
     * service_type : 1
     * service_no : 1571709544753362
     * service_name : 7天云存储服务
     * order_no : 1571709544093362
     * product_no : YCC0001
     * device_id : 2224
     * device_sn : SS101D8BS00088
     * device_name :
     * device_model_id : 15
     * device_model : FM010
     * service_duration : 2592000
     * create_time : 1571709544
     * subscribe_time : 1571709544
     * expire_time : 1574301544
     * valid_time : 2571415
     * status : 2
     * renew_status : 1
     * renew_error_code : 5420
     */

    @SerializedName("id")
    private int id;
    @SerializedName("service_type")
    private int serviceType;
    @SerializedName("service_no")
    private String serviceNo;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("order_no")
    private String orderNo;
    @SerializedName("product_no")
    private String productNo;
    @SerializedName("device_id")
    private int deviceId;
    @SerializedName("device_sn")
    private String deviceSn;
    @SerializedName("device_name")
    private String deviceName;
    @SerializedName("device_model_id")
    private int deviceModelId;
    @SerializedName("device_model")
    private String deviceModel;
    @SerializedName("service_duration")
    private int serviceDuration;
    @SerializedName("create_time")
    private int createTime;
    @SerializedName("subscribe_time")
    private int subscribeTime;
    @SerializedName("expire_time")
    private int expireTime;
    @SerializedName("valid_time")
    private int validTime;
    @SerializedName("status")
    private int status;
    @SerializedName("renew_status")
    private int renewStatus;
    @SerializedName("renew_error_code")
    private int renewErrorCode;

    private boolean isBind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(int deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(int subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public int getValidTime() {
        return validTime;
    }

    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRenewStatus() {
        return renewStatus;
    }

    public void setRenewStatus(int renewStatus) {
        this.renewStatus = renewStatus;
    }

    public int getRenewErrorCode() {
        return renewErrorCode;
    }

    public void setRenewErrorCode(int renewErrorCode) {
        this.renewErrorCode = renewErrorCode;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
}
