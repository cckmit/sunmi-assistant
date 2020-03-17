package sunmi.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import sunmi.common.base.BaseApplication;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ServiceEnableResp;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yinhui
 * @date 2020-01-10
 */
public class ConfigManager implements BaseNotification.NotificationCenterDelegate {

    private static final String TAG = ConfigManager.class.getSimpleName();

    private static final String CONFIG_NAME = "prefs_config";

    private static final int RESULT_DISABLE = 0;
    private static final int RESULT_ENABLE = 1;

    private static final String PREFS_SERVICE_ENABLE_LOADED = "prefs_service_enable_loaded";
    private static final String PREFS_CASH_SECURITY_ENABLE = "prefs_cash_security_enable";

    private static final class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager get() {
        return Holder.INSTANCE;
    }

    private ConfigManager() {
        clearLoaded();
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.shopSwitched);
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.companySwitch);
        BaseNotification.newInstance().addStickObserver(this, CommonNotifications.perspectiveSwitch);
    }

    public void load(@Nullable Callback<?> callback) {
        clearLoaded();
        if (CommonHelper.isGooglePlay()) {
            getPrefs().edit()
                    .putBoolean(PREFS_SERVICE_ENABLE_LOADED, true)
                    .putBoolean(PREFS_CASH_SECURITY_ENABLE, false)
                    .apply();
            return;
        }
        SunmiStoreApi.getInstance().getServiceEnable(new RetrofitCallback<ServiceEnableResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceEnableResp data) {
                getPrefs().edit()
                        .putBoolean(PREFS_SERVICE_ENABLE_LOADED, true)
                        .putBoolean(PREFS_CASH_SECURITY_ENABLE, data.getAuditSecurityStatus() == RESULT_ENABLE)
                        .apply();
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceEnableResp data) {
                clearLoaded();
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
    }

    public boolean getCashSecurityEnable() {
        if (isLoaded()) {
            return getPrefs().getBoolean(PREFS_CASH_SECURITY_ENABLE, false);
        } else {
            return false;
        }
    }

    public void clearLoaded() {
        getPrefs().edit().putBoolean(PREFS_SERVICE_ENABLE_LOADED, false).apply();
    }

    public boolean isLoaded() {
        return getPrefs().getBoolean(PREFS_SERVICE_ENABLE_LOADED, false);
    }

    private SharedPreferences getPrefs() {
        return BaseApplication.getContext().getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.shopSwitched
                || id == CommonNotifications.companySwitch
                || id == CommonNotifications.perspectiveSwitch) {
            load(null);
        }
    }

    public interface Callback<T> {

        void onSuccess(T result);

        void onFail();
    }
}
