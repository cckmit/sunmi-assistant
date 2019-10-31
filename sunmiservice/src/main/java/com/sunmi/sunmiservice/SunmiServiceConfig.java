package com.sunmi.sunmiservice;

import android.content.Context;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import sunmi.common.base.BaseConfig;

public class SunmiServiceConfig extends BaseConfig {

    //增值创新服务
    public static String VALUE_ADDED_SERVICES = "";
    public static String SUNMI_MALL_HOST = "";

    //微信app id
    public static String WECHAT_APP_ID = "wxd1d7c6ec2279cfdc";
    public static String WECHAT_SECRET = "cd5ffb1b5b5fbc24b0816514c9baae13";

    //微信小程序相关
    public static String WECHART_USER_NAME = "gh_933e62947669";
    public static String WECHAT_PATH = "pages/index/index?from=app";
    public static int WECHAT_MINI_PROGRAM_TYPE;

    //微众银行
    public static String WE_BANK_HOST = "";


    //umeng id
    public static String UMENG_ID = "5c25e82df1f556993d00008c";

    //增值创新服务
    public static String FUMINBAO_APP_ID = "";
    public static String FUMINBAO_SECRET = "";

    @Override
    public void init(Context context, String env) {
        super.init(context, env);
        UMConfigure.init(context, UMENG_ID, "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        //与箭头处所指的地方进行对照，包名和签名如果不一样，改成一样即可。注意所谓的一样是指，大小写都要一样，
        // 不能有冒号出现在调试微信的时候请注意你的签名，
        //注意微信有时会在微信客户端缓存签名数据，因此修改微信开放平台应用签名后，注意清除微信客户端数据后再进行测试
        PlatformConfig.setWeixin(WECHAT_APP_ID, WECHAT_SECRET);
        UMConfigure.setLogEnabled(true);
    }

    @Override
    protected void initDev(Context context, String env) {
        VALUE_ADDED_SERVICES = "https://test-engine.sunmi.com/housekeeper/services-next";
        SUNMI_MALL_HOST = "https://test.h5.mall.sunmi.com/";
        FUMINBAO_APP_ID = "20190121639982";
        FUMINBAO_SECRET = "cce3387477aaac220b589c5d8c0b5f9d";
        WECHAT_MINI_PROGRAM_TYPE = 1;
        WE_BANK_HOST = "https://test.engine.sunmi.com/third-party/we-bank";
    }

    @Override
    protected void initTest(Context context, String env) {
        VALUE_ADDED_SERVICES = "https://test-engine.sunmi.com/housekeeper/services-next";
        SUNMI_MALL_HOST = "https://h5-mall.test.sunmi.com/";
        FUMINBAO_APP_ID = "20190121639982";
        FUMINBAO_SECRET = "cce3387477aaac220b589c5d8c0b5f9d";
        WECHAT_MINI_PROGRAM_TYPE = 1;
        WE_BANK_HOST = "https://test.engine.sunmi.com/third-party/we-bank";
    }

    @Override
    protected void initRelease(Context context, String env) {
        VALUE_ADDED_SERVICES = "https://engine.sunmi.com/housekeeper/services-next";
        SUNMI_MALL_HOST = "https://h5.mall.sunmi.com/";
        FUMINBAO_APP_ID = "20190121639982";
        FUMINBAO_SECRET = "cce3387477aaac220b589c5d8c0b5f9d";
        WECHAT_MINI_PROGRAM_TYPE = 0;
        WE_BANK_HOST = "https://engine.sunmi.com/third-party/we-bank";
    }

    @Override
    protected void initUat(Context context, String env) {
        VALUE_ADDED_SERVICES = "https://uat-engine.sunmi.com/housekeeper/services-next";
        SUNMI_MALL_HOST = "https://h5-mall.uat.sunmi.com/";
        FUMINBAO_APP_ID = "20190121639982";
        FUMINBAO_SECRET = "cce3387477aaac220b589c5d8c0b5f9d";
        WECHAT_MINI_PROGRAM_TYPE = 2;
        WE_BANK_HOST = "https://uat.engine.sunmi.com/third-party/we-bank";
    }

}
