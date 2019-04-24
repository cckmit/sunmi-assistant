package sunmi.common.model;

import java.io.Serializable;

/**
 * Description:
 * Created by bruce on 2019/4/1.
 */
public class SunmiDevice implements Serializable {

    /**
     * ip : xxxxxxxx
     * mac : xxxxxxxx
     * firmware : xxxxxxxx
     * name : xxxxxxxx
     * model : xxxxxxxx
     * type : xxxxxxxx
     * networkrol : xxxxxxxx
     * factory : xxxxxxxx
     * deviceid : xxxxxxxx
     */

    private String ip;
    private String mac;
    private String firmware;
    private String name;
    private String model;
    private String type;
    private String networkrol;
    private String factory;
    private String deviceid;
    private int status;
    private String network;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNetworkrol() {
        return networkrol;
    }

    public void setNetworkrol(String networkrol) {
        this.networkrol = networkrol;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

}
