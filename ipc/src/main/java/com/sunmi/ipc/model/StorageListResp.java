package com.sunmi.ipc.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
public class StorageListResp {

    @SerializedName("device_list")
    private List<DeviceListBean> deviceList;

    public List<DeviceListBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceListBean> deviceList) {
        this.deviceList = deviceList;
    }

    public static class DeviceListBean {
        /**
         * device_id : 123
         * device_sn : FS1234
         * device_name : 设备名称
         * status : 1
         * expire_time : 1570764698
         * valid_time : 12345678
         * img_url : http://cdn.image.com
         */

        @SerializedName("device_id")
        private int deviceId;
        @SerializedName("device_sn")
        private String deviceSn;
        @SerializedName("device_name")
        private String deviceName;
        @SerializedName("status")
        private int status;
        @SerializedName("expire_time")
        private int expireTime;
        @SerializedName("valid_time")
        private int validTime;
        @SerializedName("img_url")
        private String imgUrl;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }
}
