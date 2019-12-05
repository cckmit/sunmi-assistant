package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
public class ServiceListResp {

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
         * service_type : 1
         * service_name : 7天云存储服务
         * device_id : 2224
         * device_sn : SS101D8BS00088
         * device_name : 设备名称
         * device_model_id : 15
         * device_model : FM010
         * service_duration : 2592000
         * active_status : 1
         * active_time : 1571709544
         * active_expire_time : 1574301544
         * status : 1
         * expire_time : 1570764698
         * valid_time : 12345678
         * img_url : http://cdn.image.com
         */

        @SerializedName("service_type")
        private int serviceType;
        @SerializedName("service_name")
        private String serviceName;
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
        @SerializedName("active_status")
        private int activeStatus;
        @SerializedName("active_time")
        private int activeTime;
        @SerializedName("active_expire_time")
        private int activeExpireTime;
        @SerializedName("status")
        private int status;
        @SerializedName("expire_time")
        private int expireTime;
        @SerializedName("valid_time")
        private int validTime;
        @SerializedName("img_url")
        private String imgUrl;

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

        public int getActiveStatus() {
            return activeStatus;
        }

        public void setActiveStatus(int activeStatus) {
            this.activeStatus = activeStatus;
        }

        public int getActiveTime() {
            return activeTime;
        }

        public void setActiveTime(int activeTime) {
            this.activeTime = activeTime;
        }

        public int getActiveExpireTime() {
            return activeExpireTime;
        }

        public void setActiveExpireTime(int activeExpireTime) {
            this.activeExpireTime = activeExpireTime;
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
