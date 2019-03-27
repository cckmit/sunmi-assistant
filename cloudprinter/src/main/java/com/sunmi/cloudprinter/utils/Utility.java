package com.sunmi.cloudprinter.utils;

import com.sunmi.cloudprinter.bean.Router;

public class Utility {


    /**
     * 获取命令码
     * @param data
     * @return
     */
    public static int getCmd(byte[] data){
        return (int) data[5];
    }

    /**
     * 获取版本号
     * @param data
     * @return
     */
    public static byte getVersion(byte[] data){
        return data[4];
    }

    /**
     * 命令码 00
     * @param data
     * @return
     */
    public static String getSn(byte[] data) {
        byte[] bSn = new byte[14];
        System.arraycopy(data, 6, bSn, 0, bSn.length);
        return new String(bSn);
    }

    /**
     * 命令码 02
     * @param data
     * @return
     */
    public static Router getRouter(byte[] data) {
        byte[] bName = new byte[64];
        byte[] bRssi = new byte[4];
        System.arraycopy(data, 6, bName, 0, bName.length);
        byte bHasPwd = data[6 + bName.length];
        System.arraycopy(data, 7 + bName.length, bRssi, 0, bRssi.length);
        Router router = new Router();
        router.setName(new String(bName));
        if (bHasPwd == 0) {
            router.setHasPwd(false);
        } else {
            router.setHasPwd(true);
        }
        router.setRssi(ByteUtils.byte4ToInt(bRssi));
        router.setEssid(bName);
        return router;
    }

    /**
     * 命令码 05
     * @param version
     * @return
     */
    public static byte[] cmdGetSn(byte version) {
        byte[] getSn = new byte[6];
        byte[] len = ByteUtils.intToByte4(6);
        System.arraycopy(len, 0, getSn, 0, len.length);
        getSn[4] = version;
        getSn[5] = 5;
        return getSn;
    }

    /**
     * 命令码06
     * @param version
     * @return
     */
    public static byte[] cmdGetWifi(byte version) {
        byte[] getWifi = new byte[6];
        byte[] len = ByteUtils.intToByte4(6);
        System.arraycopy(len, 0, getWifi, 0, len.length);
        getWifi[4] = version;
        getWifi[5] = 6;
        return getWifi;
    }

    /**
     * 命令码 07
     * @param version
     * @param ssid
     * @param pwd
     * @return
     */
    public static byte[] cmdConnectWifi(byte version, byte[] ssid, byte[] pwd) {
        byte[] connectWifi = new byte[134];
        byte[] len = ByteUtils.intToByte4(134);
        System.arraycopy(len, 0, connectWifi, 0, len.length);
        connectWifi[4] = version;
        connectWifi[5] = 7;
        System.arraycopy(ssid, 0, connectWifi, 6, ssid.length);
        System.arraycopy(pwd, 0, connectWifi, 6 + ssid.length, pwd.length);
        return connectWifi;
    }

    /**
     * 命令码 08
     * @param version
     * @return
     */
    public static byte[] cmdAlreadyConnectedWifi(byte version) {
        byte[] connectedWifi = new byte[6];
        byte[] len = ByteUtils.intToByte4(6);
        System.arraycopy(len, 0, connectedWifi, 0, len.length);
        connectedWifi[4] = version;
        connectedWifi[5] = 8;
        return connectedWifi;
    }




}
