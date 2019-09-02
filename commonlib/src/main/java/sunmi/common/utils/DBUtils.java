package sunmi.common.utils;

import android.content.Context;
import android.text.TextUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import sunmi.common.model.SunmiDevice;

/**
 * Description:
 * Created by bruce on 2019/9/2.
 */
public class DBUtils {
    public static void initDb(Context context) {
        LitePal.initialize(context);
        if (!TextUtils.isEmpty(SpUtils.getUID())
                && SpUtils.isLoginSuccess()) {
            LitePalDB litePalDB = LitePalDB.fromDefault(SpUtils.getUID());
            LitePal.use(litePalDB);
        }
    }

    public static void switchDb(String name) {
        LitePalDB litePalDB = LitePalDB.fromDefault(name);
        LitePal.use(litePalDB);
    }

    public static void deleteSunmiDevice(String sn) {
        DataSupport.deleteAll(SunmiDevice.class, "deviceid=?", sn);
    }

    public static void deleteSunmiDeviceByType(String type) {
        DataSupport.deleteAll(SunmiDevice.class, "type=?", type);
    }

}
