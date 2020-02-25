package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
public class ServiceResp {

    @SerializedName("device_list")
    private List<Info> list;

    public List<Info> getList() {
        return list;
    }

    public static class Info {
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
        @SerializedName("service_tag")
        private int serviceTag;
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

        public String getServiceName() {
            return serviceName;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public String getDeviceSn() {
            return deviceSn;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public int getDeviceModelId() {
            return deviceModelId;
        }

        public String getDeviceModel() {
            return deviceModel;
        }

        public int getServiceDuration() {
            return serviceDuration;
        }

        public int getActiveStatus() {
            return activeStatus;
        }

        public int getActiveTime() {
            return activeTime;
        }

        public int getActiveExpireTime() {
            return activeExpireTime;
        }

        public int getServiceTag() {
            return serviceTag;
        }

        public int getStatus() {
            return status;
        }

        public int getExpireTime() {
            return expireTime;
        }

        public int getValidTime() {
            return validTime;
        }

        public String getImgUrl() {
            return imgUrl;
        }
    }
}
