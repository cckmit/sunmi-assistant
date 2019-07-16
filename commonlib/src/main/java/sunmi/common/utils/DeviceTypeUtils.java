package sunmi.common.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.commonlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sunmi.common.base.BaseApplication;
import sunmi.common.model.DeviceTypeBean;

/**
 * Description:
 * Created by bruce on 2019/7/16.
 */
public class DeviceTypeUtils {
    private List<DeviceTypeBean> list = new ArrayList<>();
    private Map<String, DeviceTypeBean> sunmiDevice = new HashMap<>();

    private static class SingletonHolder {
        private static DeviceTypeUtils INSTANCE = new DeviceTypeUtils();
    }

    public static DeviceTypeUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public DeviceTypeUtils() {
        init();
    }

    private void init() {
        list = JSON.parseArray(FileUtils.getStringFromAssets(
                BaseApplication.getContext(), "device.json"), DeviceTypeBean.class);
        for (DeviceTypeBean object : list) {
            if (!TextUtils.isEmpty(object.getModel()))
                sunmiDevice.put(object.getModel(), object);
        }
    }

    public int getBrandImage(String mac) {
        int resourceId = R.mipmap.unknow;//设置默认

        for (DeviceTypeBean object : list) {
            if (object.getMac() != null)
                for (String s : object.getMac()) {
                    if (s.substring(0, 8).equalsIgnoreCase(mac)) {
                        resourceId = CommonHelper.getResource(BaseApplication.getContext(),
                                object.getBrand(), R.mipmap.unknow);
                        break;
                    }
                }
        }
        return resourceId;
    }

    public int getSunmiDeviceImage(String model) {
        int resourceId = R.mipmap.unknow;
        if (sunmiDevice.get(model) != null) {
            resourceId = CommonHelper.getResource(BaseApplication.getContext(),
                    sunmiDevice.get(model).getBrand(), R.mipmap.unknow);
        }
        return resourceId;
    }

    public boolean isSS1(String model) {
        if (sunmiDevice.get(model) != null) {
            return TextUtils.equals("ss1", sunmiDevice.get(model).getBrand());
        }
        return false;
    }

    public boolean isFS1(String model) {
        if (sunmiDevice.get(model) != null) {
            return TextUtils.equals("fs1", sunmiDevice.get(model).getBrand());
        }
        return false;
    }

}
