package com.sunmi.cloudprinter.utils;

import com.sunmi.cloudprinter.bean.PrintRouter;
import com.sunmi.cloudprinter.constant.Constants;

import java.io.UnsupportedEncodingException;

import sunmi.common.utils.ByteUtils;

import static com.sunmi.cloudprinter.constant.Constants.PRINTER_CMD_TAG1;
import static com.sunmi.cloudprinter.constant.Constants.PRINTER_CMD_TAG2;

public class Utility {

    public static boolean isFirstPac(byte[] data) {
        int aa = data[0] & 0xFF;
        int bb = data[1] & 0xFF;
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
        return aa;
    }

    /**
     * 获取命令码
     */
    public static int getCmdId(byte[] data) {
        return (int) data[5];
    }

    /**
     * cmd = 1 返回的错误码
     */
    public static int getErrorCode(byte[] data) {
        return (int) data[6];
    }

    /**
     * 获取版本号
     */
    public static byte getVersion(byte[] data) {
        return data[4];
    }

    /**
     * 命令码 00 - 获取到打印机发过来的sn
     */
    public static String getSn(byte[] data) {
        byte[] bSn = new byte[13];
        System.arraycopy(data, 6, bSn, 0, bSn.length);
        return new String(bSn);
    }

    /**
     * 命令码 02
     */
    public static PrintRouter getRouter(byte[] data) {
        PrintRouter printRouter = new PrintRouter();
        try {
            byte[] bName = new byte[64];
            byte[] bRssi = new byte[4];
            System.arraycopy(data, 6, bName, 0, bName.length);
            byte bHasPwd = data[6 + bName.length];
            System.arraycopy(data, 7 + bName.length, bRssi, 0, bRssi.length);
            printRouter.setName(new String(bName, "utf-8"));
            if (bHasPwd == 0) {
                printRouter.setHasPwd(false);
            } else {
                printRouter.setHasPwd(true);
            }
            printRouter.setRssi(ByteUtils.byte4ToIntL(bRssi));
            printRouter.setEssid(bName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return printRouter;
    }

    /**
     * 命令码 05 - 获取打印机的sn
     */
    public static byte[] cmdGetSn() {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(6),
                getCmd(Constants.CMD_REQ_SN));
    }

    /**
     * 命令码06 - 获取打印机wifi列表
     */
    public static byte[] cmdGetWifi() {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(6),
                getCmd(Constants.CMD_REQ_WIFI_LIST));
    }

    /**
     * 命令码 07 - 打印机配置wifi
     */
    public static byte[] cmdConnectWifi(byte[] ssid, byte[] pwd) {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(134),
                getCmd(Constants.CMD_REQ_CONNECT_WIFI),
                ssid, pwd);
    }

    /**
     * 命令码 08 - 收到成功连接上指定AP通知的应答
     */
    public static byte[] cmdAlreadyConnectedWifi() {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(6),
                getCmd(Constants.CMD_REQ_WIFI_CONNECTED));
    }

    /**
     * 命令码 0A - 请求退出配网过程
     */
    public static byte[] cmdQuitConfig() {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(6),
                getCmd(Constants.CMD_REQ_QUIT_CONFIG));
    }

    /**
     * 命令码 0B - 请求删除wifi 配置
     */
    public static byte[] cmdDeleteWifiInfo() {
        return ByteUtils.byteMergerAll(
                getCmdTag(),
                ByteUtils.intToByte2(6),
                getCmd(Constants.CMD_REQ_DELETE_WIFI_INFO));
    }

    private static byte[] getCmd(int cmd) {
        return new byte[]{Constants.PRINTER_CMD_VERSION, (byte) cmd};
    }

}
