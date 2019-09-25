package com.sunmi.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.meituan.android.walle.WalleChannelReader;
import com.sunmi.apmanager.config.ApConfig;
import com.sunmi.apmanager.rpc.mqtt.MQTTManager;
import com.sunmi.assistant.config.BootLoader;
import com.tencent.bugly.Bugly;
import com.tencent.stat.StatService;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.impl.application.ModuleManager;

import sunmi.common.base.BaseApplication;
import sunmi.common.rpc.mqtt.MqttManager;
import sunmi.common.utils.SpUtils;
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
        // 初始化组件化相关
        Component.init(this, BuildConfig.DEBUG);


        // 装载各个业务组件
        ModuleManager.getInstance().registerArr(
                "app", "ipc"
        );
        if (BuildConfig.DEBUG) {
            ModuleManager.getInstance().check();
        }
        BootLoader bootLoader = new BootLoader(this);
        bootLoader.init();
        StatService.registerActivityLifecycleCallbacks(this);
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
