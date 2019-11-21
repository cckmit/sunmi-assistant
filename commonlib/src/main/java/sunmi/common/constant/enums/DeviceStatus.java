package sunmi.common.constant.enums;

import com.commonlibrary.R;

import sunmi.common.base.BaseApplication;

/**
 * Description:设备在线状态
 * Created by bruce on 2019/5/31.
 */
public enum DeviceStatus {
    OFFLINE(BaseApplication.getInstance().getString(R.string.device_status_offline)),
    ONLINE(BaseApplication.getInstance().getString(R.string.device_status_online)),
    EXCEPTION(BaseApplication.getInstance().getString(R.string.device_status_exception)),
    UNKNOWN(BaseApplication.getInstance().getString(R.string.device_status_unknown));

    private final String value;

    DeviceStatus(String value) {
        this.value = value;
    }

    public static DeviceStatus valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException(" enum DeviceStatus Invalid ordinal");
        }
        return values()[ordinal];
    }

    public String getValue() {
        return value;
    }

}
