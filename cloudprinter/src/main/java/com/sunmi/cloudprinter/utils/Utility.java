package com.sunmi.cloudprinter.utils;

import com.sunmi.cloudprinter.bean.Router;

import sunmi.common.utils.ByteUtils;
import sunmi.common.utils.log.LogCat;

import static com.sunmi.cloudprinter.constant.Constants.PRINTER_CMD_TAG1;
import static com.sunmi.cloudprinter.constant.Constants.PRINTER_CMD_TAG2;

public class Utility {

    private static byte btCmdVersion = 100;

    public static boolean isFirstPac(byte[] data) {
        int aa = data[0] & 0xFF;
        int bb = data[1] & 0xFF;
        LogCat.e("util", "555555 isFirstPac{" + aa + "," + bb + "}");
        return aa == 0xAA && bb == 0x55;
    }

    public static byte[] getCmdTag() {
        return new byte[]{PRINTER_CMD_TAG1, PRINTER_CMD_TAG2};
    }

    //命令头
    public static byte[] getCmdHead(int cmdLen) {
        return new byte[]{PRINTER_CMD_TAG1, PRINTER_CMD_TAG2};
    }

    /**
     * 获取包的长度
     */
    public static int getPacLength(byte[] data) {
        byte[] len = new byte[2];
        System.arraycopy(data, 2, len, 0, 2);
        int aa = ByteUtils.byte2ToInt(len);
        LogCat.e("util", "555555 getPacLength aa = " + aa);
        return aa;
    }

    /**
     * 获取命令码
     *
     * @param data
     * @return
     */
    public static int getCmd(byte[] data) {
        return (int) data[5];
    }

    /**
     * 获取版本号
     *
     * @param data
     * @return
     */
    public static byte getVersion(byte[] data) {
        return data[4];
    }

    /**
     * 命令码 00 - 获取到打印机发过来的sn
     */
    public static String getSn(byte[] data) {
        byte[] bSn = new byte[14];
        System.arraycopy(data, 6, bSn, 0, bSn.length);
        return new String(bSn);
    }

    /**
     * 命令码 02
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
     * 命令码 05 - 获取打印机的sn
     */
    public static byte[] cmdGetSn(byte version) {
        byte[] getSn = new byte[6];
        byte[] cmdTag = getCmdTag();
        System.arraycopy(cmdTag, 0, getSn, 0, cmdTag.length);
        byte[] len = ByteUtils.intToByte2(6);
        System.arraycopy(len, 0, getSn, 2, len.length);
        getSn[4] = version;
        getSn[5] = 5;
        return getSn;
    }

    /**
     * 命令码06 - 获取打印机wifi列表
     */
    public static byte[] cmdGetWifi(byte version) {
        byte[] getWifi = new byte[6];
        byte[] cmdTag = getCmdTag();
        System.arraycopy(cmdTag, 0, getWifi, 0, cmdTag.length);
        byte[] len = ByteUtils.intToByte2(6);
        System.arraycopy(len, 0, getWifi, 2, len.length);
        getWifi[4] = version;
        getWifi[5] = 6;
        return getWifi;
    }

    /**
     * 命令码 07 - 打印机配置wifi
     */
    public static byte[] cmdConnectWifi(byte[] ssid, byte[] pwd) {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(134),
                getCmd(7),
                ssid, pwd);
//        byte[] connectWifi = new byte[134];
//        byte[] len = ByteUtils.intToByte4(134);
//        System.arraycopy(len, 0, connectWifi, 0, len.length);
//        connectWifi[4] = version;
//        connectWifi[5] = 7;
//        System.arraycopy(ssid, 0, connectWifi, 6, ssid.length);
//        System.arraycopy(pwd, 0, connectWifi, 6 + ssid.length, pwd.length);
//        return connectWifi;
    }

    /**
     * 命令码 08
     */
    public static byte[] cmdAlreadyConnectedWifi() {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(6),
                getCmd(8));

//        byte[] connectedWifi = new byte[6];
//        byte[] len = ByteUtils.intToByte4(6);
//        System.arraycopy(len, 0, connectedWifi, 0, len.length);
//        connectedWifi[4] = version;
//        connectedWifi[5] = 8;
//        return connectedWifi;
    }

    private static byte[] getCmd(int cmd) {
        return new byte[]{btCmdVersion, (byte) cmd};
    }

}
