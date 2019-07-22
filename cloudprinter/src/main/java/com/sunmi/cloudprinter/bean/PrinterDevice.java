package com.sunmi.cloudprinter.bean;

import java.io.Serializable;

public class PrinterDevice implements Serializable {
    private String name; // 蓝牙设备的名称
    private String address; // 蓝牙设备的MAC地址
    private String sn;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
