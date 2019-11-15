package com.sunmi.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-12.
 */
public class BundleServiceMsg {

    @SerializedName("subscription_list")
    private List<SubscriptionListBean> subscriptionList;

    public List<SubscriptionListBean> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<SubscriptionListBean> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public static class SubscriptionListBean {
        /**
         * service_type : 1
         * service_name : 7天云存储服务
         * device_id : 2224
         * device_sn : SS101D8BS00088
         * active_status : 1
         * active_expire_time : 1574301544
         */

        @SerializedName("service_type")
        private int serviceType;
        @SerializedName("service_name")
        private String serviceName;
        @SerializedName("device_id")
        private int deviceId;
        @SerializedName("device_sn")
        private String deviceSn;
        @SerializedName("active_status")
        private int activeStatus;
        @SerializedName("active_expire_time")
        private int activeExpireTime;

        public int getServiceType() {
            return serviceType;
        }

        public void setServiceType(int serviceType) {
            this.serviceType = serviceType;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
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

        public int getActiveStatus() {
            return activeStatus;
        }

        public void setActiveStatus(int activeStatus) {
            this.activeStatus = activeStatus;
        }

        public int getActiveExpireTime() {
            return activeExpireTime;
        }

        public void setActiveExpireTime(int activeExpireTime) {
            this.activeExpireTime = activeExpireTime;
        }
    }
}
