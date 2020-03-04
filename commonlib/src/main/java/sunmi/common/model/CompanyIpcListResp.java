package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class CompanyIpcListResp {

    @SerializedName("shop_list")
    private List<ShopIpc> shopList;

    public List<ShopIpc> getShopList() {
        return shopList;
    }

    public static class ShopIpc {
        /**
         * shop_id : 10456
         * device_list : [{"id":2211,"sn":"FS101D8BS00087","model":"","device_name":"摄像头设备1"},{"id":2255,"sn":"FS101D8BS00089","model":"","device_name":"摄像头设备666"},"..."]
         */

        @SerializedName("shop_id")
        private int shopId;
        @SerializedName("device_list")
        private List<Device> deviceList;

        public int getShopId() {
            return shopId;
        }

        public List<Device> getDeviceList() {
            return deviceList;
        }
    }

    public static class Device {
        /**
         * id : 2211
         * sn : FS101D8BS00087
         * model :
         * device_name : 摄像头设备1
         */

        @SerializedName("id")
        private int id;
        @SerializedName("sn")
        private String sn;
        @SerializedName("model")
        private String model;
        @SerializedName("device_name")
        private String deviceName;

        public int getId() {
            return id;
        }

        public String getSn() {
            return sn;
        }

        public String getModel() {
            return model;
        }

        public String getDeviceName() {
            return deviceName;
        }
    }
}
