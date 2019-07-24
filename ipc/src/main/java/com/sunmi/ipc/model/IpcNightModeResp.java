package com.sunmi.ipc.model;

/**
 * Created by YangShiJie on 2019/7/18.
 */
public class IpcNightModeResp {

    /**
     * led_indicator : 1  0:关闭/1:开启
     * night_mode : 2  夜视模式 0:始终关闭/1:始终开启/2:自动切换
     * rotation : 0  0:关闭/1:开启
     * sn : SS101D8BS00115
     */

    private int led_indicator;
    private int night_mode;
    private int rotation;
    private String sn;

    public int getLed_indicator() {
        return led_indicator;
    }

    public void setLed_indicator(int led_indicator) {
        this.led_indicator = led_indicator;
    }

    public int getNight_mode() {
        return night_mode;
    }

    public void setNight_mode(int night_mode) {
        this.night_mode = night_mode;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
