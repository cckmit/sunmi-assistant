package com.sunmi.sunmiservice;

import android.content.Context;
import android.text.TextUtils;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import sunmi.common.base.BaseBootLoader;

public class SunmiServiceConfig extends BaseBootLoader {

    //增值创新服务
    public static String VALUE_ADDED_SERVICES = "";

    //微信app id
    public static String WECHAT_APP_ID = "wxd1d7c6ec2279cfdc";
    public static String WECHAT_SECRET = "cd5ffb1b5b5fbc24b0816514c9baae13";

    //umeng id
    public static String UMENG_ID = "5c25e82df1f556993d00008c";

    //增值创新服务
    public static String FUMINBAO_APP_ID = "";
    public static String FUMINBAO_SECRET = "";

    @Override
    public void init(Context context, String env) {
        initEnv(env);
        UMConfigure.init(context, UMENG_ID, "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        //与箭头处所指的地方进行对照，包名和签名如果不一样，改成一样即可。注意所谓的一样是指，大小写都要一样，
        // 不能有冒号出现在调试微信的时候请注意你的签名，
        //注意微信有时会在微信客户端缓存签名数据，因此修改微信开放平台应用签名后，注意清除微信客户端数据后再进行测试
        PlatformConfig.setWeixin(WECHAT_APP_ID, WECHAT_SECRET);
        UMConfigure.setLogEnabled(true);
    }

    /**
     * 初始化
     */
    public static void initEnv(String env) {
        if (TextUtils.equals(ENV_DEV, env)) {
            VALUE_ADDED_SERVICES = "https://test.engine.sunmi.com/housekeeper/services-next";
            FUMINBAO_APP_ID = "20190121639982";
            FUMINBAO_SECRET = "cce3387477aaac220b589c5d8c0b5f9d";
        } else if (TextUtils.equals(ENV_TEST, env)) {
            VALUE_ADDED_SERVICES = "https://test.engine.sunmi.com/housekeeper/services-next";
//            VALUE_ADDED_SERVICES = "http://10.10.160.87:3005/housekeeper/services";
            FUMINBAO_APP_ID = "20190121639982";
            FUMINBAO_SECRET = "cce3387477aaac220b589c5d8c0b5f9d";
        } else if (TextUtils.equals(ENV_RELEASE, env)) {
            VALUE_ADDED_SERVICES = "https://engine.sunmi.com/housekeeper/services-next";
        }
    }

}
