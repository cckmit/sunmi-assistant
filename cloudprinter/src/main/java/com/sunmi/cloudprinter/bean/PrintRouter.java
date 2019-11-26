package com.sunmi.cloudprinter.bean;

import java.io.Serializable;

public class PrintRouter implements Serializable {
    private String name;
    private boolean hasPwd;
    private String pwd;
    private int rssi;
    private byte[] essid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasPwd() {
        return hasPwd;
    }

    public void setHasPwd(boolean hasPwd) {
        this.hasPwd = hasPwd;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getEssid() {
        return essid;
    }

    public void setEssid(byte[] essid) {
        this.essid = essid;
    }
}
