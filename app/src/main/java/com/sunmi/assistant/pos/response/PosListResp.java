package com.sunmi.assistant.pos.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangShiJie
 * @date 2019-11-20
 */
public class PosListResp {

    @SerializedName("device_list")
    private List<DeviceListBean> deviceList;

    public List<DeviceListBean> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<DeviceListBean> deviceList) {
        this.deviceList = deviceList;
    }

    public static class DeviceListBean implements Serializable {
        /**
         * sn : 7102V01015290208
         * model : T1
         * model_detail : T1
         * channel_id : 10000289
         * img_path : http://test.cdn.com/a.jpg
         * active_status : 0
         */

        @SerializedName("sn")
        private String sn;
        @SerializedName("model")
        private String model;
        @SerializedName("model_detail")
        private String modelDetail;
        @SerializedName("channel_id")
        private int channelId;
        @SerializedName("img_path")
        private String imgPath;
        @SerializedName("active_status")
        private int activeStatus;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getModelDetail() {
            return modelDetail;
        }

        public void setModelDetail(String modelDetail) {
            this.modelDetail = modelDetail;
        }

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public int getActiveStatus() {
            return activeStatus;
        }

        public void setActiveStatus(int activeStatus) {
            this.activeStatus = activeStatus;
        }
    }
}
