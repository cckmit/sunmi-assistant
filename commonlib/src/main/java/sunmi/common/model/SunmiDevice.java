package sunmi.common.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Description:
 * Created by bruce on 2019/4/1.
 */
public class SunmiDevice extends DataSupport implements Serializable {

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
    private String deviceid;
    private int serverId;
    private String ip;//设备的ip
    private String mac;
    private String firmware;
    private String name;
    private String model;
    private String type;
    private String networkrol;
    private String factory;
    private String network;
    private String token;//绑定设备使用
    private String uid;
    private int channelId;

    @Column(defaultValue = "3")
    private int status;

    private int shopId;

    @Column(ignore = true)
    private boolean isSelected;

    public String getIp() {
        return "https://" + ip + "/api/";//192.168.100.159/api/192.168.103.122
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getId() {
        return serverId;
    }

    public void setId(int id) {
        this.serverId = id;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

}
