package sunmi.common.constant;

import android.text.TextUtils;
import android.util.ArrayMap;

import java.util.Map;

import sunmi.common.model.SunmiDevice;

/**
 * Created by YangShiJie on 2019/7/17.
 */
public class CommonUdpData {
    public static Map<String, SunmiDevice> sunmiDevMap = new ArrayMap<>();

    public static String currentRouterDeviceId(String deviceid) {
        if (sunmiDevMap == null || sunmiDevMap.isEmpty() || TextUtils.isEmpty(deviceid)) {
            return "";
        }
        if (CommonUdpData.sunmiDevMap.containsKey(deviceid)) {
            return CommonUdpData.sunmiDevMap.get(deviceid).getDeviceid();
        }
        return "";
    }

    public static String currentFactory(String deviceid) {
        if (sunmiDevMap == null || sunmiDevMap.isEmpty() || TextUtils.isEmpty(deviceid)) {
            return "";
        }
        if (CommonUdpData.sunmiDevMap.containsKey(deviceid) &&
                CommonUdpData.sunmiDevMap.get(deviceid).getType().equalsIgnoreCase("ROUTER")) {
            return CommonUdpData.sunmiDevMap.get(deviceid).getFactory();
        }
        return "";
    }

    public static String currentIpcDeviceId(String deviceid) {
        if (sunmiDevMap == null || sunmiDevMap.isEmpty() || TextUtils.isEmpty(deviceid)) {
            return "";
        }
        if (CommonUdpData.sunmiDevMap.containsKey(deviceid) &&
                CommonUdpData.sunmiDevMap.get(deviceid).getType().equalsIgnoreCase("IPC")) {
            return CommonUdpData.sunmiDevMap.get(deviceid).getDeviceid();
        }
        return "";
    }

    public static String currentIpcIp(String deviceid) {
        if (sunmiDevMap == null || sunmiDevMap.isEmpty() || TextUtils.isEmpty(deviceid)) {
            return "";
        }
        if (CommonUdpData.sunmiDevMap.containsKey(deviceid) &&
                CommonUdpData.sunmiDevMap.get(deviceid).getType().equalsIgnoreCase("IPC")) {
            return CommonUdpData.sunmiDevMap.get(deviceid).getIp();
        }
        return "";
    }
}
