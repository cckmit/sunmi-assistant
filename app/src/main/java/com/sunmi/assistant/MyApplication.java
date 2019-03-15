package com.sunmi.assistant;

import android.text.TextUtils;

import com.sunmi.apmanager.config.ApConfig;
import com.sunmi.assistant.config.BootLoader;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

import sunmi.common.base.BaseApplication;
import sunmi.common.utils.Utils;

/**
 * Created by shiJie.yang on 2018/9/13.
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        BootLoader bootLoader = new BootLoader(this);
        bootLoader.init();
        //腾讯移动分析
        StatConfig.setDebugEnable(!TextUtils.equals(Utils.getMetaValue(this,
                "ENV_DATA", ApConfig.ENV_TEST), ApConfig.ENV_RELEASE));
        StatService.registerActivityLifecycleCallbacks(this);

    }

}
