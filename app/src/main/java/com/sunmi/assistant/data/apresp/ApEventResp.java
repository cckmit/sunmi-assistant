package com.sunmi.assistant.data.apresp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangShiJie
 * @date 2020-01-08
 */
public class ApEventResp {

    /**
     * msg_id :
     * params : [{"event":"0x211e","param":{"device_list":[{"Sn":"W101191D02259","ShopId":8316,"ActiveStatus":1}]}}]
     */

    @SerializedName("msg_id")
    private String msgId;
    @SerializedName("params")
    private List<ParamsBean> params;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public List<ParamsBean> getParams() {
        return params;
    }

    public void setParams(List<ParamsBean> params) {
        this.params = params;
    }

    public static class ParamsBean implements Serializable {
        /**
         * event : 0x211e
         * param : {"device_list":[{"Sn":"W101191D02259","ShopId":8316,"ActiveStatus":1}]}
         */

        @SerializedName("event")
        private String event;
        @SerializedName("param")
        private ParamBean param;

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public ParamBean getParam() {
            return param;
        }

        public void setParam(ParamBean param) {
            this.param = param;
        }

        public static class ParamBean implements Serializable {
            @SerializedName("device_list")
            private List<DeviceListBean> deviceList;
            @SerializedName("sn")
            private String sn;

            public String getSn() {
                return sn;
            }

            public void setSn(String sn) {
                this.sn = sn;
            }

            public List<DeviceListBean> getDeviceList() {
                return deviceList;
            }

            public void setDeviceList(List<DeviceListBean> deviceList) {
                this.deviceList = deviceList;
            }

            public static class DeviceListBean implements Serializable {
                /**
                 * Sn : W101191D02259
                 * ShopId : 8316
                 * ActiveStatus : 1
                 */

                @SerializedName("Sn")
                private String Sn;
                @SerializedName("ShopId")
                private int ShopId;
                @SerializedName("ActiveStatus")
                private int ActiveStatus;

                public String getSn() {
                    return Sn;
                }

                public void setSn(String Sn) {
                    this.Sn = Sn;
                }

                public int getShopId() {
                    return ShopId;
                }

                public void setShopId(int ShopId) {
                    this.ShopId = ShopId;
                }

                public int getActiveStatus() {
                    return ActiveStatus;
                }

                public void setActiveStatus(int ActiveStatus) {
                    this.ActiveStatus = ActiveStatus;
                }
            }
        }
    }
}
