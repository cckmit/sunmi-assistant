package sunmi.common.model;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/7/15.
 */
public class DeviceTypeBean {

    private String brand;
    private String model;
    private List<String> mac;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getMac() {
        return mac;
    }

    public void setMac(List<String> mac) {
        this.mac = mac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}