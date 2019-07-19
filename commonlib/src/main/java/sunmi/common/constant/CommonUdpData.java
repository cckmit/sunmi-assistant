package sunmi.common.constant;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.model.SunmiDevice;

/**
 * Created by YangShiJie on 2019/7/17.
 */
public class CommonUdpData {
    public static List<SunmiDevice> udpList = new ArrayList<>();

    public static String currentRouterDeviceId(String deviceid) {
        if (udpList == null || udpList.size() == 0) {
            return "";
        }
        for (SunmiDevice sd : udpList) {
            if ("ROUTER".equalsIgnoreCase(sd.getType()) && sd.getDeviceid().equalsIgnoreCase(deviceid)) {
                return sd.getDeviceid();
            }
        }
        return "";
    }

    public static String currentFactory(String deviceid) {
        if (udpList == null || udpList.size() == 0) {
            return "";
        }
        for (SunmiDevice sd : udpList) {
            if ("ROUTER".equalsIgnoreCase(sd.getType()) && sd.getDeviceid().equalsIgnoreCase(deviceid)) {
                return sd.getFactory();
            }
        }
        return "";
    }

    public static String currentIpcDeviceId(String deviceid) {
        if (udpList == null || udpList.size() == 0) {
            return "";
        }
        for (SunmiDevice sd : udpList) {
            if ("IPC".equalsIgnoreCase(sd.getType()) && sd.getDeviceid().equalsIgnoreCase(deviceid)) {
                return sd.getDeviceid();
            }
        }
        return "";
    }

    public static String currentIpcIp(String deviceid) {
        if (udpList == null || udpList.size() == 0) {
            return "";
        }
        for (SunmiDevice sd : udpList) {
            if ("IPC".equalsIgnoreCase(sd.getType()) && sd.getDeviceid().equalsIgnoreCase(deviceid)) {
                return sd.getIp();
            }
        }
        return "";
    }

    public static String currentIpcModel(String deviceid) {
        if (udpList == null || udpList.size() == 0) {
            return "";
        }
        for (SunmiDevice sd : udpList) {
            if ("IPC".equalsIgnoreCase(sd.getType()) && sd.getDeviceid().equalsIgnoreCase(deviceid)) {
                return sd.getModel();
            }
        }
        return "";
    }
}
