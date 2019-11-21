package com.sunmi.ipc.utils;

import java.util.regex.Pattern;

import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.enums.DeviceStatus;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-11-14
 */
public class IpcUtils {

    private static final String TAG = "IpcUtils";

    private static final Pattern IPC_VERSION_NAME = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");

    public static int getVersionCode(String version) {
        if (!isVersionValid(version)) {
            LogCat.e(TAG, "Version name of \"" + version + "\" is invalid.");
            return -1;
        }
        String[] split = version.split("\\.");
        int versionCode = 0;
        for (int i = 0, size = split.length; i < size; i++) {
            versionCode += Integer.valueOf(split[i]) * (int) Math.pow(100, 2 - i);
        }
        return versionCode;
    }

    public static boolean isVersionValid(String version) {
        return IPC_VERSION_NAME.matcher(version).matches();
    }

    public static boolean isIpcManageable(String deviceId, int status) {
        return status == DeviceStatus.ONLINE.ordinal()
                || CommonConstants.SUNMI_DEVICE_MAP.containsKey(deviceId);
    }

}
