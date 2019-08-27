package com.sunmi.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.sunmi.apmanager.config.ApConfig;
import com.sunmi.apmanager.rpc.mqtt.MQTTManager;
import com.sunmi.assistant.config.BootLoader;
import com.sunmi.ipc.rpc.mqtt.MqttManager;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import sunmi.common.base.BaseApplication;
import sunmi.common.constant.CommonConfig;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;

/**
 * Created by shiJie.yang on 2018/9/13.
 */
public class MyApplication extends BaseApplication {

    private static final int TIME_TO_CHECK_KILL_SELF = 10 * 60 * 1000;
    private int startedActivityCount = 0;
    private static boolean isInBackground = false;
    private static Handler handler = new Handler();

    private Runnable mAutoKillSelfRunnable = new Runnable() {
        @Override
        public void run() {
            if (isInBackground && !MQTTManager.getInstance().isConnected()) {
                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                handler.postDelayed(this, TIME_TO_CHECK_KILL_SELF);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        this.registerActivityLifecycleCallbacks(new HhActivityLifecycleCallbacks());
    }

    private void init() {
        BootLoader bootLoader = new BootLoader(this);
        bootLoader.init();
        //腾讯移动分析
        StatConfig.setDebugEnable(!TextUtils.equals(Utils.getMetaValue(this,
                "ENV_DATA", ApConfig.ENV_TEST), ApConfig.ENV_RELEASE));
        StatService.registerActivityLifecycleCallbacks(this);
        initMiPush();
    }

    private void initMiPush() {
        MiPushClient.registerPush(this, CommonConfig.MI_PUSH_APP_ID, CommonConfig.MI_PUSH_APP_KEY);
        if (!TextUtils.isEmpty(SpUtils.getUID()))
            MiPushClient.setAlias(this, SpUtils.getUID(), null);
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d("mipush", content, t);
            }

            @Override
            public void log(String content) {
                Log.d("mipush", content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    class HhActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (startedActivityCount == 0) {
                LogCat.e("HhActivityLifecycleCB", "onActivityStarted : hasLogin = "
                        + SpUtils.getLoginStatus() + ", activity = " + activity.getClass().getName());
                if (SpUtils.isLoginSuccess()) {
                    MQTTManager.getInstance().reconnect();
                    MqttManager.getInstance().reconnect();
                }
            }
            startedActivityCount++;
            isInBackground = (startedActivityCount == 0);
            if (!isInBackground) {
                handler.removeCallbacks(mAutoKillSelfRunnable);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            startedActivityCount--;
            isInBackground = (startedActivityCount == 0);
            if (isInBackground) {
                handler.postDelayed(mAutoKillSelfRunnable, TIME_TO_CHECK_KILL_SELF);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

}
