package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2020-03-04
 */
public class IpcDevice {

    /**
     * shop_id : 10456
     * id : 2211
     * sn : FS101D8BS00087
     * model :
     * device_name : 摄像头设备1
     */

    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("id")
    private int id;
    @SerializedName("sn")
    private String sn;
    @SerializedName("model")
    private String model;
    @SerializedName("device_name")
    private String deviceName;

    public int getShopId() {
        return shopId;
    }

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
